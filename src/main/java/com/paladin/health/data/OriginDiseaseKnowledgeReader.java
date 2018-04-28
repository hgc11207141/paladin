package com.paladin.health.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.paladin.framework.common.GeneralCriteriaBuilder;
import com.paladin.framework.common.QueryType;
import com.paladin.framework.utils.StringUtil;
import com.paladin.health.data.parser.KnowledgePageParser;
import com.paladin.health.data.parser.KnowledgePageParser.ArticleElement;
import com.paladin.health.data.parser.KnowledgePageParser.ArticleTitle;
import com.paladin.health.data.parser.KnowledgePageParser.ElementType;
import com.paladin.health.data.parser.KnowledgePageParser.Knowledge;
import com.paladin.health.mapper.origin.OriginDiseaseKnowledgeContentMapper;
import com.paladin.health.mapper.origin.OriginDiseaseKnowledgeMapper;
import com.paladin.health.model.origin.OriginDiseaseKnowledge;
import com.paladin.health.model.origin.OriginDiseaseKnowledgeContent;
import com.paladin.health.model.origin.OriginDiseaseName;
import com.paladin.health.service.origin.OriginDiseaseKnowledgeService;
import com.paladin.health.service.origin.OriginDiseaseNameService;

@Component
public class OriginDiseaseKnowledgeReader {

	private static Logger logger = LoggerFactory.getLogger(OriginDiseaseKnowledgeReader.class);

	static KnowledgePageParser knowledgePageParser = new KnowledgePageParser();

	static String[][] categories = new String[][] { { "yyzl", "治疗" }, { "zztz", "典型症状" }, { "blby", "发病原因" }, { "yfhl", "预防" }, { "jcjb", "临床检查" },
			{ "jb", "鉴别" }, { "hl", "护理" }, { "ysbj", "饮食保健" }, { "bfbz", "并发症" } };

	static Map<String, String[]> categoryNameMap = new HashMap<>();

	static {

		for (String[] ss : categories) {
			String[] v = new String[ss.length - 1];
			System.arraycopy(ss, 1, v, 0, ss.length - 1);
			categoryNameMap.put(ss[0], v);
		}

	}

	@Autowired
	OriginDiseaseNameService diseaseNameService;
	@Autowired
	OriginDiseaseKnowledgeService diseaseKnowledgeService;
	@Autowired
	OriginDiseaseKnowledgeMapper diseaseKnowledgeMapper;
	@Autowired
	OriginDiseaseKnowledgeContentMapper diseaseKnowledgeContentMapper;

	volatile int threadCount;

	public void readDiseaseKnowledge() {

		logger.info("开始读取疾病知识数据");

		final List<OriginDiseaseName> names = diseaseNameService
				.searchAll(new GeneralCriteriaBuilder.Condition(OriginDiseaseName.COLUMN_FIELD_TYPE, QueryType.EQUAL, 1));

		int count = names.size();
		int i = 0;
		int size = 1000;
		boolean goon = true;

		logger.info("开始读取疾病名称数据");

		while (goon) {

			if (i > count) {
				break;
			}

			int end = i + size;
			if (end > count) {
				end = count;
				goon = false;
			}

			final int s = i;
			final int e = end - 1;
			final int ti = ++threadCount;

			if (ti == 8 || ti == 5) {

				Thread th = new Thread(new Runnable() {
					@Override
					public void run() {
						logger.info("开始读取第" + s + "条到第" + e + "条疾病知识");

						int d = 1000000;

						int kbase = ti * d;
						int cbase = kbase;

						int underId = kbase + d;

						Integer kmax = diseaseKnowledgeMapper.getMaxId(underId);
						Integer cmax = diseaseKnowledgeMapper.getContentMaxId(underId);

						if (cmax != null && cmax > cbase) {
							cbase = cmax;
						}

						if (kmax != null && kmax > kbase) {
							kbase = kmax;
						}

						int[] ids = new int[] { kbase, cbase };
						boolean isNew = false;

						for (int x = s; x <= e; x++) {
							OriginDiseaseName name = names.get(x);
							String disease = name.getNameKey();
							String diseaseName = name.getName();
							if (!isNew && diseaseKnowledgeService.searchAll(new GeneralCriteriaBuilder.Condition("diseaseKey", QueryType.EQUAL, disease))
									.size() > 0) {
								continue;
							} else {
								isNew = true;
							}

							for (String[] category : categories) {
								String categoryKey = category[0];
								try {
									Knowledge know = knowledgePageParser.parse(disease, categoryKey);
									ArticleItem articleItem = doCategory(categoryKey, diseaseName, know);
									save(articleItem, disease, categoryKey, null, ids);
								} catch (Exception e) {
									logger.error("读取数据异常[" + diseaseName + ":" + disease + "][类型:" + categoryKey + "][序号:" + x + "]", e);
									logger.info("删除疾病[" + disease + "]数据");
									diseaseKnowledgeMapper.deleteDiseaseContent(disease);
									diseaseKnowledgeMapper.deleteDiseaseKnowledge(disease);
									logger.info("退出读取第" + s + "条到第" + e + "条疾病知识");
									return;
								}
							}

						}
						logger.info("成功读取第" + s + "条到第" + e + "条疾病知识");
						endThread();
					}
				});

				th.start();
			}
			i = end;
		}
	}

	private void endThread() {
		if (--threadCount == 0) {
			logger.info("结束读取疾病知识数据");
		}
	}

	private void save(ArticleItem articleItem, String diseaseKey, String categoryKey, Integer parentId, int[] ids) {
		OriginDiseaseKnowledge k = new OriginDiseaseKnowledge();

		int kid = ids[0];
		ids[0] = ++kid;

		k.setId(kid);
		k.setDiseaseKey(diseaseKey);
		k.setName(articleItem.title);
		k.setParentId(parentId);
		k.setCategoryKey(categoryKey);

		diseaseKnowledgeMapper.insert(k);

		for (String content : articleItem.contents) {

			OriginDiseaseKnowledgeContent kc = new OriginDiseaseKnowledgeContent();
			kc.setContent(content);
			kc.setDiseaseKey(diseaseKey);
			kc.setKnowledgeId(kid);

			int cid = ids[1];
			ids[1] = ++cid;

			kc.setId(cid);

			diseaseKnowledgeContentMapper.insert(kc);
		}

		for (ArticleItem child : articleItem.children) {
			save(child, diseaseKey, categoryKey, kid, ids);
		}
	}

	private ArticleItem doCategory(String category, String diseaseName, Knowledge knowledge) {
		ArticleItem item = new ArticleItem();
		item.title = categoryNameMap.get(category)[0];

		if (knowledge == null) {
			return item;
		}

		for (List<ArticleElement> list : knowledge.getArticleElements()) {
			if (list.size() > 0) {
				Cursor cursor = new Cursor();
				// cursor.category = category;
				cursor.diseaseName = diseaseName;
				cursor.setList(list);

				doContent(cursor, item);
			}
		}

		repair(item);

		return item;
	}

	private void doContent(Cursor cursor, ArticleItem item) {

		while (cursor.hasNext()) {
			ArticleElement element = cursor.next();

			ElementType type = element.getType();

			if (type == ElementType.TYPE_CONTENT) {
				item.contents.add(element.getContent());
			} else {

				if (type == ElementType.TYPE_TITLE) {
					ArticleTitle title = element.getTitle();
					int index = title.getIndex();

					if (index == item.currentIndex + 1) {

					} else {
						int tl = title.getTitleLevel();
						String style = title.getStyle();

						if (item.childTitleLevel != null) {
							if ("".equals(style)) {
								if (tl != item.childTitleLevel) {
									cursor.back();
									break;
								}
							} else {
								if (!style.equals(item.childTitleStyle) || tl != item.childTitleLevel) {
									cursor.back();
									break;
								}
							}
						} else {

							if (tl == 3) {
								if (item.parent != null && item.parent.childTitleLevel == 3) {
									cursor.back();
									break;
								}
							} else {
								cursor.back();
								break;
							}
						}
					}

					ArticleItem child = new ArticleItem();
					child.parent = item;
					child.level = item.level + 1;
					child.title = element.getContent();
					item.children.add(child);

					item.currentIndex++;
					if (item.childTitleLevel == null) {
						item.childTitleLevel = title.getTitleLevel();
						item.childTitleStyle = title.getStyle();
					}

					doContent(cursor, child);

				} else {
					if (item.level == 1) {
						String name = element.getContent();

						if (name.startsWith(cursor.diseaseName)) {
							name = name.substring(cursor.diseaseName.length());
							if (name.startsWith("病") && !cursor.diseaseName.endsWith("病")) {
								name = name.substring(1);
							}
						}

						/*
						 * String[] names = categoryNameMap.get(cursor.category); boolean has = false;
						 * for (String n : names) { if (n.equals(name)) { has = true; break; } }
						 * 
						 * if (has) { continue; }
						 */

						ArticleItem child = new ArticleItem();
						child.parent = item;
						child.level = item.level + 1;
						child.title = name;
						item.children.add(child);

						doContent(cursor, child);
					} else {
						cursor.back();
						break;
					}
				}
			}
		}
	}

	private void repair(ArticleItem item) {

		/*
		 * 修复部分数据，标题头字符不正确，①排序错误
		 */

		String title = item.title;
		if (title != null && title.length() > 0) {
			title = StringUtil.strongTrim(title);
			if (title.startsWith(".") || title.startsWith("．")) {
				title = title.substring(1);
			}

			char c = title.charAt(title.length() - 1);
			if ((c >= '0' && c <= '9') || (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z') || (c >= 0x4E00 && c <= 0x9FA5)) {

			} else {
				title = title.substring(0, title.length() - 1);
			}

			item.title = title;
		}

		char[] cs = new char[100];

		int i = 0;
		int s = -1;
		int e = 0;
		for (int j = 0; j < item.contents.size(); j++) {
			String content = item.contents.get(j);
			if (content.length() > 0) {
				char c = content.charAt(0);
				if (c >= '①' && c <= '⑲') {
					cs[i++] = c;

					if (s == -1) {
						s = j;
					}
					e = j;
				}
			}
		}

		if (i > 0) {

			boolean wrong = false;
			int b = '①';
			if (cs[0] != b) {
				wrong = true;
			} else {
				for (int m = 0; m < i; m++) {
					if (cs[m] != (b + m)) {
						wrong = true;
						break;
					}
				}
			}

			if (wrong) {
				if (i == (e - s + 1)) {
					// 可修复
					int k = 0;
					for (; s <= e; s++) {
						String content = item.contents.get(s);
						content = String.valueOf((char) (b + k)) + content.substring(1);
						k++;
						item.contents.set(s, content);
					}
				}
			}
		}

		if (item.children != null) {
			for (ArticleItem sub : item.children) {
				repair(sub);
			}
		}

	}

	private static class Cursor {

		int index;
		String diseaseName;
		// String category;
		List<ArticleElement> list;

		void setList(List<ArticleElement> list) {
			boolean isWrong = false;
			int count = 0;
			// 处理分类和标题错误情况
			for (int i = 0; i < list.size(); i++) {
				ArticleElement e = list.get(i);
				if (e.getType() == ElementType.TYPE_CATEGORY) {
					i++;
					if (i >= list.size()) {
						break;
					}

					e = list.get(i);
					if (e.getType() == ElementType.TYPE_TITLE) {
						if (e.getTitle().getIndex() != 1) {
							isWrong = true;
							break;
						}
					}

					if (isWrong) {
						break;
					}
				}
			}

			for (ArticleElement e : list) {
				if (e.getType() == ElementType.TYPE_CATEGORY) {
					count++;
				}
			}

			if (isWrong || count == 1) {
				List<ArticleElement> newList = new ArrayList<>();
				for (ArticleElement e : list) {
					if (e.getType() != ElementType.TYPE_CATEGORY) {
						newList.add(e);
					}
				}

				list = newList;
			}

			this.list = list;
		}

		boolean hasNext() {
			return index < list.size();
		}

		ArticleElement next() {
			if (index < list.size()) {
				return list.get(index++);
			}
			return null;
		}

		void back() {
			index--;
		}

	}

	public static class ArticleItem {

		String title;
		List<String> contents = new ArrayList<>();

		Integer currentIndex = 0;
		int level = 1;
		String childTitleStyle;
		Integer childTitleLevel;

		ArticleItem parent;
		List<ArticleItem> children = new ArrayList<>();

		public String getTitle() {
			return title;
		}

		public List<String> getContents() {
			return contents;
		}

		public List<ArticleItem> getChildren() {
			return children;
		}

	}
}

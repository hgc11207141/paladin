package com.paladin.health.data.parser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.paladin.framework.utils.PinyinUtil;
import com.paladin.framework.utils.StringUtil;

public class SummaryPageParser extends PageParser {

	private static HashMap<String, Point> pointMap = new HashMap<>();

	static {

		List<Point> points = new ArrayList<>();
		points.add(new Point("是否属于医保", PointType.DEFAULT));
		points.add(new Point("别名", PointType.MUTIL_ITEM));
		points.add(new Point("发病部位", PointType.MUTIL_ITEM));
		points.add(new Point("传染性", PointType.DEFAULT));
		points.add(new Point("多发人群", PointType.MUTIL_ITEM));
		points.add(new Point("相关症状", PointType.MUTIL_ITEM));
		points.add(new Point("并发疾病", PointType.MUTIL_ITEM));

		points.add(new Point("是否遗传", PointType.DEFAULT));
		points.add(new Point("是否会遗传", "sfyc", PointType.DEFAULT));
		points.add(new Point("遗传方式", PointType.DEFAULT));
		points.add(new Point("传播途径", PointType.DEFAULT));
		points.add(new Point("传染病种类", PointType.DEFAULT));
		points.add(new Point("潜伏期", PointType.DEFAULT));
		points.add(new Point("潜伏期表现", PointType.DEFAULT));

		points.add(new Point("就诊科室", PointType.MUTIL_ITEM));
		points.add(new Point("治疗费用", PointType.DEFAULT));
		points.add(new Point("治愈率", PointType.DEFAULT));
		points.add(new Point("治疗周期", PointType.DEFAULT));
		points.add(new Point("治疗方法", PointType.MUTIL_ITEM));
		points.add(new Point("相关检查", PointType.MUTIL_ITEM));
		points.add(new Point("相关手术", PointType.MUTIL_ITEM));
		points.add(new Point("常用药品", PointType.MUTIL_ITEM));

		points.add(new Point("最佳就诊时间", PointType.DEFAULT));
		points.add(new Point("就诊时长", PointType.DEFAULT));
		points.add(new Point("复诊频率/诊疗周期", PointType.DEFAULT));
		points.add(new Point("门诊治疗", "fzplzlzq", PointType.DEFAULT));
		points.add(new Point("就诊前准备", PointType.DEFAULT));

		for (Point point : points) {
			pointMap.put(point.name, point);
		}

	}

	public static void main(String[] args) throws IOException {
		SummaryPageParser p = new SummaryPageParser();

		System.out.println(new ObjectMapper().writeValueAsString(p.parse("tnb")));
	}

	public Summary parse(String disease) throws IOException {

		Document doc = getDocument("http://jbk.39.net/" + disease + "/jbzs/");
		Summary summary = new Summary();

		Elements chiKnowElements = doc.getElementsByClass("chi-know");

		if (chiKnowElements == null || chiKnowElements.size() != 1) {
			throw new RuntimeException("疾病[" + disease + "]页面无法解析");
		}

		Elements infoElements = chiKnowElements.get(0).children();
		List<SummaryPoint> summaryPoints = new ArrayList<>();

		for (Element infoElement : infoElements) {

			if (infoElement.hasClass("intro")) {
				summary.setSummary(infoElement.child(1).text());
			} else if (infoElement.hasClass("info")) {

				Elements ddElements = infoElement.children();
				for (Element dd : ddElements) {
					if ("dd".equals(dd.tag().getName())) {
						String name = null;
						try {
							Element titleElement = dd.child(0);
							if (!"i".equals(titleElement.tag().getName())) {
								continue;
							}
							name = titleElement.text();
						} catch (Exception e) {

							String text = dd.text();
							int j = text.indexOf("：");

							if (j > 0) {
								name = text.substring(0, j + 1);
							} else {								
								System.out.println("疾病[" + disease + "]页面无法解析--->" + text);
								continue;
							}
						}

						boolean hasMaohao = false;
						if (name.endsWith(":") || name.endsWith("：")) {
							name = name.substring(0, name.length() - 1);
							hasMaohao = true;
						}

						Point point = pointMap.get(name);
						if (point == null) {
							System.out.println("存在新的Point[" + name + "]");
							continue;
						}

						List<String> pointContents = new ArrayList<>();

						if (point.type == PointType.DEFAULT) {
							String content = dd.text();
							int start = hasMaohao ? name.length() + 1 : name.length();
							content = content.substring(start, content.length());
							pointContents.add(content);
						} else if (point.type == PointType.MUTIL_ITEM) {

							Elements aElements = dd.children();
							if (aElements.size() > 1) {
								for (int i = 1; i < aElements.size(); i++) {

									Element e = aElements.get(i);
									if ("a".equals(e.tag().getName())) {
										String content = e.text();
										if (content.endsWith("...")) {
											String x = e.attr("title");
											if (x != null && x.length() != 0) {
												content = x;
											}
										}

										pointContents.addAll(splitContent(content));
									}
								}
							} else {
								String content = dd.text();
								int start = hasMaohao ? name.length() + 1 : name.length();
								content = content.substring(start, content.length());
								List<String> cs = splitContent(content);
								if (cs != null) {
									pointContents.addAll(cs);
								}
							}
						}
						summaryPoints.add(new SummaryPoint(point, pointContents));
					}
				}
			}
		}

		summary.setPoints(summaryPoints);
		return summary;
	}

	private List<String> splitContent(String content) {

		if (content == null || content.length() == 0) {
			return null;
		}

		String[] ss = content.split("，");
		if (ss.length == 1) {
			ss = content.split("、");
		}

		List<String> list = new ArrayList<>(ss.length);

		for (String s : ss) {
			String[] y = s.split("和");
			for (String x : y) {
				list.add(x);
			}
		}

		return list;
	}

	public static class Summary {

		String summary;
		List<SummaryPoint> points;

		public String getSummary() {
			return summary;
		}

		public void setSummary(String summary) {
			this.summary = StringUtil.strongTrim(summary);
		}

		public List<SummaryPoint> getPoints() {
			return points;
		}

		public void setPoints(List<SummaryPoint> points) {
			this.points = points;
		}
	}

	public static class SummaryPoint {

		String name;
		String key;
		PointType type;
		List<String> contents;

		SummaryPoint(Point point, List<String> contents) {
			this.name = point.name;
			this.type = point.type;
			this.key = point.key;

			List<String> nc = new ArrayList<>(contents.size());
			for (String n : contents) {
				if (!n.contains("暂无相关资料")) {
					nc.add(n.trim());
				}
			}

			this.contents = nc;
			repair();
		}

		private void repair() {
			if (contents != null && contents.size() > 0) {
				int i = contents.size() - 1;
				String s = contents.get(i);
				if ("[详细]".equals(s)) {
					contents.remove(i);
				} else if (s.endsWith("[详细]")) {
					contents.set(i, s.substring(0, s.length() - 4));
				}
			}
		}

		public String getName() {
			return name;
		}

		public PointType getType() {
			return type;
		}

		public List<String> getContents() {
			return contents;
		}

		public String getKey() {
			return key;
		}

	}

	private static class Point {

		String key;
		String name;
		PointType type;

		Point(String name, String key, PointType type) {
			this.name = name;
			this.type = type;
			this.key = key;
		}

		Point(String name, PointType type) {
			this.name = name;
			this.type = type;
			this.key = PinyinUtil.toHanyuPinyinFirstArray(name);
		}
	}

	public static enum PointType {
		DEFAULT, MUTIL_ITEM;
	}

}

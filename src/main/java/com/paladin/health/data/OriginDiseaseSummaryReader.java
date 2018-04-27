package com.paladin.health.data;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.paladin.framework.common.GeneralCriteriaBuilder;
import com.paladin.framework.common.QueryType;
import com.paladin.framework.utils.reflect.NameUtil;
import com.paladin.framework.utils.reflect.ReflectUtil;
import com.paladin.health.data.parser.SummaryPageParser;
import com.paladin.health.data.parser.SummaryPageParser.PointType;
import com.paladin.health.data.parser.SummaryPageParser.Summary;
import com.paladin.health.data.parser.SummaryPageParser.SummaryPoint;
import com.paladin.health.mapper.origin.OriginDiseaseSummaryMapper;
import com.paladin.health.model.origin.OriginDiseaseName;
import com.paladin.health.model.origin.OriginDiseaseSummary;
import com.paladin.health.service.origin.OriginDiseaseNameService;

@Component
public class OriginDiseaseSummaryReader {

	private static Logger logger = LoggerFactory.getLogger(OriginDiseaseSummaryReader.class);

	static SummaryPageParser summaryPageParser = new SummaryPageParser();

	static HashMap<String, Method> setMethodMap = new HashMap<>();

	static {
		Method[] ms = OriginDiseaseSummary.class.getMethods();
		for (Method m : ms) {
			if (ReflectUtil.isSetMethod(m)) {
				setMethodMap.put(NameUtil.removeGetOrSet(m.getName()), m);
			}
		}
	}

	@Autowired
	OriginDiseaseSummaryMapper diseaseSummaryMapper;

	@Autowired
	OriginDiseaseNameService diseaseNameService;

	volatile int threadCount;

	public void readDiseaseSummary() {

		logger.info("开始读取疾病简介数据");

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

			Thread t = new Thread(new Runnable() {

				@Override
				public void run() {

					logger.info("开始读取第" + s + "条到第" + e + "条疾病简介");

					for (int x = s; x <= e; x++) {

						OriginDiseaseName name = names.get(x);
						String key = name.getNameKey();
						Summary summary = null;
						try {
							summary = summaryPageParser.parse(key);
						} catch (IOException e1) {
							logger.error("无法读取页面数据[disease_key:" + key + "]");
							e1.printStackTrace();
						} catch (Exception e) {
							e.printStackTrace();
						}

						OriginDiseaseSummary diseaseSummary = new OriginDiseaseSummary();

						if(summary == null) {
							continue;
						}
						
						diseaseSummary.setDiseaseKey(key);
						diseaseSummary.setDiseaseName(name.getName());
						diseaseSummary.setSummary(summary.getSummary());

						for (SummaryPoint point : summary.getPoints()) {
							String pointKey = point.getKey();
							Method setMethod = setMethodMap.get(pointKey);
							if (setMethod == null) {
								throw new RuntimeException("无法找到对应point：" + pointKey);
							}

							String content = null;
							List<String> contents = point.getContents();
							if (contents == null || contents.size() == 0) {
								continue;
							}

							if (point.getType() == PointType.DEFAULT) {
								content = contents.get(0);
							} else {

								StringBuilder sb = new StringBuilder();
								for (String c : contents) {
									sb.append(c).append(",");
								}

								sb.deleteCharAt(sb.length() - 1);
								content = sb.toString();
							}

							if (content == null || content.length() == 0) {
								continue;
							}

							try {
								setMethod.invoke(diseaseSummary, content);
							} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e1) {
								logger.error("无法插入值" + point.getName() + ":" + content);
								e1.printStackTrace();
							}
						}
						diseaseSummaryMapper.insert(diseaseSummary);
					}

					logger.info("成功读取第" + s + "条到第" + e + "条疾病简介");
					endThread();
				}
			});

			threadCount++;
			t.start();

			i = end;
		}
	}

	private void endThread() {
		if (--threadCount == 0) {
			logger.info("结束读取疾病简介数据");
		}
	}

	public static void main(String[] args) {

	}

}

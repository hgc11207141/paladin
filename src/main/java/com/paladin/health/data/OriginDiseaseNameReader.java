package com.paladin.health.data;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.paladin.health.data.parser.DiseaseNameParser;
import com.paladin.health.data.parser.DiseaseNameParser.DiseaseName;
import com.paladin.health.mapper.origin.OriginDiseaseNameMapper;
import com.paladin.health.model.origin.OriginDiseaseName;

@Component
public class OriginDiseaseNameReader {

	private static Logger logger = LoggerFactory.getLogger(OriginDiseaseNameReader.class);

	@Autowired
	private OriginDiseaseNameMapper originDiseaseNameMapper;

	public void readDiseaseName() {

		int pageCount = 1519;

		int i = 0;
		int size = 100;
		boolean goon = true;

		logger.info("开始读取疾病名称数据");
		
		while (goon) {

			if (i > pageCount) {
				break;
			}

			int end = i + size;
			if (end > pageCount) {
				end = pageCount;
				goon = false;
			}

			DiseaseNameReader reader = new DiseaseNameReader(i, end - 1, originDiseaseNameMapper);
			Thread t = new Thread(reader);
			t.start();

			i = end;
		}
	}

	static DiseaseNameParser diseaseNameParser = new DiseaseNameParser();

	public static class DiseaseNameReader implements Runnable {

		int start;
		int end;

		OriginDiseaseNameMapper originDiseaseNameMapper;

		public DiseaseNameReader(int start, int end, OriginDiseaseNameMapper originDiseaseNameMapper) {
			this.start = start;
			this.end = end;
			this.originDiseaseNameMapper = originDiseaseNameMapper;
		}

		@Override
		public void run() {

			for (int i = start; i <= end; i++) {
				List<DiseaseName> names = null;
				try {
					names = diseaseNameParser.parse(i);
				} catch (IOException e) {
					System.out.println("can't read page :" + i);
					e.printStackTrace();
				}

				if (names != null) {
					for (DiseaseName name : names) {
						OriginDiseaseName record = new OriginDiseaseName();
						record.setName(name.getName());
						record.setNameKey(name.getPinyin());
						record.setType(name.getType());
						try {
							originDiseaseNameMapper.insert(record);
						} catch (Exception e) {
							System.out.println("can't insert record :" + record);
							e.printStackTrace();
						}
					}
				}
			}

			logger.info("成功读取第" + start + "页到" + end + "页疾病名称");
		}
	}

}

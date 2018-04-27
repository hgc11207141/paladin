package com.paladin.health.data.parser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.paladin.health.model.origin.OriginDiseaseName;

public class DiseaseNameParser extends PageParser {

	public List<DiseaseName> parse(int pageNum) throws IOException {

		Document doc = getDocument("http://jbk.39.net/bw_p" + pageNum + "#ps");
		List<DiseaseName> names = new ArrayList<>();

		Elements resList = doc.getElementsByClass("res_list");
		
		for(Element res : resList) {			
			
			Element h3  =res.getElementsByTag("h3").get(0);
			Element a = h3.child(0);
				
			
			String href = a.attr("href");
			String pinyin = href.endsWith("/")? href.substring(18,href.length() - 1) : href.substring(18);
						
			DiseaseName name = new DiseaseName();
			name.name = a.text();
			if(name.name.endsWith("...")) {
				name.name = a.attr("title");
			}
			
			name.pinyin = pinyin;
			if(name.pinyin.endsWith(".html")) {
				continue;
			}
			
			if(name.pinyin.startsWith("zhengzhuang/")) {
				name.pinyin = name.pinyin.substring(12);
				name.type = OriginDiseaseName.TYPE_SYMPTOM;
			}
			
			names.add(name);
		}
		
		return names;
	}

	public static class DiseaseName {

		String name;
		String pinyin;
		int type = OriginDiseaseName.TYPE_DISEASE;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getPinyin() {
			return pinyin;
		}

		public void setPinyin(String pinyin) {
			this.pinyin = pinyin;
		}

		public int getType() {
			return type;
		}

		public void setType(int type) {
			this.type = type;
		}
		

	}

}

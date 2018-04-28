package com.paladin.health.data.parser;

import java.io.IOException;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class PageParser {
	
	private String userAgent = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.132 Safari/537.36";
	
	protected Document getDocument(String url) throws IOException {
		Connection conn = Jsoup.connect(url);
		conn.timeout(60000);
		// 修改http包中的header,伪装成浏览器进行抓取
		conn.header("User-Agent", userAgent);
		return conn.get();
	}
	
}

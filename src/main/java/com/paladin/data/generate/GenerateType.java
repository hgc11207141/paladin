package com.paladin.data.generate;

public enum GenerateType {

	SERVICE("service", "Service.java"), 
	MAPPER("mapper", "Mapper.java"), 
	MODEL("model", ".java"), 
	CONTROLLER("controller", "Controller.java"), 
	SQLMAPPER("mapper", "_mapper.xml"), 
	PAGE_INDEX("templates", "_index.html"), 
	PAGE_VIEW("templates", "_view.html"), 
	PAGE_EDIT("templates", "_edit.html");

	private String name;
	private String fileSuffix;

	GenerateType(String name, String fileSuffix) {
		this.name = name;
		this.fileSuffix = fileSuffix;
	}

	public String getName() {
		return name;
	}

	public String getFileSuffix() {
		return fileSuffix;
	}

}

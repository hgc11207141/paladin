package com.paladin.data.generate;

import org.springframework.stereotype.Service;

import com.paladin.framework.core.ServiceSupport;

public class GenerateServiceClassUtil {

	public static String createClassContent(GenerateTableOption tableOption) {

		StringBuilder sb = new StringBuilder();

		sb.append("package ").append(GenerateUtil.getClassPackage(tableOption, GenerateType.SERVICE)).append(";\n\n");
		
		sb.append("import ").append(Service.class.getName()).append(";\n\n");

		sb.append("import ").append(tableOption.getModelFullName()).append(";\n");
		sb.append("import ").append(ServiceSupport.class.getName()).append(";\n\n");
		
		sb.append("@Service\n");
		sb.append("public class ").append(tableOption.getModelName()).append("Service extends ").append(ServiceSupport.class.getSimpleName()).append("<")
				.append(tableOption.getModelName()).append(">{\n\n}");

		return sb.toString();
	}

}

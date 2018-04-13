package com.paladin.data.generate;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

public class GenerateControllerClassUtil {

	public static String createClassContent(GenerateTableOption tableOption) {

		StringBuilder sb = new StringBuilder();

		sb.append("package ").append(GenerateUtil.getClassPackage(tableOption, GenerateType.CONTROLLER)).append(";\n\n");

		sb.append("import ").append(Controller.class.getName()).append(";\n");
		sb.append("import ").append(RequestMapping.class.getName()).append(";\n\n");

		//sb.append("import ").append(tableOption.getModelFullName()).append(";\n");

		sb.append("@Controller\n");
		sb.append("@RequestMapping(\"").append(tableOption.getModelRequestPath()).append("\")\n");
		sb.append("public class ").append(tableOption.getModelName()).append("Controller").append("{\n\n}");

		return sb.toString();
	}

}

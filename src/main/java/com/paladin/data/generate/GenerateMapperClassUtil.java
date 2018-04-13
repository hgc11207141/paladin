package com.paladin.data.generate;

import com.paladin.framework.mybatis.CustomMapper;

public class GenerateMapperClassUtil {

	public static String createClassContent(GenerateTableOption tableOption) {

		StringBuilder sb = new StringBuilder();

		sb.append("package ").append(GenerateUtil.getClassPackage(tableOption, GenerateType.MAPPER)).append(";\n\n");
		sb.append("import ").append(tableOption.getModelFullName()).append(";\n");
		sb.append("import ").append(CustomMapper.class.getName()).append(";\n\n");

		sb.append("public interface ").append(tableOption.getModelName()).append("Mapper extends ").append(CustomMapper.class.getSimpleName()).append("<")
				.append(tableOption.getModelName()).append(">{\n\n}");

		return sb.toString();
	}

}

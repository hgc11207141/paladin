package com.paladin.data.generate;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

import com.paladin.framework.utils.reflect.NameUtil;

public class GenerateUtil {

	public static String getClassPackage(GenerateTableOption tableOption, GenerateType generateType) {

		String basePackage = tableOption.getBasePackage();
		String model = tableOption.getModel();
		String subModel = tableOption.getSubModel();

		if (model != null && model.length() != 0) {
			basePackage += "." + model;
		}

		basePackage += "." + generateType.getName();

		if (subModel != null && subModel.length() != 0) {
			String[] subModels = subModel.split("\\.");
			if (subModels.length > 0) {
				for (String sm : subModels) {
					basePackage += "." + sm;
				}
			}
		}

		return basePackage;
	}

	public static String getRequestPath(GenerateTableOption tableOption) {

		String path = null;

		String model = tableOption.getModel();
		String subModel = tableOption.getSubModel();

		if (model != null && model.length() != 0) {
			path += "/" + model;
		}

		if (subModel != null && subModel.length() != 0) {
			String[] subModels = subModel.split("\\.");
			if (subModels.length > 0) {
				for (String sm : subModels) {
					path += "/" + sm;
				}
			}
		}

		return path == null ? "/" : path;
	}

	/**
	 * 获取spring boot项目类的路径
	 * 
	 * @param projectPath
	 * @param tableOption
	 * @param generateType
	 * @return
	 * @throws IOException
	 */
	public static Path getBootProjectJavaPath(String projectPath, GenerateTableOption tableOption, GenerateType generateType) throws IOException {
		return getBootProjectClassPath(projectPath, getClassPackage(tableOption, generateType), tableOption.getModelName() + generateType.getFileSuffix());
	}
	
	/**
	 * 获取spring boot资源地址
	 * @param projectPath
	 * @param tableOption
	 * @param generateType
	 * @return
	 * @throws IOException
	 */
	public static Path getBootProjectResourcesPath(String projectPath, GenerateTableOption tableOption, GenerateType generateType) throws IOException {
		
		if (projectPath != null && projectPath.length() > 0) {
			projectPath = projectPath.replaceAll("\\\\", "/");
			projectPath += projectPath.endsWith("/") ? "src/main/resources" : "/src/main/resources";
		} else {
			throw new IOException("项目路径不能为空");
		}
		
		String model = tableOption.getModel();
		String subModel = tableOption.getSubModel();
		
		ArrayList<String> subPaths = new ArrayList<>();
		subPaths.add(generateType.getName());		
		
		if (model != null && model.length() != 0) {
			subPaths.add(model);
		}
		
		if (subModel != null && subModel.length() != 0) {
			String[] subModels = subModel.split("\\.");
			if (subModels.length > 0) {
				for (String sm : subModels) {
					subPaths.add(sm);
				}
			}
		}

		subPaths.add(NameUtil.hump2underline(NameUtil.firstLowerCase(tableOption.getModelName())) + generateType.getFileSuffix());
			
		String[] subPathArray = new String[subPaths.size()];	
		subPathArray = subPaths.toArray(subPathArray);
		
		String[] dirPath = Arrays.copyOf(subPathArray, subPathArray.length -1);	
		Files.createDirectories(Paths.get(projectPath, dirPath));
		return Paths.get(projectPath, subPathArray);
	}

	/**
	 * 获取spring boot项目类的路径
	 * 
	 * @param projectPath
	 * @param classPackage
	 * @param className
	 * @return
	 * @throws IOException
	 */
	public static Path getBootProjectClassPath(String projectPath, String classPackage, String fileName) throws IOException {

		if (projectPath != null && projectPath.length() > 0) {
			projectPath = projectPath.replaceAll("\\\\", "/");
			projectPath += projectPath.endsWith("/") ? "src/main/java" : "/src/main/java";
		}

		String[] more = null;
		if (classPackage == null || classPackage.equals("")) {
			more = new String[] { fileName };
		} else {
			more = classPackage.split("\\.");
			Files.createDirectories(Paths.get(projectPath, more));
			more = Arrays.copyOf(more, more.length + 1);
			more[more.length - 1] = fileName;
		}

		Path path = Paths.get(projectPath, more);
		return path;
	}
}

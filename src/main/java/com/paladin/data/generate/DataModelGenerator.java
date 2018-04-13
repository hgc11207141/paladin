package com.paladin.data.generate;

import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Id;

import org.apache.ibatis.type.JdbcType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.paladin.data.database.DataBaseType;
import com.paladin.data.database.DataTypeUtil;
import com.paladin.data.database.model.Column;
import com.paladin.data.database.model.Table;
import com.paladin.data.database.model.constraint.ColumnConstraint;
import com.paladin.framework.utils.reflect.NameUtil;
import com.paladin.framework.utils.reflect.ReflectUtil;

/**
 * 数据模型对象生成器
 * 
 * @author TontoZhou
 * 
 */
public class DataModelGenerator {

	private static Logger logger = LoggerFactory.getLogger(DataModelGenerator.class);

	private ModelNameConverter modelNameConverter;

	public void generateModelFile(Table table, Class<?> parentClass, String basePackage, DataBaseType dbType, String path) {
		ModelClass modelClass = createModelClass(table, parentClass, basePackage, dbType);
		String content = createClassContent(modelClass);
		try {
			Files.write(getFilePath(path, modelClass.classPackage, modelClass.className), content.toString().getBytes());
			logger.info("---创建表[" + modelClass.tableName + "]对应的数据模型Java类[" + modelClass.className + "]成功---");
		} catch (IOException e) {
			logger.warn("无法创建表[" + modelClass.tableName + "]对应的数据模型Java类[" + modelClass.className + "]", e);
		}
	}

	public String generateModelContent(Table table, Class<?> parentClass, String basePackage, DataBaseType dbType) {
		ModelClass modelClass = createModelClass(table, parentClass, basePackage, dbType);
		return createClassContent(modelClass);
	}

	private ModelClass createModelClass(Table table, Class<?> parentClass, String basePackage, DataBaseType dbType) {

		ModelNameConverter converter = modelNameConverter == null ? defaultNameConverter : modelNameConverter;

		ModelClass modelClass = new ModelClass();
		modelClass.classPackage = converter.convertPackage(table, basePackage);
		modelClass.className = converter.convertTableName(table);
		modelClass.tableName = table.getName();

		Set<String> parentProperty = new HashSet<>();
		if (parentClass != null && !parentClass.isInterface()) {

			for (Method method : parentClass.getMethods()) {
				if (ReflectUtil.isGetMethod(method)) {
					String propertyName = NameUtil.removeGetOrSet(method.getName());
					parentProperty.add(propertyName);
				}
			}

			modelClass.setParentClass(parentClass);
		}

		for (Column column : table.getChildren()) {
			ModelProperty property = new ModelProperty();
			property.columnName = column.getName();
			property.propertyName = converter.convertColumnName(column);
			property.propertyClass = DataTypeUtil.getJavaType(column, dbType);
			property.primary = column.isPrimary();

			if (dbType == DataBaseType.ORACLE && Number.class.isAssignableFrom(property.propertyClass))
				property.jdbcType = JdbcTypeUtil.getJdbcType(property.propertyClass);
			else
				property.jdbcType = JdbcTypeUtil.getJdbcType(dbType, column.getDataType());

			/*
			 * 外键，需要处理组合外键
			 */
			ColumnConstraint columnConstraint = column.getForeignKey();
			if (columnConstraint != null) {
				Reference reference = new Reference();
				reference.table = columnConstraint.getReferencedTable();
				reference.column = columnConstraint.getReferencedColumn();

				if (column.isMultiForeignKey())
					reference.id = columnConstraint.getTableConstraint().getName();

				property.reference = reference;
			}

			/*
			 * 唯一键
			 */
			columnConstraint = column.getForeignKey();
			if (columnConstraint != null) {
				property.unique = true;
				if (column.isMultiUnique())
					property.uniqueId = columnConstraint.getTableConstraint().getName();
			}

			modelClass.addProperty(property);
		}

		return modelClass;
	}

	private String createClassContent(ModelClass modelClass) {
		String tab = "\t";

		StringBuilder sb = new StringBuilder();

		sb.append("package ").append(modelClass.classPackage).append(";\n\n");

		String[] classNames = new String[modelClass.importClassSet.size()];

		int i = 0;
		for (Class<?> importClass : modelClass.importClassSet)
			classNames[i++] = importClass.getName();

		Arrays.sort(classNames);

		for (String className : classNames) {
			if (!className.matches("^java\\.lang\\.\\w+$"))
				sb.append("import ").append(className).append(";\n");
		}

		sb.append("public class ").append(modelClass.className);
		if(modelClass.parentClass != null) {
			sb.append(" extends ").append(modelClass.parentClass.getSimpleName());
		}
		
		sb.append(" {\n\n");

		for (ModelProperty property : modelClass.properties)
			sb.append(tab).append("private ").append(property.propertyClass.getSimpleName()).append(" ").append(property.propertyName).append(";\n\n");

		for (ModelProperty property : modelClass.properties) {
			/*// 注解DataColumn
			sb.append(tab).append("@DataColumn(name = \"").append(property.columnName).append("\", jdbcType = JdbcType.").append(property.jdbcType);
			if (property.primary)
				sb.append(", primary = true");
			if (property.unique)
				sb.append(", unique = true");
			if (property.uniqueId != null)
				sb.append(", uniqueId = \"").append(property.uniqueId).append("\"");
			sb.append(")\n");

			// 注解DataReference
			if (property.reference != null) {
				Reference reference = property.reference;
				sb.append(tab).append("@DataReference(table = \"").append(reference.table).append("\", column = \"").append(reference.column).append("\"");
				if (reference.id != null)
					sb.append(", id = \"").append(reference.id).append("\"");
				sb.append(")\n");
			}*/
			
			if(property.primary) {
				sb.append(tab).append("@Id").append("\n");
			}

			// getMethod
			sb.append(tab).append("public ").append(property.propertyClass.getSimpleName()).append(" ").append(NameUtil.addGet(property.propertyName))
					.append("() {\n").append(tab).append(tab).append("return ").append(property.propertyName).append(";\n").append(tab).append("}\n\n");

			// setMethod
			sb.append(tab).append("public void ").append(NameUtil.addSet(property.propertyName)).append("(").append(property.propertyClass.getSimpleName())
					.append(" ").append(property.propertyName).append(") {\n").append(tab).append(tab).append("this.").append(property.propertyName)
					.append(" = ").append(property.propertyName).append(";\n").append(tab).append("}\n\n");
		}

		sb.append("}");

		return sb.toString();
	}

	private static Path getFilePath(String basePath, String _package, String name) throws IOException {
		String[] more = null;
		name += ".java";
		if (_package == null || _package.equals("")) {
			more = new String[] { name };
		} else {
			more = _package.split("\\.");
			Files.createDirectories(Paths.get(basePath, more));
			more = Arrays.copyOf(more, more.length + 1);
			more[more.length - 1] = name;
		}

		Path path = Paths.get(basePath, more);
		return path;
	}

	private static class ModelClass {

		Set<Class<?>> importClassSet = new HashSet<>();
		Class<?> parentClass;
		String classPackage;
		String className;
		String tableName;

		List<ModelProperty> properties = new ArrayList<>();

		public ModelClass() {
			importClassSet.add(JdbcType.class);
			importClassSet.add(Id.class);
		}

		public void setParentClass(Class<?> parentClass) {
			this.parentClass = parentClass;
			if (!parentClass.isPrimitive() && !parentClass.getName().matches("^java.lang.[^.]")) {
				importClassSet.add(parentClass);
			}
		}

		public void addProperty(ModelProperty property) {
			Class<?> clazz = property.propertyClass;

			if (clazz.isArray())
				clazz = ReflectUtil.getArrayType(clazz);

			if (!clazz.isPrimitive() && !clazz.getName().matches("^java.lang.[^.]"))
				importClassSet.add(clazz);

			properties.add(property);

			// if (property.reference != null)
			// importClassSet.add(DataReference.class);

		}

	}
	
	@SuppressWarnings("unused")
	private static class ModelProperty {

		Class<?> propertyClass;		
		String columnName;
		String propertyName;
		Reference reference;
		boolean primary;
		boolean unique;
		String uniqueId;
		JdbcType jdbcType;
	}
	
	@SuppressWarnings("unused")
	private static class Reference {

		String id;
		String table;
		String column;

	}

	public static interface ModelNameConverter {

		public String convertColumnName(Column column);

		public String convertTableName(Table table);

		public String convertPackage(Table table, String basePackage);
	}

	private static ModelNameConverter defaultNameConverter = new ModelNameConverter() {

		@Override
		public String convertColumnName(Column column) {
			return NameUtil.underline2hump(column.getName());
		}

		@Override
		public String convertTableName(Table table) {
			return NameUtil.firstUpperCase(NameUtil.underline2hump(table.getName()));
		}

		@Override
		public String convertPackage(Table table, String basePackage) {
			return basePackage;
		}

	};
}

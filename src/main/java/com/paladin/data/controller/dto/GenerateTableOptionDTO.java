package com.paladin.data.controller.dto;

import java.util.List;

/**
 * @see com.paladin.data.generate.GenerateTableOption
 * @author TontoZhou
 * @since 2018年4月11日
 */
public class GenerateTableOptionDTO {
	
	private String dbName;
	
	private String tableName;
	
	private String basePackage;
	
	private String model;
	
	private String subModel;
	
	private String projectPath;
	
	private List<GenerateColumnOptionDTO> columnOptions;
	
	public String getDbName() {
		return dbName;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public List<GenerateColumnOptionDTO> getColumnOptions() {
		return columnOptions;
	}

	public void setColumnOptions(List<GenerateColumnOptionDTO> columnOptions) {
		this.columnOptions = columnOptions;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getBasePackage() {
		return basePackage;
	}

	public void setBasePackage(String basePackage) {
		this.basePackage = basePackage;
	}

	public String getSubModel() {
		return subModel;
	}

	public void setSubModel(String subModel) {
		this.subModel = subModel;
	}

	/**
	 * @see com.paladin.data.generate.GenerateColumnOption
	 * @author TontoZhou
	 * @since 2018年4月11日
	 */
	public static class GenerateColumnOptionDTO {
		
		private String columnName;
		
		/**
		 * 自动生成方式，UUID等
		 */
		private String generatedType;

		/**
		 * 属性名称
		 */
		private String fieldName;

		/**
		 * 标题
		 */
		private String title;

		/**
		 * 必填
		 */
		private boolean required;

		/**
		 * 可编辑
		 */
		private boolean editable;

		/**
		 * 可新增
		 */
		private boolean addable;

		/**
		 * 最大长度
		 */
		private int maxLength;

		/**
		 * 正则表达式
		 */
		private String regularExpression;
		

		/**
		 * 正则类型，例如日期，邮箱，电话等
		 */
		private String regularType;


		public String getGeneratedType() {
			return generatedType;
		}


		public void setGeneratedType(String generatedType) {
			this.generatedType = generatedType;
		}


		public String getFieldName() {
			return fieldName;
		}


		public void setFieldName(String fieldName) {
			this.fieldName = fieldName;
		}


		public String getTitle() {
			return title;
		}


		public void setTitle(String title) {
			this.title = title;
		}


		public boolean isRequired() {
			return required;
		}


		public void setRequired(boolean required) {
			this.required = required;
		}


		public boolean isEditable() {
			return editable;
		}


		public void setEditable(boolean editable) {
			this.editable = editable;
		}


		public boolean isAddable() {
			return addable;
		}


		public void setAddable(boolean addable) {
			this.addable = addable;
		}


		public int getMaxLength() {
			return maxLength;
		}


		public void setMaxLength(int maxLength) {
			this.maxLength = maxLength;
		}


		public String getRegularExpression() {
			return regularExpression;
		}


		public void setRegularExpression(String regularExpression) {
			this.regularExpression = regularExpression;
		}


		public String getRegularType() {
			return regularType;
		}


		public void setRegularType(String regularType) {
			this.regularType = regularType;
		}


		public String getColumnName() {
			return columnName;
		}


		public void setColumnName(String columnName) {
			this.columnName = columnName;
		}
	}

	public String getProjectPath() {
		return projectPath;
	}

	public void setProjectPath(String projectPath) {
		this.projectPath = projectPath;
	}
	
}

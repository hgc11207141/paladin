package com.paladin.data.generate;

import com.paladin.data.database.DataBaseType;
import com.paladin.data.database.DataTypeUtil;
import com.paladin.data.database.model.Column;
import com.paladin.framework.utils.reflect.NameUtil;

public class GenerateColumnOption {

	/**
	 * 具体列描述
	 */
	private Column column;

	/**
	 * 数据库类型
	 */
	private DataBaseType dataBaseType;
	
	/**
	 * 是否主键
	 */
	private boolean isPrimary;

	/**
	 * 自动生成方式，UUID等
	 */
	private String generatedType;
	
	/**
	 * 属性java类型
	 */
	private Class<?> fieldType;

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

	public GenerateColumnOption(Column column, DataBaseType dataBaseType) {
		this.column = column;
		this.dataBaseType = dataBaseType;
		this.isPrimary = column.isPrimary();

		this.fieldType = DataTypeUtil.getJavaType(column, dataBaseType);
		this.fieldName = NameUtil.underline2hump(column.getName());
		
		this.regularType = GenerateConfig.getRegularType(fieldType);
		
		if(fieldType == String.class) {
			Integer length = column.getDataLength(); 
			if(length != null) {
				this.maxLength = length;
			}
		}
	}

	public Column getColumn() {
		return column;
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

	public DataBaseType getDataBaseType() {
		return dataBaseType;
	}

	public Class<?> getFieldType() {
		return fieldType;
	}

	public void setFieldType(Class<?> fieldType) {
		this.fieldType = fieldType;
	}

	public boolean isPrimary() {
		return isPrimary;
	}

	public String getGeneratedType() {
		return generatedType;
	}

	public void setGeneratedType(String generatedType) {
		this.generatedType = generatedType;
	}

}

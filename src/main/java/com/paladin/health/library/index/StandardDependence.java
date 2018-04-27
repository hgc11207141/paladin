package com.paladin.health.library.index;

/**
 * 标准依赖
 * @author TontoZhou
 * @since 2018年4月17日
 */
public class StandardDependence {
	
	private String targetKey;
	
	/**
	 * 依赖标准Key，该依赖的标准必须是absolute = true的，否则没有意义
	 */
	private String dependenceKey;

	public String getDependenceKey() {
		return dependenceKey;
	}

	public void setDependenceKey(String dependenceKey) {
		this.dependenceKey = dependenceKey;
	}

	public String getTargetKey() {
		return targetKey;
	}

	public void setTargetKey(String targetKey) {
		this.targetKey = targetKey;
	}
	
}

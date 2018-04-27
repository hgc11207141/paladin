package com.paladin.health.library.index;

import java.util.ArrayList;
import java.util.List;

/**
 * 标准
 * @author TontoZhou
 * @since 2018年4月17日
 */
public class Standard {
	
	private String key;
	private String value;
	
	/**
	 * 标识是否该标准可以独立存在，表示完整意思。
	 * <p>例如：有，无都是不完整的，必须与其他项目组合才有意义，而有吸烟则是一个完整的标准</p>
	 * <p>完整的标准可直接用于标识病人情况</p>
	 */
	private boolean absolute;
	
	private List<StandardDependence> dependences = new ArrayList<>();
	

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public boolean isAbsolute() {
		return absolute;
	}

	public void setAbsolute(boolean absolute) {
		this.absolute = absolute;
	}

	public List<StandardDependence> getDependences() {
		return new ArrayList<>(dependences);
	}
	
	public void addDependence(StandardDependence dependence) {
		this.dependences.add(dependence);
	}

	public void setDependences(List<StandardDependence> dependences) {
		this.dependences.addAll(dependences);
	}
	
	
	
}

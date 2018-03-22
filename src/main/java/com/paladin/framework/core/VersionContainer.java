package com.paladin.framework.core;


public interface VersionContainer {

	/**
	 * 容器ID
	 * @return
	 */
	public String getId();
	
	/**
	 * 执行顺序
	 * @return
	 */
	public int order();
	
	/**
	 * 版本改变处理
	 * @param version
	 * @return
	 */
	public boolean versionChangedHandle(long version);
	
	
}

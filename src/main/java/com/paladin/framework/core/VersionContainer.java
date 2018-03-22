package com.paladin.framework.core;


public interface VersionContainer {

	public String getContainerId();
	
	public int order();
	
	public boolean versionChangedHandle(long version);
	
	
}

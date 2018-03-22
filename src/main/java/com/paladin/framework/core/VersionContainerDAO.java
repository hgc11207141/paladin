package com.paladin.framework.core;

/**
 * 版本容器数据持久化接口
 * @author TontoZhou
 * @since 2018年3月22日
 */
public interface VersionContainerDAO {
	
	public long getVersion(String containerId) ;
	
	public void updateVersion(String containerId, long version);
	
}

package com.paladin.configuration;

import org.springframework.beans.factory.annotation.Value;

public class ShiroConfigProperties {
	
	@Value("${shiro.cluster.share}")
	private boolean clusterShare;
	
	@Value("${shiro.cluster.local-cache-name}")
	private String localCacheName;

	public boolean isClusterShare() {
		return clusterShare;
	}

	public void setClusterShare(boolean clusterShare) {
		this.clusterShare = clusterShare;
	}

	public String getLocalCacheName() {
		return localCacheName;
	}

	public void setLocalCacheName(String localCacheName) {
		this.localCacheName = localCacheName;
	}
	
	
	
}

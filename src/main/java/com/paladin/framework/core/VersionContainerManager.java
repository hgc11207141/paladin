package com.paladin.framework.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.paladin.framework.spring.SpringBeanHelper;
import com.paladin.framework.spring.SpringContainer;

@Component
public class VersionContainerManager implements SpringContainer {

	private static Logger logger = LoggerFactory.getLogger(VersionContainerManager.class);

	Map<String, VersionObject> versionObjectMap = new HashMap<>();
	List<VersionObject> versionObjects = new ArrayList<>();

	@Value("${container.version.run}")
	private boolean doRun;

	private VersionContainerDAO versionContainerDAO;

	private static VersionContainerManager manager;

	@Override
	public boolean initialize() {

		manager = this;

		Map<String, VersionContainer> versionContainerMap = SpringBeanHelper.getBeansByType(VersionContainer.class);
		
		// 尝试去spring寻找bean
		if(versionContainerDAO == null) {
			versionContainerDAO = SpringBeanHelper.getFirstBeanByType(VersionContainerDAO.class);
		}

		for (Entry<String, VersionContainer> entry : versionContainerMap.entrySet()) {
			VersionContainer container = entry.getValue();
			String containerId = container.getId();

			if (versionObjectMap.containsKey(containerId)) {
				logger.warn("===>已经存在版本容器[ID:" + containerId + "]，该容器会被忽略");
				continue;
			}

			VersionObject versionObject = new VersionObject(container);
			versionObjectMap.put(containerId, versionObject);
			versionObjects.add(versionObject);
		}

		if (versionObjects.size() > 0) {
			Collections.sort(versionObjects, new Comparator<VersionObject>() {

				@Override
				public int compare(VersionObject o1, VersionObject o2) {
					return o1.container.order() - o2.container.order();
				}
			});

			checkVersion();

			if (doRun) {
				logger.info("===>启动版本容器管理定时任务<===");
				startTimer();
			}
		}

		return true;
	}

	@Override
	public boolean afterInitialize() {
		return true;
	}

	@Override
	public int order() {
		return 0;
	}
	
	/**
	 * 检测版本
	 */
	private void checkVersion() {

		for (VersionObject versionObject : versionObjects) {
			long version = 0;

			// 尝试去数据库中获取版本号
			if (versionContainerDAO != null) {
				String containerId = versionObject.id;
				version = versionContainerDAO.getVersion(containerId);
			}

			if (versionObject.version == null || versionObject.version != version) {
				if (versionObject.container.versionChangedHandle(version)) {
					logger.info("版本容器[" + versionObject.id + "]更新版本号为：" + version);
					versionObject.version = version;
				}
			}
		}

		if (logger.isDebugEnabled()) {
			logger.debug("===>完成一次容器版本检测<===");
		}
	}

	private Timer timer;

	private void startTimer() {
		timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				checkVersion();
			}
		}, 60 * 1000, 10 * 1000);
	}

	public void versionChangedHandle(String containerId) {

		VersionObject versionObject = versionObjectMap.get(containerId);

		if (versionObject != null) {

			long newVersion = 0;

			if (versionObject.version != null) {
				newVersion = versionObject.version + 1;
			}

			// 持久化version到数据库
			if (versionContainerDAO != null) {
				versionContainerDAO.updateVersion(containerId, newVersion);
			}

			versionObject.container.versionChangedHandle(newVersion);
			versionObject.version = newVersion;
		} else {
			logger.error("无法找到对应容器[ID:" + containerId + "]");
		}

	}

	private static class VersionObject {

		String id;
		Long version;
		VersionContainer container;

		VersionObject(VersionContainer container) {
			this.id = container.getId();
			this.container = container;
		}

	}

	public static void versionChanged(String containerId) {
		manager.versionChangedHandle(containerId);
	}

	public void setVersionContainerDAO(VersionContainerDAO versionContainerDAO) {
		this.versionContainerDAO = versionContainerDAO;
	}

}

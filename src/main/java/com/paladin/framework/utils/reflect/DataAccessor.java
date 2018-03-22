package com.paladin.framework.utils.reflect;

/**
 * 数据访问抽象类，用于继承
 * @author TontZhou
 *
 */
public abstract class DataAccessor {

	private String dataPath;

	public String getDataPath() {
		return dataPath;
	}

	public void setDataPath(String dataPath) {
		this.dataPath = dataPath;
	}

	/**
	 * 根据路径寻找数据
	 * 
	 * @param source
	 * @return
	 */
	public Object getData(Object source) {
		if (dataPath == null)
			return null;
		return PathGetter.get(source, dataPath);
	}

}

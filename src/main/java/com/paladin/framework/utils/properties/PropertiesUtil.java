package com.paladin.framework.utils.properties;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

/**
 * 
 * .properties 配置文件工具类
 * 
 * @author TontoZhou
 * 
 */
public class PropertiesUtil {

	private static Map<String, Map<String, String>> cache_properties = new HashMap<>();

	/**
	 * 获取 .properties 文件中某属性值（非实时，有缓存，只加载一次）
	 * 
	 * @param file
	 * @param key
	 * @return
	 */
	public static String getProperty(String file, String key) {
		Map<String, String> props = getProperties(file);
		if (props != null)
			return props.get(key);
		return null;
	}

	/**
	 * 获取 .properties 文件中所有属性值（非实时，有缓存，只加载一次）
	 * 
	 * @param file
	 * @return
	 */
	public static Map<String, String> getProperties(String file) {

		Map<String, String> properties = cache_properties.get(file);

		if (properties == null) {

			Properties props = new Properties();

			properties = new HashMap<>();

			try {

				props.load(PropertiesUtil.class.getClassLoader().getResourceAsStream(file));

				for (Entry<Object, Object> entry : props.entrySet()) {
					Object oKey = entry.getKey();

					if (oKey != null && oKey instanceof String) {

						Object oVal = entry.getValue();

						properties.put((String) oKey, (oVal == null) ? "" : oVal.toString());
					}
				}

			} catch (IOException e) {
				return null;
			}

			cache_properties.put(file, properties);

			return properties;
		}

		return properties;
	}

	private static Map<String, ConfigCache> configCacheMap = new HashMap<>();

	/**
	 * <p>
	 * 获取 .properties 文件中某属性值
	 * </p>
	 * <p>
	 * 通过判断文件是否被修改来进行是否重新读取配置文件的操作，保证一定的实时性和效率
	 * </p>
	 * 
	 * @param file
	 * @param key
	 * @return
	 */
	public static String getInstantProperty(String file, String key) {
		Map<String, String> props = getInstantProperties(file);
		if (props != null)
			return props.get(key);
		return null;
	}

	/**
	 * <p>
	 * 获取 .properties 文件中所有属性值
	 * </p>
	 * <p>
	 * 通过判断文件是否被修改来进行是否重新读取配置文件的操作，保证一定的实时性和效率
	 * </p>
	 * 
	 * @param file
	 * @return
	 */
	public static Map<String, String> getInstantProperties(String file) {

		ConfigCache cache = configCacheMap.get(file);

		if (cache == null) {

			synchronized (ConfigCache.class) {
				cache = configCacheMap.get(file);

				if (cache == null) {
					cache = new ConfigCache();
					cache.file = file;

					URL url = PropertiesUtil.class.getClassLoader().getResource(file);

					if (url == null) {
						return null;
					}

					try {
						cache.filepath = URLDecoder.decode(url.getPath(), "UTF-8");
					} catch (UnsupportedEncodingException e) {
						return null;
					}

					configCacheMap.put(file, cache);
				}
			}

		}

		File configFile = new File(cache.filepath);
		long lastModified = configFile.lastModified();

		if (lastModified > cache.lastModified) {

			synchronized (ConfigCache.class) {
				if (lastModified > cache.lastModified) {
					Properties props = new Properties();
					Map<String, String> properties = new HashMap<>();

					try {

						props.load(PropertiesUtil.class.getClassLoader().getResourceAsStream(file));

						for (Entry<Object, Object> entry : props.entrySet()) {
							Object oKey = entry.getKey();

							if (oKey != null && oKey instanceof String) {

								Object oVal = entry.getValue();

								properties.put((String) oKey, (oVal == null) ? "" : oVal.toString());
							}
						}

						cache.properties = properties;

					} catch (IOException e) {
						return null;
					}

					cache.lastModified = lastModified;
				}
			}
		}

		return cache.properties;
	}

	/**
	 * 缓存
	 * 
	 * @author TontoZhou
	 * 
	 */
	private static class ConfigCache {

		// 文件名
		@SuppressWarnings("unused")
		String file;
		// 文件路径
		String filepath;
		// 最后修改时间
		long lastModified;
		// 属性MAP
		Map<String, String> properties;

	}

}

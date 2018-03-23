package com.paladin.framework.core.container;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.paladin.framework.core.VersionContainer;

@Component
public class ConstantsContainer implements VersionContainer{
		
	private static Map<String, List<KeyValue>> constantMap = new HashMap<>();

	public boolean initialize() {
		
		Map<String, List<KeyValue>> enumConstantMap = new HashMap<>();
		
		List<KeyValue> sexTypes = new ArrayList<>();
		sexTypes.add(new KeyValue("1", "男"));
		sexTypes.add(new KeyValue("2", "女"));
		enumConstantMap.put("sex-type", sexTypes);
		
		List<KeyValue> booleanTypes = new ArrayList<>();
		booleanTypes.add(new KeyValue("1", "是"));
		booleanTypes.add(new KeyValue("0", "否"));
		enumConstantMap.put("boolean-type", booleanTypes);
		
		constantMap = enumConstantMap;
		
		return true;
	}
	
	@Override
	public String getId() {
		return "constants_container";
	}

	
	@Override
	public boolean versionChangedHandle(long version) {
		initialize();
		return true;
	}

	@Override
	public int order() {
		return 0;
	}

	public static Map<String, List<KeyValue>> getTypeChildren(String... typeCodes) {
		Map<String, List<KeyValue>> result = new HashMap<>();
		for (String typeCode : typeCodes) {
			result.put(typeCode, constantMap.get(typeCode));
		}
		return result;
	}
	
	public final static class KeyValue {
		String key;
		String value;

		public KeyValue(String key, String value) {
			this.key = key;
			this.value = value;
		}

		public KeyValue(int key, String value) {
			this.key = String.valueOf(key);
			this.value = value;
		}

		public String getKey() {
			return key;
		}

		public String getValue() {
			return value;
		}
	}


	

	
}

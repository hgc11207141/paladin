package com.paladin.framework.utils.properties.copy;

import java.lang.reflect.Field;

import com.paladin.framework.utils.random.RandomObject;

public class PropertyCopyUtil {

	@SuppressWarnings("unchecked")
	public static <T> T cloneSimpleBean(T source) {
		if (source == null)
			return null;

		Class<?> clazz = source.getClass();
		try {
			Object target = clazz.newInstance();
			Field[] fields = clazz.getDeclaredFields();
			for (Field field : fields)
				field.set(target, field.get(source));

			return (T) target;
		} catch (InstantiationException | IllegalAccessException e) {
			return null;
		}

	}
	
	public static void main(String[] args) throws CloneNotSupportedException{
		
		A a = new RandomObject().createRandomObject(A.class);
		
		
		long current = System.currentTimeMillis();
		
		int count = 1000000;
		
		for(;count>0;count--)
		{
			cloneSimpleBean(a);
			//a.clone();
		}
		
		System.out.println(System.currentTimeMillis() - current);
	}
	
	
	
	public static class A implements Cloneable{
		
		private String name;
		private int age;
		private String address;
		private String url;
		private String aaaaa;
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public int getAge() {
			return age;
		}
		public void setAge(int age) {
			this.age = age;
		}
		public String getAddress() {
			return address;
		}
		public void setAddress(String address) {
			this.address = address;
		}
		public String getUrl() {
			return url;
		}
		public void setUrl(String url) {
			this.url = url;
		}
		public String getAaaaa() {
			return aaaaa;
		}
		public void setAaaaa(String aaaaa) {
			this.aaaaa = aaaaa;
		}
		
		@Override  
	    protected Object clone() throws CloneNotSupportedException {  
	        return super.clone();  
	    } 
		
	}

}

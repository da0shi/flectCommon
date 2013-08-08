package jp.co.flect.json;

import java.util.LinkedHashMap;

public class JsonUtils {
	
	private static JsonImpl impl;
	
	static {
		JsonImpl ret = null;
		try {
			Class.forName("com.google.gson.Gson");
			ret = new JsonImplByGson();
		} catch (Exception e) {
		}
		try {
			Class.forName("org.codehaus.jackson.map.ObjectMapper");
			ret = new JsonImplByJackson();
		} catch (Exception e) {
		}
		if (ret == null) {
			throw new IllegalStateException("Json libraries not found.");
		}
		impl = ret;
	}
	
	public static JsonImpl geJsonImpl() { return impl;}
	public static void setJsonImpl(JsonImpl v) { impl = v;}
	
	public static <T> T fromJson(String json, Class<T> clazz) throws JsonException {
		return impl.fromJson(json, clazz);
	}
	
	public static LinkedHashMap<String, Object> fromJsonToMap(String json) throws JsonException {
		return fromJson(json, LinkedHashMap.class);
	}
	
	public static String toJson(Object o) {
		return impl.toJson(o);
	}
}

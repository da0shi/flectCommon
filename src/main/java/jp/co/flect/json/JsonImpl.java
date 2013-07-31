package jp.co.flect.json;

public interface JsonImpl {
	
	public <T> T fromJson(String json, Class<T> clazz) throws JsonException;
}

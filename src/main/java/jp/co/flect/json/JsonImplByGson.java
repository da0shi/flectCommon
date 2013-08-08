package jp.co.flect.json;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class JsonImplByGson implements JsonImpl {
	
	public <T> T fromJson(String json, Class<T> clazz) throws JsonException {
		try {
			return new Gson().fromJson(json, clazz);
		} catch (JsonSyntaxException e) {
			throw new JsonException(e);
		}
	}
	
	public String toJson(Object o) {
		return new Gson().toJson(o);
	}
}

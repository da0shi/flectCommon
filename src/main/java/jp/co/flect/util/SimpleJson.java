package jp.co.flect.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.util.Map;
import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonElement;

/**
 * シンプルなJSONを扱うための簡易クラス
 */
public class SimpleJson extends ExtendedMap {
	
	public SimpleJson() {
	}
	
	public SimpleJson(String name, Object value) {
		set(name, value);
	}
	
	public SimpleJson set(String name, Object value) {
		putDeep(name, value);
		return this;
	}
	
	public SimpleJson setArray(String name, Object... values) {
		putDeep(name, values);
		return this;
	}
	
	public String toString() {
		return new Gson().toJson(this);
	}
	
	public static SimpleJson fromJson(String json) {
		JsonElement el = new JsonParser().parse(json);
		SimpleJson map = new SimpleJson();
		build(map, el.getAsJsonObject());
		return map;
	}
	
	private static void build(SimpleJson map, JsonObject obj) {
		for (Map.Entry<String, JsonElement> entry : obj.entrySet()) {
			String key = entry.getKey();
			JsonElement el = entry.getValue();
			if (el.isJsonPrimitive()) {
				map.put(key, el.getAsString());
			} else if (el.isJsonArray()) {
				List<Object> list = toList(el.getAsJsonArray());
				map.put(key, list);
			} else if (el.isJsonObject()) {
				SimpleJson child = new SimpleJson();
				build(child, el.getAsJsonObject());
				map.put(key, child);
			} else if (el.isJsonNull()) {
				map.put(key, null);
			} else {
				throw new IllegalStateException();
			}
		}
	}
	
	private static List<Object> toList(JsonArray array) {
		List<Object> list = new ArrayList<Object>();
		Iterator<JsonElement> it = array.iterator();
		while (it.hasNext()) {
			JsonElement el = it.next();
			if (el.isJsonPrimitive()) {
				list.add(el.getAsString());
			} else if (el.isJsonArray()) {
				list.add(toList(el.getAsJsonArray()));
			} else if (el.isJsonObject()) {
				SimpleJson child = new SimpleJson();
				build(child, el.getAsJsonObject());
				list.add(child);
			} else if (el.isJsonNull()) {
				list.add(null);
			} else {
				throw new IllegalStateException();
			}
		}
		return list;
	}
}

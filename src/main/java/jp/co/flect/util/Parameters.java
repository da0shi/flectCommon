package jp.co.flect.util;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import com.google.gson.Gson;
import java.io.Serializable;

import jp.co.flect.net.HttpUtils;

/**
 * (主にHTTPで使用される)パラメータをラップするクラス
 */
public class Parameters implements Serializable {
	
	private static final long serialVersionUID = 2176320121527785180L;

	private TreeMap<String, Object> _map = new TreeMap<String, Object>();
	
	public Parameters() {
	}
	
	public Parameters(Parameters params) {
		if (params != null) {
			_map.putAll(params._map);
		}
	}
	
	public Parameters(Map<String, ? extends Object> map) {
		if (map != null) {
			for (Map.Entry<String, ? extends Object> entry : map.entrySet()) {
				String key = entry.getKey();
				Object value = entry.getValue();
				if (value == null) {
					//Do nothing
				} else if (value instanceof String[]) {
					String[] values = (String[])value;
					if (values.length == 1) {
						_map.put(key, values[0]);
					} else if (values.length > 1) {
						_map.put(key, values);
					}
				} else {
					_map.put(key, value.toString());
				}
			}
		}
	}
	
	public Set<String> keySet() { return this._map.keySet();}
	
	public void add(String key, String value) {
		Object o = _map.get(key);
		if (o == null) {
			_map.put(key, value);
		} else if (o instanceof String) {
			String[] values = new String[2];
			values[0] = o.toString();
			values[1] = value;
			_map.put(key, values);
		} else if (o instanceof String[]) {
			String[] origin = (String[])o;
			String[] values = new String[origin.length+1];
			System.arraycopy(origin, 0, values, 0, origin.length);
			values[origin.length] = value;
			_map.put(key, values);
		}
	}
	
	public void replace(String key, String value) {
		_map.put(key, value);
	}
	
	public void replace(String key, String[] values) {
		_map.put(key, values);
	}
	
	public Object remove(String key) {
		return _map.remove(key);
	}
	
	public boolean contains(String key) {
		return _map.containsKey(key);
	}
	
	public String get(String key) {
		return get(key, null);
	}
	
	public String get(String key, String defaults) {
		Object o = _map.get(key);
		if (o == null) {
			return defaults;
		} else if (o instanceof String) {
			return o.toString();
		} else if (o instanceof String[]) {
			String[] a = (String[])o;
			return a.length == 0 ? defaults : a[0];
		}
		throw new IllegalStateException();
	}
	
	public String[] getArray(String key) {
		return getArray(key, null);
	}
	
	public String[] getArray(String key, String[] defaults) {
		Object o = _map.get(key);
		if (o == null) {
			return defaults;
		} else if (o instanceof String) {
			String[] ret = new String[1];
			ret[0] = o.toString();
			return ret;
		} else if (o instanceof String[]) {
			return (String[])o;
		}
		throw new IllegalStateException();
	}
	
	public int getInt(String key) {
		return getInt(key, 0);
	}
	
	public int getInt(String key, int defaults) {
		String s = get(key);
		return s == null ? defaults : Integer.parseInt(s);
	}
	
	public long getLong(String key) {
		return getLong(key, 0);
	}
	
	public long getLong(String key, long defaults) {
		String s = get(key);
		return s == null ? defaults : Long.parseLong(s);
	}
	
	public boolean getBoolean(String key) {
		return getBoolean(key, false);
	}
	
	public boolean getBoolean(String key, boolean defaults) {
		String s = get(key);
		return s == null ? defaults : "true".equalsIgnoreCase(s);
	}
	
	public String toQuery() {
		return toQuery("utf-8");
	}
	
	public String toQuery(String encoding) {
		return HttpUtils.map2query(_map, encoding);
	}
	
	public String toJson() {
		return new Gson().toJson(_map);
	}
	
	public String toString() {
		return toJson();
	}
}

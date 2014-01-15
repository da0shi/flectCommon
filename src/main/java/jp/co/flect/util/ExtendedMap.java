package jp.co.flect.util;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

/**
 * テンプレート用にメソッド拡張したHashMapです。<br>
 * getDeep/putDeepメソッドで「login.username」のように「.」で区切られたKeyを
 * 階層的に取得、変更することができます。
 */
public class ExtendedMap extends HashMap<String, Object> {
	
	private static final long serialVersionUID = -1612294146820603959L;
	
	private boolean xmlEscape;
	
	/**
	 * コンストラクタ - this(false);
	 */
	public ExtendedMap() {
		this(false);
	}
	
	/**
	 * コンストラクタ
	 * @param xmlEscape setXmlEscape参照
	 */
	public ExtendedMap(boolean xmlEscape) {
		this.xmlEscape = xmlEscape;
	}
	
	/**
	 * Stringのput時に「&<>'"」の各文字をXMLの実体参照に変換するかどうかを返します。
	 */
	public boolean isXMLEscape() { return this.xmlEscape;}
	
	/**
	 * Stringのput時に「&<>'"」の各文字をXMLの実体参照に変換するかどうかを設定します。
	 */
	public void setXMLEscape(boolean b) { this.xmlEscape = b;}
	
	@Override
	public Object put(String key, Object value) {
		if (this.xmlEscape && value instanceof String) {
			value = escape(value.toString());
		}
		return super.put(key, value);
	}
	
	private String escape(String s) {
		StringBuilder buf = null;
		int i=0;
		int len = s.length();
		for (; i<len; i++) {
			char c = s.charAt(i);
			if (c == '&' || c == '<' || c == '>' || c == '"' || c == '\'') {
				buf = new StringBuilder(len + 10);
				buf.append(s.substring(0, i));
				break;
			}
		}
		if (buf == null) {
			return s;
		}
		for (; i<len; i++) {
			char c = s.charAt(i);
			switch (c) {
				case '&':
					buf.append("&amp;");
					break;
				case '<':
					buf.append("&lt;");
					break;
				case '>':
					buf.append("&gt;");
					break;
				case '"':
					buf.append("&quot;");
					break;
				case '\'':
					buf.append("&apos;");
					break;
				default:
					buf.append(c);
					break;
			}
		}
		return buf.toString();
	}
	
	/**
	 * 「aaa.bbb.ccc」のように「.」で区切られたキーで値を階層的に追加します。
	 * 階層の途中にあるObjectがExtendedMapでなかった場合はExtendedMapに置き換えられます。
	 */
	public Object putDeep(String key, Object value) {
		int idx = key.indexOf('.');
		if (idx == -1) {
			return put(key, value);
		}
		String key1 = key.substring(0, idx);
		String key2 = key.substring(idx+1);
		
		ExtendedMap child = null;
		Object o = get(key1);
		if (o instanceof ExtendedMap) {
			child = (ExtendedMap)o;
		} else {
			child = new ExtendedMap(this.xmlEscape);
			if (o instanceof Map) {
				child.putAll((Map)o);
			}
			put(key1, child);
		}
		return child.putDeep(key2, value);
	}
	
	/**
	 * 「aaa.bbb.ccc」のように「.」で区切られたキーで値を階層的に取得します。
	 */
	public Object getDeep(String key) {
		int idx = key.indexOf('.');
		if (idx == -1) {
			return get(key);
		}
		String key1 = key.substring(0, idx);
		String key2 = key.substring(idx+1);
		Object o = get(key1);
		if (o == null) {
			return null;
		} else if (o instanceof ExtendedMap) {
			return ((ExtendedMap)o).getDeep(key2);
		} else {
			throw new IllegalArgumentException("Invvalid parameter path : " + key);
		}
	}
	
	/**
	 * 指定のクラスを再帰探索し、最初に見つけたオブジェクトを返します。
	 */
	public <T> T searchClass(Class<T> clazz) {
		return doSearchClass(this, clazz);
	}
	
	private static <T> T doSearchClass(Map map, Class<T> clazz) {
		List<Map> list = null;
		for (Object value : map.values()) {
			if (value.getClass() == clazz) {
				return (T)value;
			} else if (value instanceof Map) {
				if (list == null) {
					list = new ArrayList<Map>();
				}
				list.add((Map)value);
			}
		}
		if (list != null) {
			for (Map childMap : list) {
				T ret = doSearchClass(childMap, clazz);
				if (ret != null) {
					return ret;
				}
			}
		}
		return null;
	}
	
	/**
	 * 指定のクラスのリストを再帰探索し、最初に見つけたオブジェクトを返します。
	 */
	public <T> List<T> searchListOfClass(Class<T> clazz) {
		return doSearchListOfClass(this, clazz);
	}
	
	private static <T> List<T> doSearchListOfClass(Map map, Class<T> clazz) {
		List<Map> mapList = null;
		for (Object value : map.values()) {
			if (value instanceof List) {
				List list = (List)value;
				if (list.size() > 0 && list.get(0).getClass() == clazz) {
					List<T> ret = new ArrayList<T>();
					for (Object v : list) {
						ret.add((T)v);
					}
					return ret;
				}
			} else if (value instanceof Map) {
				if (mapList == null) {
					mapList = new ArrayList<Map>();
				}
				mapList.add((Map)value);
			}
		}
		if (mapList != null) {
			for (Map childMap : mapList) {
				List<T> ret = doSearchListOfClass(childMap, clazz);
				if (ret != null) {
					return ret;
				}
			}
		}
		return null;
	}
	
}

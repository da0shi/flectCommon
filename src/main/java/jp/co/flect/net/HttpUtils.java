package jp.co.flect.net;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Iterator;
import java.util.Scanner;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpMessage;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.util.EntityUtils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonParser;

import jp.co.flect.util.ExtendedMap;
import jp.co.flect.util.Parameters;

/**
 * Http関連のUtilityメソッドです。
 * commons.httpclient 4.xを簡易に使うためのメソッドも提供されます。
 */
public class HttpUtils {
	
	public static final String HEADER_CONTENTTYPE = "content-type";
	
	public static final String CONTENTTYPE_JSON = "application/json";
	public static final String CONTENTTYPE_URLENCODED = "application/x-www-form-urlencoded";
	
	/**
	 * Mapの内容をURLのクエリー形式に変換して返します
	 * @param map パラメータと値のマップ
	 * @param encoding エンコーディング
	 */
	public static String map2query(Map<String, Object> map, String encoding) {
		URLEncoder encoder = new URLEncoder(encoding);
		StringBuilder buf = new StringBuilder();
		for (String key : map.keySet()) {
			Object o = map.get(key);
			if (o == null) {
				continue;
			}
			key = encoder.encode(key);
			if (o instanceof String[]) {
				String[] values = (String[])o;
				for (int i=0; i<values.length; i++) {
					buf.append(key).append("=").append(encoder.encode(values[i])).append("&");
				}
			} else {
				buf.append(key).append("=").append(encoder.encode(o.toString())).append("&");
			}
		}
		return buf.substring(0, buf.length()-1);
	}
	
	/**
	 * ステータスコードが2xxであればtrueを返します
	 */
	public static boolean isResponseOk(int n) {
		return n >= 200 && n < 300;
	}
	
	/**
	 * ステータスコードが2xxであればtrueを返します
	 */
	public static boolean isResponseOk(HttpResponse res) {
		return isResponseOk(res.getStatusLine().getStatusCode());
	}
	
	/**
	 * ステータスコードが4xxであればtrueを返します
	 */
	public static boolean isClientError(int n) {
		return n >= 400 && n < 500;
	}
	
	/**
	 * ステータスコードが4xxであればtrueを返します
	 */
	public static boolean isClientError(HttpResponse res) {
		return isClientError(res.getStatusLine().getStatusCode());
	}
	
	/**
	 * ステータスコードが5xxであればtrueを返します
	 */
	public static boolean isServerError(int n) {
		return n >= 500 && n < 600;
	}
	
	/**
	 * ステータスコードが5xxであればtrueを返します
	 */
	public static boolean isServerError(HttpResponse res) {
		return isServerError(res.getStatusLine().getStatusCode());
	}
	
	/**
	 * ContentTypeが「application/json」であればtrueを返します。
	 */
	public static boolean isJsonMessage(HttpMessage msg) {
		Header h = msg.getFirstHeader(HEADER_CONTENTTYPE);
		return h != null && h.getValue().toLowerCase().startsWith(CONTENTTYPE_JSON);
	}
	
	/**
	 * HttpResponseのcontentを文字列で取得します。
	 */
	public static String getContent(HttpResponse msg) throws IOException, ParseException {
		return EntityUtils.toString(msg.getEntity(), "utf-8");
	}
	
	/**
	 * HttpResponseのcontentを文字列で取得します。
	 * contentサイズがmaxContentLengthより大きい場合はExceptionが発生します
	 */
	public static String getContent(HttpResponse msg, int maxContentLength) throws IOException, ParseException {
		if (maxContentLength == 0) {
			return getContent(msg);
		}
		HttpEntity entity = msg.getEntity();
		InputStream instream = entity.getContent();
		if (instream == null) {
			return null;
		}
		try {
			if (entity.getContentLength() > maxContentLength) {
				throw new ContentLengthExceedException("Too large content-length", maxContentLength, entity.getContentLength());
			}
			int len = (int)entity.getContentLength();
			if (len < 0) {
				len = 4096;
			}
			String charset = EntityUtils.getContentCharSet(entity);
			if (charset == null) {
				charset = "utf-8";
			}
			ByteArrayOutputStream bos = new ByteArrayOutputStream(len);
			int readTotal = 0;
			byte[] buf = new byte[len];
			int n = instream.read(buf);
			while (n != -1) {
				readTotal += n;
				if (readTotal > maxContentLength) {
					throw new ContentLengthExceedException("Too large content-length", maxContentLength, -1);
				}
				bos.write(buf, 0, n);
				n = instream.read(buf);
			}
			return new String(bos.toByteArray(), charset);
		} finally {
			instream.close();
		}
	}
	
	/**
	 * HttpHeaderの値を返します
	 */
	public static String getHeaderValue(HttpMessage msg, String name) {
		Header h = msg.getFirstHeader(name);
		return h == null ? null : h.getValue();
	}
	
	/**
	 * URLエンコードされた文字列をParametersに変換します。
	 */
	public static Parameters parseParameters(String content) {
		Parameters ret = new Parameters();
		
		List<NameValuePair> list = new ArrayList<NameValuePair>();
		URLEncodedUtils.parse(list, new Scanner(content), "us-ascii");
		for (NameValuePair p : list) {
			ret.add(p.getName(), p.getValue());
		}
		return ret;
	}
	
	public static ExtendedMap parseJson(String str) {
		JsonElement el = new JsonParser().parse(str);
		if (!el.isJsonObject()) {
			throw new IllegalArgumentException("Not object: " + str);
		}
		ExtendedMap map = new ExtendedMap();
		doParseJson(map, el.getAsJsonObject());
		return map;
	}
	
	private static void doParseJson(ExtendedMap map, JsonObject obj) {
		for (Map.Entry<String, JsonElement> entry : obj.entrySet()) {
			String name = entry.getKey();
			JsonElement child = entry.getValue();
			map.put(name, getWrappedObject(child));
		}
	}
	
	private static Object getWrappedObject(JsonElement child) {
		if (child.isJsonPrimitive()) {
			JsonPrimitive jp = child.getAsJsonPrimitive();
			if (jp.isString()) {
				return jp.getAsString();
			} else if (jp.isNumber()) {
				return jp.getAsNumber();
			} else if (jp.isBoolean()) {
				return jp.getAsBoolean();
			} else {
				throw new IllegalStateException();
			}
		} else if (child.isJsonObject()) {
			ExtendedMap childMap = new ExtendedMap();
			doParseJson(childMap, child.getAsJsonObject());
			return childMap;
		} else if (child.isJsonArray()) {
			List<Object> list = new ArrayList<Object>();
			Iterator<JsonElement> it = child.getAsJsonArray().iterator();
			while (it.hasNext()) {
				list.add(getWrappedObject(it.next()));
			}
			return list;
		} else if (child.isJsonNull()) {
			return null;
		} else {
			throw new IllegalStateException();
		}
	}
}

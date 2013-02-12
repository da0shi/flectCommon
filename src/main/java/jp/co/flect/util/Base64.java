package jp.co.flect.util;

import java.io.UnsupportedEncodingException;

/**
 * Base64
 */
public class Base64 {
	
	/**
	 * エンコード
	 */
	public static String encode(byte[] data) {
		try {
			return new String(org.apache.commons.codec.binary.Base64.encodeBase64(data), "us-ascii");
		} catch (UnsupportedEncodingException e) {
			//not occur
			throw new IllegalStateException(e);
		}
	}
	
	/**
	 * デコード
	 */
	public static byte[] decode(String data) {
		try {
			return org.apache.commons.codec.binary.Base64.decodeBase64(data.getBytes("us-ascii"));
		} catch (UnsupportedEncodingException e) {
			//not occur
			throw new IllegalStateException(e);
		}
	}
	
	/**
	 * デコード
	 */
	public static byte[] decode(byte[] data) {
		return org.apache.commons.codec.binary.Base64.decodeBase64(data);
	}
	
	/**
	 * 引数の文字列がBase64文字列であるかどうかを返します。
	 */
	public static boolean isBase64(String s) {
		int eq = 0;
		for (int i=0; i<s.length(); i++) {
			char c = s.charAt(i);
			if (c == '=') {
				eq++;
				continue;
			}
			if (eq > 0) {
				return false;
			}
			if ((c >= '0' && c <= '9') || (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '+' || c == '/') {
				continue;
			}
			return false;
		}
		return eq < 4;
	}
}
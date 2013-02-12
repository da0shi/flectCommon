package jp.co.flect.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;

public class StringUtils {
	
	/**
	 * 引数がnullか長さ0の文字列ならnullを返し、それ以外は元の文字列を返します。
	 */
	public static String checkNull(String s) {
		return s == null || s.length() == 0 ? null : s;
	}
	
	/**
	 * 引数がnullでなく長さ1以上の文字列ならtrueを返します。
	 */
	public static boolean notEmpty(String s) {
		return s != null && s.length() > 0;
	}
	
	/**
	 * 引数がnullか長さ0の文字列ならtrueを返します。
	 */
	public static boolean isEmpty(String s) {
		return s == null || s.length() == 0;
	}
	
	/**
	 * 引数の長さのスペースを返します。
	 */
	public static String getSpace(int length) {
		StringBuilder buf = new StringBuilder();
		for (int i=0; i<length; i++) {
			buf.append(' ');
		}
		return buf.toString();
	}
	
	/**
	 * 文字列を1行づつ比較します。ただしignoresに含まれる文字が入っている行は比較の対象としません。
	 */
	public static boolean assertEquals(String s1, String s2, List<String> ignores) {
		BufferedReader r1 = new BufferedReader(new StringReader(s1));
		BufferedReader r2 = new BufferedReader(new StringReader(s2));
		try {
			String l1 = r1.readLine();
			while (l1 != null) {
				String l2 = r2.readLine();
				if (l2 == null) {
					return false;
				}
				String ig1 = contains(l1, ignores);
				String ig2 = contains(l2, ignores);
				if (ig1 != null) {
					if (!ig1.equals(ig2)) {
						return false;
					}
				} else if (ig2 != null) {
					return false;
				} else if (!l1.equals(l2)) {
					return false;
				}
				l1 = r1.readLine();
			}
			if (r2.readLine() != null) {
				return false;
			}
		} catch (IOException e) {
			//not occur
			throw new IllegalStateException(e);
		}
		return true;
	}
	
	/**
	 * 文字列にlist内の文字が含まれている場合最初に見つかった行をを1行づつ比較します。ただしignoresに含まれる文字が入っている行は比較の対象としません。
	 */
	public static String contains(String str, List<String> list) {
		for (String s : list) {
			if (str.indexOf(s) != -1) {
				return s;
			}
		}
		return null;
	}
	
	/**
	 * StackTraceを文字列として取得します
	 */
	public static String stackTraceToString(Throwable e) {
		StringWriter writer = new StringWriter();
		e.printStackTrace(new PrintWriter(writer));
		return writer.toString();
	}
	
	public static boolean equals(String s1, String s2) {
		if (s1 == null) {
			return s2 == null;
		} else {
			return s1.equals(s2);
		}
	}
}

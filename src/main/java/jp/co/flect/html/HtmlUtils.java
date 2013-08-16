package jp.co.flect.html;

public class HtmlUtils {
	
	public static String escape(String s) {
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
}

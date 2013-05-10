package jp.co.flect.html;

public class HtmlUtils {
	
	public static String escape(String s) {
		if (s == null || s.length() == 0) {
			return s;
		}
		StringBuilder buf = new StringBuilder();
		for (int i=0; i<s.length(); i++) {
			char c = s.charAt(i);
			switch (c) {
				case '<':
					buf.append("&lt;");
					break;
				case '>':
					buf.append("&gt;");
					break;
				case '\'':
					buf.append("&apos;");
					break;
				case '\"':
					buf.append("&quot;");
					break;
				case '&':
					buf.append("&amp;");
					break;
				default:
					buf.append(c);
					break;
			}
		}
		return buf.toString();
	}
}

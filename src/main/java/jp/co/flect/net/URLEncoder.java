package jp.co.flect.net;

import java.util.BitSet;
import java.net.URLDecoder;
import java.io.UnsupportedEncodingException;

/**
 * URLエンコード/でコードを行うクラス
 * 「 (0x20」「~(0x7E)」「*(0x2A)」の扱いがJavaの標準実装と異なります
 * 参考
 * http://tools.ietf.org/rfc/rfc3896.txt
 * http://tools.ietf.org/rfc/rfc1866.txt
 * http://en.wikipedia.org/wiki/Percent-encoding
 */
public class URLEncoder {
	
	private static final BitSet needEncoding = new BitSet(128);
	private static final char[] hexDigits = {
		'0', '1', '2', '3', '4', '5', '6', '7',
		'8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
	};
	
	static {
		int i;
		for (i = 'a'; i <= 'z'; i++) {
			needEncoding.set(i);
		}
		for (i = 'A'; i <= 'Z'; i++) {
			needEncoding.set(i);
		}
		for (i = '0'; i <= '9'; i++) {
			needEncoding.set(i);
		}
		needEncoding.set('-');
		needEncoding.set('_');
		needEncoding.set('.');
		needEncoding.set('~');
		needEncoding.flip(0, needEncoding.size());
	}
	
	private String encoding;
	private boolean spaceAsPlus;
	
	/**
	 * this("utf-8", false);
	 */
	public URLEncoder() {
		this("utf-8", false);
	}
	
	/**
	 * this(encoding, false);
	 */
	public URLEncoder(String encoding) {
		this(encoding, false);
	}
	
	/**
	 * this("utf-8", spaceAsPlus);
	 */
	public URLEncoder(boolean spaceAsPlus) {
		this("utf-8", spaceAsPlus);
	}
	
	/**
	 * コンストラクタ
	 * @param encoding エンコーディング
	 * @param spaceAsPlus trueの場合スペースを「+」にする
	 */
	public URLEncoder(String encoding, boolean spaceAsPlus) {
		this.encoding = encoding;
		this.spaceAsPlus = spaceAsPlus;
	}
	
	public String getEncoding() { return this.encoding;}
	public boolean isSpaceAsPlus() { return this.spaceAsPlus;}
	
	public String encode(String str) {
		if (str == null || str.length() == 0) {
			return str;
		}
		try {
			StringBuilder buf = null;
			for (int i=0; i<str.length(); i++) {
				char c = str.charAt(i);
				if (isTarget(c) || c == ' ') {
					buf = new StringBuilder(str.length() * 2);
					buf.append(str.substring(0, i));
					str = str.substring(i);
					break;
				}
			}
			if (buf == null) {
				return str;
			}
			byte[] data = str.getBytes(this.encoding);
			for (int i=0; i<data.length; i++) {
				char c = (char)data[i];
				if (c == ' ') {
					buf.append(this.spaceAsPlus ? "+" : "%20");
				} else if (isTarget(c)) {
					buf.append('%')
						.append(hexDigits[(c >> 4) & 0x0F])
						.append(hexDigits[c & 0x0F]);
				} else {
					buf.append(c);
				}
			}
			return buf.toString();
		} catch (UnsupportedEncodingException e) {
			throw new IllegalArgumentException(this.encoding);
		}
	}
	
	public String decode(String str) {
		try {
			return URLDecoder.decode(str, this.encoding);
		} catch (UnsupportedEncodingException e) {
			throw new IllegalArgumentException(this.encoding);
		}
	}
	
	private static boolean isTarget(char c) {
		if (c >= 0x80) {
			return true;
		}
		return needEncoding.get(c);
	}

}
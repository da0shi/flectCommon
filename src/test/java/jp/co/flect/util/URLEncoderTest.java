package jp.co.flect.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import org.junit.Test;
import jp.co.flect.net.URLEncoder;

public class URLEncoderTest {
	
	private static final String[] testdata = {
		"abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789",
		" !\"#$%&'()=-~^|\\`@{[+;*:}]<,>.?/_ !\"#$%&'()=-~^|\\`@{[+;*:}]<,>.?/_",
		"abcdefg !\"#$%&",
		"あいうえお"
	};
	
	@Test
	public void test() throws Exception {
		//process static block
		new URLEncoder();
		java.net.URLEncoder.encode("dummy");
		testdata[0].getBytes("utf-8");
		testdata[0].getBytes("shift_jis");
		
		for (int i=0; i<testdata.length; i++) {
			assertEquals(
				encode1(testdata[i], "utf-8", true),
				encode2(testdata[i], "utf-8", true));
			assertEquals(
				encode1(testdata[i], "utf-8", false),
				encode2(testdata[i], "utf-8", false));
			assertEquals(
				encode1(testdata[i], "shift_jis", true),
				encode2(testdata[i], "shift_jis", true));
			assertEquals(
				encode1(testdata[i], "shift_jis", false),
				encode2(testdata[i], "shift_jis", false));
		}
	}
	
	private static String encode1(String str, String encoding, boolean spaceAsPlus) {
		System.out.println(str);
		long n = System.nanoTime();
		String ret = new URLEncoder(encoding, spaceAsPlus).encode(str);
		System.out.println("encode1: " + (System.nanoTime() - n));
		return ret;
	}
	
	private static String encode2(String str, String encoding, boolean spaceAsPlus) throws Exception {
		long n = System.nanoTime();
		String ret = java.net.URLEncoder.encode(str, encoding);
		System.out.println("encode2: " + (System.nanoTime() - n));
		if (!spaceAsPlus) {
			ret = ret.replace("+", "%20");
		}
		ret = ret.replace("%7E", "~");
		ret = ret.replace("*", "%2A");
		return ret;
	}
}

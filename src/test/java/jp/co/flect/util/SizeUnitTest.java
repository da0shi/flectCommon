package jp.co.flect.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import org.junit.Test;

public class SizeUnitTest {
	
	private static final long[] testdata = {
		1024L,
		1024L * 1024L,
		1024L * 1024L * 1024L,
		1024L * 1024L * 500L,
		(long)(1024L * 1024L * 1.2)
	};
	
	@Test
	public void test() throws Exception {
		//process static block
		for (long l : testdata) {
			System.out.println(l + "Byte = " + SizeUnit.KB.toString(l));
			System.out.println(l + "Byte = " + SizeUnit.MB.toString(l));
			System.out.println(l + "Byte = " + SizeUnit.GB.toString(l));
		}
	}
	
}

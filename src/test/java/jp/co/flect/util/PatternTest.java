package jp.co.flect.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import org.junit.Test;

public class PatternTest {
	
	@Test
	public void test() throws Exception {
		MultiPatternMatcher m = new MultiPatternMatcher();
		m.addAllow(".*@flect.co.jp");
		m.addDeny("skonishi@flect.co.jp");
		assertTrue(m.matches("k-shunji@flect.co.jp"));
		assertFalse(m.matches("k-shunji@hogehoge.co.jp"));
		assertFalse(m.matches("skonishi@flect.co.jp"));
	}
	
}

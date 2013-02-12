package jp.co.flect.template;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Test;

import java.util.Map;
import java.util.HashMap;
import java.io.StringWriter;

public class TemplateTest {
	
	@Test
	public void velocity() {
		String str1 = "Hello $test";
		String str2 = "Hello 小西";
		Map map = new HashMap();
		map.put("test", "小西");
		try {
			VelocityTemplateEngine engine = new VelocityTemplateEngine();
			Template template = engine.createTemplate(str1);
			StringWriter sw = new StringWriter();
			template.merge(map, sw);
			assertEquals(sw.toString(), str2);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.toString());
		}
	}
}

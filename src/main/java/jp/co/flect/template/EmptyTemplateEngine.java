package jp.co.flect.template;

import java.util.Map;
import java.io.Writer;
import java.io.IOException;
import java.io.Serializable;

/**
 * 何もしないTemplateEngine
 */
public class EmptyTemplateEngine implements TemplateEngine, Serializable {
	
	private static final long serialVersionUID = 6426708444907143197L;

	public Template createTemplate(String str) throws TemplateException {
		return new EmptyTemplate(str);
	}
	
	private static class EmptyTemplate implements Template {
		
		private String str;
		
		public EmptyTemplate(String s) {
			this.str = s;
		}
		public void merge(Map params, Writer writer) throws TemplateException, IOException {
			writer.write(this.str);
		}
	}
}

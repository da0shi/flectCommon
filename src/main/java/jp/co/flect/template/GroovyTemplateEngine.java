package jp.co.flect.template;

import org.codehaus.groovy.control.CompilationFailedException;
import groovy.text.GStringTemplateEngine;
import java.util.Map;
import java.io.Writer;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

/**
 * Groovyを使用するTemplateEngine
 */
public class GroovyTemplateEngine implements TemplateEngine, Serializable {
	
	private static final long serialVersionUID = -8954038826603728026L;

	private transient groovy.text.TemplateEngine engine = new GStringTemplateEngine();
//	private groovy.text.TemplateEngine engine = new groovy.text.SimpleTemplateEngine();
	
	public Template createTemplate(String str) throws TemplateException {
		try {
			return new GroovyTemplate(this.engine.createTemplate(str));
		} catch (ClassFormatError e) {
			throw new GroovyLimitException("Too large template", e);
		} catch (CompilationFailedException e) {
			throw new TemplateException(e);
		} catch (ClassNotFoundException e) {
			throw new TemplateException(e);
		} catch (IOException e) {
			throw new TemplateException(e);
		}
	}
	
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.defaultReadObject();
		this.engine = new GStringTemplateEngine();
	}
	
	private static class GroovyTemplate implements Template {
		
		private groovy.text.Template template;
		
		public GroovyTemplate(groovy.text.Template t) {
			this.template = t;
		}
		
		public void merge(Map params, Writer writer) throws TemplateException, IOException {
			this.template.make(params).writeTo(writer);
		}
	}
}

package jp.co.flect.template;

import java.util.Map;
import java.util.UUID;
import java.io.Writer;
import java.io.IOException;
import java.io.Serializable;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.resource.loader.StringResourceLoader;
import org.apache.velocity.runtime.resource.util.StringResourceRepository;

/**
 * Velocityを使用するTemplateEngine
 */
public class VelocityTemplateEngine implements TemplateEngine, Serializable {
	
	private static final long serialVersionUID = -5043361027028829588L;
	
	private static final VelocityEngine engine;
	
	static {
		engine = new VelocityEngine();
		engine.addProperty("resource.loader", "string");
		engine.addProperty(VelocityEngine.RUNTIME_LOG_LOGSYSTEM_CLASS, "org.apache.velocity.runtime.log.NullLogSystem");
		engine.init();
	}
	
	public Template createTemplate(String str) throws TemplateException {
		String key = UUID.randomUUID().toString();
		StringResourceRepository repo = StringResourceLoader.getRepository();
		try {
			repo.putStringResource(key, str);
			return new VelocityTemplate(engine.getTemplate(key));
		} catch (ResourceNotFoundException e) {
			//not occur
			throw new IllegalStateException(e);
		} catch (ParseErrorException e) {
			throw new TemplateException(e);
		} finally {
			repo.removeStringResource(key);
		}
	}
	
	private static class VelocityTemplate implements Template {
		
		private org.apache.velocity.Template template;
		
		public VelocityTemplate(org.apache.velocity.Template t) {
			this.template = t;
		}
		
		public void merge(Map params, Writer writer) throws TemplateException, IOException {
			this.template.merge(new VelocityContext(params), writer);
		}
	}
}

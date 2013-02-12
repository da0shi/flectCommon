package jp.co.flect.template;

/**
 * Groovyでテンプレートサイズの上限に引っかかった場合のException
 */
public class GroovyLimitException extends TemplateException {
	
	private static final long serialVersionUID = -8343447164793504106L;

	public GroovyLimitException(String msg, Throwable e) {
		super(msg, e);
		setLimitation(true);
	}
	
}

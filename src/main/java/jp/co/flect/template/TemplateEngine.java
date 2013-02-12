package jp.co.flect.template;

/**
 * テンプレート生成を行うクラスのインターフェース
 */
public interface TemplateEngine {
	
	public Template createTemplate(String str) throws TemplateException;
	
}

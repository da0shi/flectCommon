package jp.co.flect.template;

import java.util.Map;
import java.io.Writer;
import java.io.IOException;

/**
 * テンプレート処理を行うクラスのインターフェース
 */
public interface Template {
	
	/**
	 * テンプレートに対してmapの値を適用してWriterに書き込みます。
	 */
	public void merge(Map params, Writer writer) throws TemplateException, IOException;
	
}

package jp.co.flect.template;

/**
 * テンプレート処理で発生するException
 */
public class TemplateException extends Exception {
	
	private static final long serialVersionUID = -4526486214450704785L;
	
	private boolean limitation;
	
	public TemplateException(String msg) {
		super(msg);
	}
	
	public TemplateException(Throwable e) {
		super(e);
	}
	
	public TemplateException(String msg, Throwable e) {
		super(msg, e);
	}
	
	public boolean isLimitation() { return this.limitation;}
	protected void setLimitation(boolean b) { this.limitation = b;}
}

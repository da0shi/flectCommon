package jp.co.flect.xml;

/**
 * StaxConstructorによるオブジェクトの構築中に発生するException
 */
public class StAXConstructException extends Exception {
	
	private static final long serialVersionUID = -4562936692210786654L;

	public StAXConstructException(String msg) {
		super(msg);
	}
	
	public StAXConstructException(Throwable e) {
		super(e);
	}
}

package jp.co.flect.net;

import java.io.IOException;

/**
 * HttpResponseのContentLengthが制限サイズを超過していた場合のException
 */
public class ContentLengthExceedException extends IOException {
	
	private String urlOrOp;
	private int limit;
	private long contentLength;
	
	public ContentLengthExceedException(String msg, int limit, long contentLength) {
		super(msg);
		this.limit = limit;
		this.contentLength = contentLength;
	}
	
	public int getLimit() { return this.limit;}
	public long getContentLength() { return this.contentLength;}
	
	public String getUrlOrOpeartion() { return this.urlOrOp;}
	public ContentLengthExceedException setUrlOrOperation(String s) {
		this.urlOrOp = s;
		return this;
	}
}

package jp.co.flect.net;

import java.io.IOException;
import java.util.UUID;
import org.apache.http.protocol.HttpContext;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;

public class PasswordAndNonceHandler implements HttpRequestInterceptor {
	
	private String password;
	
	public PasswordAndNonceHandler(String password) {
		this.password = password;
	}
	
	public void process(HttpRequest request, HttpContext context) throws HttpException, IOException {
		String nonce = UUID.randomUUID().toString();
		String time = Long.toString(System.currentTimeMillis());
		String hash = PasswordAndNonceFilter.createHash(this.password, nonce + time);
		request.addHeader(PasswordAndNonceFilter.HEADER_NAME, hash + ":" + nonce + ":" + time);
	}
}

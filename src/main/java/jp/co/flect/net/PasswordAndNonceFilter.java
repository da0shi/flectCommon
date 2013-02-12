package jp.co.flect.net;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.MessageDigest;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import jp.co.flect.util.Base64;

public class PasswordAndNonceFilter implements Filter {
	
	public static final String HEADER_NAME = "x-flect-auth";
	
	private String password;
	private long expired = 30 * 60 * 1000;
	
	public void init(FilterConfig config) throws ServletException {
		this.password = config.getInitParameter("password");
		if (this.password == null) {
			this.password = "";
		};
		String strExpired = config.getInitParameter("expired");
		if (strExpired != null && strExpired.length() > 0) {
			try {
				this.expired = Long.parseLong(strExpired);
			} catch (NumberFormatException e) {
			}
		}
	}
	
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws java.io.IOException, ServletException {
		String str = ((HttpServletRequest)request).getHeader(HEADER_NAME);
		if (str == null || str.length() == 0) {
			((HttpServletResponse)response).sendError(400, HEADER_NAME + " header is required.");
			return;
		}
		if (!authenticate(str)) {
			((HttpServletResponse)response).sendError(400, "Authentication failed.");
			return;
		}
		chain.doFilter(request, response);
	}
	
	private boolean authenticate(String str) {
		String[] strs = str.split(":");
		if (strs.length != 3) {
			return false;
		}
		try {
			long n = Long.parseLong(strs[2]);
			if (System.currentTimeMillis() > n + this.expired) {
				return false;
			}
		} catch (NumberFormatException e) {
			return false;
		}
		String hash = strs[0];
		String nonce = strs[1] + strs[2];
		
		return createHash(this.password, nonce).equals(hash);
	}
	
	public void destroy() {
		this.password = null;
	}
	
	public static String createHash(String password, String nonce) {
		try {
			byte[] data = (password + nonce).getBytes("utf-8");
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			byte[] digest = md.digest(data);
			return Base64.encode(data);
		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException(e);
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException(e);
		}
	}
}
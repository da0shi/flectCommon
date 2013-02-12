package jp.co.flect.net;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * ServletRequestに任意の属性をセットするFilter<br>
 * server.xmlのログ出力設定と組み合わせることでログ出力を制御することができる
 */
public class AttributeSetFilter implements Filter {
	
	private String attrName;
	private String attrValue;
	
	public void init(FilterConfig config) throws ServletException {
		this.attrName = config.getInitParameter("name");
		this.attrValue = config.getInitParameter("value");
	}
	
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws java.io.IOException, ServletException {
		if (attrName != null && attrValue != null) {
			request.setAttribute(attrName, attrValue);
		}
		chain.doFilter(request, response);
	}
	
	public void destroy() {
		this.attrName = null;
		this.attrValue = null;
	}
	
}
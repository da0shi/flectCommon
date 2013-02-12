package jp.co.flect.net;

import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import jp.co.flect.log.Logger;
import jp.co.flect.log.LoggerFactory;

public class OAuth2 {
	
	private static final Logger log = LoggerFactory.getLogger(OAuth2.class);
	
	public enum ResponseType {
		code,
		token,
		code_and_token
	};
	
	private String authorizationUrl;
	private String accessTokenUrl;
	private String clientid;
	private String secret;
	private String redirectUrl;
	private ResponseType responseType = ResponseType.code;
	private Map<String, String> loginOptions;
	private Map<String, String> authOptions;
	private boolean useGet = false;
	
	public OAuth2(String authorizationUrl, String accessTokenUrl, String clientid, String secret, String redirectUrl) {
		this.authorizationUrl = authorizationUrl;
		this.accessTokenUrl = accessTokenUrl;
		this.clientid = clientid;
		this.secret = secret;
		this.redirectUrl = redirectUrl;
	}
	
	public ResponseType getResponseType() { return this.responseType;}
	public void setResponseType(ResponseType type) { this.responseType = type;}
	
	public String getLoginUrl() {
		URLEncoder encoder = new URLEncoder("utf-8", true);
		StringBuilder buf = new StringBuilder()
			.append(this.authorizationUrl)
			.append("?client_id=").append(this.clientid)
			.append("&response_type=").append(this.responseType)
			.append("&redirect_uri=").append(encoder.encode(this.redirectUrl));
		if (this.loginOptions != null) {
			for (Map.Entry<String, String> entry : this.loginOptions.entrySet()) {
				buf.append("&").append(encoder.encode(entry.getKey()))
					.append("=").append(encoder.encode(entry.getValue()));
			}
		}
		log.debug("LoginUrl: {0}", buf);
		return buf.toString();
	}
	
	public OAuthResponse authenticate(String code) throws IOException {
		HttpClient client = new DefaultHttpClient();
		HttpResponse res = null;
		if (useGet) {
			String url = getOAuthUrl(code);
			HttpGet method = new HttpGet(url);
			res = client.execute(method);
		} else {
			HttpPost method = new HttpPost(this.accessTokenUrl);
			List<NameValuePair> list = new ArrayList<NameValuePair>();
			list.add(new BasicNameValuePair("grant_type", "authorization_code"));
			list.add(new BasicNameValuePair("redirect_uri", this.redirectUrl));
			list.add(new BasicNameValuePair("client_id", this.clientid));
			list.add(new BasicNameValuePair("client_secret", this.secret));
			list.add(new BasicNameValuePair("code", code));
			
			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list, "utf-8");
			if (log.isDebugEnabled()) {
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				entity.writeTo(bos);
				log.debug("Auth request body: {0}", new String(bos.toByteArray(), "utf-8"));
			}
			method.setEntity(entity);
			res = client.execute(method);
		}
		int ret = res.getStatusLine().getStatusCode();
		if (ret >= 200 && ret < 300) {
			String content = HttpUtils.getContent(res);
			log.debug("Auth response body: {0}", content);
			if (HttpUtils.isJsonMessage(res)) {
				return new OAuthResponse(HttpUtils.parseJson(content));
			} else {
				return new OAuthResponse(HttpUtils.parseParameters(content));
			}
		}
		String msg = res.getEntity().getContentLength() > 0 ? HttpUtils.getContent(res) : res.getStatusLine().toString();
		throw new IOException(msg);
	}
	
	public String getOAuthUrl(String code) {
		URLEncoder encoder = new URLEncoder("utf-8", true);
		StringBuilder buf = new StringBuilder()
			.append(this.accessTokenUrl)
			.append("?grant_type=authorization_code")
			.append("&client_id=").append(this.clientid)
			.append("&redirect_uri=").append(encoder.encode(this.redirectUrl))
			.append("&client_secret=").append(this.secret)
			.append("&code=").append(code);
		if (this.authOptions != null) {
			for (Map.Entry<String, String> entry : this.authOptions.entrySet()) {
				buf.append("&").append(encoder.encode(entry.getKey()))
					.append("=").append(encoder.encode(entry.getValue()));
			}
		}
		log.debug("OAuthUrl: {0}", buf);
		return buf.toString();
	}
	
	public void setLoginOption(String key, String value) {
		if (this.loginOptions == null) {
			this.loginOptions = new HashMap<String, String>();
		}
		this.loginOptions.put(key, value);
	}
	
	public String getLoginOption(String key) {
		return this.loginOptions == null ? null : this.loginOptions.get(key);
	}
	
	public void setAuthOption(String key, String value) {
		if (this.authOptions == null) {
			this.authOptions = new HashMap<String, String>();
		}
		this.authOptions.put(key, value);
	}
	
	public String getAuthOption(String key) {
		return this.authOptions == null ? null : this.authOptions.get(key);
	}
	
}

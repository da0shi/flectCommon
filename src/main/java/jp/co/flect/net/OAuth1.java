package jp.co.flect.net;

import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.security.NoSuchAlgorithmException;
import java.security.InvalidKeyException;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import jp.co.flect.log.Logger;
import jp.co.flect.log.LoggerFactory;
import jp.co.flect.util.Parameters;
import jp.co.flect.util.Base64;

public class OAuth1 {
	
	public static final Logger log = LoggerFactory.getLogger(OAuth1.class);
	
	public enum SignatureMethod {
		PLAINTEXT,
		HMAC_SHA1
	}
	
	private String consumerKey;
	private String consumerSecret;
	private String requestTokenUrl;
	private String authUrl;
	private String accessTokenUrl;
	private boolean useHeader = true;
	
	private SignatureMethod signMethod = SignatureMethod.HMAC_SHA1;
	
	public OAuth1(String consumerKey, String consumerSecret, String requestTokenUrl, String authUrl, String accessTokenUrl) {
		this.consumerKey = consumerKey;
		this.consumerSecret = consumerSecret;
		this.requestTokenUrl = requestTokenUrl;
		this.authUrl = authUrl;
		this.accessTokenUrl = accessTokenUrl;
	}
	
	public boolean isUseHeader() { return this.useHeader;}
	public void setUseHeader(boolean b) { this.useHeader = b;}
	
	private long getTimestamp() {
		return System.currentTimeMillis() / 1000;
	}
	
	private String getNonce() {
		return String.valueOf(getTimestamp() + new Random().nextInt());
	}
	
	private String generateSignature(String method, String url, Parameters params, String tokenKey) {
		URLEncoder encoder = new URLEncoder("utf-8");
		
		StringBuilder buf = new StringBuilder()
			.append(method.toUpperCase())
			.append("&")
			.append(encoder.encode(url))
			.append("&")
			.append(encoder.encode(params.toQuery()));
		String strToSign = buf.toString();
		String key = encoder.encode(this.consumerSecret) + "&";
		if (tokenKey != null) {
			key += encoder.encode(tokenKey);
		}
		if (signMethod == SignatureMethod.PLAINTEXT) {
			return tokenKey;
		}
		try {
			Mac mac = Mac.getInstance("HmacSHA1");
			mac.init(new SecretKeySpec(key.getBytes("utf-8"), "HmacSHA1"));
			mac.update(strToSign.getBytes("utf-8"));
			byte[] data = mac.doFinal();
			return Base64.encode(data);
		} catch (UnsupportedEncodingException e) {
			//not occur
			e.printStackTrace();
			throw new IllegalStateException();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			throw new IllegalStateException();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
			throw new IllegalStateException();
		}
	}
	
	private void addParameters(Parameters params) {
		params.add("oauth_consumer_key", this.consumerKey);
		params.add("oauth_signature_method", this.signMethod.toString().replace("_", "-"));
		params.add("oauth_timestamp", Long.toString(getTimestamp()));
		params.add("oauth_nonce", getNonce());
		params.add("oauth_version", "1.0");
	}
	
	private void addSignature(HttpRequest method, Parameters params) throws IOException {
		if (this.useHeader) {
			URLEncoder encoder = new URLEncoder("utf-8");
			StringBuilder buf = new StringBuilder();
			buf.append("OAuth ");
			boolean added = false;
			for (String key : params.keySet()) {
				if (added) {
					buf.append(", ");
				}
				buf.append(key).append("=\"")
					.append(encoder.encode(params.get(key)))
					.append("\"");
				added = true;
			}
			method.addHeader("Authorization", buf.toString());
			log.debug("AuthorizationHeader: {0}", buf);
		} else {
			List<NameValuePair> list = new ArrayList<NameValuePair>();
			for (String key : params.keySet()) {
				list.add(new BasicNameValuePair(key, params.get(key)));
			}
			
			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list, "utf-8");
			if (log.isDebugEnabled()) {
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				entity.writeTo(bos);
				log.debug("Temporary credential request body: {0}", new String(bos.toByteArray(), "utf-8"));
			}
			((HttpPost)method).setEntity(entity);
		}
	}
	
	public OAuthResponse getTemporaryCredential(String redirectUrl) throws IOException {
		Parameters params = new Parameters();
		params.add("oauth_callback", redirectUrl);
		addParameters(params);
		params.add("oauth_signature", generateSignature("POST", this.requestTokenUrl, params, null));
		
		HttpClient client = new DefaultHttpClient();
		HttpPost method = new HttpPost(this.requestTokenUrl);
		addSignature(method, params);
		
		HttpResponse res = client.execute(method);
		int ret = res.getStatusLine().getStatusCode();
		log.debug("HttpResponse code: {0}", ret);
		if (ret >= 200 && ret < 300) {
			String content = HttpUtils.getContent(res);
			log.debug("Temporary credential response body: {0}", content);
			if (HttpUtils.isJsonMessage(res)) {
				return new OAuthResponse(HttpUtils.parseJson(content));
			} else {
				return new OAuthResponse(HttpUtils.parseParameters(content));
			}
		}
		String msg = res.getEntity().getContentLength() > 0 ? HttpUtils.getContent(res) : res.getStatusLine().toString();
		throw new IOException(msg);
	}
	
	public String getLoginUrl(String oauthToken) {
		return authUrl + "?oauth_token=" + oauthToken;
	}
	
	public OAuthResponse authenticate(String token, String verifier, String secret) throws IOException {
		Parameters params = new Parameters();
		params.add("oauth_token", token);
		params.add("oauth_verifier", verifier);
		addParameters(params);
		params.add("oauth_signature", generateSignature("POST", this.accessTokenUrl, params, secret));
		
		HttpClient client = new DefaultHttpClient();
		HttpPost method = new HttpPost(this.accessTokenUrl);
		addSignature(method, params);
		
		HttpResponse res = client.execute(method);
		int ret = res.getStatusLine().getStatusCode();
		log.debug("HttpResponse code: {0}", ret);
		if (ret >= 200 && ret < 300) {
			String content = HttpUtils.getContent(res);
			log.debug("Access Token response body: {0}", content);
			if (HttpUtils.isJsonMessage(res)) {
				return new OAuthResponse(HttpUtils.parseJson(content));
			} else {
				return new OAuthResponse(HttpUtils.parseParameters(content));
			}
		}
		String msg = res.getEntity().getContentLength() > 0 ? HttpUtils.getContent(res) : res.getStatusLine().toString();
		throw new IOException(msg);
	}
	
}

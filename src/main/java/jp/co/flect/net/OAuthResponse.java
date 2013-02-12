package jp.co.flect.net;

import jp.co.flect.util.Parameters;
import java.util.Map;

public class OAuthResponse extends Parameters {
	
	private static final long serialVersionUID = 2967851363104172638L;

	public OAuthResponse(Map<String, ? extends Object> map) {
		super(map);
	}
	
	public OAuthResponse(Parameters params) {
		super(params);
	}
	
	public String getAccessToken() {
		return get("access_token");
	}
	
	public String getOAuthToken() {
		return get("oauth_token");
	}
	
	public String getOAuthTokenSecret() {
		return get("oauth_token_secret");
	}
	
	public boolean isOAuthCallbackConfirmed() {
		return "true".equalsIgnoreCase(get("oauth_callback_confirmed"));
	}
	
}

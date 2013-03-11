package jp.co.flect.net;

public class UserAgent {
	
	public static final String MSIE = "MSIE";
	public static final String CHROME = "Chrome";
	public static final String IPAD = "iPad";
	public static final String IPHONE = "iPhone";
	public static final String ANDROID = "Android";
	public static final String FIREFOX = "Firefox";
	public static final String SAFARI = "Safari";
	
	private String raw;
	private String main;
	private String sub;
	private String version;
	
	public UserAgent(String ua) {
		this.raw = ua;
		parse();
	}
	
	private void parse() {
		if (check(ANDROID)) {
			if (this.raw.indexOf(CHROME) != -1) {
				this.sub = CHROME;
			}
			return;
		}
		if (check(CHROME)) return;
		if (check(MSIE)) return;
		if (check(IPHONE)) return;
		if (check(IPAD)) return;
		if (check(FIREFOX)) return;
		if (check(SAFARI)) return;
		
		this.main = raw;
		this.version = "Unknown";
	}
	
	private boolean check(String str) {
		int idx = this.raw.indexOf(str);
		if (idx == -1) {
			return false;
		}
		
		this.main = str;
		this.version = parseVersion(this.raw, idx + str.length());
		return true;
	}
	
	private String parseVersion(String s, int spos) {
		int epos = -1;
		for (int i=spos; i<s.length(); i++) {
			char c = s.charAt(i);
			if (c == ' ' || c == '/') {
				if (epos == -1) {
					spos++;
				} else {
					epos = i;
					break;
				}
			} else if ((c >= '0' && c <= '9') || c == '.') {
				epos = i + 1;
			} else {
				epos = i;
				break;
			}
		}
		if (epos == -1) {
			return "Unknown";
		} else {
			return s.substring(spos, epos);
		}
	}
	
	public String getRawUserAgent() { return this.raw;}
	public String getMainString() { return this.main;}
	public String getSubString() { return this.sub;}
	public String getVersion() { return this.version;}
	
	public int getMajorVersion() {
		String s = this.version;
		int idx = s.indexOf('.');
		if (idx != -1) {
			s = s.substring(0, idx);
		}
		try {
			return Integer.parseInt(s);
		} catch (NumberFormatException e) {
			return 0;
		}
	}
	
	public boolean isIE() { return this.main.equals(MSIE);}
	public boolean isAndroid() { return this.main.equals(ANDROID);}
	public boolean isIPad() { return this.main.equals(IPAD);}
	public boolean isIPhone() { return this.main.equals(IPHONE);}
	public boolean isChrome() { return this.main.equals(CHROME);}
	public boolean isFirefox() { return this.main.equals(FIREFOX);}
	public boolean isSafari() { return this.main.equals(SAFARI);}
	
	public boolean isIOS() { return isIPad() || isIPhone();}
	
}
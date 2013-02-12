package jp.co.flect.util;

import java.util.List;
import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 * 許可、または拒否を表す複数の正規表現にマッチするかどうかを判断するクラス
 */
public class MultiPatternMatcher {
	
	private List<Pattern> allowList;
	private List<Pattern> denyList;
	
	public void addAllow(String s) {
		addAllow(s, 0);
	}
	
	public void addAllow(String s, int flags) {
		if (this.allowList == null) {
			this.allowList = new ArrayList<Pattern>();
		}
		this.allowList.add(Pattern.compile(s, flags));
	}
	
	public void addDeny(String s) {
		addDeny(s, 0);
	}
	
	public void addDeny(String s, int flags) {
		if (this.denyList == null) {
			this.denyList = new ArrayList<Pattern>();
		}
		this.denyList.add(Pattern.compile(s, flags));
	}
	
	public boolean matches(String s) {
		if (this.allowList != null) {
			boolean ret = false;
			for (Pattern p : this.allowList) {
				if (p.matcher(s).matches()) {
					ret = true;
					break;
				}
			}
			if (!ret) {
				return false;
			}
		}
		if (this.denyList != null) {
			for (Pattern p : this.denyList) {
				if (p.matcher(s).matches()) {
					return false;
				}
			}
		}
		return true;
	}
}

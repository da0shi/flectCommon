package jp.co.flect.util;

public class Pair<T1, T2> {
	
	public T1 first;
	public T2 second;
	
	public Pair(T1 first, T2 second) {
		this.first = first;
		this.second = second;
	}
	
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}
		if (o instanceof Pair) {
			Pair p = (Pair)o;
			if (this.first == null) {
				if (p.first != null) {
					return false;
				}
			} else if (!this.first.equals(p.first)) {
				return false;
			}
			if (this.second == null) {
				return p.second == null;
			} else {
				return this.second.equals(p.second);
			}
		}
		return false;
	}
}


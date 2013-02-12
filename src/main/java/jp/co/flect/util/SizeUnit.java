package jp.co.flect.util;

import java.text.DecimalFormat;
import java.math.BigDecimal;

public enum SizeUnit {
	B(1L, "B"),
	KB(1024L, "KB"),
	MB(1024L * 1024L, "MB"),
	GB(1024L * 1024L * 1024L, "GB"),
	TB(1024L * 1024L * 1024L * 1024L, "TB"),
	PB(1024L * 1024L * 1024L * 1024L * 1024L, "PB"),
	EB(1024L * 1024L * 1024L * 1024L * 1024L * 1024L, "EB");
	
	public static final SizeUnit MIN = B;
	public static final SizeUnit MAX = EB;
	
	private long multiplier;
	private String unit;
	private SizeUnit(long multiplier, String unit) {
		this.multiplier = multiplier;
		this.unit = unit;
	}
	
	public long getMultiplier() { return this.multiplier;}
	public String getUnit() { return this.unit;}
	
	public long toByte(long l) {
		return l * this.multiplier;
	}
	
	public String toString(long n) {
		BigDecimal ret = new BigDecimal(n).setScale(2).divide(new BigDecimal(this.multiplier));
		return new DecimalFormat("#,##0.##").format(ret) + this.unit;
	}
	
	public static String toSuitableString(long n) {
		SizeUnit unit = B;
		for (SizeUnit u : SizeUnit.values()) {
			if (n >= u.multiplier) {
				unit = u;
			} else {
				break;
			}
		}
		return unit.toString(n);
	}
}


package jp.co.flect.util;

import java.math.BigDecimal;

/**
 * 数値を合計するクラス
 */ 
public class Totalizer {
	
	private static final int MODE_LONG       = 1;
	private static final int MODE_DOUBLE     = 2;
	private static final int MODE_BIGDECIMAL = 3;
	
	private int mode;
	
	private long lTotal;
	private double dTotal;
	private BigDecimal bdTotal;
	
	public <T extends Number> Totalizer(Class<T> clazz) {
		Class c = clazz;
		if (clazz == Integer.class || clazz == Long.class || clazz == Byte.class || clazz == Short.class) {
			mode = MODE_LONG;
			lTotal = 0L;
		} else if (clazz == Double.class || clazz == Float.class) {
			mode = MODE_DOUBLE;
			dTotal = 0.0;
		} else {
			mode = MODE_BIGDECIMAL;
			bdTotal = new BigDecimal(0);
		}
		
	}
	public void add(Number n) {
		switch (mode) {
			case MODE_LONG:
				lTotal += n.longValue();
				break;
			case MODE_DOUBLE:
				dTotal += n.doubleValue();
				break;
			case MODE_BIGDECIMAL:
				bdTotal.add(new BigDecimal(n.toString()));
				break;
			default:
				throw new IllegalStateException();
		}
	}
	
	public Number getTotal() {
		switch (mode) {
			case MODE_LONG:
				return new Long(lTotal);
			case MODE_DOUBLE:
				return new Double(dTotal);
			case MODE_BIGDECIMAL:
				return bdTotal;
			default:
				throw new IllegalStateException();
		}
	}
	
}

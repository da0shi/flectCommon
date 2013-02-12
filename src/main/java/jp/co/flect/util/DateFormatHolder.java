package jp.co.flect.util;

import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import java.text.SimpleDateFormat;
import java.text.DateFormat;

/**
 * 日時、日付のみ、時刻のみ、それぞれのDateFormatを保持するHolderクラス
 */
public class DateFormatHolder {
	
	public static final String DEFAULT_DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
	public static final String DEFAULT_DATE_FORMAT = "yyyy/MM/dd";
	public static final String DEFAULT_TIME_FORMAT = "HH:mm:ss.SSS";
	
	private TimeZone timezone = TimeZone.getDefault();
	private Map<String, SimpleDateFormat> formatMap = new HashMap<String, SimpleDateFormat>();
	private int timeDifference = 0;
	
	/** 日付時刻型の書式文字列を返します */
	public String getDatetimeString() {
		return getString(DEFAULT_DATETIME_FORMAT);
	}
	
	/** 日付型の書式文字列を返します */
	public String getDateString() {
		return getString(DEFAULT_DATE_FORMAT);
	}
	
	/** 時刻型の書式文字列を返します */
	public String getTimeString() {
		return getString(DEFAULT_TIME_FORMAT);
	}
	
	/** 日付時刻型の書式文字列を設定します */
	public void setDatetimeString(String s) {
		setString(DEFAULT_DATETIME_FORMAT, s);
	}
	
	/** 日付型の書式文字列を設定します */
	public void setDateString(String s) {
		setString(DEFAULT_DATE_FORMAT, s);
	}
	
	/** 時刻型の書式文字列を設定します */
	public void setTimeString(String s) {
		setString(DEFAULT_TIME_FORMAT, s);
	}
	
	private String getString(String defaultFormat) {
		SimpleDateFormat fmt = this.formatMap.get(defaultFormat);
		return fmt == null ? defaultFormat : fmt.toPattern();
	}
	
	private void setString(String defaultFormat, String pattern) {
		this.formatMap.put(defaultFormat, new SimpleDateFormat(pattern));
	}
	
	/** 日付時刻型の書式を返します */
	public DateFormat getDatetimeFormat() {
		return getFormat(DEFAULT_DATETIME_FORMAT);
	}
	
	/** 日付型の書式を返します */
	public DateFormat getDateFormat() {
		return getFormat(DEFAULT_DATE_FORMAT);
	}
	
	/** 時刻型の書式を返します */
	public DateFormat getTimeFormat() {
		return getFormat(DEFAULT_TIME_FORMAT);
	}
	
	private SimpleDateFormat getFormat(String defaultFormat) {
		SimpleDateFormat fmt = this.formatMap.get(defaultFormat);
		if (fmt == null) {
			fmt = new SimpleDateFormat(defaultFormat);
			fmt.setTimeZone(this.timezone);
			this.formatMap.put(defaultFormat, fmt);
		}
		return fmt;
	}
	
	/* 出力書式のタイムゾーンを返します */
	public TimeZone getTimeZone() { return this.timezone;}
	/* 出力書式のタイムゾーンを設定します */
	public void setTimeZone(TimeZone t) { 
		this.timezone = t;
		for (SimpleDateFormat fmt : this.formatMap.values()) {
			fmt.setTimeZone(t);
		}
	}
	
	/** DBの日付／時刻型のフィールド値と出力したい時刻の時差をミリ秒単位で返します。 */
	public int getTimeDifference() { return timeDifference;}
	/** DBの日付／時刻型のフィールド値と出力したい時刻の時差を指定のTimeUnitで返します。 */
	public int getTimeDifference(TimeUnit unit) {
		return (int)unit.convert(this.timeDifference, TimeUnit.MILLISECONDS);
	}
	
	/** DBの日付／時刻型のフィールド値と出力したい時刻の時差をミリ秒単位で設定します。 */
	public void setTimeDifference(int n) { this.timeDifference = n;}
	/** DBの日付／時刻型のフィールド値と出力したい時刻の時差を指定のTimeUnitで設定します。 */
	public void setTimeDifference(int n, TimeUnit unit) { 
		this.timeDifference = (int)TimeUnit.MILLISECONDS.convert(n, unit);
	}
}

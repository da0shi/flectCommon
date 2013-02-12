package jp.co.flect.util;

import jp.co.flect.log.Logger;
import jp.co.flect.log.LoggerFactory;
import java.text.MessageFormat;

/**
 * スレッド毎の処理時間を監視するクラス
 */
public class ThreadWatcher {
	
	private static Logger LOG = LoggerFactory.getLogger(ThreadWatcher.class);
	private static boolean ENABLED = false;
	
	public static Logger getLogger() { return LOG;}
	public static void setLogger(Logger log) { LOG = log;}
	
	public static boolean isEnabled() { return ENABLED;}
	public static void setEnabled(boolean b) { ENABLED = b;}
	
	private static final ThreadLocal<ThreadWatcher> CACHE = new ThreadLocal<ThreadWatcher>() {
		
		@Override
		protected ThreadWatcher initialValue() {
			return new ThreadWatcher(LOG);
		}
	};
	
	public static void reset(Object msg, Object... params) {
		if (ENABLED) {
			ThreadWatcher t = CACHE.get();
			t._reset(msg, params);
		}
	}
	
	public static void add(Object msg, Object... params) {
		if (ENABLED) {
			ThreadWatcher t = CACHE.get();
			t._add(msg, params);
		}
	}
	
	public static void out(Object msg, Object... params) {
		if (ENABLED) {
			ThreadWatcher t = CACHE.get();
			t._out(msg, params);
		}
	}
	
	private Logger log;
	private long startTime;
	private long baseTime;
	StringBuilder msgBuf = new StringBuilder();
	
	private ThreadWatcher(Logger log) {
		this.log = log;
	}
	
	private void _reset(Object msg, Object... params) {
		this.msgBuf.setLength(0);
		this.startTime = System.currentTimeMillis();
		this.baseTime = this.startTime;
		_add(msg, 0, 0, params);
	}
	
	private void _add(Object msg, Object... params) {
		long t = System.currentTimeMillis() - this.baseTime;
		_add(msg, t, 0, params);
	}
	
	private void _add(Object msg, long time, long total, Object... params) {
		if (params.length > 0) {
			msg = MessageFormat.format(msg.toString(), params);
		}
		if (this.msgBuf.length() > 0) {
			this.msgBuf.append("\n    ");
		}
		this.msgBuf.append(msg);
		if (time != 0) {
			this.msgBuf.append(" (").append(time).append("ms)");
		}
		if (total > 0) {
			this.msgBuf.append(" Total: ").append(total).append("ms");
		}
	}
	
	private void _out(Object msg, Object... params) {
		long t = System.currentTimeMillis();
		_add(msg, t - this.baseTime, t - this.startTime, params);
		log.info(this.msgBuf);
		this.msgBuf.setLength(0);
	}
}

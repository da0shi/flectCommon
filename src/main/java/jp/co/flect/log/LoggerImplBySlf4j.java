package jp.co.flect.log;

import java.text.MessageFormat;

public class LoggerImplBySlf4j implements Logger {
	
	private static final org.slf4j.Marker MARKER_FATAL = org.slf4j.MarkerFactory.getMarker("FATAL");
    
	/*
	static org.apache.log4j.Level convertLevel(Level l) {
		switch(l.getIndex()) {
			case Level.IDX_TRACE: return org.apache.log4j.Level.TRACE;
			case Level.IDX_DEBUG: return org.apache.log4j.Level.DEBUG;
			case Level.IDX_INFO:  return org.apache.log4j.Level.INFO;
			case Level.IDX_WARN:  return org.apache.log4j.Level.WARN;
			case Level.IDX_ERROR: return org.apache.log4j.Level.ERROR;
			case Level.IDX_FATAL: return org.apache.log4j.Level.FATAL;
		}
		throw new IllegalStateException();
	}
	*/
	
	private org.slf4j.Logger log;
	
	public LoggerImplBySlf4j(org.slf4j.Logger log) {
		this.log = log;
		debug("Logger class: " + log.getClass());
	}
	
	public boolean isDebugEnabled() { return this.log.isDebugEnabled();};
	public boolean isErrorEnabled() { return this.log.isErrorEnabled();};
	public boolean isFatalEnabled() { return true;};
	public boolean isInfoEnabled() { return this.log.isInfoEnabled();};
	public boolean isTraceEnabled() { return this.log.isTraceEnabled();};
	public boolean isWarnEnabled() { return this.log.isWarnEnabled();};
	
	public void trace(Object message) { this.log.trace(message.toString());}
	public void trace(Object message, Throwable t) { this.log.trace(message.toString(), t);}
	public void trace(Throwable t) { this.log.trace(t.toString(), t);}
	public void debug(Object message) { this.log.debug(message.toString());}
	public void debug(Object message, Throwable t) { this.log.debug(message.toString(), t);}
	public void debug(Throwable t) { this.log.debug(t.toString(), t);}
	public void info(Object message) { this.log.info(message.toString());}
	public void info(Object message, Throwable t) { this.log.info(message.toString(), t);}
	public void info(Throwable t) { this.log.info(t.toString(), t);}
	public void warn(Object message) { this.log.warn(message.toString());}
	public void warn(Object message, Throwable t) { this.log.warn(message.toString(), t);}
	public void warn(Throwable t) { this.log.warn(t.toString(), t);}
	public void error(Object message) { this.log.error(message.toString());}
	public void error(Object message, Throwable t) { this.log.error(message.toString(), t);}
	public void error(Throwable t) { this.log.error(t.toString(), t);}
	public void fatal(Object message) { this.log.error(MARKER_FATAL, message.toString());}
	public void fatal(Object message, Throwable t) { this.log.error(MARKER_FATAL, message.toString(), t);}
	public void fatal(Throwable t) { this.log.error(MARKER_FATAL, t.toString(), t);}

	public void trace(Object message, Object... args) {
		if (isTraceEnabled()) {
			message = format(message, args);
			trace(message);
		}
	}
	
	public void debug(Object message, Object... args) {
		if (isDebugEnabled()) {
			message = format(message, args);
			debug(message);
		}
	}
	
	public void info(Object message, Object... args) {
		if (isInfoEnabled()) {
			message = format(message, args);
			info(message);
		}
	}
	
	public void warn(Object message, Object... args) {
		if (isWarnEnabled()) {
			message = format(message, args);
			warn(message);
		}
	}
	
	public void error(Object message, Object... args) {
		if (isErrorEnabled()) {
			message = format(message, args);
			error(message);
		}
	}
	
	public void fatal(Object message, Object... args) {
		if (isFatalEnabled()) {
			message = format(message, args);
			fatal(message);
		}
	}
	
	private String format(Object message, Object[] args) {
		if (args == null || args.length == 0) {
			return message.toString();
		}
		return MessageFormat.format(message.toString(), args);
	}
	
	public Level getLevel() { 
		throw new UnsupportedOperationException();
	}
	public void setLevel(Level l) { 
		throw new UnsupportedOperationException();
		/*
		this.log.setLevel(convertLevel(l));
		*/
	}
}

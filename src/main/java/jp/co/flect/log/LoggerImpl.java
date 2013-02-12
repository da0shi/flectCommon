package jp.co.flect.log;

import org.apache.log4j.Level;
import java.text.MessageFormat;

public class LoggerImpl implements Logger {
	
	private org.apache.log4j.Logger log;
	
	public LoggerImpl(org.apache.log4j.Logger log) {
		this.log = log;
	}
	
	public boolean isDebugEnabled() { return this.log.isEnabledFor(Level.DEBUG);};
	public boolean isErrorEnabled() { return this.log.isEnabledFor(Level.ERROR);};
	public boolean isFatalEnabled() { return this.log.isEnabledFor(Level.FATAL);};
	public boolean isInfoEnabled() { return this.log.isEnabledFor(Level.INFO);};
	public boolean isTraceEnabled() { return this.log.isEnabledFor(Level.TRACE);};
	public boolean isWarnEnabled() { return this.log.isEnabledFor(Level.WARN);};
	
	public void trace(Object message) { this.log.trace(message);}
	public void trace(Object message, Throwable t) { this.log.trace(message, t);}
	public void trace(Throwable t) { this.log.trace(t.toString(), t);}
	public void debug(Object message) { this.log.debug(message);}
	public void debug(Object message, Throwable t) { this.log.debug(message, t);}
	public void debug(Throwable t) { this.log.debug(t.toString(), t);}
	public void info(Object message) { this.log.info(message);}
	public void info(Object message, Throwable t) { this.log.info(message, t);}
	public void info(Throwable t) { this.log.info(t.toString(), t);}
	public void warn(Object message) { this.log.warn(message);}
	public void warn(Object message, Throwable t) { this.log.warn(message, t);}
	public void warn(Throwable t) { this.log.warn(t.toString(), t);}
	public void error(Object message) { this.log.error(message);}
	public void error(Object message, Throwable t) { this.log.error(message, t);}
	public void error(Throwable t) { this.log.error(t.toString(), t);}
	public void fatal(Object message) { this.log.fatal(message);}
	public void fatal(Object message, Throwable t) { this.log.fatal(message, t);}
	public void fatal(Throwable t) { this.log.fatal(t.toString(), t);}

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
	
	public Level getLevel() { return this.log.getLevel();}
	public void setLevel(Level l) { this.log.setLevel(l);}
}

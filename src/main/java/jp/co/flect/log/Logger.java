package jp.co.flect.log;

public interface Logger {

	public static final Level LEVEL_TRACE = Level.TRACE;
	public static final Level LEVEL_DEBUG = Level.DEBUG;
	public static final Level LEVEL_INFO  = Level.INFO;
	public static final Level LEVEL_WARN  = Level.WARN;
	public static final Level LEVEL_ERROR = Level.ERROR; 
	public static final Level LEVEL_FATAL = Level.FATAL;
	
	public void trace(Object message, Object... args);
	public void debug(Object message, Object... args);
	public void info(Object message, Object... args);
	public void warn(Object message, Object... args);
	public void error(Object message, Object... args);
	public void fatal(Object message, Object... args);

	public void trace(Throwable t);
	public void debug(Throwable t);
	public void info(Throwable t);
	public void warn(Throwable t);
	public void error(Throwable t);
	public void fatal(Throwable t);
	
	public Level getLevel();
	public void setLevel(Level l);
	
	//Copy from commons-logging
	public void debug(Object message);
	public void debug(Object message, Throwable t);
	public void error(Object message);
	public void error(Object message, Throwable t);
	public void fatal(Object message);
	public void fatal(Object message, Throwable t);
	public void info(Object message);
	public void info(Object message, Throwable t);
	public boolean isDebugEnabled();
	public boolean isErrorEnabled();
	public boolean isFatalEnabled();
	public boolean isInfoEnabled();
	public boolean isTraceEnabled();
	public boolean isWarnEnabled();
	public void trace(Object message);
	public void trace(Object message, Throwable t);
	public void warn(Object message);
	public void warn(Object message, Throwable t);
	
}

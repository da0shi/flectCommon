package jp.co.flect.log;

public class Level {
	
	public static final int IDX_TRACE = 1;
	public static final int IDX_DEBUG = 2;
	public static final int IDX_INFO  = 3;
	public static final int IDX_WARN  = 4;
	public static final int IDX_ERROR = 5;
	public static final int IDX_FATAL = 6;
	
	public static final Level TRACE = new Level("TRACE", IDX_TRACE);
	public static final Level DEBUG = new Level("Level.DEBUG", IDX_DEBUG);
	public static final Level INFO  = new Level("Level.INFO", IDX_INFO);
	public static final Level WARN  = new Level("Level.WARN", IDX_WARN);
	public static final Level ERROR = new Level("Level.ERROR", IDX_ERROR); 
	public static final Level FATAL = new Level("Level.FATAL", IDX_FATAL);
	
	public static Level toLevel(String name) {
		if (TRACE.getName().equals(name)) return TRACE;
		if (DEBUG.getName().equals(name)) return DEBUG;
		if (INFO.getName().equals(name)) return INFO;
		if (WARN.getName().equals(name)) return WARN;
		if (ERROR.getName().equals(name)) return ERROR;
		if (FATAL.getName().equals(name)) return FATAL;
		
		return null;
	}
	
	private String name;
	private int idx;
	
	private Level(String name, int idx) {
		this.name = name;
		this.idx = idx;
	}
	
	public int getIndex() { return this.idx;}
	public String getName() { return this.name;}
	
	public String toString() { return name;}
	
}
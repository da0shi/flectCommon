package jp.co.flect.log;

import java.net.URL;
import java.util.Properties;
import org.w3c.dom.Element;

public abstract class LoggerFactory {
	
	private static final LoggerFactory impl;
	static {
		LoggerFactory ret = null;
		String factoryClass = System.getProperty("jp.co.flect.log.LoggerFactory");
		if (factoryClass != null) {
			try {
				Class c = Class.forName(factoryClass);
				ret = (LoggerFactory)c.newInstance();
			} catch (Exception e) {
			}
		}
		if (ret == null) {
			try {
				Class.forName("org.apache.log4j.LogManager");
				ret = new LoggerFactoryByLog4j();
			} catch (Exception e) {
			}
		}
		if (ret == null) {
			try {
				Class.forName("org.slf4j.LoggerFactory");
				ret = new LoggerFactoryBySlf4j();
			} catch (Exception e) {
			}
		}
		if (ret == null) {
			throw new IllegalStateException("Can not find any logger libraries.");
		}
		impl = ret;
	}
	
	public static LoggerFactory getInstance() { return impl;}
	
	public static Logger getLogger(String name) {
		return impl.doGetLogger(name);
	}
	
	public static Logger getLogger(Class clazz) {
		return impl.doGetLogger(clazz);
	}
	
	public static void configure() {
		impl.doConfigure();
	}
	
	public static void configure(URL url) {
		impl.doConfigure(url, false);
	}
	
	public static void configure(URL url, boolean xml) {
		impl.doConfigure(url, xml);
	}
	
	public static void configure(Properties props) {
		impl.doConfigure(props);
	}
	
	public static void configure(Element el) {
		impl.doConfigure(el);
	}
	
	public static void setRootLevel(Level l) {
		impl.doSetRootLevel(l);
	}
	
	public abstract Logger doGetLogger(String name);
	public abstract Logger doGetLogger(Class clazz);
	public abstract void doConfigure();
	public abstract void doConfigure(URL url);
	public abstract void doConfigure(URL url, boolean xml);
	public abstract void doConfigure(Properties props);
	public abstract void doConfigure(Element el);
	public abstract void doSetRootLevel(Level l);
}

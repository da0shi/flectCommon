package jp.co.flect.log;

import java.net.URL;
import java.util.Properties;
import org.w3c.dom.Element;

import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.xml.DOMConfigurator;
import org.apache.log4j.LogManager;
import org.apache.log4j.Level;
import org.apache.log4j.helpers.Loader;

public class LoggerFactory {
	
	static {
		URL url1 = Loader.getResource("log4j.xml");
		URL url2 = Loader.getResource("log4j.properties");
		if (url1 == null && url2 == null) {
			configure();
		}
	}
	
	public static Logger getLogger(String name) {
		return new LoggerImpl(LogManager.getLogger(name));
	}
	
	public static Logger getLogger(Class clazz) {
		return new LoggerImpl(LogManager.getLogger(clazz));
	}
	
	public static void configure() {
		String name = LoggerFactory.class.getName();
		name = name.replace('.', '/');
		name = name.substring(0, name.lastIndexOf('/') + 1) + "log4j.properties";
		URL url = LoggerFactory.class.getClassLoader().getResource(name);
		configure(url);
	}
	
	public static void configure(URL url) {
		configure(url, false);
	}
	
	public static void configure(URL url, boolean xml) {
		if (xml) {
			DOMConfigurator.configure(url);
		} else {
			PropertyConfigurator.configure(url);
		}
	}
	
	public static void configure(Properties props) {
		PropertyConfigurator.configure(props);
	}
	
	public static void configure(Element el) {
		DOMConfigurator.configure(el);
	}
	
	public static void setRootLevel(Level l) {
		LogManager.getRootLogger().setLevel(l);
	}
}

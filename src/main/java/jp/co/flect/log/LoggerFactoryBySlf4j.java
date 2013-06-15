package jp.co.flect.log;

import java.net.URL;
import java.util.Properties;
import org.w3c.dom.Element;

public class LoggerFactoryBySlf4j extends LoggerFactory {
	
	static {
		/*
		URL url1 = Loader.getResource("log4j.xml");
		URL url2 = Loader.getResource("log4j.properties");
		if (url1 == null && url2 == null) {
			new LoggerFactoryByLog4j().doConfigure();
		}
		*/
	}
	
	LoggerFactoryBySlf4j() {}
	
	@Override
	public Logger doGetLogger(String name) {
		return new LoggerImplBySlf4j(org.slf4j.LoggerFactory.getLogger(name));
	}
	
	@Override
	public Logger doGetLogger(Class clazz) {
		return new LoggerImplBySlf4j(org.slf4j.LoggerFactory.getLogger(clazz));
	}
	
	@Override
	public void doConfigure() {
		/*
		String name = LoggerFactoryByLog4j.class.getName();
		name = name.replace('.', '/');
		name = name.substring(0, name.lastIndexOf('/') + 1) + "log4j.properties";
		URL url = LoggerFactoryByLog4j.class.getClassLoader().getResource(name);
		doConfigure(url);
		*/
	}
	
	@Override
	public void doConfigure(URL url) {
		doConfigure(url, false);
	}
	
	@Override
	public void doConfigure(URL url, boolean xml) {
		/*
		if (xml) {
			DOMConfigurator.configure(url);
		} else {
			PropertyConfigurator.configure(url);
		}
		*/
	}
	
	@Override
	public void doConfigure(Properties props) {
		/*
		PropertyConfigurator.configure(props);
		*/
	}
	
	@Override
	public void doConfigure(Element el) {
		/*
		DOMConfigurator.configure(el);
		*/
	}
	
	@Override
	public void doSetRootLevel(Level l) {
		/*
		LogManager.getRootLogger().setLevel(LoggerImpl.convertLevel(l));
		*/
	}
	
}

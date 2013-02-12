package jp.co.flect.xml;

import java.io.FileNotFoundException;
import java.io.StringReader;
import java.io.Reader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamException;

/**
 * StAXでオブジェクトを構築するクラス
 */
public class StAXConstructor<T extends StAXConstruct> {
	
	private Map<QName, T> map = new HashMap<QName, T>();
	private boolean stopAtOnce;
	
	/**
	 * コンストラクタ
	 */
	public StAXConstructor() {
	}
	
	/**
	 * コンストラクタ + add(qname, obj);
	 */
	public StAXConstructor(QName qname, T obj) {
		add(qname, obj);
	}
	
	/**
	 * 対象のオブジェクトを一度構築したらその時点でパースを中止する場合はtrueを返します。
	 */
	public boolean isStopAtOnce() { return this.stopAtOnce;}
	/**
	 * 対象のオブジェクトを一度構築したらその時点でパースを中止する場合はtrueを設定します。
	 */
	public void setStopAtOnce(boolean b) { this.stopAtOnce = b;}
	
	/**
	 * 構築対象のオブジェクトとその要素名を追加します。
	 */
	public void add(QName qname, T obj) {
		this.map.put(qname, obj);
	}
	
	/**
	 * XML文字列をパースしてオブジェクトを構築します。
	 */
	public List<T> build(String str) throws XMLStreamException, StAXConstructException {
		return build(new StringReader(str));
	}
	
	/**
	 * Readerをパースしてオブジェクトを構築します。
	 */
	public List<T> build(Reader reader) throws XMLStreamException, StAXConstructException {
		XMLInputFactory factory = XMLInputFactory.newInstance();
		factory.setProperty(XMLInputFactory. IS_COALESCING, Boolean.TRUE);
		return build(factory.createXMLStreamReader(reader));
	}
	
	/**
	 * ファイルをパースしてオブジェクトを構築します。
	 */
	public List<T> build(File file) throws XMLStreamException, FileNotFoundException, StAXConstructException {
		return build(new FileInputStream(file));
	}
	
	/**
	 * InputStreamをパースしてオブジェクトを構築します。
	 */
	public List<T> build(InputStream is) throws XMLStreamException, StAXConstructException {
		XMLInputFactory factory = XMLInputFactory.newInstance();
		factory.setProperty(XMLInputFactory. IS_COALESCING, Boolean.TRUE);
		return build(factory.createXMLStreamReader(is));
	}
	
	private List<T> build(XMLStreamReader reader) throws XMLStreamException, StAXConstructException {
		List<T> list = new ArrayList<T>();
		while (reader.hasNext()) {
			int event = reader.next();
			if (event == XMLStreamReader.START_ELEMENT) {
				QName qname = reader.getName();
				T obj = this.stopAtOnce ? this.map.remove(qname) : this.map.get(qname);
				if (obj != null) {
					obj = (T)obj.newInstance();
					obj.build(reader);
					list.add(obj);
					if (this.map.size() == 0) {
						reader.close();
						break;
					}
				}
			} else if (event == XMLStreamReader.END_DOCUMENT) {
				reader.close();
				break;
			}
		}
		return list;
	}
	
}

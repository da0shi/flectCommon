package jp.co.flect.xml;

import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamException;

/**
 * Staxによって構築されるオブジェクトのインターフェース
 */
public interface StAXConstruct<T extends StAXConstruct> {
	
	/**
	 * StAXのReaderから自分自身の内容を構築します。
	 */
	public void build(XMLStreamReader reader) throws XMLStreamException, StAXConstructException;
	
	/**
	 * 新しいインスタンスを返します。
	 */
	public T newInstance();
	
}

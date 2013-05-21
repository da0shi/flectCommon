package jp.co.flect.xml;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.BufferedWriter;
import java.io.Writer;
import java.io.UnsupportedEncodingException;
import jp.co.flect.xml.XMLUtils;
import java.util.Map;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;
import org.w3c.dom.Node;
import org.w3c.dom.Comment;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Attr;
import org.w3c.dom.NamedNodeMap;

/**
 * 簡易XMLWriter。厳密な出力が必要な場合は使用不可
 * 制限事項(実装予定なし)
 * - DoctypeとEntityReference
 * - CharacterReference
 * - 属性の出力順序不定
 */
public class XMLWriter {
	
	private Writer writer;
	private String space;
	private String encoding;
	private boolean emptyTag = true;
	
	private int level = 0;
	
	public XMLWriter(OutputStream os) {
		this(os, "utf-8", 0);
	}
	
	public XMLWriter(OutputStream os, String encoding) {
		this(os, encoding, 0);
	}
	
	public XMLWriter(OutputStream os, String encoding, int indent) {
		try {
			this.writer = new BufferedWriter(new OutputStreamWriter(os, encoding));
		} catch (UnsupportedEncodingException e) {
			throw new IllegalArgumentException(encoding, e);
		}
		this.encoding = encoding;
		setIndent(indent);
	}
	
	public XMLWriter(Writer writer) {
		this(writer, "utf-8", 0);
	}
	
	public XMLWriter(Writer writer, String encoding) {
		this(writer, encoding, 0);
	}
	
	public XMLWriter(Writer writer, String encoding, int indent) {
		this.writer = writer;
		this.encoding = encoding;
		setIndent(indent);
	}
	
	public int getIndent() { return space == null ? 0 : space.length();}
	public void setIndent(int n) {
		if (n > 0) {
			StringBuilder buf = new StringBuilder(n);
			for (int i=0; i<n; i++) {
				buf.append(" ");
			}
			this.space = buf.toString();
		} else {
			this.space = null;
		}
	}
		
	public int getIndentLevel() { return this.level;}
	public void setIndentLevel(int n) { this.level = n;}
	
	public boolean isUseEmptyTag() { return this.emptyTag;}
	public void setUseEmptyTag(boolean b) { this.emptyTag = b;}
	
	public void write(Document doc) throws IOException {
		xmlDecl();
		indent(false);
		write(doc.getDocumentElement());
	}
	
	public void write(Element el) throws IOException {
		openElement(el.getNodeName());
		NamedNodeMap attrs = el.getAttributes();
		if (attrs != null) {
			for (int i=0; i<attrs.getLength(); i++) {
				Attr a = (Attr)attrs.item(i);
				attr(a.getName(), a.getValue());
			}
		}
		if (el.getFirstChild() == null) {
			if (this.emptyTag) {
				emptyTag();
			} else {
				endTag();
				endElement(el.getNodeName());
			}
			return;
		}
		endTag();
		if (isElementOnly(el)) {
			Node node = el.getFirstChild();
			boolean first = true;
			while (node != null) {
				indent(first);
				first = false;
				write((Element)node);
				node = node.getNextSibling();
			}
			unindent();
		} else {
			Node node = el.getFirstChild();
			while (node != null) {
				switch (node.getNodeType()) {
					case Node.ELEMENT_NODE:
						write((Element)node);
						break;
					case Node.TEXT_NODE:
						write((Text)node);
						break;
					case Node.CDATA_SECTION_NODE:
						write((CDATASection)node);
						break;
					case Node.COMMENT_NODE:
						write((Comment)node);
						break;
					case Node.PROCESSING_INSTRUCTION_NODE: 
						write((ProcessingInstruction)node);
						break;
					case Node.DOCUMENT_TYPE_NODE:
					case Node.ENTITY_REFERENCE_NODE:
					default:
						throw new UnsupportedOperationException(node.toString());
				}
				node = node.getNextSibling();
			}
		}
		endElement(el.getNodeName());
	}
	
	public void write(Text node) throws IOException {
		content(node.getNodeValue());
	}
	
	public void write(CDATASection node) throws IOException {
		startCDATASection();
		write(node.getNodeValue());
		endCDATASection();
	}
	
	public void write(Comment node) throws IOException {
		startComment();
		write(node.getNodeValue());
		endComment();
	}
	
	public void write(ProcessingInstruction node) throws IOException {
		startPI();
		write(node.getTarget());
		if (node.getData() != null) {
			write(" ");
			write(node.getData());
		}
		endPI();
	}
	
	private boolean isElementOnly(Element el) {
		Node node = el.getFirstChild();
		while (node != null) {
			if (node.getNodeType() != Node.ELEMENT_NODE) {
				return false;
			}
			node = node.getNextSibling();
		}
		return true;
	}
	
	public void indent(boolean inc) throws IOException {
		if (space != null) {
			write("\n");
			if (inc) {
				level++;
			}
			for (int i=0; i<level; i++) {
				write(space);
			}
		}
	}
	
	public void unindent() throws IOException {
		if (space != null) {
			level--;
			write("\n");
			for (int i=0; i<level; i++) {
				write(space);
			}
		}
	}
	
	public void writeln() throws IOException {
		write("\n");
	}
	
	public void write(String s) throws IOException {
		this.writer.write(s);
	}
	
	public void content(String s) throws IOException {
		write(escape(s, false));
	}
	
	public void attributeValue(String s) throws IOException {
		write(escape(s, true));
	}
	
	public String escape(String s, boolean attr) {
		StringBuilder buf = null;
		int i=0;
		int len = s.length();
		for (; i<len; i++) {
			char c = s.charAt(i);
			if (c == '&' || c == '<' || c == '>' || (c == '"' && attr)) {
				buf = new StringBuilder(len + 10);
				buf.append(s.substring(0, i));
				break;
			}
		}
		if (buf == null) {
			return s;
		}
		for (; i<len; i++) {
			char c = s.charAt(i);
			switch (c) {
				case '&':
					buf.append("&amp;");
					break;
				case '<':
					buf.append("&lt;");
					break;
				case '>':
					buf.append("&gt;");
					break;
				case '"':
					if (attr) {
						buf.append("&quot;");
						break;
					}
				default:
					buf.append(c);
					break;
			}
		}
		return buf.toString();
	}
	
	public void xmlDecl() throws IOException {
		write("<?xml version=\"1.0\" encoding=\"");
		write(encoding);
		write("\"?>");
	}
	
	public void openElement(String qname) throws IOException {
		write("<");
		write(qname);
	}
	
	public void endTag() throws IOException {
		write(">");
	}
	
	public void emptyTag() throws IOException {
		write("/>");
	}
	
	public void startCDATASection() throws IOException {
		write("<![CDATA[");
	}
	
	public void endCDATASection() throws IOException {
		write("]]>");
	}
	
	public void startComment() throws IOException {
		write("<!--");
	}
	
	public void endComment() throws IOException {
		write("-->");
	}
	
	public void startPI() throws IOException {
		write("<?");
	}
	
	public void endPI() throws IOException {
		write("?>");
	}
	
	public void endElement(String qname) throws IOException {
		write("</");
		write(qname);
		write(">");
	}
	
	public void attr(String name, String value) throws IOException {
		write(" ");
		write(name);
		write("=\"");
		attributeValue(value);
		write("\"");
	}
	
	public void flush() throws IOException {
		this.writer.flush();
	}
	
	public void close() throws IOException {
		this.writer.close();
	}
	
}
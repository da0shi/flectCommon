package jp.co.flect.xml;

import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class NodeIterator implements Iterator<Node> {
	
	private Element initialNode;
	private boolean elementOnly;
	private Node nextNode;
	
	public NodeIterator(Element el) {
		this(el, true);
	}
	
	public NodeIterator(Element el, boolean elementOnly) {
		this.initialNode = el;
		this.elementOnly = elementOnly;
		this.nextNode = el;
	}
	
	public boolean isElementOnly() { return this.elementOnly;}
	
	private Node searchNext(Node node, boolean deep) {
		if (deep) {
			Node child = node.getFirstChild();
			while (child != null) {
				if (!this.elementOnly) {
					return child;
				}
				if (child.getNodeType() == Node.ELEMENT_NODE) {
					return child;
				}
				child = child.getNextSibling();
			}
		}
		if (node == initialNode) {
			return null;
		}
		Node next = node.getNextSibling();
		while (next != null) {
			if (!this.elementOnly) {
				return next;
			}
			if (next.getNodeType() == Node.ELEMENT_NODE) {
				return next;
			}
			next = next.getNextSibling();
		}
		Node parent = node.getParentNode();
		if (parent == initialNode) {
			return null;
		}
		return searchNext(parent, false);
	}
	
	public boolean hasNext() {
		return this.nextNode != null;
	}
	
	public Node next() {
		if (this.nextNode == null) {
			throw new NoSuchElementException();
		}
		Node ret = this.nextNode;
		this.nextNode = searchNext(ret, true);
		return ret;
	}
	
	public void remove() {
		throw new UnsupportedOperationException();
	}
}

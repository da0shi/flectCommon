package jp.co.flect.html;

import java.util.List;
import java.util.LinkedList;
import java.util.Collections;
import java.util.NoSuchElementException;

/**
 * パン屑リスト
 */
public class BreadCrumbs {
	
	private LinkedList<Crumb> list = new LinkedList<Crumb>();
	private String aTagClass = null;
	private String crumbHtml = "<span>?</span>";
	private String sepHtml = "<span> &gt; </span>";
	
	/**
	 * リストアイテムの追加
	 */
	public void push(String text, String url) {
		this.list.add(new Crumb(text, url));
	}
	
	/**
	 * 最後のリストアイテムを削除
	 */
	public Crumb pop() {
		if (this.list.size() == 0) {
			throw new NoSuchElementException();
		}
		return this.list.removeLast();
	}
	
	/**
	 * パン屑リストを取得
	 */
	public List<Crumb> getList() {
		return Collections.unmodifiableList(this.list);
	}
	
	
	/**
	 * パン屑リストのサイズを取得
	 */
	public int size() { return this.list.size();}
	
	/**
	 * デフォルトでAタグに付加するclass属性
	 */
	public String getATagClass() { return this.aTagClass;}
	/**
	 * デフォルトでAタグに付加するclass属性
	 */
	public void setATagClass(String s) { this.aTagClass = s;}
	
	/**
	 * パン屑部分のHTML。「?」がAタグに置き換わります。
	 */
	public String getCrumbHtml() { return this.crumbHtml;}
	
	/**
	 * パン屑部分のHTML。「?」がAタグに置き換わります。
	 */
	public void setCrumbHtml(String s) { this.crumbHtml = s;}
	
	/**
	 * セパレータ部分のHTML
	 */
	public String getSeparatorHtml() { return this.sepHtml;}
	
	/**
	 * セパレータ部分のHTML
	 */
	public void setSeparatorHtml(String s) { this.sepHtml = s;}
	
	/**
	 * パン屑リストのHTMLを返します。
	 */
	public String toString() {
		return toString(this.crumbHtml, this.sepHtml);
	}
	
	/**
	 * バン屑部分のHTMLを指定してHTMLを返します。
	 */
	public String toString(String crumb) {
		return toString(crumb, this.sepHtml);
	}
	
	/**
	 * バン屑部分とセパレータのHTMLを指定してHTMLを返します。
	 */
	public String toString(String crumb, String sep) {
		StringBuilder buf = new StringBuilder();
		boolean addSep = false;
		for (Crumb c : this.list) {
			if (addSep) {
				buf.append(sep);
			}
			buf.append(crumb.replace("?", buildATag(c)));
			addSep = true;
		}
		return buf.toString();
	}
	
	private String buildATag(Crumb c) {
		StringBuilder buf = new StringBuilder();
		buf.append("<a href=\"")
			.append(c.getUrl())
			.append("\"");
		if (this.aTagClass != null) {
			buf.append(" class=\"")
				.append(this.aTagClass)
				.append("\"");
		}
		buf.append(">")
			.append(HtmlUtils.escape(c.getText()))
			.append("</a>");
		return buf.toString();
	}
	
	public static class Crumb {
		
		private String text;
		private String url;
		
		public Crumb(String text, String url) {
			this.text = text;
			this.url = url;
		}
		
		public String getText() { return this.text;}
		public String getUrl() { return this.url;}
		
	}
	
}

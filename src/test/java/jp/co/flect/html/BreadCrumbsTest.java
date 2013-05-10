package jp.co.flect.html;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import org.junit.Test;
import org.junit.BeforeClass;

public class BreadCrumbsTest {
	
	@Test 
	public void simple() throws Exception {
		BreadCrumbs bc = new BreadCrumbs();
		bc.push("test1", "http://www.yahoo.co.jp/");
		bc.push("test2", "http://www.google.com/");
		bc.push("hoge'\"<>&fuga", "/hoge");
		
		String ret = bc.toString();
		assertEquals("<span><a href=\"http://www.yahoo.co.jp/\">test1</a></span><span> &gt; </span><span><a href=\"http://www.google.com/\">test2</a></span><span> &gt; </span><span><a href=\"/hoge\">hoge&apos;&quot;&lt;&gt;&amp;fuga</a></span>", ret);
	}
	
	@Test 
	public void aClass() throws Exception {
		BreadCrumbs bc = new BreadCrumbs();
		bc.push("test1", "http://www.yahoo.co.jp/");
		bc.push("test2", "http://www.google.com/");
		bc.push("hoge'\"<>&fuga", "/hoge");
		bc.setATagClass("hoge");
		
		String ret = bc.toString();
		assertEquals("<span><a href=\"http://www.yahoo.co.jp/\" class=\"hoge\">test1</a></span><span> &gt; </span><span><a href=\"http://www.google.com/\" class=\"hoge\">test2</a></span><span> &gt; </span><span><a href=\"/hoge\" class=\"hoge\">hoge&apos;&quot;&lt;&gt;&amp;fuga</a></span>", bc.toString());
	}
	
	@Test 
	public void custom() throws Exception {
		BreadCrumbs bc = new BreadCrumbs();
		bc.push("test1", "http://www.yahoo.co.jp/");
		bc.push("test2", "http://www.google.com/");
		bc.push("hoge'\"<>&fuga", "/hoge");
		bc.setATagClass("hoge");
		
		String ret = bc.toString("<b>?</b>");
		assertEquals("<b><a href=\"http://www.yahoo.co.jp/\" class=\"hoge\">test1</a></b><span> &gt; </span><b><a href=\"http://www.google.com/\" class=\"hoge\">test2</a></b><span> &gt; </span><b><a href=\"/hoge\" class=\"hoge\">hoge&apos;&quot;&lt;&gt;&amp;fuga</a></b>", ret);
	}
}

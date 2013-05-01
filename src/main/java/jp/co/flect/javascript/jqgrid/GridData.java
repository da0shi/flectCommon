package jp.co.flect.javascript.jqgrid;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;

import com.google.gson.Gson;

/**
 * jqgridに表示するためのJSONデータを生成するラッパークラス<br>
 * GsonでJSON化することができる
 */
public class GridData {
	
	private transient int rowCnt;
	private transient int idIndex;
	private transient String idPrefix;
	
	private int start;
	private int total;
	private int page;
	private int records;
	private List<RowData> rows = new ArrayList<RowData>();
	private Map<String, Object> userdata = null;
	
	public GridData(int rowCnt, int page) {
		this.rowCnt = rowCnt;
		this.page = page;
		this.start = (page - 1) * rowCnt + 1;
		this.idIndex = this.start;
	}
	
	public int getRecordCount() { return this.records;}
	public void setRecordCount(int n) { 
		this.records = n;
		this.total = n / this.rowCnt;
		if (n % this.rowCnt != 0) {
			this.total++;
		}
	}
	
	public void setRecordCountAndTotal(int r, int t) {
		this.records = r;
		this.total = t;
	}
	
	public int getTotalPage() { return this.total;}
	public int getPage() { return this.page;}
	public int getRowCount() { return this.rowCnt;}
	
	public int getStartIndex() { return this.start;}
	public void setStartIndex(int n) { 
		this.start = n;
		this.idIndex = n;
	}
	
	public String getIdPrefix() { return this.idPrefix;}
	public void setIdPrefix(String s) { this.idPrefix = s;}
	
	public List<RowData> getRows() { return this.rows;}
	
	public void addRow(Object... items) {
		addRow(Arrays.asList(items));
	}
	
	public void addRow(List<Object> items) {
		String id = Integer.toString(idIndex++);
		if (idPrefix != null) {
			id = idPrefix + id;
		}
		rows.add(new RowData(id, items));
	}
	
	public void setUserdata(String name, Object value) {
		if (userdata == null) {
			userdata = new HashMap<String, Object>();
		}
		userdata.put(name, value);
	}
	
	public Object getUserdata(String name) {
		return userdata == null ? null : userdata.get(name);
	}
	
	public Map<String, Object> getUserdata() {
		if (userdata == null) {
			userdata = new HashMap<String, Object>();
		}
		return userdata;
	}
		
	
	public String toJson() {
		return new Gson().toJson(this);
	}
	
	public static class RowData {
		
		private String id;
		private List<Object> cell;
		
		private RowData(String id, List<Object> cell) {
			this.id = id;
			this.cell = cell;
		}
		
		public String getId() { return this.id;}
		public List<Object> getCell() { return this.cell;}
	}
}   

package jp.co.flect.javascript.jqgrid;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;
import java.sql.Types;

import jp.co.flect.util.ExtendedMap;

import com.google.gson.Gson;

public class ColModel implements Serializable {
	
	private static final long serialVersionUID = 1559680925004472075L;
	
	public static final String ALIGN_LEFT    = "left";
	public static final String ALIGN_CENTER  = "center";
	public static final String ALIGN_RIGHT   = "right";
	
	public static final String FMT_INTEGER   = "integer";
	public static final String FMT_NUMBER    = "number";
	public static final String FMT_CHECKBOX  = "checkbox";
	public static final String FMT_CURRENCY  = "currency";
	public static final String FMT_DATE      = "date";
	public static final String FMT_EMAIL     = "email";
	public static final String FMT_LINK      = "link";
	public static final String FMT_SHOWLINK  = "showlink";
	public static final String FMT_SELECT    = "select";
	public static final String FMT_ACTIONS   = "actions";
	
	public static final String FMT_OPTION_THOUSANDS_SEPARATOR = "thousandsSeparator";
	public static final String FMT_OPTION_DEFAULT_VALUE = "defaultValue";
	public static final String FMT_OPTION_DECIMAL_PLACES = "decimalPlaces";
	
	private List<Column> colModel = new ArrayList<Column>();
	
	public List<Column> getList() { return this.colModel;}
	
	public Column add(String name) {
		Column col = new Column(name);
		this.colModel.add(col);
		return col;
	}
	
	public Column add(Column col) {
		this.colModel.add(col);
		return col;
	}
	
	public Column getColumn(int idx) { return this.colModel.get(idx);}
	public int getColumnCount() { return this.colModel.size();}
	
	public Column getColumn(String name) {
		for (Column col : this.colModel) {
			if (col.getName().equals(name)) {
				return col;
			}
		}
		return null;
	}
	
	public String toJson() {
		return new Gson().toJson(colModel);
	}
	
	public static class Column extends ExtendedMap {
		
		private static final long serialVersionUID = 3960199776120300168L;
		
		private transient int sqlType = Types.OTHER;
		
		public Column(String name) {
			put("name", name);
		}
		
		public String getName() { return (String)get("name");}
		
		public String getLabel() { return (String)get("label");}
		public void setLabel(String s) { put("label", s);}
		
		public String getAlign() { return (String)get("align");}
		public void setAlign(String s) { put("align", s);}
		
		public String getFormatter() { return (String)get("formatter");}
		public void setFormatter(String s) { put("formatter", s);}
		
		public Object getFormatOption(String name) {
			return getDeep("formatoptions." + name);
		}
		
		public void setFormatOption(String name, Object value) {
			putDeep("formatoptions." + name, value);
		}
		
		public String getWidth() { return (String)get("width");}
		public void setWidth(String s) { put("width", s);}
		public void setWidth(int n) { put("width", n + "px");}
		
		public boolean isSortable() { 
			Boolean b = (Boolean)get("sortable");
			return b != null ? b.booleanValue() : true;
		}
		
		public void setSortable(boolean b) { put("sortable", Boolean.valueOf(b));}
		
		public boolean isResizable() { 
			Boolean b = (Boolean)get("resizable");
			return b != null ? b.booleanValue() : true;
		}
		
		public void setResizable(boolean b) { put("resizable", Boolean.valueOf(b));}
		
		public boolean isHidden() { 
			Boolean b = (Boolean)get("hidden");
			return b != null ? b.booleanValue() : true;
		}
		
		public void setHidden(boolean b) { put("hidden", Boolean.valueOf(b));}
		
		public int getSqlType() { return this.sqlType;}
		public void setSqlType(int n) { this.sqlType = n;}
	}
}

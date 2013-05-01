package jp.co.flect.javascript.jqgrid;

import java.math.BigDecimal;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import jp.co.flect.javascript.jqgrid.ColModel.Column;

/**
 * jqGridのsearchモジュールのfiltersからWHERE句を生成する
 */
public class Filters {
	
	private ColModel model;
	
	public Filters(ColModel model) {
		this.model = model;
	}
	
	public Group parse(String jsonStr) {
		return new Gson().fromJson(jsonStr, Group.class);
	}
	
	public SQLInfo getSQL(String jsonStr) {
		return getSQL(parse(jsonStr));
	}
	
	public SQLInfo getSQL(Group group) {
		StringBuilder buf = new StringBuilder();
		List<Object> params = new ArrayList<Object>();
		buildSQL(group, buf, params);
		return new SQLInfo(buf.toString(), params);
	}
	
	private void buildSQL(Group group, StringBuilder buf, List<Object> params) {
		boolean first = true;
		if (group.rules != null) {
			for (Rule rule : group.rules) {
				if (!first) {
					buf.append(" ").append(group.groupOp).append(" ");
				}
				buildSQL(rule, buf, params);
				first = false;
			}
		}
		if (group.groups != null) {
			for (Group subGroup : group.groups) {
				if (!first) {
					buf.append(" ").append(group.groupOp).append(" ");
				}
				buf.append("(");
				buildSQL(subGroup, buf, params);
				buf.append(")");
				first = false;
			}
		}
	}
	
	private void buildSQL(Rule rule, StringBuilder buf, List<Object> params) {
		Column col = this.model.getColumn(rule.field);
		if (col == null) {
			throw new IllegalArgumentException(rule.field);
		}
		String name = col.getName();
		String op = getSqlOp(rule.op);
		String value = rule.data;
		if (rule.op.equals("bw") || rule.op.equals("bn")) {
			value += "%";
		} else if (rule.op.equals("ew") || rule.op.equals("en")) {
			value = "%" + value;
		} else if (rule.op.equals("cn") || rule.op.equals("nc")) {
			value = "%" + value + "%";
		}
		
		if (rule.op.equals("in") || rule.op.equals("ni")) {
			value = "(" + value + ")";
		} else if (rule.op.equals("nu") || rule.op.equals("nn")) {
			value = "";
		} else {
			params.add(convertValue(col, value));
			value = "?";
		}
		buf.append(name).append(" ")
			.append(op).append(" ")
			.append(value);
	}
	
	private static Object convertValue(Column col, String value) {
		switch (col.getSqlType()) {
			case Types.BIGINT:
				return Long.parseLong(value);
			case Types.INTEGER:
			case Types.SMALLINT:
			case Types.TINYINT:
			case Types.ROWID:
				return Integer.parseInt(value);
			case Types.DECIMAL:
			case Types.NUMERIC:
				return new BigDecimal(value);
			case Types.DOUBLE:
			case Types.REAL:
			case Types.FLOAT:
				return Double.parseDouble(value);
			case Types.BIT:
			case Types.BOOLEAN:
				return Boolean.valueOf(value);
			case Types.CHAR:
			case Types.LONGNVARCHAR:
			case Types.LONGVARCHAR:
			case Types.NCHAR:
			case Types.NVARCHAR:
			case Types.VARCHAR:
			case Types.CLOB:
			case Types.NCLOB:
			case Types.SQLXML:
				return value;
			case Types.DATE:
			case Types.TIME:
			case Types.TIMESTAMP:
				//ToDo
			default: 
				throw new IllegalArgumentException(Integer.toString(col.getSqlType()));
		}
	}
	
	private static String getSqlOp(String op) {
		if ("eq".equals(op)) return "=";
		if ("ne".equals(op)) return "<>";
		if ("lt".equals(op)) return "<";
		if ("le".equals(op)) return "<=";
		if ("gt".equals(op)) return ">";
		if ("ge".equals(op)) return ">=";
		if ("bw".equals(op)) return "LIKE";
		if ("bn".equals(op)) return "NOT LIKE";
		if ("in".equals(op)) return "IN";
		if ("ni".equals(op)) return "NOT IN";
		if ("ew".equals(op)) return "LIKE";
		if ("en".equals(op)) return "NOT LIKE";
		if ("cn".equals(op)) return "LIKE";
		if ("nc".equals(op)) return "NOT LIKE";
		if ("nu".equals(op)) return "IS NULL";
		if ("nn".equals(op)) return "IS NOT NULL";
		
		throw new IllegalArgumentException(op);
	}
	
	public static class Group {
		
		private String groupOp;
		private Rule[] rules;
		private Group[] groups;
		
		public String getGroupOp() { return this.groupOp;}
		public Rule[] getRules() { return this.rules;}
		public Group[] getGroups() { return this.groups;}
		
	}
	
	public static class Rule {
		
		private String field;
		private String op;
		private String data;
		
		public String getField() { return this.field;}
		public String getOp() { return this.op;}
		public String getData() { return this.data;}
	}
	
	public static class SQLInfo {
		
		private String sql;
		private List<Object> params;
		
		private SQLInfo(String sql, List<Object> params) {
			this.sql = sql;
			this.params = params;
		}
		
		public String getSQL() { return this.sql;}
		public List<Object> getParams() { return this.params;}
	}
}


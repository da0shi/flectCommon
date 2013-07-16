package jp.co.flect.javascript.jqgrid;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;

public class RdbColModelFactory {
	
	private Connection con;
	private boolean nameQualified;
	
	public RdbColModelFactory(Connection con) {
		this(con, false);
	}
	
	public RdbColModelFactory(Connection con, boolean nameQualified) {
		this.con = con;
		this.nameQualified = nameQualified;
	}
	
	public Connection getConnection() { return this.con;}
	public boolean isNameQualified() { return this.nameQualified;}
	
	public ColModel getTableModel(String table) throws SQLException {
		String query = "SELECT * FROM " + table + " WHERE 1 = 2";
		return getQueryModel(query);
	}
	
	public ColModel getQueryModel(String query) throws SQLException {
		PreparedStatement stmt0 = con.prepareStatement(query);
		try {
			ResultSetMetaData meta = stmt0.getMetaData();
			if (meta != null) {
				return getQueryModel(query, meta);
			}
		} finally {
			stmt0.close();
		}
		PreparedStatement stmt = con.prepareStatement("SELECT * FROM (" + query + ") TEMP WHERE 1=2");
		try {
			ResultSet rs = stmt.executeQuery();
			try {
				return getQueryModel(query, rs.getMetaData());
			} finally {
				rs.close();
			}
		} finally {
			stmt.close();
		}
	}
	
	public ColModel getQueryModel(ResultSetMetaData meta) throws SQLException {
		return getQueryModel(null, meta);
	}
	
	private ColModel getQueryModel(String query, ResultSetMetaData meta) throws SQLException {
		ColModel model = new ColModel();
		int len = meta.getColumnCount();
		Map<String, String> aliasMap = null;
		if (this.nameQualified && query != null) {
			AliasExtractor ext = new AliasExtractor(query);
			aliasMap = new HashMap<String, String>();
			for (int i=0; i<len; i++) {
				String tblName = meta.getTableName(i+1);
				if (!aliasMap.containsKey(tblName)) {
					String alias = ext.getTableAlias(tblName);
					if (alias == null) {
						alias = tblName;
					}
					aliasMap.put(tblName, alias);
				}
			}
		}
		for (int i=0; i<len; i++) {
			int idx = i+1;
			String name = meta.getColumnName(idx);
			if (this.nameQualified) {
				String tblName = meta.getTableName(idx);
				String alias = aliasMap == null ? tblName : aliasMap.get(tblName);
				name = alias + "." + name;
			}
			String label = meta.getColumnLabel(idx);
			int type = meta.getColumnType(idx);
			if (!isSupportedType(type)) {
				continue;
			}
			
			ColModel.Column col = model.add(name);
			if (label != null && !label.equals(name)) {
				col.setLabel(label);
			}
			col.setSqlType(type);
			String fmt = null;
			switch (type) {
				case Types.BIGINT:
				case Types.INTEGER:
				case Types.SMALLINT:
				case Types.TINYINT:
				case Types.ROWID:
					//integer
					fmt = ColModel.FMT_INTEGER;
					//break;
				case Types.DECIMAL:
				case Types.NUMERIC:
				case Types.DOUBLE:
				case Types.REAL:
				case Types.FLOAT:
					//number
					if (fmt == null) {
						fmt = ColModel.FMT_NUMBER;
					}
					col.setFormatter(fmt);
					col.setFormatOption(ColModel.FMT_OPTION_THOUSANDS_SEPARATOR, ",");
					col.setFormatOption(ColModel.FMT_OPTION_DEFAULT_VALUE, "");
					col.setWidth(80);
					col.setAlign(ColModel.ALIGN_RIGHT);
					break;
				case Types.BIT:
				case Types.BOOLEAN:
					//boolean
					col.setAlign(ColModel.ALIGN_CENTER);
					col.setFormatter(ColModel.FMT_CHECKBOX);
					col.setWidth(40);
					break;
				case Types.CHAR:
				case Types.LONGNVARCHAR:
				case Types.LONGVARCHAR:
				case Types.NCHAR:
				case Types.NVARCHAR:
				case Types.VARCHAR:
				case Types.CLOB:
				case Types.NCLOB:
				case Types.SQLXML:
				case Types.OTHER:
					//string
					break;
				case Types.DATE:
				case Types.TIME:
				case Types.TIMESTAMP:
					break;
			}
			col.setSortable(true);
			col.setResizable(true);
		}
		return model;
	}
	
	private static boolean isSupportedType(int type) {
		switch (type) {
			case Types.BIGINT:
			case Types.DECIMAL:
			case Types.NUMERIC:
			case Types.BIT:
			case Types.BOOLEAN:
			case Types.CHAR:
			case Types.LONGNVARCHAR:
			case Types.LONGVARCHAR:
			case Types.NCHAR:
			case Types.NVARCHAR:
			case Types.VARCHAR:
			case Types.CLOB:
			case Types.NCLOB:
			case Types.DATE:
			case Types.DOUBLE:
			case Types.REAL:
			case Types.FLOAT:
			case Types.INTEGER:
			case Types.SMALLINT:
			case Types.TINYINT:
			case Types.ROWID:
			case Types.SQLXML:
			case Types.TIME:
			case Types.TIMESTAMP:
			case Types.OTHER:
				return true;
			case Types.BINARY:
			case Types.LONGVARBINARY:
			case Types.VARBINARY:
			case Types.BLOB:
			case Types.JAVA_OBJECT:
			case Types.NULL:
			case Types.REF:
			case Types.ARRAY:
			case Types.DATALINK:
			case Types.DISTINCT:
			case Types.STRUCT:
			default:
				return false;
		}
	}
	
	/**
	 * nameQualifiedの場合に使用するテーブル別名の取得(簡易版)
	 * すべてのケースで正しく取得できる訳ではないのでその場合はnameQualifiedは使用できない
	 * (自己結合やSUB SELECTの場合テーブル名からエイリアスを判断することが論理的にできない)
	 */
	private static class AliasExtractor {
		
		private List<String> strs = new ArrayList<String>();
		
		public AliasExtractor(String sql) {
			StringTokenizer st = new StringTokenizer(sql, " \t\r\n,", true);
			while (st.hasMoreTokens()) {
				String s = st.nextToken().trim();
				if (s.length() == 0) {
					continue;
				}
				char c = s.charAt(0);
				if (c == '\t' || c == '\r' || c == '\n') {
					continue;
				}
				this.strs.add(s);
			}
		}
		
		public String getTableAlias(String tblName) {
			int len = this.strs.size();;
			for (int i=0; i<len; i++) {
				String s = this.strs.get(i);
				if (s.equalsIgnoreCase(tblName)) {
					String ret = null;
					if (i+1 < len) {
						ret = this.strs.get(i+1);
						if (ret.equalsIgnoreCase("as") && i+2 < len) {
							ret = this.strs.get(i+2);
						}
					}
					return isAlias(ret) ? ret : null;
				}
			}
			return null;
		}
		
	}
	
	private static boolean isAlias(String s) {
		final String[] notAlias = {
			",",
			"where",
			"group",
			"having",
			"order",
			"limit",
			"offset",
			"left",
			"right",
			"outer",
			"inner", 
			"join"
		};
		for (int i=0; i<notAlias.length; i++) {
			if (notAlias[i].equalsIgnoreCase(s)) {
				return false;
			}
		}
		return true;
	}
	
}

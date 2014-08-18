package jp.co.flect.rdb;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.HashMap;
import java.lang.reflect.Method;
import org.yaml.snakeyaml.Yaml;

public class Fixtures {
	
	//For playframework 1.x
	public static void loadModels(String filename) {
		try {
			Class clazz = Class.forName("play.db.DB");
			Method m = clazz.getMethod("getConnection");
			Connection con = (Connection)m.invoke(null);
			con.setAutoCommit(false);
			
			File f = new File(filename);
			if (!f.exists()) {
				f = new File("test/" + filename);
			}
			new Fixtures(con).load(f);
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	private Connection con;
	
	public Fixtures(Connection con) {
		this.con = con;
	}
	
	public int load(File file) throws IOException, SQLException {
		InputStream is = new FileInputStream(file);
		try {
			try {
				Yaml yaml = new Yaml();
				Map map = (Map)yaml.load(is);
				return process(map);
			} finally {
				is.close();
			}
		} finally {
			is.close();
		}
	}
	
	private int process(Map map) throws SQLException {
		int ret = 0;
		for (Object o : map.keySet()) {
			String tableName = getTableName(o.toString());
			ret += insert(tableName, (Map)map.get(o));
		}
		con.commit();
		return ret;
	}
	
	private String getTableName(String str) {
		int idx = str.indexOf('(');
		if (idx != -1) {
			str = str.substring(0, idx);
		}
		idx = str.lastIndexOf('.');
		if (idx != -1) {
			str = str.substring(idx+1);
		}
		return str;
	}
	
	private String buildSQL(String tableName, Map map) {
		StringBuilder buf = new StringBuilder();
		buf.append("INSERT INTO ").append(tableName).append("(");
		boolean first = true;
		for (Object o : map.keySet()) {
			if (first) {
				first = false;
			} else {
				buf.append(",");
			}
			buf.append(o.toString());
		}
		buf.append(") VALUES(");
		
		for (int i=0; i<map.size(); i++) {
			if (i != 0) {
				buf.append(",");
			}
			buf.append("?");
		}
		buf.append(")");
		return buf.toString();
	}
	
	private int insert(String tableName, Map map) throws SQLException {
		String sql = buildSQL(tableName, map);
		PreparedStatement stmt = con.prepareStatement(sql);
		try {
			int idx = 1;
			for (Object o : map.values()) {
				if (o instanceof java.util.Date) {
					o = new java.sql.Timestamp(((java.util.Date)o).getTime());
				}
				stmt.setObject(idx++, o);
			}
			return stmt.executeUpdate();
		} finally {
			stmt.close();
		}
	}
}

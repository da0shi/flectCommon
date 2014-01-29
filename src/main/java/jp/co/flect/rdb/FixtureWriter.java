package jp.co.flect.rdb;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.io.IOException;
import java.io.File;
import java.io.PrintStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.text.SimpleDateFormat;

public class FixtureWriter {
	
	private PrintStream os;
	private Set<String> excludes = new HashSet<String>();
	private Map<String, SimpleDateFormat> dfMap= new HashMap<String, SimpleDateFormat>();
	
	public FixtureWriter(File f) throws IOException {
		this(new PrintStream(f, "utf-8"));
	}
	
	public FixtureWriter(OutputStream os) {
		try {
			this.os = new PrintStream(os, false, "utf-8");
		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException(e);
		}
	}
	
	public void close() throws IOException {
		this.os.close();
	}
	
	public void addExclude(String colName) {
		this.excludes.add(colName.toLowerCase());
	}
	
	public void addDateFormat(String colName, String format) {
		this.dfMap.put(colName, new SimpleDateFormat(format));
	}
	
	public void write(String modelName, String keyField, ResultSet rs) throws SQLException, IOException {
		ResultSetMetaData meta = rs.getMetaData();
		List<String> columns = new ArrayList();
		boolean keyExist = false;
		for (int i=0; i<meta.getColumnCount(); i++) {
			String col = meta.getColumnName(i+1);
			if (!this.excludes.contains(col.toLowerCase())) {
				columns.add(col);
				if (col.equalsIgnoreCase(keyField)) {
					keyExist = true;
					keyField = col;
				}
			}
		}
		if (!keyExist) {
			throw new IllegalArgumentException("Can not found keyField in ResultSet: " + keyField);
		}
		while (rs.next()) {
			String key = rs.getString(keyField);
			os.println(modelName + "(" + key + "):");
			for (String c : columns) {
				String v = null;
				SimpleDateFormat df = dfMap.get(c);
				if (df != null) {
					Date d = rs.getTimestamp(c);
					if (d != null) {
						v = df.format(d);
					}
				} else {
					v = rs.getString(c);
				}
				if (v != null) {
					os.println("  " + c + ": " + v);
				}
			}
			os.println();
		}
	}
}

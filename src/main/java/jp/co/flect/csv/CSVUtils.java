package jp.co.flect.csv;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Arrays;

public class CSVUtils {
	
	public static void resultSetToCsv(ResultSet rs, File f) throws IOException, SQLException {
		CSVWriter writer = new CSVWriter(f);
		try {
			ResultSetMetaData meta = rs.getMetaData();
			String[] lines = new String[meta.getColumnCount()];
			for (int i=0; i<lines.length; i++) {
				lines[i] = meta.getColumnLabel(i+1);
			}
			writer.write(lines);
			while (rs.next()) {
				Arrays.fill(lines, null);
				for (int i=0; i<lines.length; i++) {
					lines[i] = rs.getString(i+1);
				}
				writer.write(lines);
			}
		} finally {
			writer.close();
		}
	}
}

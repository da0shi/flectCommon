package jp.co.flect.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * 読み終わった時(close時)にファイルを削除するFileInputStream
 */
public class FileInputStreamWithDelete extends FileInputStream {
	
	private File file;
	
	public FileInputStreamWithDelete(File file) throws FileNotFoundException {
		super(file);
		this.file = file;
	}
	
	@Override
	public void close() throws IOException {
		super.close();
		this.file.delete();
	}
}


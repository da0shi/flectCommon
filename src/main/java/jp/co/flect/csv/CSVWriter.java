package jp.co.flect.csv;

import java.io.Writer;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;

public class CSVWriter {
	
	public enum QuoteType {
		NONE,
		ALL,
		NECESSARY
	};
	
	private char separator = ',';
	private char quoteChar = '"';
	private QuoteType quoteType = QuoteType.ALL;
	private char escapeChar = quoteChar;
	private String newLine = NewLine.LF.getValue();
	
	private Writer writer;
	
	public CSVWriter(File file) throws FileNotFoundException, UnsupportedEncodingException {
		try {
			init(new FileOutputStream(file), "utf-8");
		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException(e);
		}
	}
	
	public CSVWriter(File file, String enc) throws FileNotFoundException, UnsupportedEncodingException {
		init(new FileOutputStream(file), enc);
	}
	
	public CSVWriter(OutputStream os) {
		try {
			init(os, "utf-8");
		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException(e);
		}
	}
	
	public CSVWriter(OutputStream os, String enc) throws UnsupportedEncodingException {
		init(os, enc);
	}
	
	private void init(OutputStream os, String enc) throws UnsupportedEncodingException {
		this.writer = new BufferedWriter(new OutputStreamWriter(os, enc));
	}
	
	public CSVWriter(Writer writer) {
		this.writer = writer;
	}
	
	public char getSeparator() { return this.separator;}
	public void setSeparator(char c) { this.separator = c;}
	
	public char getQuoteChar() { return this.quoteChar;}
	public void setQuoteChar(char c) { 
		this.quoteChar = c;
		if (this.escapeChar != '\\') {
			this.escapeChar = c;
		}
	}
	
	public QuoteType getQuoteType() { return this.quoteType;}
	public void setQuoteType(QuoteType t) { this.quoteType = t;}
	
	public boolean isEscapeWithBackslash() { 
		return this.escapeChar == '\\';
	}
	
	public void setEscapeWithBackslash(boolean b) { 
		this.escapeChar = b ? '\\' : this.quoteChar;
	}
	
	public NewLine getNewLine() {
		for (NewLine v : NewLine.values()) {
			if (v.getValue().equals(this.newLine)) {
				return v;
			}
		}
		throw new IllegalStateException();
	}
	
	public void setNewLine(NewLine v) { this.newLine = v.getValue();}
	
	public void write(String[] line) throws IOException {
		if (line != null && line.length > 0) {
			switch (this.quoteType) {
				case NONE: 
					writeNone(line); 
					break;
				case ALL: 
					writeAll(line); 
					break;
				case NECESSARY: 
					writeNecessary(line); 
					break;
				default: 
					throw new IllegalStateException();
			}
		}
		writer.write(newLine);
	}
	
	private void writeNone(String[] line) throws IOException {
		writeNoQuote(line[0]);
		for (int i=1; i<line.length; i++) {
			writer.write(separator);
			writeNoQuote(line[i]);
		}
	}
	
	private void writeAll(String[] line) throws IOException {
		writeQuote(line[0]);
		for (int i=1; i<line.length; i++) {
			writer.write(separator);
			writeQuote(line[i]);
		}
	}
	
	private void writeNecessary(String[] line) throws IOException {
		if (isNeedQuote(line[0])) {
			writeQuote(line[0]);
		} else {
			writeNoQuote(line[0]);
		}
		for (int i=1; i<line.length; i++) {
			writer.write(separator);
			if (isNeedQuote(line[i])) {
				writeQuote(line[i]);
			} else {
				writeNoQuote(line[i]);
			}
		}
	}
	
	private void writeNoQuote(String s) throws IOException {
		if (s == null || s.length() == 0) {
			return;
		}
		writer.write(s);
	}
	
	private void writeQuote(String s) throws IOException {
		writer.write(quoteChar);
		if (s != null) {
			int len = s.length();
			for (int i=0; i<len; i++) {
				char c = s.charAt(i);
				if (c == quoteChar) {
					writer.write(escapeChar);
				} else if (c == '\\' && escapeChar == '\\') {
					writer.write(escapeChar);
				}
				writer.write(c);
			}
		}
		writer.write(quoteChar);
	}
	
	private boolean isNeedQuote(String s) {
		if (s == null || s.length() == 0) {
			return false;
		}
		int len = s.length();
		for (int i=0; i<len; i++) {
			char c = s.charAt(i);
			if (c == quoteChar || c == separator || c == '\r' || c == '\n') {
				return true;
			}
		}
		return false;
	}
	
	public void flush() throws IOException {
		this.writer.flush();
	}
	
	public void close() throws IOException {
		this.writer.close();
	}
}

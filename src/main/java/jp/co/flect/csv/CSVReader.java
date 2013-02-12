package jp.co.flect.csv;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * See opencsv-1.7 
 */
public class CSVReader {
	
	private BufferedReader br = null;
	private LineReader lr = null;
	private String encoding = null;
	
	private StringBuilder sb = new StringBuilder();
	private List tokensOnThisLine = new ArrayList();
	private boolean hasNext = true;
	private char separator = ',';
	private char quoteChar = '"';
	private String newLine = NewLine.LF.getValue();
	private boolean bTrim = true;
	private boolean escapeWithBackslash;
	
	public CSVReader(File file, String enc) throws IOException {
		this(new FileInputStream(file), enc);
	}
		
	public CSVReader(Reader reader) {
		if (reader instanceof BufferedReader) {
			this.br = (BufferedReader)reader;
		} else {
			this.br = new BufferedReader(reader);
		}
	}
	
	public CSVReader(InputStream is, String enc) throws UnsupportedEncodingException {
		if ("utf-16".equalsIgnoreCase(enc) ||
		    "utf-16le".equalsIgnoreCase(enc) ||
		    "utf-16be".equalsIgnoreCase(enc) ||
			"JISAutoDetect".equals( enc ) ) {
			this.br = new BufferedReader(new InputStreamReader(is, enc));
		} else {
			this.lr = new LineReader(is, enc);
		}
		this.encoding = enc;
	}
	
	public char getSeparator() { return separator;}
	public void setSeparator(char c) { separator = c;}
	
	public char getQuoteChar() { return quoteChar;}
	public void setQuoteChar(char c) { quoteChar = c;}
	
	public boolean isEscapeWithBackslash() { 
		return this.escapeWithBackslash;
	}
	
	public void setEscapeWithBackslash(boolean b) { 
		this.escapeWithBackslash = b;
	}
	
	public boolean isTrim() { return bTrim;}
	public void setTrim(boolean b) { bTrim = b;}
	
	public NewLine getNewLine() {
		for (NewLine v : NewLine.values()) {
			if (v.getValue().equals(this.newLine)) {
				return v;
			}
		}
		throw new IllegalStateException();
	}
	
	public void setNewLine(NewLine v) { this.newLine = v.getValue();}
	
	
	public List<String[]> readAll() throws IOException {
		List<String[]> allElements = new ArrayList<String[]>();
		while (hasNext) {
			String[] nextLineAsTokens = readNext();
			if (nextLineAsTokens != null) {
				allElements.add(nextLineAsTokens);
			}
		}
		return allElements;
	}

	public String[] readNext() throws IOException {
		String nextLine = getNextLine(true);
		return hasNext ? parseLine(nextLine) : null;
	}
	
	private String trimString(String s, boolean bLeft, boolean bRight) {
		int st = 0;
		int cnt = s.length();
		int len = cnt;
		if (bLeft) {
			while (st < len) {
				char c = s.charAt(st);
				if ((separator != ' ' && c == ' ') || 
				    (separator != '\t' && c == '\t') || 
				    (separator != 0xFEFF && c == 0xFEFF))
				{
					st++;
				} else {
					break;
				}
			}
		}
		if (bRight) {
			while (st < len) {
				char c = s.charAt(len-1);
				if ((separator != ' ' && c == ' ') || 
				    (separator != '\t' && c == '\t') || 
				    (separator != 0xFEFF && c == 0xFEFF))
				{
					len--;
				} else {
					break;
				}
			}
		}
		if (st > 0 || len < cnt) {
			return s.substring(st, len);
		} else {
			return s;
		}
	}
	
	private String getNextLine(boolean bFirst) throws IOException {
		if (!hasNext) {
			return null;
		}
		
		String nextLine = doGetNextLine();
		if (nextLine == null) {
			hasNext = false;
			return null;
		}
		if (bFirst && bTrim) {
			nextLine = trimString(nextLine, true, false);
		}
		return nextLine;
	}
	
	private String doGetNextLine() throws IOException {
		if (br != null) {
			return br.readLine();
		} else {
			return lr.readLine();
		}
	}
	
	private String[] parseLine(String nextLine) throws IOException {
		if (nextLine == null) {
			return null;
		}
		
		tokensOnThisLine.clear();
		sb.setLength(0);
		
		boolean bBuild = false;
		int spos = -1;
		boolean inQuotes = false;
		do {
			if (inQuotes) {
				if (!bBuild) {
					if (spos != -1) {
						sb.append(nextLine.substring(spos));
					}
					bBuild = true;
				}
				// continuing a quoted section, reappend newline
				sb.append(newLine);
				nextLine = getNextLine(false);
				if (nextLine == null) {
					break;
				}
			}
			int lineLen = nextLine.length();
			for (int i = 0; i < lineLen; i++) {
				char c = nextLine.charAt(i);

				if (c == quoteChar) {
					// this gets complex... the quote may end a quoted block, or escape another quote.
					// do a 1-char lookahead:
					if(inQuotes) {
						if (!escapeWithBackslash 
						    && lineLen > (i+1)
						    && nextLine.charAt(i+1) == quoteChar )
						{
							if (!bBuild) {
								sb.append(nextLine.substring(spos, i));
								bBuild = true;
							}
							sb.append(quoteChar);
							i++;
						} else {
							int j = i+1;
							for (; j<lineLen; j++) {
								char c2 = nextLine.charAt(j);
								if (c2 == separator) {
									break;
								}
							}
							String str = null;
							if (bBuild) {
								str = sb.toString();
							} else if (spos == -1) {
								str = "";
							} else {
								str = nextLine.substring(spos, i);
							}
							tokensOnThisLine.add(str);
							sb.setLength(0);
							bBuild = false;
							inQuotes = false;
							spos = -1;
							i = j;
						}
					} else {
						if (spos == -1) {
							inQuotes = true;
							spos = i+1;
						} else if (bBuild) {
							sb.append(c);
						}
					}
				} else if (c == separator && !inQuotes) {
					String str = null;
					if (bBuild) {
						str = sb.toString();
					} else if (spos == -1) {
						str = "";
					} else {
						str = nextLine.substring(spos, i);
					}
					if (bTrim) {
						str = trimString(str, true, true);
					}
					tokensOnThisLine.add(str);
					sb.setLength(0);
					bBuild = false;
					spos = -1;
				} else if (c == '\\'
				           && escapeWithBackslash
				           && inQuotes
				           && lineLen > (i+1))
				{
					if (!bBuild) {
						sb.append(nextLine.substring(spos, i));
						bBuild = true;
					}
					char c2 = nextLine.charAt(i+1);
					sb.append(c2);
					i++;
				} else {
					if (spos == -1) {
						spos = i;
					} else if (bBuild) {
						sb.append(c);
					}
				}
			}
		} while (inQuotes);
		if (bBuild || spos != -1) {
			String str = bBuild ? sb.toString() : nextLine.substring(spos);
			if (bTrim) {
				str = trimString(str, true, true);
			}
			tokensOnThisLine.add(str);
		} else if (nextLine.length() == 0 ) {
			tokensOnThisLine.add("");
		} else if (nextLine.charAt(nextLine.length() - 1) == separator) {
			tokensOnThisLine.add("");
		}
		return (String[]) tokensOnThisLine.toArray(new String[tokensOnThisLine.size()]);
	}

	/**
	 * Closes the underlying reader.
	 *
	 * @throws IOException if the close fails
	 */
	public void close() throws IOException{
		if (br != null)
			br.close();
		else
			lr.close();
	}
}

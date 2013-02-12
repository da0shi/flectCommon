package jp.co.flect.csv;

public enum NewLine {
	LF("\n"),
	CR("\r"),
	CRLF("\r\n")
	;
	
	private String value;
	
	private NewLine(String value) {
		this.value = value;
	}
	
	public String getValue() { return this.value;}
};


package jp.co.flect.csv;

import java.io.InputStream;
import java.io.IOException;

public class LineReader {
	
	private static final int BUF_SIZE = 8192;

	// 2^26 byte -> 64 M
	private static final int MAX_BUF_SIZE = 67108864;
	
	private InputStream is;
	private String encoding = null;
	
	private byte[] buf = new byte[BUF_SIZE];
	private int bufIndex = 0;
	private int bufLength = 0;
	
	public LineReader(InputStream is, String enc) {
		this.is = is;
		this.encoding = enc;
	}
	
	public String readLine() throws IOException {
		if (bufIndex == bufLength) {
			if (bufIndex == -1) {
				return null;
			}
			
			bufLength = is.read(buf);
			if (bufLength == -1) {
				bufIndex = -1;
				return null;
			}
			bufIndex = 0;
		}
		return getNextLine(bufIndex);
	}

	private String getNextLine(int spos) throws IOException {
		int idx = spos;
		while (idx <bufLength) {
			byte n = buf[idx];

			if (n == 0x0D) {
				int len = idx - bufIndex;
				String ret = new String(buf, bufIndex, len, encoding);
				if (idx + 1 < bufLength && buf[idx+1] == 0xA) {
					len++;
				}
				bufIndex += len + 1;
				if (idx+1 == bufLength) {
					bufLength = is.read(buf);
					if (bufLength == -1) {
						bufIndex = -1;
					} else {
						bufIndex = 0;
						if (buf[0] == 0xA) {
							bufIndex++;
						}
					}
				}
				return ret;
			}
			if (n == 0x0A) {
				int len = idx - bufIndex;
				String ret = new String(buf, bufIndex, len, encoding);
				bufIndex += len + 1;
				return ret;
			}
			idx++;
		}

		if (bufLength != buf.length) {
			int len = bufLength - bufIndex;
			String ret = new String(buf, bufIndex, len, encoding);
			bufIndex = 0;
			bufLength = 0;
			return ret;
		}
		if (bufIndex == 0) {
			if (buf.length >= MAX_BUF_SIZE)
				throw new IOException("Buffer over flow: " + buf.length);

			byte[] temp = new byte[buf.length * 2];
			System.arraycopy(buf, 0, temp, 0, bufLength);
			buf = temp;
			int nSpos = bufLength - 1;
			int n = is.read(buf, bufLength, bufLength);
			if (n != -1) {
				bufLength += n;
			}
			return getNextLine(nSpos);
		} else {
			int len = bufLength - bufIndex;
			System.arraycopy(buf, bufIndex, buf, 0, len);
			int n = is.read(buf, len, buf.length - len);
			int nSpos = len - 1;
			bufIndex = 0;
			if (n == -1) {
				bufLength = 0;
				return new String(buf, bufIndex, len, encoding);
			} else {
				bufLength = len + n;
				return getNextLine(nSpos);
			}
		}
	}
	
	public void close() throws IOException {
		is.close();
	}
}

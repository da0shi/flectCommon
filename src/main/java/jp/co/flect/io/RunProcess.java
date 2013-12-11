package jp.co.flect.io;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.HashMap;

public class RunProcess {
	
	private File dir;
	private Map<String, String> envMap = new HashMap<String, String>();
	
	private InputStream toStdIn = null;
	private OutputStream fromStdOut = null;
	private OutputStream fromStdErr = null;
	
	public RunProcess() {
		this(new File("."));
	}
	
	public RunProcess(File dir) {
		this.dir = dir;
	}
	
	public File getDirectory() { return this.dir;}
	
	public String getEnv(String name) { return this.envMap.get(name);}
	public void setEnv(String name, String value) { this.envMap.put(name, value);}
	
	public InputStream getStdIn() { return this.toStdIn;}
	public void setStdIn(InputStream is) { this.toStdIn = is;}
	
	public OutputStream getStdOut() { return this.fromStdOut;}
	public void setStdOut(OutputStream os) { this.fromStdOut = os;}
	
	public OutputStream getStdErr() { return this.fromStdErr;}
	public void setStdErr(OutputStream os) { this.fromStdErr = os;}
	
	public String getInputString() {
		if (toStdIn instanceof ByteArrayInputStream) {
			ByteArrayInputStream bis = (ByteArrayInputStream)toStdIn;
			String ret = getString(bis, null);
			bis.reset();
			return ret;
		}
		return null;
	}
	
	public void setInputString(String s) {
		this.toStdIn = new ByteArrayInputStream(s.getBytes());
	}
	
	private String getString(InputStream is, String enc) {
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			byte[] buf = new byte[8192];
			int n = is.read(buf);
			while (n != -1) {
				bos.write(buf, 0, n);
				n = is.read(buf);
			}
			return enc == null ? new String(bos.toByteArray()) : new String(bos.toByteArray(), enc);
		} catch (UnsupportedEncodingException e) {
			throw new IllegalArgumentException(enc);
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}
	
	public boolean isStdOutAsString() { return this.fromStdOut instanceof ByteArrayOutputStream;}
	public void setStdOutAsString(boolean b) { if (b) this.fromStdOut = new ByteArrayOutputStream();}
	
	public boolean isStdErrAsString() { return this.fromStdErr instanceof ByteArrayOutputStream;}
	public void setStdErrAsString(boolean b) { if (b) this.fromStdErr = new ByteArrayOutputStream();}
	
	public String getStdOutAsString() {
		if (this.fromStdOut instanceof ByteArrayOutputStream) {
			return new String(((ByteArrayOutputStream)this.fromStdOut).toByteArray());
		}
		return null;
	}
	
	public String getStdErrAsString() {
		if (this.fromStdErr instanceof ByteArrayOutputStream) {
			return new String(((ByteArrayOutputStream)this.fromStdErr).toByteArray());
		}
		return null;
	}
	
	public int run(String... command) throws IOException {
		String[] env = null;
		if (this.envMap.size() > 0) {
			env = new String[envMap.size()];
			int idx = 0;
			for (Map.Entry<String, String> entry : this.envMap.entrySet()) {
				env[idx++] = entry.getKey() + "=" + entry.getValue();
			}
		}
		Process p = Runtime.getRuntime().exec(command, env, this.dir);
		if (this.toStdIn != null) {
			new ReadThread(toStdIn, p.getOutputStream()).start();
		}
		new ReadThread(p.getInputStream(), this.fromStdOut).start();
		new ReadThread(p.getErrorStream(), this.fromStdErr).start();
		try {
			return p.waitFor();
		} catch (InterruptedException e) {
			e.printStackTrace();
			throw new IllegalStateException(e);
		}
	}
	
	private static class ReadThread extends Thread {
		
		private InputStream is;
		private OutputStream os;
		
		public ReadThread(InputStream is, OutputStream os) {
			this.is = is;
			this.os = os;
		}
		
		@Override
		public void run() {
			byte[] buf = new byte[8194];
			try {
				try {
					int n = is.read(buf);
					while (n != -1) {
						if (os != null) {
							os.write(buf, 0, n);
						}
						n = is.read(buf);
					}
					if (os != null) {
						os.flush();
					}
				} finally {
					is.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
}
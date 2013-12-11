package jp.co.flect.jvmtool;

import java.io.StringReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.File;
import java.util.List;
import java.util.ArrayList;
import java.util.StringTokenizer;
import jp.co.flect.io.RunProcess;

public class JMap {
	
	private String command;
	private String lastError;
	private boolean d64;
	
	public JMap() {
		this("jmap");
	}
	
	public JMap(String command) {
		this.command = command;
	}
	
	public boolean is64bit() { return this.d64;}
	public void set64bit(boolean b) { this.d64 = b;}
	public JMap with64bit(boolean b) { this.d64 = b; return this;}
	
	public String getLastError() { return this.lastError;}
	
	public String run(int pid) throws IOException {
		return run("", pid);
	}
	
	public String heap(int pid) throws IOException {
		return run("-heap", pid);
	}
	
	public String histo(int pid) throws IOException {
		return histo(false, pid);
	}
	
	public String histo(boolean live, int pid) throws IOException {
		String op = "-histo";
		if (live) {
			op += ":live";
		}
		return run(op, pid);
	}
	
	public String permstat(int pid) throws IOException {
		return run("-permstat", pid);
	}
	
	public String finalizerinfo(int pid) throws IOException {
		return run("-finalizerinfo", pid);
	}
	
	public void dump(int pid, File file) throws IOException {
		dump(false, pid, file);
	}
	
	public void dump(boolean live, int pid, File file) throws IOException {
		StringBuilder buf = new StringBuilder();
		buf.append("-dump:");
		if (live) {
			buf.append("live,");
		}
		buf.append("format=b,file=").append(file.getCanonicalPath());
		run(buf.toString(), pid);
	}
	
	public String run(String option, int pid) throws IOException {
		RunProcess process = new RunProcess();
		process.setStdOutAsString(true);
		process.setStdErrAsString(true);
		int n = this.d64 ?
			process.run(this.command, "-J-d64", option, Integer.toString(pid)) :
			process.run(this.command, option, Integer.toString(pid));
		this.lastError = process.getStdErrAsString();
		return process.getStdOutAsString();
	}
	
	public static List<Item> parse(String str) {
		List<Item> list = new ArrayList<Item>();
		BufferedReader reader = new BufferedReader(new StringReader(str));
		try {
			String line = reader.readLine();
			while (line != null) {
				int idx = line.indexOf(':');
				if (idx != -1) {
					try {
						int num = Integer.parseInt(line.substring(0, idx).trim());
						
						
						StringTokenizer st = new StringTokenizer(line.substring(idx+1).trim(), " ");
						if (st.countTokens() != 3) {
							throw new IllegalArgumentException(line);
						}
						int instances = Integer.parseInt(st.nextToken());
						long bytes = Long.parseLong(st.nextToken());
						String className = st.nextToken();
						
						list.add(new Item(num, instances, bytes, className));
					} catch (NumberFormatException e) {
						throw new IllegalArgumentException(line);
					}
				}
				line = reader.readLine();
			}
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
		return list;
	}
	
	public static class Item {
		
		private int num;
		private int instances;
		private long bytes;
		private String className;
		
		public Item(int num, int instances, long bytes, String className) {
			this.num = num;
			this.instances = instances;
			this.bytes = bytes;
			this.className = className;
		}
		
		public int getNum() { return this.num;}
		public int getInstances() { return this.instances;}
		public long getBytes() { return this.bytes;}
		public String getClassName() { return this.className;}
		
		public String toString() {
			StringBuilder buf = new StringBuilder();
			buf.append(this.num)
				.append(": instances=").append(this.instances)
				.append(", bytes=").append(this.bytes)
				.append(", name=").append(this.className);
			return buf.toString();
		}
	}
}

package jp.co.flect.jvmtool;

import java.io.StringReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.File;
import java.util.List;
import java.util.ArrayList;
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
	
}

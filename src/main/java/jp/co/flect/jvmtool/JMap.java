package jp.co.flect.jvmtool;

import java.io.StringReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import jp.co.flect.io.RunProcess;

public class JMap {
	
	private String command;
	
	public JMap() {
		this("jmap");
	}
	
	public JMap(String command) {
		this.command = command;
	}
	
	public String run(int pid) throws IOException {
		return run("", pid);
	}
	
	public String heap(int pid) throws IOException {
		return run("-heap", pid);
	}
	
	public String histo(int pid) throws IOException {
		return run("-histo", pid);
	}
	
	public String permstat(int pid) throws IOException {
		return run("-permstat", pid);
	}
	
	public String run(String option, int pid) throws IOException {
		RunProcess process = new RunProcess();
		process.setStdOutAsString(true);
		
		int n = process.run(this.command, option, Integer.toString(pid));
		return process.getStdOutAsString();
	}
	
}

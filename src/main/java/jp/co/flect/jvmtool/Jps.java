package jp.co.flect.jvmtool;

import java.io.StringReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import jp.co.flect.io.RunProcess;

public class Jps {
	
	private String command;
	
	private boolean showFullname;
	private boolean showArgs;
	private boolean showVmArgs;
	
	public Jps() {
		this("jps");
	}
	
	public Jps(String command) {
		this.command = command;
	}
	
	public boolean isShowFullName() { return this.showFullname;}
	public void setShowFullName(boolean b) { this.showFullname = b;}
	public Jps withShowFullName(boolean b) { this.showFullname = b; return this;}
	
	public boolean isShowArgs() { return this.showArgs;}
	public void setShowArgs(boolean b) { this.showArgs = b;}
	public Jps withShowArgs(boolean b) { this.showArgs = b; return this;}
	
	public boolean isShowVmArgs() { return this.showVmArgs;}
	public void setShowVmArgs(boolean b) { this.showVmArgs = b;}
	public Jps withShowVmArgs(boolean b) { this.showVmArgs = b; return this;}
	
	public List<Result> run() throws IOException {
		StringBuilder argsBuf = new StringBuilder();
		if (this.showFullname || this.showArgs || this.showVmArgs) {
			argsBuf.append("-");
			if (this.showFullname) argsBuf.append("l");
			if (this.showArgs) argsBuf.append("m");
			if (this.showVmArgs) argsBuf.append("vV");
		}
		RunProcess process = new RunProcess();
		process.setStdOutAsString(true);
		
		int n = process.run(this.command, argsBuf.toString());
		
		List<Result> list = new ArrayList<Result>();
		BufferedReader reader = new BufferedReader(new StringReader(process.getStdOutAsString()));
		String line = reader.readLine();
		while (line != null) {
			int idx = line.indexOf(' ');
			int id = Integer.parseInt(line.substring(0, idx));
			
			line = line.substring(idx).trim();
			idx = line.indexOf(' ');
			String name = idx == -1 ? line : line.substring(0, idx);
			String args = idx == -1 ? null : line.substring(idx).trim();
			
			list.add(new Result(id, name, args));
			line = reader.readLine();
		}
		return list;
	}
	
	public Result searchProcess(String str) throws IOException {
		List<Result> list = run();
		for (Result ret : list) {
			if (ret.toString().indexOf(str) != -1) {
				return ret;
			}
		}
		return null;
	}
	
	public static class Result {
		
		private int lvmid;
		private String name;
		private String args;
		
		public Result(int id, String name, String args) {
			this.lvmid = id;
			this.name = name;
			this.args = args;
		}
		
		public int getLiveVmId() { return lvmid;}
		//Alias of liveVmId
		public int getPid() { return getLiveVmId();}
		
		public String getName() { return this.name;}
		public String getArgs() { return this.args;}
		
		public String toString() {
			StringBuilder buf = new StringBuilder();
			buf.append(this.lvmid)
				.append(" ")
				.append(name);
			if (this.args != null) {
				buf.append(" ").append(this.args);
			}
			return buf.toString();
		}
	}
}

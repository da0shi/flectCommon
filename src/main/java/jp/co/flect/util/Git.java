package jp.co.flect.util;

import jp.co.flect.io.RunProcess;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

public class Git {
	
	private File dir;
	private String stdOut;
	private String stdErr;
	
	public Git(File dir) {
		this.dir = dir;
	}
	
	public String getStdOut() { return this.stdOut;}
	public String getStdErr() { return this.stdErr;}
	
	public File clone(String uri) throws IOException {
		int n = run("git", "clone", uri);
		File ret = null;
		if (n == 0) {
			int idx = uri.lastIndexOf("/");
			String repoName = uri.substring(idx+1);
			if (repoName.endsWith(".git")) {
				repoName = repoName.substring(0, repoName.length() - 4);
			}
			ret = new File(this.dir, repoName);
			//assert ret.exists();
		}
		return ret;
	}
	
	public void commit(boolean bAdd, String msg) throws IOException {
		List<String> list = new ArrayList<String>();
		list.add("git");
		list.add("commit");
		if (bAdd) {
			list.add("-a");
		}
		list.add("-m");
		list.add(msg);
		run(list.toArray(new String[list.size()]));
	}
	
	public void push(String uriOrRemote, RefSpec refSpec) throws IOException {
		List<String> list = new ArrayList<String>();
		list.add("git");
		list.add("push");
		list.add(uriOrRemote);
		if (refSpec != null) {
			list.add(refSpec.toString());
		}
		run(list.toArray(new String[list.size()]));
	}
	
	private int run(String... command) throws IOException {
		RunProcess rp = new RunProcess(this.dir);
		rp.setStdOutAsString(true);
		rp.setStdErrAsString(true);
		int ret = rp.run(command);
		this.stdOut = rp.getStdOutAsString();
		this.stdErr = rp.getStdErrAsString();
		return ret;
	}
	
	public static class RefSpec {
		
		private String src;
		private String dest;
		
		public RefSpec(String src, String dest) {
			this.src = src;
			this.dest = dest;
		}
		
		public RefSpec(String srcAndDest) {
			this(srcAndDest, srcAndDest);
		}
		
		public String toString() {
			if (src.equals(dest)) {
				return src;
			} else {
				return src + ":" + dest;
			}
		}
	}
}
package jp.co.flect.util;

import java.util.ArrayList;
import java.util.List;

public class StopWatch {
	
	private String name;
	private long start;
	
	private List<Item> list = new ArrayList<Item>();
	
	public StopWatch(String name) {
		this.name = name;
		this.start = System.currentTimeMillis();
	}
	
	public void start() {
		this.list.clear();
		this.start = System.currentTimeMillis();
	}
	
	public long lap(String name) {
		Item item = new Item(name);
		long prev = list.size() == 0 ? start : list.get(list.size() - 1).time;
		this.list.add(item);
		return item.time - prev;
	}
	
	public long getLapTime(String name) {
		long prev = this.start;
		for (Item item : this.list) {
			if (item.name.equals(name)) {
				return item.time - prev;
			}
			prev = item.time;
		}
		return -1;
	}
	
	public long getTotalTime(String name) {
		for (Item item : this.list) {
			if (item.name.equals(name)) {
				return item.time - this.start;
			}
		}
		return -1;
	}
	
	public String getLog() {
		StringBuilder buf = new StringBuilder();
		buf.append(this.name).append(": ");
		long prev = this.start;
		long current = prev;
		for (Item item : this.list) {
			current = item.time;
			buf.append(item.name).append("=").append(current - prev).append("ms, ");
			prev = current;
		}
		buf.append("Total=").append(current - start).append("ms");
		return buf.toString();
	}
	
	private static class Item {
		
		public String name;
		public long time;
		
		public Item(String name) {
			this.name = name;
			this.time = System.currentTimeMillis();
		}
	}
}

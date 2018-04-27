package me.aelesia.commons.logger2;

import java.time.LocalDateTime;

public class Logger {

	public static int TRACE = 6;
	public static int DEBUG = 5;
	public static int INFO = 4;
	public static int WARN = 3;
	public static int ERROR = 2;
	
	public static int logLevel = DEBUG;
	
	public String prefix = "";
	
	public Logger(String prefix) {
		this.prefix = prefix;
	}
	
	public void trace(Object log) {
		if (logLevel >= TRACE) {
			System.out.println("["+LocalDateTime.now()+"]"+prefix+" " + log);
		}
	}
	
	public void debug(Object log) {
		if (logLevel >= DEBUG) { 
			System.out.println("["+LocalDateTime.now()+"]"+prefix+" " + log);
		}
	}
	
	public void error(Object log, Exception e) {
		this.error(log);
		e.printStackTrace();
	}
	
	public void error(Object log) {
		if (logLevel >= ERROR) {
			System.out.println("["+LocalDateTime.now()+"]"+prefix+" ERROR: " + log);
		}
	}
	
	public void warn(Object log) {
		if (logLevel >= WARN) {
			System.out.println("["+LocalDateTime.now()+"]"+prefix+" WARNING:" + log);
		}
	}
	
	public void info(Object log) {
		if (logLevel >= INFO) {
			System.out.println("["+LocalDateTime.now()+"]"+prefix+" " + log);
		}
	}
}

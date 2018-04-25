package me.aelesia.commons.logger;

import java.time.LocalDateTime;

public class Logger {

	public static int TRACE = 6;
	public static int DEBUG = 5;
	public static int INFO = 4;
	public static int WARN = 3;
	public static int ERROR = 2;
	
	public static int logLevel = TRACE;
	
	public static void trace(Object log) {
		if (logLevel >= TRACE) {
			System.out.println("["+LocalDateTime.now()+"] " + log);
		}
	}
	
	public static void debug(Object log) {
		if (logLevel >= DEBUG) { 
			System.out.println("["+LocalDateTime.now()+"] " + log);
		}
	}
	
	public static void info(Object log) {
		if (logLevel >= INFO) {
			System.out.println("["+LocalDateTime.now()+"] " + log);
		}
	}
	
	public static void error(Object log, Exception e) {
		Logger.error(log);
		e.printStackTrace();
	}
	
	public static void error(Object log) {
		if (logLevel >= ERROR) {
			System.out.println("["+LocalDateTime.now()+"] ERROR: " + log);
		}
	}
	
	public static void warn(Object log) {
		if (logLevel >= WARN) {
			System.out.println("["+LocalDateTime.now()+"] WARNING: " + log);
		}
	}
}

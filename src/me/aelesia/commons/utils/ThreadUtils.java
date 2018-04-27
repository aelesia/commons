package me.aelesia.commons.utils;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class ThreadUtils {
	
	/** 
	 * Use only for uninterruptable sleep
	 * 
	 * @param millis  Number of milliseconds to sleep
	 */
	@Deprecated
	public static void sleep(int millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			throw new RuntimeException("Method is not supposed to be used for interruptable sleep", e);
		}
	}
	
	public static void sleepFor(long ms) {
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {
			throw new RuntimeException("Method is not supposed to be used for interruptable sleep", e);
		}
	}
	
	public static void sleepFor(long time, ChronoUnit unit) {
		long ms;
		switch (unit) {
			case MILLIS: ms = time; 
				break;
			case SECONDS: ms = time*1000; 
				break;
			case MINUTES: ms = time*1000*60;
				break;
			case HOURS: ms = time*1000*60*60;
				break;
			default: throw new IllegalArgumentException("Chronounit " + unit.name().toString() + " not supported.");
		}
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {
			throw new RuntimeException("Method is not supposed to be used for interruptable sleep", e);
		}
	}
	
	public static void sleepUntil(LocalDateTime sleepTime) {
		try {
			long millis = LocalDateTime.now().until(sleepTime, ChronoUnit.MILLIS);
			if (millis>0) {
				Thread.sleep(millis);
			}
		} catch (InterruptedException e) {
			throw new RuntimeException("Method is not supposed to be used for interruptable sleep", e);
		}
	}
	
	/** 
	 * Starts all threads
	 */
	public static void startAllThreads(List<Thread> threadList) {
		for (Thread t: threadList) {
			t.start();
		}
	}
	
	/** 
	 * Use only for uninterruptable threads
	 */
	public static void joinAllThreads(List<Thread> threadList) {
		for (Thread t: threadList) {
			try {
				t.join();
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	public static List<Object> joinAllFutures(List<Future<?>> futureList) throws InterruptedException, ExecutionException {
		List<Object> objectList = new ArrayList<Object>();
		for (Future<?> f : futureList) {
			objectList.add(f.get());
		}
		return objectList;
	}
	
	public static List<Object> cancelAllFutures(List<Future<?>> futureList) {
		List<Object> objectList = new ArrayList<Object>();
		for (Future<?> f : futureList) {
			if (!f.isDone()) {
				f.cancel(false);
			}
		}
		return objectList;
	}
}

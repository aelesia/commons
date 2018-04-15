package me.aelesia.commons.utils;

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
	public static void sleep(int millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
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
}

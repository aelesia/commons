package me.aelesia.commons.http2;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.util.EntityUtils;

public class HttpClientManager {
	
	ScheduledExecutorService executor;
	HttpClientPool client;
	
	public HttpClientManager(int numThreads) {
		this.executor = Executors.newScheduledThreadPool(numThreads);
		this.client = new HttpClientPool(numThreads);
	}
	
	/**
	 * Executes a http request, and waits until it finishes executing then
	 *
	 * @param  request  The HttpRequest object
	 * @return  The http response of the request
	 */
	public HttpResponse execute(HttpUriRequest request) throws IOException {
		return client.execute(request);
	}
	
	/**
	 * Executes a http request, and does not care about the response.
	 *
	 * @param  request  The HttpRequest object
	 */
	public void executeForget(HttpUriRequest request) {
		Runnable runnable = () -> {
            try {
				EntityUtils.consume(client.execute(request).getEntity());
			} catch (IOException e) {
			} 
        };
        executor.submit(runnable);
        
	}
	
//	/**
//	 * Executes a list of http requests, and waits until it finishes executing them
//	 *
//	 * @param  request  The HttpRequest object
//	 * @return  The list of http responses
//	 */
//	@SuppressWarnings("unchecked")
//	public List<HttpResponse> batchExecuteWait(List<HttpUriRequest> requestList) throws InterruptedException, ExecutionException {
//		List<Future<?>> futureList = new ArrayList<Future<?>>();
//		for (HttpUriRequest request : requestList) {
//	        Callable<HttpResponse> callable = () -> {
//	            return client.execute(request);
//	        };
//	        futureList.add(executorService.submit(callable));
//		}
//		return (List<HttpResponse>)(Object) ThreadUtils.joinAllFutures(futureList);
//	}
	
//	/**
//	 * Executes an asynchronous http request, and returns a Future object
//	 *
//	 * @param  request  The HttpRequest object
//	 * @return  The Future HttpResponse
//	 */
//	public Future<HttpResponse> execute(HttpUriRequest request) {
//        Callable<HttpResponse> callable = () -> {
//            return client.execute(request);
//        };
//        return executorService.submit(callable);
//	}
	
	/**
	 * Executes a http request, then performs whatever the listener was programmed to do
	 *
	 * @param  request  The HttpRequest object
	 * @param  listener  A listener that will execute after the request has been performed
	 */
	public void execute(HttpUriRequest request, HttpResponseListener listener) {
		this.execute(request, 0, listener);
	}
	
	public Future<?> execute(HttpUriRequest request, LocalDateTime scheduledTime, HttpResponseListener listener) {
		if (scheduledTime==null || LocalDateTime.now().isAfter(scheduledTime)) {
			return this.execute(request, 0, listener);
		} else {
			return this.execute(request, LocalDateTime.now().until(scheduledTime, ChronoUnit.MILLIS), listener);
		}
	}
		
	public Future<?> execute(HttpUriRequest request, long delayMs, HttpResponseListener listener) {
		Runnable runnable = () -> {
        		try {
        			listener.executeBefore();
				HttpResponse response = client.execute(request);
				listener.executeAfter(response);
			} catch (IOException e) {
				listener.executeOnException(e);
			}
        };
        if (delayMs!=0) {
    			return executor.schedule(runnable, delayMs, TimeUnit.MILLISECONDS);
        }
        return executor.submit(runnable);
	}
	
	public Future<HttpResponse> executeFuture(HttpUriRequest request, LocalDateTime scheduledTime) {
		if (scheduledTime==null || LocalDateTime.now().isAfter(scheduledTime)) {
			return this.executeFuture(request, 0);
		} else {
			return this.executeFuture(request, LocalDateTime.now().until(scheduledTime, ChronoUnit.MILLIS));
		}
	}
	
	public Future<HttpResponse> executeFuture(HttpUriRequest request, long delayMs) {
		Callable<HttpResponse> callable = () -> {
			return client.execute(request);
		};
        if (delayMs!=0) {
    			return executor.schedule(callable, delayMs, TimeUnit.MILLISECONDS);
        }
        return executor.submit(callable);
	}
}

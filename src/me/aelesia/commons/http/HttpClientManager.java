package me.aelesia.commons.http;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;

public class HttpClientManager {
	
	ExecutorService executorService = Executors.newFixedThreadPool(60);
	HttpClientPool client;
	
	public HttpClientManager() {
		PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
//		cm.setDefaultMaxPerRoute(100);
//		client = HttpClients.createMinimal(cm);
		client = new HttpClientPool(60);
	}
	
	/**
	 * Executes a http request, and waits until it finishes executing then
	 *
	 * @param  request  The HttpRequest object
	 * @return  The http response of the request
	 */
	public HttpResponse executeWait(HttpUriRequest request) throws IOException {
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
        executorService.submit(runnable);
        
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
		Runnable runnable = () -> {
        		try {
				HttpResponse response = client.execute(request);
				listener.executeAfter(response);
			} catch (IOException e) {
				listener.executeOnException(e);
			}
        };
        executorService.submit(runnable);
	}
	
	public Future<HttpResponse> execute(HttpUriRequest request) {
		Callable<HttpResponse> callable = () -> {
			return client.execute(request);
		};
		return executorService.submit(callable);
	}
}

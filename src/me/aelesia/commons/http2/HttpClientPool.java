package me.aelesia.commons.http2;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;

import me.aelesia.commons.logger.Logger;
import me.aelesia.commons.utils.ThreadUtils;

@SuppressWarnings("deprecation")
public class HttpClientPool implements HttpClient {
	
	private static int SLEEP_TIME_MS = 10;
	private int poolSize = 1;
	
	private Queue<HttpClient> idlePool = new LinkedList<HttpClient>();
	private List<HttpClient> activePool = new ArrayList<HttpClient>();
	
	public HttpClientPool(int poolSize) {
		this.poolSize = poolSize;		
		for (int i=0; i<this.poolSize; i++) {
			HttpClient httpClient = createHttpClient();
			idlePool.add(httpClient);
		}
	}

	
	private synchronized HttpClient getClient() {
		if (idlePool.isEmpty()) {
//			String output = "";
//	        for (LocalDateTime value : activePool.values()){
//	            output += value + ", ";
//	        }
			Logger.debug("Waiting for available HTTP client.");
			while (idlePool.isEmpty()) {
				ThreadUtils.sleep(SLEEP_TIME_MS);
			}
		}
		HttpClient client = idlePool.remove();
		activePool.add(client);
		return client;
	}
	
	private synchronized void releaseClient(HttpClient client) {
		activePool.remove(client);
		idlePool.add(client);
	}
	
	private HttpClient createHttpClient() {
		return HttpClients.createDefault();
	}
	
	@Override
	public HttpResponse execute(HttpUriRequest request) throws IOException {
		HttpClient client = this.getClient();
		HttpResponse response = client.execute(request);
		this.releaseClient(client);
		return response;
	}

//	@Override
//	public HttpResponse execute(HttpUriRequest arg0, HttpContext arg1) throws IOException, ClientProtocolException {
//		HttpClient client = this.getClient();
//		HttpResponse response = client.execute(arg0, arg1);
//		this.releaseClient(client);
//		return response;
//	}
//
//	@Override
//	public HttpResponse execute(HttpHost arg0, HttpRequest arg1) throws IOException, ClientProtocolException {
//		HttpClient client = this.getClient();
//		HttpResponse response = client.execute(arg0, arg1);
//		this.releaseClient(client);
//		return response;
//	}
//
//	@Override
//	public <T> T execute(HttpUriRequest arg0, ResponseHandler<? extends T> arg1)
//			throws IOException, ClientProtocolException {
//		HttpClient client = this.getClient();
//		T response = client.execute(arg0, arg1);
//		this.releaseClient(client);
//		return response;
//	}
//
//	@Override
//	public HttpResponse execute(HttpHost arg0, HttpRequest arg1, HttpContext arg2)
//			throws IOException, ClientProtocolException {
//		HttpClient client = this.getClient();
//		HttpResponse response = client.execute(arg0, arg1, arg2);
//		this.releaseClient(client);
//		return response;
//	}
//
//	@Override
//	public <T> T execute(HttpUriRequest arg0, ResponseHandler<? extends T> arg1, HttpContext arg2)
//			throws IOException, ClientProtocolException {
//		HttpClient client = this.getClient();
//		T response = client.execute(arg0, arg1, arg2);
//		this.releaseClient(client);
//		return response;
//	}
//
//	@Override
//	public <T> T execute(HttpHost arg0, HttpRequest arg1, ResponseHandler<? extends T> arg2)
//			throws IOException, ClientProtocolException {
//		HttpClient client = this.getClient();
//		T response = client.execute(arg0, arg1, arg2);
//		this.releaseClient(client);
//		return response;
//	}
//
//	@Override
//	public <T> T execute(HttpHost arg0, HttpRequest arg1, ResponseHandler<? extends T> arg2, HttpContext arg3)
//			throws IOException, ClientProtocolException {
//		HttpClient client = this.getClient();
//		T response = client.execute(arg0, arg1, arg2, arg3);
//		this.releaseClient(client);
//		return response;
//	}

	@Override
	public ClientConnectionManager getConnectionManager() {
		throw new RuntimeException("Method not implemented");
	}

	@Override
	public HttpParams getParams() {
		throw new RuntimeException("Method not implemented");
	}


	@Override
	public HttpResponse execute(HttpUriRequest arg0, HttpContext arg1) throws IOException, ClientProtocolException {
		throw new RuntimeException("Method not implemented");
	}


	@Override
	public HttpResponse execute(HttpHost arg0, HttpRequest arg1) throws IOException, ClientProtocolException {
		throw new RuntimeException("Method not implemented");
	}


	@Override
	public <T> T execute(HttpUriRequest arg0, ResponseHandler<? extends T> arg1)
			throws IOException, ClientProtocolException {
		throw new RuntimeException("Method not implemented");
	}


	@Override
	public HttpResponse execute(HttpHost arg0, HttpRequest arg1, HttpContext arg2)
			throws IOException, ClientProtocolException {
		throw new RuntimeException("Method not implemented");
	}


	@Override
	public <T> T execute(HttpUriRequest arg0, ResponseHandler<? extends T> arg1, HttpContext arg2)
			throws IOException, ClientProtocolException {
		throw new RuntimeException("Method not implemented");
	}


	@Override
	public <T> T execute(HttpHost arg0, HttpRequest arg1, ResponseHandler<? extends T> arg2)
			throws IOException, ClientProtocolException {
		throw new RuntimeException("Method not implemented");
	}


	@Override
	public <T> T execute(HttpHost arg0, HttpRequest arg1, ResponseHandler<? extends T> arg2, HttpContext arg3)
			throws IOException, ClientProtocolException {
		throw new RuntimeException("Method not implemented");
	}
}

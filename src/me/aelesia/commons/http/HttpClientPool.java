package me.aelesia.commons.http;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import me.aelesia.commons.utils.ThreadUtils;

@Deprecated
public class HttpClientPool {
	
	private static int SLEEP_TIME_MS = 10;
	private int poolSize = 1;
	
	private Queue<HttpClient> idlePool = new LinkedList<HttpClient>();
	private List<HttpClient> activePool = new ArrayList<HttpClient>();
	
	public HttpClientPool(int poolSize) {
		this.poolSize = poolSize;		
		for (int i=0; i<this.poolSize; i++) {
			CloseableHttpClient httpClient = HttpClients.createDefault();
			idlePool.add(httpClient);
		}
	}
	
	public HttpResponse execute(HttpUriRequest request) throws IOException {
		HttpClient client = this.getClient();
		HttpResponse response = client.execute(request);
		this.releaseClient(client);
		return response;
	}
	
	private synchronized HttpClient getClient() {
		if (idlePool.isEmpty()) {
//			String output = "";
//	        for (LocalDateTime value : activePool.values()){
//	            output += value + ", ";
//	        }
//			Logger.debug("Waiting for available HTTP client. List of HTTP Clients: " + output);
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
}

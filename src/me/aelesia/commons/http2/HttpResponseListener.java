package me.aelesia.commons.http2;

import org.apache.http.HttpResponse;

public abstract class HttpResponseListener {
	public abstract void executeAfter(HttpResponse response);
	
	public abstract void executeOnException(Exception e);
	
	public void executeBefore() {};
}

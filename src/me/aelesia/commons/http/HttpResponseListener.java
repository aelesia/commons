package me.aelesia.commons.http;

import org.apache.http.HttpResponse;

@Deprecated
public abstract class HttpResponseListener {
	public abstract void executeAfter(HttpResponse response);
	
	public abstract void executeOnException(Exception e);
}

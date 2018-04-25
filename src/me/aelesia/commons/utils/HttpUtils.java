package me.aelesia.commons.utils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

public class HttpUtils {
	/**
	 * Returns the body content of a HttpResponse object
	 * 
	 * @param HttpResponse  
	 */
	public static String entityToString(HttpResponse response) {
		try {
			HttpEntity entity =  response.getEntity();
			String entityStr = EntityUtils.toString(entity);
			EntityUtils.consume(entity);
			return entityStr;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static HttpGet generateGet(String url, String... params) {		
		List <NameValuePair> nvps = new ArrayList <NameValuePair>();
		
		try {
			for (String param : params) {
				nvps.add(new BasicNameValuePair(param.split(":")[0], param.split(":")[1]));
			}
		} catch (NullPointerException | ArrayIndexOutOfBoundsException e) {
			throw new IllegalArgumentException("Incorrect format for parameter " + params + ". Use 'key:value' format," , e);
		}
		return HttpUtils.generateGet(url, nvps);
	}
	
	public static HttpGet generateGet(String url, List<NameValuePair> params) {
		URIBuilder builder;
		URI uri;
		try {
			builder = new URIBuilder(url);
			if (params!=null) {
				for (NameValuePair nvp : params) {
					builder.setParameter(nvp.getName(), nvp.getValue());
				}
			}
			uri = builder.build(); 
		} catch (URISyntaxException e) {
			e.printStackTrace();
			throw new RuntimeException();
		}
		HttpGet httpGet = new HttpGet(uri);
		return httpGet;
	}
	
	public static HttpPost generatePost(String url, String... params) {		
		List <NameValuePair> nvps = new ArrayList <NameValuePair>();
		
		try {
			for (String param : params) {
				nvps.add(new BasicNameValuePair(param.split(":")[0], param.split(":")[1]));
			}
		} catch (NullPointerException | ArrayIndexOutOfBoundsException e) {
			throw new IllegalArgumentException("Incorrect format for parameter " + params + ". Use 'key:value' format," , e);
		}
		return HttpUtils.generatePost(url, nvps);
	}
	
	public static HttpPost generatePost(String url, List<NameValuePair> params) {
		try {		
			HttpPost httpPost = new HttpPost(url);
			httpPost.setEntity(new UrlEncodedFormEntity(params));
			return httpPost;
		} catch (UnsupportedEncodingException e) {
			throw new IllegalArgumentException("Encoding is not supported", e);
		}
	}
}

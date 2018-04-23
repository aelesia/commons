package me.aelesia.reddit.api;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import me.aelesia.commons.logger.Logger;
import me.aelesia.commons.utils.ThreadUtils;
import me.aelesia.reddit.api.consts.URL;
import me.aelesia.reddit.api.objects.Token;
import me.aelesia.reddit.api.utils.Mapper;

public class O2AClient {	
	private HttpClient httpClient;
	private Token token;
	
	private String userAgent;
	private String username;
	private String password;
	private String clientBase64;
	
	public O2AClient(String username, String password, String appId, String secretKey, String userAgent) {
		this.httpClient = HttpClients.createDefault();
		this.username = username;
		this.password = password;
		byte[] encodedBytes = Base64.encodeBase64((appId+":"+secretKey).getBytes());
		this.clientBase64 = new String(encodedBytes);
		this.userAgent = userAgent;
	}


	/**
	 * Retrieves a new token of grant_type=password from URL.ACCESS_TOKEN
	 * Stores the token so that O2A operations may be performed later
	 */
	private void obtainToken() throws ClientProtocolException, IOException, IllegalArgumentException {
		Logger.info("Retriving new token");
		List <NameValuePair> nvps = new ArrayList <NameValuePair>();
		nvps.add(new BasicNameValuePair("grant_type", "password"));
		nvps.add(new BasicNameValuePair("username", username));
		nvps.add(new BasicNameValuePair("password", password));	

		HttpPost httpPost = new HttpPost(URL.ACCESS_TOKEN);
		httpPost.setHeader("User-Agent", userAgent);
		httpPost.setHeader( "Authorization", ("Basic " +  clientBase64));
		httpPost.setEntity(new UrlEncodedFormEntity(nvps));
		String responseBody = entityToString(httpClient.execute(httpPost));
		if (!responseBody.contains("access_token")) {
			throw new IllegalArgumentException("Access token not found in parameters");
		}
		this.token = Mapper.extractToken(responseBody);
		Logger.info(this.token);
	}
	
	/**
	 * Checks if token is valid
	 * Retrieves a new token if token is invalid or expiring
	 */
	private void checkAndObtainToken() {
		if (token == null) {
			Logger.info("Token not yet initialized");
		} else if (LocalDateTime.now().isAfter(token.expiresOn.minusMinutes(5))) {
			Logger.info("Token expiring/expired");
		} else {
			return;
		}
		int i=0;
		while (true) {
			try {
				obtainToken();
				break;
			} catch (IllegalArgumentException | IOException e) {
				Logger.warn("Unable to obtain token. Retry attempt: " + ++i + ". Retrying after " + Math.pow(Math.min(i, 10), 3) + " seconds");
				ThreadUtils.sleep((int)(Math.pow(Math.min(i, 10), 3)*1000));
			}
		}
	}
	
	/**
	 * Performs a normal HTTP Post operation and returns the response body as a String
	 * 
	 * @param url  address 
	 * @param params  list of parameters
	 */
	public String post(String url, List<NameValuePair> param) {
		try {
			HttpPost httpPost = new HttpPost(url);
			httpPost.setHeader("User-Agent", userAgent);
			httpPost.setEntity(new UrlEncodedFormEntity(param));
			HttpResponse response = httpClient.execute(httpPost);
			return entityToString(response);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Performs a normal HTTP Get operation and returns the response body as a String
	 * 
	 * @param url  address 
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 */
	public String get(String url) throws ClientProtocolException, IOException {
		return get(url, null);
	}
	
	
	/**
	 * Performs a normal HTTP Post operation and returns the response body as a String
	 * 
	 * @param url  address 
	 * @param params  list of parameters
	 * @throws URISyntaxException 
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 */
	public String get(String url, List<NameValuePair> param) throws ClientProtocolException, IOException {
		URIBuilder builder;
		URI uri;
		try {
			builder = new URIBuilder(url);
			if (param!=null) {
				for (NameValuePair nvp : param) {
					builder.setParameter(nvp.getName(), nvp.getValue());
				}
			}
			uri = builder.build(); 
		} catch (URISyntaxException e) {
			e.printStackTrace();
			throw new RuntimeException();
		}
		HttpGet httpGet = new HttpGet(uri);
		httpGet.setHeader("User-Agent", userAgent);
		HttpResponse response = httpClient.execute(httpGet);
		return entityToString(response);
	}

	/**
	 * Performs an O2A Post operation with the Authorization token populated
	 * 
	 * @param url  address 
	 * @param params  list of parameters
	 */
	public String o2aPost(String url, List<NameValuePair> param) {
		checkAndObtainToken();
		try {
			HttpPost httpPost = new HttpPost(url);
			httpPost.setHeader("User-Agent", userAgent);
			httpPost.setHeader( "Authorization", (token.tokenType + " " + token.accessToken));
			httpPost.setEntity(new UrlEncodedFormEntity(param));
			HttpResponse response = httpClient.execute(httpPost);
			return entityToString(response);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Performs an O2A Get operation with the Authorization token populated
	 * 
	 * @param url  address 
	 */
	public String o2aGet(String url) {
		checkAndObtainToken();
		try {
			HttpGet httpGet = new HttpGet(url);
			httpGet.setHeader("User-Agent", userAgent);
			httpGet.setHeader( "Authorization", (token.tokenType + " " + token.accessToken));
			HttpResponse response = httpClient.execute(httpGet);
			return entityToString(response);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
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
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException();
		}
	}
}

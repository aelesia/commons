package me.aelesia.reddit.api2;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.message.BasicNameValuePair;
import me.aelesia.commons.http2.HttpClientManager;
import me.aelesia.commons.http2.HttpResponseListener;
import me.aelesia.commons.logger.Logger;
import me.aelesia.commons.utils.HttpUtils;
import me.aelesia.commons.utils.ThreadUtils;
import me.aelesia.reddit.api2.URL;
import me.aelesia.reddit.api2.objects.RateLimit;
import me.aelesia.reddit.api2.objects.Token;

public class O2AClient {
	Object checkAndObtainTokenLock = new Object();
	Object calculateNextRequestTimeLock = new Object();
	
	private final static int THROTTLE_NUM = 60;
	
	private HttpClientManager httpClient;
	
	private RateLimit rateLimit = new RateLimit();
	private Token token;
	private LocalDateTime throttleTime = LocalDateTime.MIN;
	
	private String username;
	private String password;
	private String clientBase64;
	private String userAgent;
	
	public O2AClient(HttpClientManager httpClient, String username, String password, String appId, String secretKey, String userAgent) {
		this.httpClient = httpClient;
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
		List <NameValuePair> params = new ArrayList <NameValuePair>();
		params.add(new BasicNameValuePair("grant_type", "password"));
		params.add(new BasicNameValuePair("username", username));
		params.add(new BasicNameValuePair("password", password));	

		HttpPost httpPost = HttpUtils.generatePost(URL.ACCESS_TOKEN, params);
		httpPost.setHeader("User-Agent", userAgent);
		httpPost.setHeader( "Authorization", ("Basic " +  clientBase64));
		String responseBody = HttpUtils.entityToString(httpClient.execute(httpPost));
		System.out.println(responseBody);
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
		synchronized(checkAndObtainTokenLock) {
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
					ThreadUtils.sleepFor((int)(Math.pow(Math.min(i, 10), 3)*1000));
				}
			}
		}
	}

	
	private void refreshRateLimit(HttpResponse response) {
		try {
			RateLimit newRateLimit = Mapper.extractRatelimit(response);
			if (LocalDateTime.now().plusSeconds(1).isAfter(this.rateLimit.resetTime) || newRateLimit.remaining < this.rateLimit.remaining) {
				if (this.rateLimit.remaining<0) {
					newRateLimit.used -= this.rateLimit.remaining;
					newRateLimit.remaining += this.rateLimit.remaining;
				}
				this.rateLimit = newRateLimit;
				Logger.debug("Updating x-ratelimit: " + this.rateLimit);
			}
		} catch (NoSuchFieldException e) {
			Logger.warn("Unable to parse latest x-ratelimit");
		}
	}
	
	private synchronized LocalDateTime calculateNextRequestTime() {
		synchronized (calculateNextRequestTimeLock) {
			this.rateLimit.used++;
			this.rateLimit.remaining--;
			Logger.debug("x-ratelimit-used: " + this.rateLimit.used + ", x-ratelimit-remaining: " + this.rateLimit.remaining);
			if (this.rateLimit.remaining > THROTTLE_NUM || LocalDateTime.now().isAfter(this.rateLimit.resetTime)) {
				return null;
			} 
			
			if (this.rateLimit.remaining<=THROTTLE_NUM  &&  this.rateLimit.remaining>0) {
				if (LocalDateTime.now().isBefore(this.throttleTime)) {
					long delay = this.throttleTime.until(rateLimit.resetTime, ChronoUnit.MILLIS) / this.rateLimit.remaining;
					this.throttleTime = this.throttleTime.plus(delay, ChronoUnit.MILLIS);
				} else {
					long delay = LocalDateTime.now().until(rateLimit.resetTime, ChronoUnit.MILLIS) / this.rateLimit.remaining;
					this.throttleTime = LocalDateTime.now().plus(delay, ChronoUnit.MILLIS);
				}
				Logger.info("Throttling x-ratelimit requests, will be executed on: " + this.throttleTime + ". Remaining: " + this.rateLimit.remaining);
			} else if (this.rateLimit.remaining<=0  &&  this.rateLimit.remaining>-600) {
				this.throttleTime = this.rateLimit.resetTime;
				Logger.info("Exceeded x-ratelimit requests. Request will be executed in " + rateLimit.resetTime);
			} else {
				throw new IllegalStateException("Number of backlogged requests exceeded x-ratelimit window of 600");
			}
			return this.throttleTime;
		}
	}
	
	public String execute(HttpUriRequest request) throws IOException {
		request.setHeader("User-Agent", userAgent);
		return HttpUtils.entityToString(httpClient.execute(request));
	}
	
	public static AtomicInteger count = new AtomicInteger(0);
	
	public String executeO2A(HttpUriRequest request) throws IOException {
		this.checkAndObtainToken();
		LocalDateTime scheduledTime = this.calculateNextRequestTime();
		if (scheduledTime!=null) {
			ThreadUtils.sleepUntil(scheduledTime);
		}
		request.setHeader("User-Agent", userAgent);
		request.setHeader("Authorization", (token.tokenType + " " + token.accessToken));
		HttpResponse response = httpClient.execute(request);
		this.refreshRateLimit(response);
		return HttpUtils.entityToString(response);
	}
	
//	public void UDPexecuteO2A(HttpUriRequest request, int i) {
//
//		LocalDateTime scheduledTime = this.calculateNextRequestTime();
//		httpClient.execute(request, scheduledTime, new HttpResponseListener() {
//			@Override public void executeBefore() {
//				checkAndObtainToken();
//				request.setHeader("User-Agent", userAgent);
//				request.setHeader("Authorization", (token.tokenType + " " + token.accessToken));
//			}
//			
//			@Override public void executeOnException(Exception e) {
//				e.printStackTrace();
//			}
//			@Override public void executeAfter(HttpResponse response) { 
//				refreshRateLimit(response);
//				HttpUtils.entityToString(response);
//				System.out.println("Scheduled: " + scheduledTime + ", Completed: " + LocalDateTime.now() + ", Done #"+i + ", Total: " + count.incrementAndGet());
//			}
//		});
//	}
}

package me.aelesia.reddit.api2;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import me.aelesia.reddit.api2.objects.RateLimit;
import me.aelesia.reddit.api2.objects.RedditPost;
import me.aelesia.reddit.api2.objects.Token;

class Mapper {
	
	private static JsonParser jsonParser = new JsonParser();
	
	public static List<RedditPost> mapPosts(String json) {
		List<RedditPost> commentList = new ArrayList<RedditPost>();
		JsonObject jsonObject = jsonParser.parse(json).getAsJsonObject();
		JsonArray jsonArray = jsonObject.get("data").getAsJsonObject().get("children").getAsJsonArray();
		for(int i=0; i<jsonArray.size(); i++) {
			String kind = jsonArray.get(i).getAsJsonObject().get("kind").getAsString();
			JsonObject data  = jsonArray.get(i).getAsJsonObject().get("data").getAsJsonObject();
			RedditPost post;
			if ("t1".equals(kind)) {
				post = mapT1(data);
			} else if ("t3".equals(kind)) {
				post = mapT3(data);
			} else {
				throw new IllegalArgumentException("Unrecognized kind: " + kind);
			}
			commentList.add(post);
		}
		return commentList;
	}
	
	/**
	 * Maps a Reddit comments json to a RedditPost object
	 * 
	 * @param json  Reddit comments json
	 * @output List<RedditPost>  
	 */
	private static RedditPost mapT1(JsonObject data) { 
		RedditPost post = new RedditPost();
		post.kind = "t1";
		post.partialId = data.get("id").getAsString();
		post.subreddit = data.get("subreddit").getAsString();
		post.subredditId = data.get("subreddit_id").getAsString();
		post.parentId = data.get("parent_id").getAsString();
		post.threadId = data.get("link_id").getAsString();
		post.threadAuthor = data.get("link_author").getAsString();
		post.threadTitle = data.get("link_title").getAsString();
		post.threadUrl = data.get("link_url").getAsString();
		post.author = data.get("author").getAsString();
		post.text = data.get("body").getAsString();
		post.url = "https://www.reddit.com" + data.get("permalink").getAsString();
		post.setCreatedOn(data.get("created_utc").getAsLong());
		return post;
	}
	
	/**
	 * Maps a Reddit thread json to a RedditPost object
	 * 
	 * @param json  Reddit thread json
	 * @output List<RedditPost>  
	 */
	private static RedditPost mapT3(JsonObject data) { 
		RedditPost post = new RedditPost();
		post.kind = "t3";
		post.partialId = data.get("id").getAsString();
		post.subreddit = data.get("subreddit").getAsString();
		post.subredditId = data.get("subreddit_id").getAsString();
		post.author = data.get("author").getAsString();
		post.threadTitle = data.get("title").getAsString();
		post.text = data.get("selftext").getAsString();
		post.url = "https://www.reddit.com" + data.get("permalink").getAsString();
		post.threadId = post.id();
		post.threadAuthor = post.author;
		post.threadUrl = post.url;
		post.setCreatedOn(data.get("created_utc").getAsLong());
		return post;
	}
	
	/**
	 * Maps a Reddit access_token json to a Token object
	 * 
	 * @param json  Reddit access_token json
	 * @output Token
	 */
	public static Token extractToken(String json) { 
		JsonObject jsonObject = new JsonParser().parse(json).getAsJsonObject();
		Token token = new Token();
		token.accessToken = jsonObject.get("access_token").getAsString();
		token.tokenType = jsonObject.get("token_type").getAsString();
		long expiresIn = jsonObject.get("expires_in").getAsLong();
		token.expiresOn = LocalDateTime.now().plusSeconds(expiresIn);
		return token;
	}
	
	public static RateLimit extractRatelimit(HttpResponse response) throws NoSuchFieldException {
		try {
			RateLimit rateLimit = new RateLimit();
			rateLimit.remaining = (int)Double.parseDouble(response.getFirstHeader("x-ratelimit-remaining").getValue());
			rateLimit.used = (int)Double.parseDouble(response.getFirstHeader("x-ratelimit-used").getValue());
			rateLimit.resetTime = LocalDateTime.now().plusSeconds((long)Double.parseDouble(response.getFirstHeader("x-ratelimit-reset").getValue()));
			return rateLimit;
		} catch (NullPointerException | NumberFormatException e) {
			throw new NoSuchFieldException("Unable to parse x-ratelimit from headers: " + response.getAllHeaders());
		}
	}
}
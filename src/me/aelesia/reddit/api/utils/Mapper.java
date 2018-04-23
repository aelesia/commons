package me.aelesia.reddit.api.utils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import me.aelesia.reddit.api.objects.RedditPost;
import me.aelesia.reddit.api.objects.Token;

public class Mapper {
	
	private static JsonParser jsonParser = new JsonParser();
	
	/**
	 * Maps a Reddit comments json to a RedditPost object
	 * 
	 * @param json  Reddit comments json
	 * @output List<RedditPost>  
	 */
	public static List<RedditPost> mapComments(String json) { 

		List<RedditPost> commentList = new ArrayList<RedditPost>();
		JsonObject jsonObject = jsonParser.parse(json).getAsJsonObject();
		JsonArray jsonArray = jsonObject.get("data").getAsJsonObject().get("children").getAsJsonArray();
		for(int i=0; i<jsonArray.size(); i++) {
			RedditPost post = new RedditPost();
			post.kind = jsonArray.get(i).getAsJsonObject().get("kind").getAsString();
			JsonObject data  = jsonArray.get(i).getAsJsonObject().get("data").getAsJsonObject();
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
			commentList.add(post);
		}

		return commentList;
	}
	
	/**
	 * Maps a Reddit thread json to a RedditPost object
	 * 
	 * @param json  Reddit thread json
	 * @output List<RedditPost>  
	 */
	public static List<RedditPost> mapThreads(String json) { 

		List<RedditPost> postList = new ArrayList<RedditPost>();
		JsonObject jsonObject = jsonParser.parse(json).getAsJsonObject();
		JsonArray jsonArray = jsonObject.get("data").getAsJsonObject().get("children").getAsJsonArray();
		for(int i=0; i<jsonArray.size(); i++) {
			RedditPost post = new RedditPost();
			post.kind = jsonArray.get(i).getAsJsonObject().get("kind").getAsString();
			JsonObject data  = jsonArray.get(i).getAsJsonObject().get("data").getAsJsonObject();			
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
			postList.add(post);
		}

		return postList;
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
}
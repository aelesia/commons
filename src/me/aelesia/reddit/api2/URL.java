package me.aelesia.reddit.api2;

public class URL {
	public static String ME = "https://oauth.reddit.com/api/v1/me";
	public static String ACCESS_TOKEN = "https://www.reddit.com/api/v1/access_token";
	public static String REPLY = "https://oauth.reddit.com/api/comment";
	public static String DELETE = "https://oauth.reddit.com/api/del";
	
	public static String COMMENTS(String subreddit) {
		return "https://reddit.com/r/"+subreddit+"/comments.json";
	}
	
	public static String POSTS(String subreddit) {
			return "https://reddit.com/r/"+subreddit+"/new.json";
	}
	
	public static String USER(String username) {
		return "https://reddit.com/user/"+username+".json";
	}
}

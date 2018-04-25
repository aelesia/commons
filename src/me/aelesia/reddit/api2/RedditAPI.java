package me.aelesia.reddit.api2;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import me.aelesia.commons.logger.Logger;
import me.aelesia.commons.utils.HttpUtils;
import me.aelesia.reddit.api2.Mapper;
import me.aelesia.reddit.api2.O2AClient;
import me.aelesia.reddit.api2.URL;
import me.aelesia.reddit.api2.objects.RedditPost;

public class RedditAPI {
	private O2AClient o2aClient;
	
	private int commentLimit = 10;
	private int threadLimit = 5;
	
	public RedditAPI(O2AClient o2aClient) {
		this.o2aClient = o2aClient;
	}
	
	/**
	 * Sets the number of comments that are retrieved
	 */
	public void setCommentLimit(int commentLimit) {
		if (this.commentLimit < 1 || this.commentLimit > 100) {
			this.commentLimit = 100;
		} else {
			this.commentLimit = commentLimit;
		}
	}
	
	/**
	 * Sets the number of threads that are retrieved
	 */
	public void setThreadLimit(int threadLimit) {
		if (this.threadLimit < 1 || this.threadLimit > 100) {
			this.threadLimit=100;
		} else {
			this.threadLimit = threadLimit;
		}
	}

	
	/**
	 * Self-test
	 * @throws IOException 
	 */
	public String me() throws IOException {
		HttpUriRequest request = new HttpGet(URL.ME);
		return o2aClient.executeO2A(request);
	}
	
//	public void UDPme(int i) throws IOException {
//		HttpUriRequest request = new HttpGet(URL.ME);
//		o2aClient.UDPexecuteO2A(request, i);
//	}
	
	/**
	 * Retrieves the latest comments from a specific subreddit and converts it to a RedditPost object.
	 * The number of comments that are retrieved can be configured using 'setCommentLimit(int)'
	 *
	 * @param subreddit  Name of the subreddit that you wish to retrieve new comments from.
	 * @return List<RedditPost>  An ArrayList of RedditPost. 
	 */
	public List<RedditPost> retrieveNewComments(String subreddit) {
		List<RedditPost> commentsList;
		try {
			HttpUriRequest request = HttpUtils.generateGet(URL.COMMENTS(subreddit), ("limit:"+commentLimit));
			String commentsJson = o2aClient.execute(request);
			commentsList = Mapper.mapPosts(commentsJson);
			Logger.debug("Retrived " + commentsList.size() + " comments from /r/" + subreddit);
		} catch (IOException e) {
			commentsList = new ArrayList<RedditPost>();
			Logger.warn("Failed to load any comments from: " + URL.COMMENTS(subreddit));
		} 
		return commentsList;
	}
	
	/**
	 * Retrieves the latest threads from a specific subreddit and converts it to a RedditPost object.
	 * The number of comments that are retrieved can be configured using 'setThreadLimit(int)'
	 *
	 * @param subreddit  Name of the subreddit that you wish to retrieve new threads from.
	 * @return List<RedditPost>  An ArrayList of RedditPost. 
	 */
	public List<RedditPost> retrieveNewThreads(String subreddit) {
		List<RedditPost> threadList;
		try {
			HttpUriRequest request = HttpUtils.generateGet(URL.COMMENTS(subreddit), ("limit:"+threadLimit));
			String threadsJson = o2aClient.execute(request);
			threadList = Mapper.mapPosts(threadsJson);
			Logger.debug("Retrieved " + threadList.size() + " threads from /r/" + subreddit);
		} catch (IOException e) {
			threadList = new ArrayList<RedditPost>();
			Logger.warn("Failed to retrieve any threads from: " + URL.POSTS(subreddit));
		} 
		return threadList;
	}
	
	public List<RedditPost> retrieveAllUserPosts(String username) {
		List<RedditPost> masterList = this.retrieveUserPosts(username, null);
		List<RedditPost> subList;
		do {
			subList = this.retrieveUserPosts(username, masterList.get(masterList.size()-1).id());
			masterList.addAll(subList);
		}
		while (subList.size()==100);
		return masterList;
	}

	public List<RedditPost> retrieveUserPosts(String username, String after) {
		
		List<RedditPost> postList;
		try {
			HttpUriRequest request = HttpUtils.generateGet(URL.USER(username), "limit:100", ("after:"+after));
			String json = o2aClient.execute(request);
			postList = Mapper.mapPosts(json);
			Logger.debug("Retrieved " + postList.size() + " posts from /u/" + username);
		} catch (IOException e) {
			postList = new ArrayList<RedditPost>();
			Logger.warn("Failed to retrieve any posts from: " + URL.USER(username));
		} 
		return postList;
	}
	
	/**
	 * Replies to the specified thing_id with the message content
	 * 
	 * @param id  thing_id of the post you wish to reply to
	 * @param text  body of the message
	 * @throws IOException 
	 */
	public void reply(String id, String text) throws IOException {
		HttpUriRequest request = HttpUtils.generatePost(URL.REPLY, ("thing_id:"+id), ("text:"+text));
		o2aClient.executeO2A(request);
		Logger.info("Replied to #"+id);
	}
	
	public void delete(String id) throws IOException {
		HttpUriRequest request = HttpUtils.generatePost(URL.DELETE, ("id:"+id));
		System.out.println(o2aClient.executeO2A(request));
		Logger.info("Deleted #"+id);
	}
}

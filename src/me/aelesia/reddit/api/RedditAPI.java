package me.aelesia.reddit.api;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import me.aelesia.commons.logger.Logger;
import me.aelesia.reddit.api.consts.URL;
import me.aelesia.reddit.api.objects.RedditPost;
import me.aelesia.reddit.api.utils.Mapper;

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
	 */
	public String me() {
		return o2aClient.o2aGet(URL.ME);
	}
	
	/**
	 * Retrieves the latest comments from a specific subreddit and converts it to a RedditPost object.
	 * The number of comments that are retrieved can be configured using 'setCommentLimit(int)'
	 *
	 * @param subreddit  Name of the subreddit that you wish to retrieve new comments from.
	 * @return List<RedditPost>  An ArrayList of RedditPost. 
	 */
	public List<RedditPost> retrieveNewComments(String subreddit) {
		List <NameValuePair> nvps = new ArrayList <NameValuePair>();
		nvps.add(new BasicNameValuePair("limit", Integer.toString(commentLimit)));
		
		List<RedditPost> commentsList;
		String commentsJson = null;
		try {
			commentsJson = o2aClient.get(URL.COMMENTS(subreddit), nvps);
			commentsList = Mapper.mapComments(commentsJson);
			Logger.debug("Retrived " + commentsList.size() + " comments from /r/" + subreddit);
		} catch (Exception e) {
			commentsList = new ArrayList<RedditPost>();
			if (commentsJson!=null) { 
	 			Logger.warn("JSON could not be parsed: \n" + commentsJson);
			} else{
				Logger.warn("Failed to load any comments from: " + URL.COMMENTS(subreddit));
			}
			e.printStackTrace();
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
		List <NameValuePair> nvps = new ArrayList <NameValuePair>();
		nvps.add(new BasicNameValuePair("limit", Integer.toString(threadLimit)));

		List<RedditPost> threadList;
		String threadsJson = null;
		try {
			threadsJson = o2aClient.get(URL.POSTS(subreddit), nvps);
			threadList = Mapper.mapThreads(threadsJson);
			Logger.debug("Retrieved " + threadList.size() + " threads from /r/" + subreddit);
		} catch (Exception e) {
			threadList = new ArrayList<RedditPost>();
			if (threadsJson != null) {
				Logger.warn("JSON could not be parsed" + threadsJson);
			} else {
				Logger.warn("Failed to retrieve any threads from: " + URL.POSTS(subreddit));
			}
			e.printStackTrace();
		} 
		return threadList;
	}
	
	/**
	 * Replies to the specified thing_id with the message content
	 * 
	 * @param id  thing_id of the post you wish to reply to
	 * @param text  body of the message
	 * @param signature  your signature
	 */
	public void reply(String id, String text, String signature) {	
		String textWithSignature = text;
		textWithSignature += "\n\n_________________________\n\n";
		textWithSignature += signature;
		this.reply(id, textWithSignature);
	}
	
	/**
	 * Replies to the specified thing_id with the message content
	 * 
	 * @param id  thing_id of the post you wish to reply to
	 * @param text  body of the message
	 */
	public void reply(String id, String text) {		
		List <NameValuePair> nvps = new ArrayList <NameValuePair>();
		nvps.add(new BasicNameValuePair("thing_id", id));
		nvps.add(new BasicNameValuePair("text", text));
		o2aClient.o2aPost(URL.REPLY, nvps);
		Logger.info("Replied to #"+id);
	}
	
}

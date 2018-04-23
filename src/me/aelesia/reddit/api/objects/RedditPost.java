package me.aelesia.reddit.api.objects;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class RedditPost {
	public String kind;
	public String partialId;
	
	public LocalDateTime createdOn;
	public String url;
	public String author;
	
	public String subreddit;
	public String subredditId;
	public String parentId;
	public String threadId;
	public String threadAuthor;
	public String threadTitle;
	public String threadUrl;

	public String text;
	
	public void setCreatedOn(long epochSeconds) {
		this.createdOn = LocalDateTime.ofInstant(Instant.ofEpochSecond(epochSeconds), ZoneId.of("Asia/Singapore"));
	}
	
	public LocalDateTime createdOn() {
		return this.createdOn;
	}
	
	public String id() {
		return (kind + "_" + partialId);
	}
	
	public boolean isThread() {
		if ("t3".equals(this.kind)) {
			return true;
		}
		return false;
	}
	
	public boolean isPost() {
		if ("t1".equals(this.kind)) {
			return true;
		}
		return false;
	}
	
	@Override
	public String toString() {
		return "[" +
				"id:" + id() +
				", kind:" + kind +
				", partialId:" + partialId +
				", createdOn:" + createdOn +
				", url:" + url +
				", author:" + text + 
				", subreddit:" + subreddit + 
				", subredditId:" + subredditId + 
				", parentId:" + parentId + 
				", threadId:" + threadId + 
				", threadAuthor:" + threadAuthor + 
				", threadTitle:" + threadTitle + 
				", threadUrl:" + threadUrl + 
				", text:" + text + 
				"]";
	}
}
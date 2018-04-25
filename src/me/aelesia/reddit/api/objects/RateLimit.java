package me.aelesia.reddit.api.objects;

import java.time.LocalDateTime;

@Deprecated
public class RateLimit {
	public int used = 0;
	public int remaining = 0;
	public LocalDateTime resetTime = LocalDateTime.MIN;
	
	@Override
	public String toString() {
		return String.format("rateLimit[used: %s, remaining: %s, resetTime :%s]", used, remaining, resetTime.toString());
	}
}

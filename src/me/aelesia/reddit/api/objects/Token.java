package me.aelesia.reddit.api.objects;

import java.time.LocalDateTime;

public class Token {
	public String accessToken;
	public String tokenType;
	public LocalDateTime expiresOn;
	
	@Override
	public String toString() {
		return "accessToken: " + accessToken +
				", tokenType: " + tokenType +
				", expiresOn: " + expiresOn;
	}
}

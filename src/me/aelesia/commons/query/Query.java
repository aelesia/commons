package me.aelesia.commons.query;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Query {
//	public String query;
	public String command;
	private Map<String, String> paramMap = new HashMap<String, String>();
	public LocalDateTime createOn;
	
	public Query(String query) {
		this.createOn = LocalDateTime.now();
//		query = query.replace(", ", ",");
		String[] params = query.split(" ");
		this.command = params[0];
		for (int i=1; i<params.length; i++) {
			String[] keyValue = params[i].split(":");
			if (keyValue.length==2) {
				this.paramMap.put(keyValue[0].toLowerCase(), keyValue[1]);
			}
		}
	}
	
	public String getStr(String key) {
		return paramMap.get(key.toLowerCase());
	}
	
	public int getInt(String key) {
		return Integer.parseInt(getStr(key));
	}
	
	public List<String> getList(String key) {
		String valueString = getStr(key);
		String[] valueArray = valueString.split(",");
		return Arrays.asList(valueArray);
	}
	
	public List<String> getListAsLowercase(String key) {
		String valueString = getStr(key);
		List<String> lowerCaseList = new ArrayList<String>();
		if (valueString != null) {
			String[] valueArray = valueString.split(",");
			for (String str : valueArray) {
				lowerCaseList.add(str.toLowerCase());
			}
		}
		return lowerCaseList;
	}
	
	public LocalDateTime getLocalDateTime(String key) {
		if (getStr(key) == null) {
			return null;
		}
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/MM/yyyy");
		return LocalDateTime.from(LocalDate.parse(getStr(key), formatter).atStartOfDay(ZoneId.of("+8")));
	}
}

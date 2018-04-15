package me.aelesia.commons.storage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import me.aelesia.commons.logger.Logger;

public class JsonTextStorage implements Storage {

	Gson gson;
	
	public JsonTextStorage() {
		gson = new Gson();
	}
	
	@Override
	public void save(Object data, String location) {
		String json = gson.toJson(data);
		FileWriter fw;
		try {
			fw = new FileWriter(location);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(json);
			bw.close();
			Logger.info("Saved file: " + location);
		} catch (IOException e) {
			Logger.warn("WARNING: File cannot be saved. Data may be lost upon restart.");
			e.printStackTrace();
		}
	}


	@Override
	public Object load(String location, Class c) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(location));
	    String json = br.readLine();
	    Object o = null;
	    if (StringUtils.isNotBlank(json)) {
   			o = gson.fromJson(json, c);
	    }
	    br.close();
		Logger.info("Loaded file: " + location);
		return o;
	}

	@Override
	public List<Object> loadList(String location, Class c) throws IOException {
		List<Object> classList = new ArrayList<Object>();
		BufferedReader br = new BufferedReader(new FileReader(location));
	    String json = br.readLine();
	    if (StringUtils.isNotBlank(json)) {
		    JsonArray jsonArray = new JsonParser().parse(json).getAsJsonArray();
		    for(int i=0; i<jsonArray.size(); i++) {
   			    Object o = gson.fromJson(jsonArray.get(i), c);
   			    classList.add(o);
		    }
	    }
	    br.close();
		Logger.info("Loaded file: " + location);
		return classList;
	}


}

package me.aelesia.commons.storage;

import java.io.IOException;
import java.util.List;

public interface Storage {
	public void save(Object data, String location);
	public Object load(String location, Class c) throws IOException;
	public List<Object> loadList(String location, Class c) throws IOException;
}

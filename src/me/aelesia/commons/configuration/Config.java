package me.aelesia.commons.configuration;

import java.util.List;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.PropertiesConfigurationLayout;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.convert.DefaultListDelimiterHandler;
import org.apache.commons.configuration2.convert.ListDelimiterHandler;
import org.apache.commons.configuration2.ex.ConfigurationException;

import me.aelesia.commons.logger.Logger;

public abstract class Config {
	
	protected Configuration config;
	String fileName;
	FileBasedConfigurationBuilder<FileBasedConfiguration> builder;
	
	public Config(String fileName) {
		this.fileName = fileName;
		Parameters params = new Parameters();
		ListDelimiterHandler delimiter = new DefaultListDelimiterHandler(',');
		PropertiesConfigurationLayout layout = new PropertiesConfigurationLayout();
		layout.setForceSingleLine(true);
		builder = new FileBasedConfigurationBuilder<FileBasedConfiguration>(PropertiesConfiguration.class);
		builder.configure(params.properties().setFileName(fileName).setListDelimiterHandler(delimiter).setLayout(layout));
		this.load();
		Logger.info(this.toString());
	}
	
	protected void load() {
		try {
			config = builder.getConfiguration();
			map();
		} catch(ConfigurationException e) {
			throw new RuntimeException("Unable to load configuration", e);
		}
	}
	
	protected void save() {
		try {
			builder.save();
		} catch (ConfigurationException e) {
			throw new RuntimeException(e);
		}
	}
	
	protected abstract void map();
	
	public void update(String key, Object o) {
		if (config.containsKey(key)) {
			config.setProperty(key, o);
			Logger.info("Update configuration: " + key + ":" + o);
			this.save();
			this.load();
		} else {
			Logger.warn("Unable to find configuration: " + key);
		}
	}
	
	public void addToList(String key, String value) {
		if (config.containsKey(key)) {
			List<String> list = config.getList(String.class, key);
			if (!list.contains(value)) {
				list.add(value);
				config.setProperty(key, list);
				Logger.info("Add configuration: " + key + ":" + list);
				this.save();
				this.load();
			} else {
				Logger.warn("Value already exists: " + value);
			}
		} else {
			Logger.warn("Unable to find configuration: " + key);
		}
	}
	
	public void removeFromList(String key, String value) {
		if (config.containsKey(key)) {
			List<String> list = config.getList(String.class, key);
			if (list.contains(value)) {
				list.remove(value);
				config.setProperty(key, list);
				Logger.info("Remove configuration: " + key + ":" + list);
				this.save();
				this.load();
			} else {
				Logger.warn("Unable to find value: " + value);
			}
		} else {
			Logger.warn("Unable to find configuration: " + key);
		}
	}
	
	public Object get(String key) {
		Object o = config.get(Object.class, key);
		return o;
	}
	
	public List<String> getList(String key) {
		return config.getList(String.class, key);
	}
}

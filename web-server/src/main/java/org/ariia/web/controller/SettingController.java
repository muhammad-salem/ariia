package org.ariia.web.controller;

import java.util.Objects;

import org.ariia.config.Properties;
import org.ariia.logging.Log;
import org.ariia.mvc.annotation.RequestBody;
import org.ariia.mvc.annotation.RestController;
import org.ariia.mvc.annotation.method.GetRequest;
import org.ariia.mvc.annotation.method.PostRequest;
import org.terminal.console.log.Level;

@RestController("/setting")
public class SettingController {
	
	final Properties properties;
	
	public SettingController(Properties properties) {
		this.properties = Objects.requireNonNull(properties);
	}
		
	@GetRequest(path = "/")
	public Properties getProperties() {
		return properties;
	}
	
	@PostRequest(path = "/")
	public boolean setProperties(@RequestBody Properties properties) {
		properties.updateProperties(properties);
		return true;
	}
	
}

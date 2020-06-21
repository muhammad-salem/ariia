package org.ariia.web.controller;

import java.util.Objects;

import org.ariia.config.Properties;
import org.ariia.mvc.annotation.RequestBody;
import org.ariia.mvc.annotation.RestController;
import org.ariia.mvc.annotation.method.GetRequest;
import org.ariia.mvc.annotation.method.PostRequest;
import org.ariia.web.app.WebServiceManager;

@RestController("/settings")
public class SettingController {

	final private WebServiceManager serviceManager;
	final private Properties properties;

	public SettingController(WebServiceManager serviceManager, Properties properties) {
		this.serviceManager = Objects.requireNonNull(serviceManager);
		this.properties = Objects.requireNonNull(properties);
	}

	@GetRequest(path = "/")
	public Properties getProperties() {
		return properties;
	}

	@PostRequest(path = "/update")
	public boolean updateProperties(@RequestBody Properties properties) {
		this.properties.updateProperties(properties);
		return true;
	}

	@GetRequest(path = "/isListPaused")
	public boolean isListPaused() {
		return serviceManager.isListPaused();
	}

	@PostRequest(path = "/startList")
	public boolean startList() {
		return serviceManager.setListPaused(false);
	}

	@PostRequest(path = "/pauseList")
	public boolean pauseList() {
		return serviceManager.setListPaused(true);
	}

}

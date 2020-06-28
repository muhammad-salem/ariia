package org.ariia.web.controller;

import java.util.Objects;

import org.ariia.config.Properties;
import org.ariia.logging.Log;
import org.ariia.mvc.annotation.RequestBody;
import org.ariia.mvc.annotation.RestController;
import org.ariia.mvc.annotation.method.GetRequest;
import org.ariia.mvc.annotation.method.PostRequest;
import org.ariia.web.services.SettingService;

@RestController("/settings")
public class SettingController {

	final private SettingService settingService;

	public SettingController(SettingService settingService) {
		this.settingService = Objects.requireNonNull(settingService);
	}

	@GetRequest(path = "/")
	public Properties getProperties() {
		Log.trace(getClass(), "getProperties");
		return settingService.getProperties();
	}

	@PostRequest(path = "/update")
	public boolean updateProperties(@RequestBody Properties properties) {
		Log.trace(getClass(), "update");
		return settingService.updateProperties(properties);
	}

	@GetRequest(path = "/isAllowDownload")
	public boolean isAllowDownload() {
		Log.trace(getClass(), "isAllowDownload");
		return settingService.isAllowDownload();
	}

	@PostRequest(path = "/toggleAllowDownload")
	public boolean startList() {
		Log.trace(getClass(), "startList");
		return settingService.toggleAllowDownloadList();
	}

}

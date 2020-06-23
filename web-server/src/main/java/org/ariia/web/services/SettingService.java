package org.ariia.web.services;

import java.util.Objects;

import org.ariia.config.Properties;

import org.ariia.core.api.service.DownloadService;


public class SettingService {

	final private DownloadService downloadService;
	final private Properties properties;

	public SettingService(DownloadService downloadService, Properties properties) {
		this.downloadService = Objects.requireNonNull(downloadService);
		this.properties = Objects.requireNonNull(properties);
	}

	public Properties getProperties() {
		return properties;
	}

	public boolean updateProperties(Properties properties) {
		this.properties.updateProperties(properties);
		return true;
	}

	public boolean isAllowDownload() {
		return downloadService.isAllowDownload();
	}

	public boolean startList() {
		downloadService.setAllowDownload(true);
		return isAllowDownload();
	}

	public boolean pauseList() {
		downloadService.setAllowDownload(false);
		return isAllowDownload();
	}

}

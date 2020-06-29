package org.ariia.web.services;

import org.ariia.config.Properties;
import org.ariia.core.api.service.DownloadService;

import java.util.Objects;


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

    public boolean toggleAllowDownloadList() {
        downloadService.setAllowDownload(!downloadService.isAllowDownload());
        return isAllowDownload();
    }

}

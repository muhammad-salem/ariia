package org.ariia.web.controller;

import org.ariia.config.Properties;
import org.ariia.logging.Logger;
import org.ariia.mvc.annotation.RequestBody;
import org.ariia.mvc.annotation.RestController;
import org.ariia.mvc.annotation.method.GetRequest;
import org.ariia.mvc.annotation.method.PostRequest;
import org.ariia.web.services.SettingService;

import java.util.Objects;

@RestController("/settings")
public class SettingController {

    Logger log = Logger.create(SettingController.class);

    final private SettingService settingService;

    public SettingController(SettingService settingService) {
        this.settingService = Objects.requireNonNull(settingService);
    }

    @GetRequest(path = "/")
    public Properties getProperties() {
        log.trace("getProperties");
        return settingService.getProperties();
    }

    @PostRequest(path = "/update")
    public boolean updateProperties(@RequestBody Properties properties) {
        log.trace("update");
        return settingService.updateProperties(properties);
    }

    @GetRequest(path = "/isAllowDownload")
    public boolean isAllowDownload() {
        log.trace("isAllowDownload");
        return settingService.isAllowDownload();
    }

    @PostRequest(path = "/toggleAllowDownload")
    public boolean startList() {
        log.trace("startList");
        return settingService.toggleAllowDownloadList();
    }

}

package org.ariia.web.controller;

import org.ariia.logging.Log;
import org.ariia.mvc.annotation.RequestBody;
import org.ariia.mvc.annotation.RestController;
import org.ariia.mvc.annotation.method.GetRequest;
import org.ariia.mvc.annotation.method.PostRequest;
import org.terminal.console.log.Level;

@RestController("/logging")
public class LogLevelController {


    @GetRequest(path = "/")
    public Level getLevel() {
        return Log.getLevel();
    }

    @PostRequest(path = "/set")
    public boolean setLevel(@RequestBody String levelName) {
        Level level = Level.valueOf(levelName);
        Log.level(level);
        return true;
    }

    @GetRequest(path = "/values")
    public Level[] values() {
        return Level.values();
    }

}

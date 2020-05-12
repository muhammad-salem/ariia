package org.ariia.web.controller;

import java.util.Objects;

import org.ariia.logging.Log;
import org.ariia.mvc.annotation.RequestBody;
import org.ariia.mvc.annotation.RestController;
import org.ariia.mvc.annotation.method.PostRequest;
import org.terminal.console.log.Level;

@RestController("/logging")
public class LogLevelController {
	
		
	@PostRequest(path = "/set")
	public boolean createItem(@RequestBody String levelName) {
		Level level = Level.valueOf(levelName);
		if (Objects.nonNull(level)) {
			Log.level(level);
			return true;
		}
		return false;
	}

}

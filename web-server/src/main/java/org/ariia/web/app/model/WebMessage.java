package org.ariia.web.app.model;

import org.terminal.console.log.Level;

public class WebMessage {

    long timeMillis;
    Level level;
    String classname;
    String title;
    String message;


    public WebMessage(Level level, String classname, String title, String message) {
        this.timeMillis = System.currentTimeMillis();
        this.level = level;
        this.classname = classname;
        this.title = title;
        this.message = message;
    }

    public Level getLevel() {
        return level;
    }

    public String getClassname() {
        return classname;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

}
package org.ariia.web.app.model;

import lombok.Getter;
import lombok.Setter;
import org.terminal.console.log.Level;

import java.io.Serializable;

@Getter
@Setter
public class WebMessage  implements Serializable {

    private static final long serialVersionUID = -531261783335089623L;

    private long timeMillis;
    private Level level;
    private String classname;
    private String title;
    private String message;


    public WebMessage(Level level, String classname, String title, String message) {
        this.timeMillis = System.currentTimeMillis();
        this.level = level;
        this.classname = classname;
        this.title = title;
        this.message = message;
    }

}
package org.ariia.web.app;

import org.ariia.mvc.sse.EventProvider;
import org.ariia.mvc.sse.SourceEvent;
import org.ariia.util.Utils;
import org.ariia.web.app.model.WebMessage;
import org.terminal.console.log.Level;
import org.terminal.console.log.api.Message;
import org.terminal.console.log.api.Printer;

import java.util.Objects;

public class WebLoggerPrinter implements Printer {


    private EventProvider provider;
    private Printer terminalPrinter;

    public WebLoggerPrinter(SourceEvent sourceEvent, Printer terminalPrinter) {
        this.provider = new EventProvider("logging", Objects.requireNonNull(sourceEvent));
        this.terminalPrinter = Objects.requireNonNull(terminalPrinter);
    }

    @Override
    public void print(Level level, Class<?> classname, String title, String message) {
        terminalPrinter.print(level, classname, title, message);
        this.logMessaage(new WebMessage(level, classname.getSimpleName(), title, message));
    }

    @Override
    public void print(Message message) {
        terminalPrinter.print(message);
        this.logMessaage(new WebMessage(message.getLevel(), message.getClassname().getSimpleName(), message.getTitle(), message.getMessage()));
    }

    public void logMessaage(WebMessage message) {

        provider.send(Utils.toJson(message));
    }
}

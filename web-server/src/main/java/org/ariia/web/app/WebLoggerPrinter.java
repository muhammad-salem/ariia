package org.ariia.web.app;

import java.util.Objects;

import org.ariia.mvc.sse.EventProvider;
import org.ariia.mvc.sse.SourceEvent;
import org.ariia.util.Utils;
import org.ariia.web.app.model.WebMessage;
import org.terminal.console.log.Level;
import org.terminal.console.log.api.Message;
import org.terminal.console.log.api.Printer;

public class WebLoggerPrinter implements Printer {
	

	
	private EventProvider provider;
	public WebLoggerPrinter(SourceEvent sourceEvent) {
		this.provider = new EventProvider("logging", Objects.requireNonNull(sourceEvent));
	}
	
	@Override
	public void print(Level level, Class<?> classname, String title, String message) {
		this.logMessaage(new WebMessage(level, classname.getSimpleName(), title, message));
	}
	
	@Override
	public void print(Message message) {
		this.logMessaage(new WebMessage(message.getLevel(), message.getClassname().getSimpleName(), message.getTitle(), message.getTitle()));
	}
	
	public void logMessaage(WebMessage message) {
		provider.send(Utils.toJson(message));
	}
}

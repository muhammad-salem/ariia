package org.ariia.mvc.sse;

import java.util.Objects;


public class EventProvider {
	
	private String event; 
	private Integer id = 0;
	private SourceEvent sourceEvent;
	
	
	public EventProvider(String event, SourceEvent sourceEvent) {
		this.event = Objects.requireNonNull(event);
		this.sourceEvent = Objects.requireNonNull(sourceEvent);
	}
	
	private void send(MessageEvent event) {
		sourceEvent.send(event);
	}


	public void send(String data) {
		MessageEvent messageEvent = new MessageEvent.Builder()
				.event(event)
				.data(data)
				.id(String.valueOf(++id))
				.build();
		send(messageEvent);
	}

	public void send(String data, Integer retry) {
		MessageEvent messageEvent = new MessageEvent.Builder()
				.event(event)
				.data(data)
				.retry(retry)
				.id(String.valueOf(++id))
				.build();
		send(messageEvent);
	}

}

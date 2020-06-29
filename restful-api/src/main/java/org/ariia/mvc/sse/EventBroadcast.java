package org.ariia.mvc.sse;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;


public class EventBroadcast implements SourceEvent {

    List<Subscriber> subscribers;

    public EventBroadcast() {
        this(new LinkedList<Subscriber>());
    }

    public EventBroadcast(List<Subscriber> subscribers) {
        this.subscribers = subscribers;
    }

    @Override
    public void subscribe(Subscriber subscriber) {
        this.subscribers.add(subscriber);
    }

    @Override
    public void send(String event, String data) {
        MessageEvent messageEvent = new MessageEvent.Builder()
                .event(event)
                .data(data)
                .build();
        send(messageEvent);
    }

    @Override
    public void send(MessageEvent event) {
        Iterator<Subscriber> iterator = subscribers.iterator();
        while (iterator.hasNext()) {
            Subscriber subscriber = iterator.next();
            try {
                subscriber.message(event);
            } catch (SseCloseException e) {
                iterator.remove();
            }
        }
    }

    @Override
    public void send(String event) {
        MessageEvent messageEvent = new MessageEvent.Builder()
                .event(event)
                .build();
        send(messageEvent);
    }

    @Override
    public void send(String event, String data, Integer retry) {
        MessageEvent messageEvent = new MessageEvent.Builder()
                .event(event)
                .data(data)
                .retry(retry)
                .build();
        send(messageEvent);
    }

    @Override
    public void send(String event, String data, Integer retry, String id) {
        MessageEvent messageEvent = new MessageEvent.Builder()
                .event(event)
                .data(data)
                .retry(retry)
                .id(id)
                .build();
        send(messageEvent);
    }

}

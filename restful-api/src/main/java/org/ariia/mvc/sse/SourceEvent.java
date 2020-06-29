package org.ariia.mvc.sse;

public interface SourceEvent {

    void subscribe(Subscriber subscriber);

    void send(String event);

    void send(String event, String data);

    void send(String event, String data, Integer retry);

    void send(String event, String data, Integer retry, String id);

    void send(MessageEvent event);

//	void close();
}

package org.ariia.mvc.sse;

public interface Subscriber {

    void message(String event) throws SseCloseException;

    void message(String event, String data) throws SseCloseException;

    void message(String event, String data, Integer retry) throws SseCloseException;

    void message(String event, String data, Integer retry, String id) throws SseCloseException;

    void message(MessageEvent event) throws SseCloseException;

//	void onClose(Runnable runnable);
//	void close();
}

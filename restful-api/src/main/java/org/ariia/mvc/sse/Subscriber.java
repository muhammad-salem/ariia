package org.ariia.mvc.sse;

public interface Subscriber {
	
	void message(String event);
	void message(String event, String data);
	void message(String event, String data, Integer retry);
	void message(String event, String data, Integer retry, String id);
	void message(MessageEvent event) throws SseCloseException;

//	void onClose(Runnable runnable);
//	void close();
}

package org.ariia.mvc.sse;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;


public class EventSubscriber implements Subscriber {

    private final OutputStream outputStream;

    public EventSubscriber(OutputStream outputStream) {
        this.outputStream = Objects.requireNonNull(outputStream);
    }

    @Override
    public void message(MessageEvent event) throws SseCloseException {
        String eventResponse = event.toString();
        try {
            outputStream.write(eventResponse.getBytes(StandardCharsets.UTF_8));
            outputStream.flush();
        } catch (IOException e) {
            try {
                outputStream.close();
            } catch (Exception e2) {/*ignore exception*/}
            throw new SseCloseException(e);
        }
    }

    @Override
    public void message(String event) throws SseCloseException {
        String eventResponse = new MessageEvent.Builder()
                .event(event)
                .build()
                .toString();
        message(eventResponse);
    }

    @Override
    public void message(String event, String data) throws SseCloseException {
        String eventResponse = new MessageEvent.Builder()
                .event(event)
                .data(data)
                .build()
                .toString();
        message(eventResponse);
    }

    @Override
    public void message(String event, String data, Integer retry) throws SseCloseException {
        String eventResponse = new MessageEvent.Builder()
                .event(event)
                .data(data)
                .retry(retry)
                .build()
                .toString();
        message(eventResponse);
    }

    @Override
    public void message(String event, String data, Integer retry, String id) throws SseCloseException {
        String eventResponse = new MessageEvent.Builder()
                .event(event)
                .data(data)
                .retry(retry)
                .id(id)
                .build()
                .toString();
        message(eventResponse);
    }
}

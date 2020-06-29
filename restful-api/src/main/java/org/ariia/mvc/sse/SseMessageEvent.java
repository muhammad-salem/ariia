package org.ariia.mvc.sse;

import java.util.Objects;

public abstract class SseMessageEvent {

    protected String message;

    protected SseMessageEvent() {
    }

    protected SseMessageEvent(String message) {
        this.message = message;
    }

    @Override
    public final String toString() {
        if (Objects.nonNull(message)) {
            return message;
        }
        message = buildMessage();
        return Objects.requireNonNull(message);
    }

    protected abstract String buildMessage();

    @Override
    public boolean equals(Object obj) {
        if (Objects.isNull(obj) || !(obj instanceof SseMessageEvent)) {
            return false;
        }
        SseMessageEvent sseMessage = (SseMessageEvent) obj;
        return Objects.equals(this.message, sseMessage.message);
    }
}
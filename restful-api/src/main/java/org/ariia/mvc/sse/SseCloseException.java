package org.ariia.mvc.sse;

import java.io.IOException;

public class SseCloseException extends IOException {

    private static final long serialVersionUID = 5996226767089704396L;

    public SseCloseException() {
        super();
    }

    public SseCloseException(String message) {
        super(message);
    }

    public SseCloseException(String message, Throwable cause) {
        super(message, cause);
    }

    public SseCloseException(Throwable cause) {
        super(cause);
    }

}

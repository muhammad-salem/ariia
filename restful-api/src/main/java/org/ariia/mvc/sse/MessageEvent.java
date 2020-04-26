package org.ariia.mvc.sse;

import java.util.Objects;

public class MessageEvent {
	
	public final static MessageEvent KeepAlive = new Builder().toStringCache(":\n\n").build();
	
    private final String data;
    private final String event;
    private final Integer retry;
    private final String id;

    private final String toStringCache;

    private MessageEvent(String event, String data, Integer retry, String id, String toStringCache) {
        this.data = data;
        this.event = event;
        this.toStringCache = toStringCache;
        this.retry = retry;
        this.id = id;

    }

    public final String data() {
        return data;
    }

    public final String event() {
        return event;
    }
    
    public final Integer retry() {
        return retry;
    }

    public final String id() {
        return id;
    }

    @Override
    public final String toString() {
        return toStringCache;
    }
    
    @Override
    public boolean equals(Object obj) {
    	if (Objects.isNull(obj) || ! (obj instanceof MessageEvent)) {
			return false;
		}
    	MessageEvent message = (MessageEvent) obj;
    	return Objects.equals(event, message.event)
    			&& Objects.equals(data, message.data)
    			&& Objects.equals(retry, message.retry)
    			&& Objects.equals(id, message.id);
    	
    }

    public static class Builder {
    	
        private String data = null;
        private String event = null;
        private Integer retry = null;
        private String id = null;
        private String toStringCache = null;

        public Builder data(String data) {
            this.data = data;
            return this;
        }

        public Builder event(String event) {
            this.event = event;
            return this;
        }
        
        public Builder retry(Integer retry) {
            this.retry = retry;
            return this;
        }

        public Builder id(String id) {
            this.id = id;
            return this;
        }
        
        private Builder toStringCache(String toStringCache) {
            this.toStringCache = toStringCache;
            return this;
        }

        public MessageEvent build() {
        	if (Objects.nonNull(toStringCache)) {
				return new MessageEvent(null, null, null, null, toStringCache);
			}
            StringBuilder sb = new StringBuilder();
            if(event != null) {
                sb.append("event: ").append(event.replace("\n", "")).append('\n');
            }
            if(data != null) {
                for(String s : data.split("\n")) {
                    sb.append("data: ").append(s).append('\n');
                }
            }
            if(retry != null) {
                sb.append("retry: ").append(retry).append('\n');
            }
            if(id != null) {
                sb.append("id: ").append(id.replace("\n","")).append('\n');
            }
            sb.append('\n');
            return new MessageEvent(event,data,retry,id,sb.toString());
        }
    }
}
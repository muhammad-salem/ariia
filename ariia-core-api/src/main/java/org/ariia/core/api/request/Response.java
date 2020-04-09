package org.ariia.core.api.request;

import static java.util.Objects.requireNonNull;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.stream.Collectors;

public class Response implements Closeable {
	
	private String requestMethod;
	private String requestUrl;
	private int code;
	private String responseMessage;
	private String protocol;
	private Map<String, List<String>> headers;
	private InputStream bodyBytes;
	
	public Response() {}
	public Response(int code) { this.code = code; }
	
	public String requestMethod() { return requestMethod; }
	public String requestUrl() { return requestUrl; }
	public int code() { return code; }
	public String responseMessage() { return responseMessage; }
	public String protocol() { return protocol; }
	public Map<String, List<String>> headers() { return headers; }
	public InputStream bodyBytes() { return bodyBytes; }
	
	
	 public Optional<String> firstValue(String name) {
	        return allValues(name).stream().findFirst();
	 }
	 
	 public OptionalLong firstValueAsLong(String name) {
	        return allValues(name).stream().mapToLong(Long::valueOf).findFirst();
	 }

    public List<String> allValues(String name) {
        requireNonNull(name);
        return headers().keySet().stream()
        .filter(name::equalsIgnoreCase)
        .map(headers::get)
        .flatMap(List::stream)
        .collect(Collectors.toList());
    }
	
	public static class Builder {
		
		String requestMethod;
		String requestUrl;
		int code;
		String responseMessage;
		String protocol;
		Map<String, List<String>> headers;
		InputStream bodyBytes;
		
		public Builder requestMethod(String requestMethod) {
			this.requestMethod = requestMethod;
			return this;
		}
		public Builder requestUrl(String requestUrl) {
			this.requestUrl = requestUrl;
			return this;
		}
		public Builder code(int code) {
			this.code = code;
			return this;
		}
		public Builder responseMessage(String responseMessage) {
			this.responseMessage = responseMessage;
			return this;
		}
		public Builder protocol(String protocol) {
			this.protocol = protocol;
			return this;
		}
		public Builder headers(Map<String, List<String>> headers) {
			this.headers = headers;
			return this;
		}
		public Builder setBodyBytes(InputStream bodyBytes) {
			this.bodyBytes = bodyBytes;
			return this;
		}
		
		
		public Response build() {
			Response response = new Response();
			response.requestMethod = this.requestMethod;
			response.requestUrl = this.requestUrl;
			response.protocol = this.protocol;
			response.code = this.code;
			response.responseMessage = this.responseMessage;
			response.headers = this.headers;
			response.bodyBytes = this.bodyBytes;
			return response;
		} 
		
		
		

	}


	@Override
	public void close() throws IOException {
		if (Objects.nonNull(bodyBytes)) {
			bodyBytes.close();
		}
	}

}

package org.ariia.internal;

import java.io.IOException;
import java.io.InputStream;
import java.net.Proxy;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.ariia.core.api.request.ClientRequest;
import org.ariia.core.api.request.Response;

public class AriiaHttpClient implements ClientRequest {
	
	Proxy proxy;
	HttpClient client;
	
	public AriiaHttpClient(Proxy proxy) {
		this.proxy = proxy;
		init();
	}
	
	private void init() {
		
		client = HttpClient.newBuilder()
//			        .version(Version.HTTP_1_1)
			        .followRedirects(Redirect.ALWAYS)
			        .connectTimeout(Duration.ofSeconds(20))
			        .proxy(new AriiaProxySelector(proxy))
//			        .proxy(ProxySelector.of( (InetSocketAddress) proxy.address() ))
//			        .authenticator(Authenticator.getDefault())
//			        .executor(Executors.newCachedThreadPool())
			        .build();

	}
	

	@Override
	public Response executeRequest(String method, String url, Map<String, List<String>> headers) throws IOException {
		try {
			
			HttpRequest.Builder requestBuilder = HttpRequest.newBuilder(new URI(url));
			if ("GET".equalsIgnoreCase(method)) {
				requestBuilder.GET();
			} else if ("HEAD".equalsIgnoreCase(method)) {
				requestBuilder.method("HEAD", HttpRequest.BodyPublishers.noBody());
			}
			
			if (Objects.nonNull(headers) || !headers.isEmpty()) {
	        	headers.forEach((headerName, valueList) -> {
	        		valueList.forEach(headerValue -> {
	        			requestBuilder.header(headerName, headerValue);
	        		});
	        	});
	       	}
		
		
			HttpResponse<InputStream> response = 
					client.send(requestBuilder.build(), BodyHandlers.ofInputStream());
			
			Response.Builder responseBuilder = new Response.Builder();
	        responseBuilder.code(response.statusCode());
	        responseBuilder.requestUrl(response.uri().toString());
	        responseBuilder.requestMethod(response.request().method());
	        responseBuilder.protocol(response.version().toString());
	        responseBuilder.responseMessage(getStateCodeMessage(response.statusCode()));
	        responseBuilder.headers(response.headers().map());
	        responseBuilder.setBodyBytes(response.body());
			return responseBuilder.build();
			
		} catch ( InterruptedException | URISyntaxException e) {
			throw new IOException(e.getMessage(), e);
		}
	}

	private String getStateCodeMessage(int statusCode) {
		switch (statusCode) {
		case 100: { return "Continue"; }
		case 101: { return "Switching Protocols"; }
		case 200: { return "OK"; }
		case 201: { return "Created"; }
		case 202: { return "Accepted"; }
		case 203: { return "Non-authoritative Information"; }
		case 204: { return "No Content"; }
		case 205: { return "Reset Content"; }
		case 206: { return "Partial Content"; }
		case 300: { return "Multiple Choices"; }
		case 301: { return "Moved Permanently"; }
		case 302: { return "Found"; }
		case 303: { return "See Other"; }
		case 304: { return "Not Modified"; }
		case 305: { return "Use Proxy"; }
		case 306: { return "Unused"; }
		case 307: { return "Temporary Redirect"; }
		case 400: { return "Bad Request"; }
		case 401: { return "Unauthorized"; }
		case 402: { return "Payment Required"; }
		case 403: { return "Forbidden"; }
		case 404: { return "Not Found"; }
		case 405: { return "Method Not Allowed"; }
		case 406: { return "Not Acceptable"; }
		case 407: { return "Proxy Authentication Required"; }
		case 408: { return "Request Timeout"; }
		case 409: { return "Conflict"; }
		case 410: { return "Gone"; }
		case 411: { return "Length Required"; }
		case 412: { return "Precondition Failed"; }
		case 413: { return "Request Entity Too Large"; }
		case 414: { return "Request-url Too Long"; }
		case 415: { return "Unsupported Media Type"; }
		case 417: { return "Expectation Failed"; }
		case 500: { return "Internal Server Error"; }
		case 501: { return "Not Implemented"; }
		case 502: { return "Bad Gateway"; }
		case 503: { return "Service Unavailable"; }
		case 504: { return "Gateway Timeout"; }
		case 505: { return "HTTP Version Not Supported"; }
		default: return "";}
	}

	@Override
	public Proxy proxy() {
		return proxy;
	}

}

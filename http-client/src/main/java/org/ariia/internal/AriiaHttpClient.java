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
		return switch (statusCode) {
		case 100: { yield "Continue"; }
		case 101: { yield "Switching Protocols"; }
		case 200: { yield "OK"; }
		case 201: { yield "Created"; }
		case 202: { yield "Accepted"; }
		case 203: { yield "Non-authoritative Information"; }
		case 204: { yield "No Content"; }
		case 205: { yield "Reset Content"; }
		case 206: { yield "Partial Content"; }
		case 300: { yield "Multiple Choices"; }
		case 301: { yield "Moved Permanently"; }
		case 302: { yield "Found"; }
		case 303: { yield "See Other"; }
		case 304: { yield "Not Modified"; }
		case 305: { yield "Use Proxy"; }
		case 306: { yield "Unused"; }
		case 307: { yield "Temporary Redirect"; }
		case 400: { yield "Bad Request"; }
		case 401: { yield "Unauthorized"; }
		case 402: { yield "Payment Required"; }
		case 403: { yield "Forbidden"; }
		case 404: { yield "Not Found"; }
		case 405: { yield "Method Not Allowed"; }
		case 406: { yield "Not Acceptable"; }
		case 407: { yield "Proxy Authentication Required"; }
		case 408: { yield "Request Timeout"; }
		case 409: { yield "Conflict"; }
		case 410: { yield "Gone"; }
		case 411: { yield "Length Required"; }
		case 412: { yield "Precondition Failed"; }
		case 413: { yield "Request Entity Too Large"; }
		case 414: { yield "Request-url Too Long"; }
		case 415: { yield "Unsupported Media Type"; }
		case 417: { yield "Expectation Failed"; }
		case 500: { yield "Internal Server Error"; }
		case 501: { yield "Not Implemented"; }
		case 502: { yield "Bad Gateway"; }
		case 503: { yield "Service Unavailable"; }
		case 504: { yield "Gateway Timeout"; }
		case 505: { yield "HTTP Version Not Supported"; }
		default: yield "";};
	}

	@Override
	public Proxy proxy() {
		return proxy;
	}

}

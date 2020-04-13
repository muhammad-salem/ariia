package org.ariia.mvc.model;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import org.ariia.mvc.processing.MethodIndex;
import org.ariia.mvc.processing.ProxySwitcher;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class ControllerHandler implements HttpHandler {

	private Object controller;
	private ProxySwitcher switcher;
	private List<MethodIndex> methodIndexs;
	
	public ControllerHandler(Object controller, ProxySwitcher switcher) {
		this.controller = controller;
		this.switcher = switcher;
		this.methodIndexs = switcher.getMethodIndexs();
	}
	



	@Override
	public void handle(HttpExchange exchange) throws IOException {
		URI uri = exchange.getRequestURI();
		System.out.println("uri: " + uri);
		System.out.println("getFragment: " + uri.getFragment());
		System.out.println("getQuery: " + uri.getQuery());
		System.out.println("getUserInfo: " + uri.getUserInfo());
		System.out.println("getPath: " + uri.getPath());
		System.out.println("headers: " + exchange.getRequestHeaders().entrySet());
		if (exchange.getRequestBody().available() > 0) {
			Scanner scanner = new Scanner(exchange.getRequestBody(), "UTF_8");
			scanner.useDelimiter("\\A");
			String body = scanner.next();
			System.out.println("con Han, body: " + body);
		}
		
		String path = uri.getPath();
		String method = exchange.getRequestMethod();
		Optional<MethodIndex> optionalMethod = methodIndexs.stream()
			.filter(m -> method.equalsIgnoreCase(m.httpMethod()))
			.filter(m -> path.startsWith(switcher.getContext() + m.context()))
			.filter(m -> {
				String[] pathVariable = path.substring(m.context().length()).split("/");
				long count =  m.getParametersInfo().stream()
					.filter(info -> {
						return info.isPathVariable() || info.isRequestParam();
					})
					.count();
				return count == pathVariable.length;
			})
			
			.findFirst();
		 
		if (!optionalMethod.isPresent()) {
			System.out.println("MethodIndex: empty");
			exchange.sendResponseHeaders(404, -1);
		}
		MethodIndex index = optionalMethod.get();
		System.out.println("MethodIndex: " + index);
		if (index.context().length() == uri.toString().length()) {
			try {
				String object = index.method().invoke(controller).toString();
				exchange.sendResponseHeaders(200, object.length());
				exchange.getResponseBody().write(object.getBytes(StandardCharsets.UTF_8));
				exchange.close();
				return;
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				e.printStackTrace();
			}
		}
		String params[] = uri.toString().substring(index.context().length()).split("/");
		Parameter[] parameters = index.method().getParameters();
		Object[] objects = new Object[parameters.length];
		for (int i = 0; i < objects.length; i++) {
			objects[i] = parameters[i].getType().cast(params[i]);
		}
		try {
			String object = index.method().invoke(controller, objects).toString();
			exchange.sendResponseHeaders(200, object.length());
			exchange.getResponseBody().write(object.getBytes(StandardCharsets.UTF_8));
			exchange.close();
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		exchange.close();
	}

}

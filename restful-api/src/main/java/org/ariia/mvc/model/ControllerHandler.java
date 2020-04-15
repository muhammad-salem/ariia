package org.ariia.mvc.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
//		System.out.println("uri: " + uri);
//		System.out.println("Fragment: " + uri.getFragment());
		System.out.println("Query: " + uri.getQuery());
//		System.out.println("UserInfo: " + uri.getUserInfo());
		System.out.println("Path: " + uri.getPath());
		
		
		System.out.println("headers: " + exchange.getRequestHeaders().entrySet());
		String body;

		RequestInfo requestInfo = new RequestInfo();
		if (exchange.getRequestBody().available() > 0) {
			BufferedReader reader = 
					new BufferedReader(
							new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8)
							);
			body = reader.lines().collect(Collectors.joining("\n"));
			requestInfo.setBody(body);
			System.out.println("with, body: " + body);
		}
		
		String path = uri.getPath();
		requestInfo.setParamters(uri.getQuery());
		requestInfo.setMethodContext(path.split(switcher.getContext(), 2)[1]);
		if (switcher.getContextParamter().size() > 0) {
			for (String param : switcher.getContextParamter()) {
				String methodContext = requestInfo.getMethodContext();
				int i = methodContext.indexOf('/');
				requestInfo.putPathVariable(param, methodContext.substring(0, i));
				requestInfo.setMethodContext(methodContext.substring(i+1));
			}
			requestInfo.setMethodContext('/' + requestInfo.getMethodContext());
		}

		System.out.println("\tmethodContext " + requestInfo.getMethodContext());
		System.out.println("\tparamters " + requestInfo.getParamters());
		System.out.println("\tpathVariable " + requestInfo.getPathVariable());
		
		String method = exchange.getRequestMethod();
		Optional<MethodIndex> optionalMethod = methodIndexs.stream()
			.filter(m -> method.equalsIgnoreCase(m.httpMethod()))
//			.filter(m -> { System.out.println("m1: " + m.context());  return true;})
			.filter(m -> requestInfo.getMethodContext().startsWith(m.context()) )
//			.filter(m -> requestInfo.getMethodContext().startsWith(m.context().substring(1)))
//			.filter(m -> { System.out.println("m2: " + m.context());  return true;})
			.filter(m -> { 
				if (requestInfo.getMethodContext().equals(m.context())) {
					return true;
				}
				int methodConFragment = requestInfo.getMethodContext()
						.split(m.context())[1].split("/").length;
				int paramInfoFragment = m.getParametersInfo().size();
				System.out.printf("methodConFragment: %d, paramInfoFragment: %d\n", methodConFragment, paramInfoFragment);
				return methodConFragment == paramInfoFragment;
			})
//			.filter(m -> { System.out.println("m3: " + m.context());  return true;})
			.filter(m -> { System.out.println("m3: " + m.context());  return true;})
			.findFirst();
		 
		if (!optionalMethod.isPresent()) {
			System.out.println("MethodIndex: empty");
			exchange.sendResponseHeaders(404, -1);
		}
		MethodIndex index = optionalMethod.get();
		System.out.println("MethodIndex: " + index);
		
		/// handle body >> cast & JSON 
		/// handle path arguments, path variable, query parameter
		/// handle return type
		
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

package org.ariia.mvc.model;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.ariia.mvc.annoutation.RequestParam;
import org.ariia.mvc.processing.MethodIndex;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class ControllerHandler implements HttpHandler {

	Object controller;
	List<MethodIndex> methodIndexs;
	
	public ControllerHandler(Object controller, List<MethodIndex> methodIndexs) {
		this.controller = controller;
		this.methodIndexs = methodIndexs;
	}
	

	@Override
	public void handle(HttpExchange exchange) throws IOException {
		URI uri = exchange.getRequestURI();
		System.out.println("ControllerHandler:uri: " + uri);
//		InputStream requestBody = exchange.getRequestBody();
		String method = exchange.getRequestMethod();
		Optional<MethodIndex> optionalMethod = methodIndexs.stream()
				.filter(m -> method.equalsIgnoreCase(m.getRequestMethod()))
				.filter(m -> uri.toString().startsWith(m.getContext()))
				.filter(m -> {
					String param = uri.toString().substring(m.getContext().length());
					return m.getMethod().getParameterCount() == param.split("/").length;
				})
				.filter(m->{
					Method refMethod = m.getMethod();
					Parameter[] parameters = refMethod.getParameters();
//					System.out.println("getParameters: "+ Arrays.toString(parameters));
					ArrayList<String> definedParamer = new ArrayList<>(1);
					for (Parameter parameter : parameters) {
						if (parameter.isAnnotationPresent(RequestParam.class)) {
							RequestParam param = parameter.getAnnotation(RequestParam.class);
							if(m.getParamter().contains(param.value())) {
								definedParamer.add(param.value());
							}
						}
					}
//					System.out.println("definedParamer: "+ definedParamer);
					for (String string : definedParamer) {
						if (m.getParamter().contains(string)) {
							continue;
						} else {
							return false;
						}
					}
					return true;
				})
				.findFirst();
		 
		if (!optionalMethod.isPresent()) {
			exchange.sendResponseHeaders(404, -1);
		}
		
		MethodIndex index = optionalMethod.get();
		if (index.getContext().length() == uri.toString().length()) {
			try {
				String object = index.getMethod().invoke(controller).toString();
				exchange.sendResponseHeaders(200, object.length());
				exchange.getResponseBody().write(object.getBytes(StandardCharsets.UTF_8));
				exchange.close();
				return;
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				e.printStackTrace();
			}
		}
		String params[] = uri.toString().substring(index.getContext().length()).split("/");
		Parameter[] parameters = index.getMethod().getParameters();
		Object[] objects = new Object[parameters.length];
		for (int i = 0; i < objects.length; i++) {
			objects[i] = parameters[i].getType().cast(params[i]);
		}
		try {
			String object = index.getMethod().invoke(controller, objects).toString();
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

package org.ariia.mvc.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.ariia.mvc.processing.MethodIndex;
import org.ariia.mvc.processing.ProxySwitcher;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class ControllerHandler implements HttpHandler {
	
	private static Gson gson = new Gson();

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
		List<MethodIndex> methodList = methodIndexs.stream()
			.filter(m -> method.equalsIgnoreCase(m.httpMethod()))
//			.filter(m -> { System.out.println("m1: " + m.context());  return true;})
			.filter(m -> 
			requestInfo.getMethodContext().startsWith(m.context()) )
//			.filter(m -> requestInfo.getMethodContext().startsWith(m.context().substring(1)))
			.filter(m -> { System.out.println("m2: " + m.context());  return true;})
//			.filter(m -> { 
//				if (requestInfo.getMethodContext().equals(m.context())) {
//					return true;
//				}
//				System.out.println(requestInfo.getMethodContext());
//				System.out.println(m.context());
//				String[] temp1 = requestInfo.getMethodContext().split(m.context());
//				String[] temp2 = temp1[1].split("/");
//				int methodConFragment = temp2.length;
//				int paramInfoFragment = m.getParametersInfo().size();
//				System.out.printf("methodConFragment: %d, paramInfoFragment: %d\n", methodConFragment, paramInfoFragment);
//				System.out.printf("method: %s\n", m.getParametersInfo());
//				return methodConFragment == paramInfoFragment;
//			})
//			.filter(m -> { System.out.println("m3: " + m.context());  return true;})
			.collect(Collectors.toList());
		System.out.println("methodStream sizw: " + methodList.size());
		if (methodList.isEmpty()) {
			System.out.println("MethodIndex: empty");
			exchange.sendResponseHeaders(404, -1);
			return;
		}
		
		
		MethodIndex index = methodList.get(0);
		System.out.println("MethodIndex: " + index);
		
		String mContext = requestInfo.getMethodContext().split(index.context())[1];
		for (ParameterInfo param : index.getParametersInfo()) {
			String methodContext = mContext;
			int i = methodContext.indexOf('/');
			if (i == -1) {
				requestInfo.putPathVariable(param.name(), methodContext);
				break;
			}
			requestInfo.putPathVariable(param.name(), methodContext.substring(0, i));
			mContext = methodContext.substring(i+1);
		}
		
		/// TO:DO handle body >> cast & JSON 
		/// TO:DO handle path arguments, path variable, query parameter
		/// TO:DO handle return type
		
//		if (index.context().length() == uri.toString().length()) {
//			try {
//				String object = index.method().invoke(controller).toString();
//				exchange.sendResponseHeaders(200, object.length());
//				exchange.getResponseBody().write(object.getBytes(StandardCharsets.UTF_8));
//				exchange.close();
//				return;
//			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
//				e.printStackTrace();
//			}
//		}
		
//		String params[] = uri.toString().substring(index.context().length()).split("/");
//		Parameter[] parameters = index.method().getParameters();
//		Object[] objects = new Object[parameters.length];
//		for (int i = 0; i < objects.length; i++) {
////			if (condition) {
////				
////			}
//			objects[i] = parameters[i].getType().cast(params[i]);
//		}
		try {
			executeMathod(exchange, index, requestInfo);
		} catch (IllegalAccessException
				| IllegalArgumentException
				| InvocationTargetException e) {
			e.printStackTrace();
		}
		
		exchange.close();
	}




	void executeMathod(HttpExchange exchange, MethodIndex methodIndex, RequestInfo requestInfo )
			throws IllegalAccessException, InvocationTargetException, IOException {
		
		Object[] objects = new Object[methodIndex.getParametersInfo().size()];
		for (int i = 0; i < objects.length; i++) {
			ParameterInfo info = methodIndex.getParametersInfo().get(i);
			if (info.isRequestBody()) {
				if (info.getParameterType().isPrimitive()) {
					objects[i] = toObject(info.getParameterType(), requestInfo.getBody());
				} else if (String.class.equals(info.getParameterType())) {
					objects[i] = requestInfo.getBody();
				} else if (requestInfo.getBody().charAt(0) == '{') {
					objects[i] = gson.fromJson(requestInfo.getBody(), info.getParameterType());
				}
				
			}
			else if (info.isPathVariable()) {
				objects[i] = requestInfo.getPathVariable(info.name());
			}
			else if (info.isRequestParam()) {
				objects[i] = requestInfo.getParamter(info.name());
			}
		}
		
		Object object = methodIndex.method().invoke(controller, objects);
		if (Objects.isNull(object)) {
			exchange.sendResponseHeaders(200, -1);
			exchange.close();
		} else {
			String responseBody = gson.toJson(object);
			byte[] bodyBytes =  responseBody.getBytes(StandardCharsets.UTF_8);
			exchange.sendResponseHeaders(200, bodyBytes.length);
			exchange.getResponseBody().write(bodyBytes);
			exchange.close();
		}
	}
	
	private static Object toObject( Class<?> clazz, String value ) {
	    if( Boolean.class == clazz || Boolean.TYPE == clazz ) return Boolean.parseBoolean( value );
	    if( Byte.class == clazz || Byte.TYPE == clazz) return Byte.parseByte( value );
	    if( Short.class == clazz || Short.TYPE == clazz) return Short.parseShort( value );
	    if( Integer.class == clazz || Integer.TYPE == clazz) return Integer.parseInt( value );
	    if( Long.class == clazz || Long.TYPE == clazz) return Long.parseLong( value );
	    if( Float.class == clazz || Float.TYPE == clazz) return Float.parseFloat( value );
	    if( Double.class == clazz || Double.TYPE == clazz) return Double.parseDouble( value );
	    return value;
	}

}

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
		RequestInfo requestInfo = new RequestInfo();
		if (exchange.getRequestBody().available() > 0) {
			BufferedReader reader = 
					new BufferedReader(
							new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8)
							);
			requestInfo.setBody(reader.lines().collect(Collectors.joining("\n")));
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
		
		List<MethodIndex> methodList = filterMethods(requestInfo, exchange.getRequestMethod());
		if (methodList.isEmpty()) {
			exchange.sendResponseHeaders(500, -1);
			return;
		}
		
		MethodIndex index = methodList.get(0);
		
		if(!index.context().equals(requestInfo.getMethodContext())){
			String mContext = requestInfo.getMethodContext().split(index.context())[1];
			for (String pathVariable : index.pathVariables()) {
				String methodContext = mContext;
				int i = methodContext.indexOf('/');
				if (i == -1) {
					requestInfo.putPathVariable(pathVariable, methodContext);
					break;
				}
				requestInfo.putPathVariable(pathVariable, methodContext.substring(0, i));
				mContext = methodContext.substring(i+1);
			}
		}
		
		try {
			executeMathod(exchange, index, requestInfo);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
//	private boolean printMethodIndex(MethodIndex method) {
//		System.out.println(method);
//		return true;
//	}


	List<MethodIndex> filterMethods(RequestInfo requestInfo, String method) {
		List<MethodIndex> methodList = methodIndexs.stream()
			.filter(m -> method.equalsIgnoreCase(m.httpMethod()))
			.filter(m -> requestInfo.getMethodContext().startsWith(m.context()) )
			.filter(m -> requestInfo.hasBody() == m.hasBodyParameter())
			.filter(m -> {
				if (requestInfo.getMethodContext().equals(m.context())) {
					return true;
				}
				String[] mContext = requestInfo.getMethodContext().split(m.context(), 2);
				mContext = mContext[1].split("/");
				return mContext.length == m.pathVariables().size();
			})
//			.filter(this::printMethodIndex)
			.collect(Collectors.toList());
		return methodList;
	}


	/// TO:DO handle body >> cast & JSON 
	/// TO:DO handle path arguments, path variable, query parameter
	/// TO:DO handle return type

	private void executeMathod(HttpExchange exchange, MethodIndex methodIndex, RequestInfo requestInfo )
			throws IllegalAccessException, InvocationTargetException, IOException {
		
		Object[] objects = new Object[methodIndex.getParametersInfo().size()];
		for (int i = 0; i < objects.length; i++) {
			ParameterInfo info = methodIndex.getParametersInfo().get(i);
			if (info.isRequestBody()) {
				objects[i] = toObject(
						info.getParameterType(),
						requestInfo.getBody());
			}
			else if (info.isPathVariable()) {
				objects[i] = toObject(
						info.getParameterType(),
						requestInfo.getPathVariable(info.name()));
			}
			else if (info.isRequestParam()) {
				objects[i] = toObject(
						info.getParameterType(),
						requestInfo.getParamter(info.name()));
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
	
//	private static Object getMethodArgs(Class<?> clazz, String value) {
//		if (clazz.isPrimitive() || String.class.equals(clazz) ) {
//			return toObject(clazz, value);
//		} else if (value.charAt(0) == '{') {
//			return gson.fromJson(value, clazz);
//		}
//		return null;
//	}
	
	private static Object toObject( Class<?> clazz, String value ) {
	    if( Boolean.class == clazz || Boolean.TYPE == clazz ) return Boolean.parseBoolean( value );
	    if( Byte.class == clazz || Byte.TYPE == clazz) return Byte.parseByte( value );
	    if( Short.class == clazz || Short.TYPE == clazz) return Short.parseShort( value );
	    if( Integer.class == clazz || Integer.TYPE == clazz) return Integer.parseInt( value );
	    if( Long.class == clazz || Long.TYPE == clazz) return Long.parseLong( value );
	    if( Float.class == clazz || Float.TYPE == clazz) return Float.parseFloat( value );
	    if( Double.class == clazz || Double.TYPE == clazz) return Double.parseDouble( value );
	    if( String.class == clazz ) return value;
	    return gson.fromJson(value, clazz);
	}

}

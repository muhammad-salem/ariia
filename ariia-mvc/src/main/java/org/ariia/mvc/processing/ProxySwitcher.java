package org.ariia.mvc.processing;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import org.ariia.mvc.annoutation.DeleteRequest;
import org.ariia.mvc.annoutation.GetRequest;
import org.ariia.mvc.annoutation.HeadRequest;
import org.ariia.mvc.annoutation.HttpMethod;
import org.ariia.mvc.annoutation.OptionsRequest;
import org.ariia.mvc.annoutation.PatchRequest;
import org.ariia.mvc.annoutation.PostRequest;
import org.ariia.mvc.annoutation.PutRequest;
import org.ariia.mvc.annoutation.RequestMethod;
import org.ariia.mvc.annoutation.RestContext;
import org.ariia.mvc.annoutation.TraceRequest;

public class ProxySwitcher {
	
	private static List<Class<? extends Annotation>> trackAnnotation = new ArrayList<>();
	static {
		trackAnnotation.add(GetRequest.class);
		trackAnnotation.add(HeadRequest.class);
		trackAnnotation.add(PostRequest.class);
		trackAnnotation.add(PutRequest.class);
		trackAnnotation.add(DeleteRequest.class);
		trackAnnotation.add(OptionsRequest.class);
		trackAnnotation.add(TraceRequest.class);
		trackAnnotation.add(PatchRequest.class);
	}
	
	public static class Marker {
		RequestMethod requestMethod;
		Method method;
		String context;
		String contextParamter;
		List<String> paramter;
		List<String> headers;
		List<String> produces;
				
		public RequestMethod getRequestMethod() {return requestMethod;}
		public String getContext() {return context;}
		public String getContextParamter() {return contextParamter;}
		public Method getMethod() {return method;}
		public List<String> getHeaders() {return headers;}
		public List<String> getProduces() {return produces;}
		public List<String> getParamter() {return paramter;}
		@Override
		public String toString() {
			return String.format("%s %s\n%s %s\n%s %s %s\n", 
					requestMethod, method.getName(), 
					 context, contextParamter,
					headers, produces, paramter);
		}
	}
	
	private Object controller;
	private List<Marker> markers;
	
	public ProxySwitcher(Object controller){
		this.controller = controller;
		this.markers = new ArrayList<>();
		initController();
	}
	
	public List<Marker> getMarkers() {
		return markers;
	}
	
	private void initController() { 
		String context = "";
		Class<?> clazz = controller.getClass();
		if (clazz.isAnnotationPresent(RestContext.class)) {
			RestContext restContext = clazz.getAnnotation(RestContext.class);
			context = restContext.value();
		}
		
		//trackAnnotation
		for (Method method : clazz.getMethods()) {
			for (Class<? extends Annotation> annotationClass : trackAnnotation) {
				if (method.isAnnotationPresent(annotationClass)) {
					method.setAccessible(true);
					Annotation annotation = method.getAnnotation(annotationClass);
					markers.add(getMarker(method, annotation, context));
				}
			}
		}
	    
	}
	
	private Marker getMarker(Method method, Annotation annotation, final String rootContext) {
		try {

			Marker marker = new Marker();
			marker.method = method;
			HttpMethod httpMethod = annotation.annotationType().getAnnotation(HttpMethod.class);
			marker.requestMethod = httpMethod.method();

			Method pathMethod = annotation.getClass().getMethod("path");
			Method headersMethod = annotation.getClass().getMethod("headers");
			Method producesMethod = annotation.getClass().getMethod("produces");

			marker.headers = Arrays.asList((String[]) headersMethod.invoke(annotation));
			marker.produces = Arrays.asList((String[]) producesMethod.invoke(annotation));
			marker.contextParamter = rootContext + pathMethod.invoke(annotation).toString();
			
			Function<String, Void> paramterExploer = (param) -> {
				int index = param.indexOf('{');
				if (index >= 0 ) {
					marker.context = param.substring(0, index);
					String temp;
					ArrayList<String> list = new ArrayList<>();
					do {
						temp = param.substring(index+1);
						index = temp.indexOf('}');
						list.add(temp.substring(0, index));
						index = temp.indexOf('{');
					} while (index > 0);
					
					marker.paramter = list;
				} else {
					marker.context = param;
					marker.paramter = Collections.emptyList();
				}
				return null;
			};
			paramterExploer.apply(marker.contextParamter);
			return marker;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	

}

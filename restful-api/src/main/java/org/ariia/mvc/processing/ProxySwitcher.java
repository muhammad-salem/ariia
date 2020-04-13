package org.ariia.mvc.processing;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.ariia.mvc.annotation.PathVariable;
import org.ariia.mvc.annotation.RequestBody;
import org.ariia.mvc.annotation.RequestParam;
import org.ariia.mvc.annotation.RestContext;
import org.ariia.mvc.annotation.method.DeleteRequest;
import org.ariia.mvc.annotation.method.GetRequest;
import org.ariia.mvc.annotation.method.HeadRequest;
import org.ariia.mvc.annotation.method.HttpMethod;
import org.ariia.mvc.annotation.method.OptionsRequest;
import org.ariia.mvc.annotation.method.PatchRequest;
import org.ariia.mvc.annotation.method.PostRequest;
import org.ariia.mvc.annotation.method.PutRequest;
import org.ariia.mvc.annotation.method.TraceRequest;
import org.ariia.mvc.model.ParameterInfo;

public class ProxySwitcher {

	private static List<Class<? extends Annotation>> requestAnnotation = new ArrayList<>();
	private static List<Class<? extends Annotation>> parameterAnnotation = new ArrayList<>();
	
	static {
		requestAnnotation.add(GetRequest.class);
		requestAnnotation.add(HeadRequest.class);
		requestAnnotation.add(PostRequest.class);
		requestAnnotation.add(PutRequest.class);
		requestAnnotation.add(DeleteRequest.class);
		requestAnnotation.add(OptionsRequest.class);
		requestAnnotation.add(TraceRequest.class);
		requestAnnotation.add(PatchRequest.class);
		
		parameterAnnotation.add(PathVariable.class);
		parameterAnnotation.add(RequestParam.class);
		parameterAnnotation.add(RequestBody.class);
	}
	

	
	private Object controller;
	private String context;
	private String controllerContext;
	private List<MethodIndex> methodIndexs;
	
	public ProxySwitcher(Object controller){
		this.controller = controller;
		this.methodIndexs = new ArrayList<>();
		initController();
	}
	
	public List<MethodIndex> getMethodIndexs() {
		return methodIndexs;
	}
	
	public String getContext() {
		return context;
	}
	
	public String getControllerContext() {
		return controllerContext;
	}
	
	private void initController() { 
		Class<?> clazz = controller.getClass();
//		String context = clazz.getSimpleName();
		if (clazz.isAnnotationPresent(RestContext.class)) {
			RestContext restContext = clazz.getAnnotation(RestContext.class);
			controllerContext = restContext.value();
			int u = controllerContext.indexOf('{');
			if (u>0) {
				context = controllerContext.substring(0, u);
			}
		} else {
			controllerContext = clazz.getSimpleName();
			context = controllerContext;
		}
		
		
		System.out.println("controllerContext: " + controllerContext);
		
		//trackAnnotation
		for (Method method : clazz.getMethods()) {
			for (Class<? extends Annotation> annotationClass : requestAnnotation) {
				if (method.isAnnotationPresent(annotationClass)) {
					method.setAccessible(true);
					Annotation annotation = method.getAnnotation(annotationClass);
					methodIndexs.add(getMethodIndex(method, annotation));
					break; // for now it only support 1 request for a method
				}
			}
		}
	    
	}
	
	private MethodIndex getMethodIndex(Method method, Annotation annotation) {
		try {

			MethodIndex.MethodIndexBuilder indexBuilder = new MethodIndex.MethodIndexBuilder();
			indexBuilder.method(method);
			
			HttpMethod httpMethod = annotation.annotationType().getAnnotation(HttpMethod.class);
			indexBuilder.httpMethod(httpMethod.method().name());

			Method pathMethod = annotation.getClass().getMethod("path");
			Method headersMethod = annotation.getClass().getMethod("headers");
			Method producesMethod = annotation.getClass().getMethod("produces");

			indexBuilder.headers( Arrays.asList((String[]) headersMethod.invoke(annotation)) );
			indexBuilder.produces( Arrays.asList((String[]) producesMethod.invoke(annotation)) );
			indexBuilder.context( pathMethod.invoke(annotation).toString() );
			
			String rootContext = pathMethod.invoke(annotation).toString();
			int index = rootContext.indexOf('{');
			if (index > 0 ) {
				indexBuilder.context( rootContext.substring(0, index) );
				String temp;
				ArrayList<String> list = new ArrayList<>(1);
				do {
					temp = rootContext.substring(index+1);
					index = temp.indexOf('}');
					list.add(temp.substring(0, index));
					index = temp.indexOf('{');
				} while (index > 0);
				
				indexBuilder.paramter(list);
			} else {
				indexBuilder.context( rootContext );
				indexBuilder.paramter( Collections.emptyList() );
			}
			
			indexBuilder.parametersInfo(getParametersInfo(method));
			
			return indexBuilder.build();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private List<ParameterInfo> getParametersInfo(Method method) {
		ParameterInfo.Builder builder = null;
		Parameter parameter;
		Parameter[] parameters = method.getParameters();
		List<ParameterInfo> infos = new ArrayList<>(parameters.length);
		for (int i = 0; i < parameters.length; i++) {
			builder = new ParameterInfo.Builder();
			parameter = parameters[i];
			builder.parameterType(parameter.getType());
			for (Class<? extends Annotation> annotationClass : parameterAnnotation) {
				if (parameter.isAnnotationPresent(annotationClass)) {
					builder.annotation(parameter.getAnnotation(annotationClass));
					builder.index(i);
					builder.name(parameter.getName());
					break;
				}
			}
			infos.add(builder.build());
		};
		return infos;
	}

	

}

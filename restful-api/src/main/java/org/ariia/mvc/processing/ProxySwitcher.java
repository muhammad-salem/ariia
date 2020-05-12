package org.ariia.mvc.processing;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.ariia.mvc.annotation.DoExchange;
import org.ariia.mvc.annotation.PathVariable;
import org.ariia.mvc.annotation.RequestBody;
import org.ariia.mvc.annotation.RequestParam;
import org.ariia.mvc.annotation.RestController;
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
	private List<String> contextParamter;
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
	
	public List<String> getContextParamter() {
		return contextParamter ;
	}
	
	public String getControllerContext() {
		return controllerContext;
	}
	
	public String getContext() {
		return context;
	}
	
	private void initController() { 
		Class<?> clazz = controller.getClass();
//		String context = clazz.getSimpleName();
		if (clazz.isAnnotationPresent(RestController.class)) {
			RestController restContext = clazz.getAnnotation(RestController.class);
			controllerContext = restContext.value();
			if (controllerContext.contains("{")) {
				context = controllerContext.substring(0, controllerContext.indexOf('{'));
				contextParamter = listOfPathVariavle(controllerContext);
			} else {
				context = controllerContext;
				contextParamter = Collections.emptyList();
			}
		} else {
			// not controller, should return or throw an exceptions, not valid controller 
			// return;
			controllerContext = clazz.getSimpleName();
			context = controllerContext;
			contextParamter = Collections.emptyList();
		}
		
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
			indexBuilder.canDoExchange(method.isAnnotationPresent(DoExchange.class));
			
			HttpMethod httpMethod = annotation.annotationType().getAnnotation(HttpMethod.class);
			indexBuilder.httpMethod(httpMethod.method().name());

			Method pathMethod = annotation.getClass().getMethod("path");
			Method headersMethod = annotation.getClass().getMethod("headers");
			Method producesMethod = annotation.getClass().getMethod("produces");

			indexBuilder.headers(  Arrays.asList((String[]) headersMethod.invoke(annotation)) );
			indexBuilder.produces( Arrays.asList((String[]) producesMethod.invoke(annotation)) );
			
			String rootContext = pathMethod.invoke(annotation).toString();
			if (rootContext.contains("{")) {
				indexBuilder.context( rootContext.substring(0, rootContext.indexOf('{') ));
				indexBuilder.pathVariables(listOfPathVariavle(rootContext));
			} else {
				indexBuilder.context( rootContext );
				indexBuilder.pathVariables( Collections.emptyList() );
			}
			
			indexBuilder.parametersInfo(getParametersInfo(method));
			
			return indexBuilder.build();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	ArrayList<String> listOfPathVariavle(String context) {
		ArrayList<String> list = new ArrayList<>(1);
		int index = context.indexOf('{');
		do {
			context = context.substring(index+1);
			index = context.indexOf('}');
			list.add(context.substring(0, index));
			index = context.indexOf('{');
		} while (index > 0);
		return list;
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
					builder.index(i);
					Annotation annotation = parameter.getAnnotation(annotationClass);
					builder.annotation(annotation);
					if (annotationClass.equals(RequestBody.class)) {
						builder.name("@Body");
					} else {
						try {
							builder.name(annotationClass.getMethod("value").invoke(annotation).toString());
						} catch (Exception e) { }
					}
					break;
				}
			}
			infos.add(builder.build());
		};
		return infos;
	}

	

}

package org.ariia.mvc.processing;


import java.lang.reflect.Method;
import java.util.List;

public class MethodIndex {
	
	String requestMethod;
	Method method;
	String context;
	String contextParamter;
	List<String> paramter;
	List<String> headers;
	List<String> produces;
			
	public String getRequestMethod() {return requestMethod;}
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
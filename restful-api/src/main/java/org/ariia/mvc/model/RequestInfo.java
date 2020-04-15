package org.ariia.mvc.model;

import java.util.HashMap;
import java.util.Map;

public class RequestInfo {
	
	private Map<String, String> paramters;
	private Map<String, String> pathVariable;
	private String body;
	private String methodContext;
	
	public RequestInfo() {
		this.paramters = new HashMap<>();
		this.pathVariable = new HashMap<>();
	}
	
	public void setParamters(String query) {
		for (String qu : query.split("&")) {
			String[] values = qu.split("=", 2);
			this.paramters.put(values[0], values[1]);
		}
	}
	public Map<String, String> getParamters() {
		return paramters;
	}
	
//	public String putParamter(String key, String value) {
//		return this.paramters.put(key, value);
//	}
	
	public String putPathVariable(String key, String value) {
		return this.pathVariable.put(key, value);
	}
	
	public Map<String, String> getPathVariable() {
		return pathVariable;
	}
	
	public void setPathVariable(Map<String, String> pathVariable) {
		this.pathVariable = pathVariable;
	}
	
	public void setMethodContext(String methodContext) {
		this.methodContext = methodContext;
	}
	public String getMethodContext() {
		return methodContext;
	}
	
	public void setBody(String body) {
		this.body = body;
	}
	public String getBody() {
		return body;
	}
}

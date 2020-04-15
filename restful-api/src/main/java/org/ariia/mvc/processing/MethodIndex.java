package org.ariia.mvc.processing;


import java.lang.reflect.Method;
import java.util.List;

import org.ariia.mvc.model.ParameterInfo;


public class MethodIndex {
	
	private Method method;
	private String context;
	private String httpMethod;
	private List<String> paramter;
	private List<String> headers;
	private List<String> produces;
	private List<ParameterInfo> parametersInfo;
	
	public String httpMethod() {return httpMethod;}
	public String context() {return context;}
	public Method method() {return method;}
	public List<String> headers() {return headers;}
	public List<String> produces() {return produces;}
	public List<String> paramter() {return paramter;}
	public List<ParameterInfo> getParametersInfo() {return parametersInfo;}
	
	@Override
	public String toString() {
		return String.format("request\t%s %s\ncontext\t%s %s\n%s %s %s\n", 
				httpMethod, method.getName(), 
				context, parametersInfo,
				headers, paramter, produces);
	}
	
	public static class MethodIndexBuilder {
		
		private Method method;
		private String context;
		private String httpMethod;
		private List<String> paramter;
		private List<String> headers;
		private List<String> produces;
		private List<ParameterInfo> parametersInfo;
		
		public MethodIndexBuilder httpMethod(String httpMethod) {
			this.httpMethod = httpMethod;
			return this;
		}
		public MethodIndexBuilder method(Method method) {
			this.method = method;
			return this;
		}
		public MethodIndexBuilder context(String context) {
			this.context = context;
			return this;
		}
		public MethodIndexBuilder paramter(List<String> paramter) {
			this.paramter = paramter;
			return this;
		}
		public MethodIndexBuilder headers(List<String> headers) {
			this.headers = headers;
			return this;
		}
		public MethodIndexBuilder produces(List<String> produces) {
			this.produces = produces;
			return this;
		}
		public MethodIndexBuilder parametersInfo(List<ParameterInfo> parametersInfo) {
			this.parametersInfo = parametersInfo;
			return this;
		}
		public MethodIndex build() {
			MethodIndex methodIndex = new MethodIndex();
			
			methodIndex.httpMethod = this.httpMethod;
			methodIndex.method = this.method;
			methodIndex.context = this.context;
			methodIndex.paramter = this.paramter;
			methodIndex.headers = this.headers;
			methodIndex.produces = this.produces;
			methodIndex.parametersInfo = this.parametersInfo;
			
			return methodIndex;
		}
		
	}
	
}
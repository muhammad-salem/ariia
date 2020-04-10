package org.ariia.mvc.model;

import java.util.Collection;
import java.util.List;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class ListModelHandler<T> implements HttpHandler {
	
	private String context;
	private List<T> list;

	public ListModelHandler(List<T> list, String context) {
		this.list = list;
		this.context = context;
	}
	
	public List<T> getList() {
		return list;
	}

	public void setList(List<T> list) {
		this.list = list;
	}

	public String getContext() {
		return context;
	}
	
	
	/**
	 * > GET ->	{context}/1		--> get index 1
	 * > GET ->	{context}/all	--> get all the list
	 * 
	 * > DELETE -> {context}/1 	--> remove index 1
	 * 
	 * > POST ->	{context}/{action:delete}/{id:1}
	 * > POST ->	{context}/{action:add}/{body:item} 		--> add new item
	 * > POST ->	{context}/{action:start}/{id:1}	--> start download index 1 in waiting list
	 * > POST ->	{context}/{action:stop}/{id:1}	--> stop  download index 1 in downloading list
	 * > POST ->	{context}/{action:clear}		--> clear downloading list and waiting list
	 * 
	 * 
	 * {context}/{action}/{body?}/{id?}
	 * 
	 */

	@Override
	public void handle(HttpExchange exchange) {
//		String url = exchange.getRequestURI().toString();
//		InputStream requestBody = exchange.getRequestBody();
		String method = exchange.getRequestMethod();
		if (method.equalsIgnoreCase("get")) {
			get(exchange);
		} else if (method.equalsIgnoreCase("post")) {
			post(exchange);
		}
	}
	
	private void get(HttpExchange exchange) {
		
	}
	
	private void post(HttpExchange exchange) {
		
	}

	
	public boolean add(T e) {
		return false;
	}
	
	public boolean remove(T o) {
		return false;
	}
	
	public boolean removeAll(Collection<?> c) {
		return false;
	}

	public void clear() {
		
	}

	public T get(int index) {
		
		return null;
	}

	public T remove(int index) {
		return null;
	}

	public List<T> subList(int fromIndex, int toIndex) {
		
		return null;
	}
	
}
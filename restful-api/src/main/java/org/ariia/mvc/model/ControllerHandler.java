package org.ariia.mvc.model;

import java.io.IOException;
import java.util.List;

import org.ariia.mvc.processing.MethodIndex;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class ControllerHandler implements HttpHandler {
	
	List<MethodIndex> methodIndexs;

	@Override
	public void handle(HttpExchange exchange) throws IOException {
		// TODO Auto-generated method stub

	}

}

package org.ariia.mvc;

import java.io.IOException;

import org.ariia.mvc.model.ContextActionHandler;
import org.ariia.mvc.model.ControllerHandler;
import org.ariia.mvc.processing.MethodIndex;
import org.ariia.mvc.processing.ProxySwitcher;
import org.junit.jupiter.api.Test;

public class WebServerTest {
	
	public static void main(String[] args) throws IOException {
		new WebServerTest().testWebServer();
	}
	
	@Test
	private void testWebServer() {
		try {
			int port = 8080;
	        String resourceLocation =  "/static/angular";
	        WebServer.ResourceType type = WebServer.ResourceType.IN_MEMORY ;
	        System.out.printf("port: %d, location: %s, type: %s\n", port, resourceLocation, type);
	        WebServer server = new WebServer(port, resourceLocation, type);
	        server.createContext("/context/", new ContextActionHandler<>("/context/"));
	        
	        ItemController test = new ItemController();
	        ProxySwitcher switcher = new ProxySwitcher(test);
	        switcher.getMethodIndexs().stream()
	        	.map(MethodIndex::getContextParamter).forEach(System.out::println);
	        ControllerHandler handler = new ControllerHandler(test, switcher.getMethodIndexs());
	        server.createContext(switcher.getControllerContext(), handler);
	        
	        server.start();
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

}

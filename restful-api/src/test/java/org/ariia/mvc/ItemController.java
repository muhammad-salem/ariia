package org.ariia.mvc;

import org.ariia.mvc.annoutation.GetRequest;
import org.ariia.mvc.annoutation.PostRequest;
import org.ariia.mvc.annoutation.RequestBody;
import org.ariia.mvc.annoutation.RequestParam;
import org.ariia.mvc.annoutation.RestContext;

@RestContext("/item")
public class ItemController {
	
	@GetRequest( path = "/{id}", produces = "application/json" )
	public String getItem(@RequestParam("id") int id) {
		return "";
	}
	
	@GetRequest( path = "/{id}", produces = "application/json" )
	public PersonTestModel getItem(@RequestParam("id") String id) {
		return new PersonTestModel();
	}
	
	@PostRequest(path = "/add")
	public void addItem(@RequestBody String json) {
		
	}
	
	@PostRequest(path = "/edit/{index}")
	public void editItem(@RequestParam("index") int index, @RequestBody String json) {
		
	}
	@PostRequest(path = "/")
	private void ff() {
		// TODO Auto-generated method stub

	}

}

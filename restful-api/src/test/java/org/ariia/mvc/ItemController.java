package org.ariia.mvc;

import org.ariia.mvc.annotation.PathVariable;
import org.ariia.mvc.annotation.RequestBody;
import org.ariia.mvc.annotation.RequestParam;
import org.ariia.mvc.annotation.RestContext;
import org.ariia.mvc.annotation.method.GetRequest;
import org.ariia.mvc.annotation.method.PostRequest;

@RestContext("/item")
public class ItemController {
	
//	@GetRequest( path = "/{id}", produces = "application/json" )
//	public String getItemInt(@PathVariable("id") Integer id) {
//		System.out.println("getItemInt id " + id);
//		return "";
//	}
	
	@GetRequest( path = "/{id}", produces = "application/json" )
	public String getItemString(@PathVariable("id") String id) {
		System.out.println("getItemString id " + id);
		return id;
	}
	
	@GetRequest( path = "/info/{id}", produces = "application/json" )
	public PersonTestModel getItemModel(
			@PathVariable("id") String id,
			@RequestParam("name") String name,
			@RequestBody String body) {
		return new PersonTestModel();
	}
	
	@PostRequest(path = "/add")
	public void add(@RequestBody String json) {
		
	}
	
	@PostRequest(path = "/addItem")
	public String addItem(@RequestBody String json) {
		System.out.println("addItem json " + json);
		return json;
	}
	
	@PostRequest(path = "/edit/{index}")
	public void editItem(@RequestParam("index") int index, @RequestBody String json) {
		
	}
	@PostRequest(path = "/")
	private void ff() {
		// TODO Auto-generated method stub

	}

}

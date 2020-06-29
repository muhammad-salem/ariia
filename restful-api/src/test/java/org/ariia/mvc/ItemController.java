package org.ariia.mvc;

import org.ariia.mvc.annotation.PathVariable;
import org.ariia.mvc.annotation.RequestBody;
import org.ariia.mvc.annotation.RequestParam;
import org.ariia.mvc.annotation.RestController;
import org.ariia.mvc.annotation.method.GetRequest;
import org.ariia.mvc.annotation.method.PostRequest;

@RestController("/item/{itemData}/{test}")
public class ItemController {

    @GetRequest(path = "/{id}", produces = "application/json")
    public String getItemInt(@PathVariable("id") Integer id) {
        System.out.println("getItemInt id " + id);
        return "" + id;
    }

    @PostRequest(path = "/info/{id}", produces = "application/json")
    public long postId(
            @PathVariable("id") String id,
            @RequestParam("name") String name,
            @RequestBody PersonTestModel body) {
        System.out.println(id + " " + name + " " + body);
        return 4545l;
    }

    @GetRequest(path = "/info/{id}", produces = "application/json")
    public PersonTestModel infoModel(
            @PathVariable("id") String id,
            @RequestParam("name") String name,
            @PathVariable("itemData") String itemData) {
        System.out.println(id + " " + name + " " + itemData);
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

    @PostRequest(path = "/edit/{index}-{id}")
    public void nextToNext(@RequestParam("index") int index, @RequestBody String json) {

    }

    @PostRequest(path = "/edit/{index}{error}")
    public void nextError(@RequestParam("index") int index, @RequestBody String json) {

    }

//	@PostRequest(path = "/")
//	private void ff() {
//	}

}

package org.ariia.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Cookie;
import okhttp3.HttpUrl;

public interface RequestResolver {

	Item resolveHttpUrl(HttpUrl url, Map<String, String> headers, List<Cookie> cookies) ;
	Item resolveLessItem(HttpUrl url, Map<String, String> headers, List<Cookie> cookies) ;
	Item resolveItem(Item item);
	 List<Item> readUrlListFromFile(String filePath);
	
	default public  Item resolveUrl(String url) {
		return resolveHttpUrl(HttpUrl.parse(url), new HashMap<>(), new ArrayList<>());
	}

	default Item resolveUrl(HttpUrl url) {
		return resolveHttpUrl(url, new HashMap<>(), new ArrayList<>());
	}

	default Item resolveUrlwithCookies(HttpUrl url, List<Cookie> cookies) {
		return resolveHttpUrl(url, new HashMap<>(), cookies);
	}

	default Item resolveUrlWithHeaders(HttpUrl url, Map<String, String> headers) {
		return resolveHttpUrl(url, headers, new ArrayList<>());
	}


	// -------------------------------------------------//

	default Item resolveLessUrl(String url) {
		return resolveLessItem(HttpUrl.parse(url), new HashMap<>(), new ArrayList<>());
	}

	default Item resolveLessUrl(HttpUrl url) {
		return resolveLessItem(url, new HashMap<>(), new ArrayList<Cookie>());
	}

	default Item resolveLessUrlwithCookies(HttpUrl url, List<Cookie> cookies) {
		return resolveLessItem(url, new HashMap<>(), cookies);
	}

	default Item resolveLessUrlWithHeaders(HttpUrl url, Map<String, String> headers) {
		return resolveLessItem(url, headers, new ArrayList<>());
	}


	default List<Item> resolveItem(List<Item> items) {
		items.forEach(item -> resolveItem(item));
		return items;
	}

	

}

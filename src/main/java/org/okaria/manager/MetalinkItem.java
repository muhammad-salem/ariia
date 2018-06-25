package org.okaria.manager;

import java.util.List;

import org.okaria.Utils;

import okhttp3.HttpUrl;

public class MetalinkItem extends Item {

	
	private List<HttpUrl> urls;

	public List<HttpUrl> getUrls() {
		return urls;
	}

	public void setUrls(List<HttpUrl> urls) {
		this.urls = urls;
	}
	
	public HttpUrl getUrl(int index) {
		return urls.get(index);
	}
	public HttpUrl getUrl() {
		return urls.get(0);
	}

	public void setUrl(HttpUrl url) {
		this.urls.add(url);
	}

	static MetalinkItem jsonMetalinkItem(String filePath) {
		return Utils.fromJson(filePath, MetalinkItem.class);
	}
	static boolean toJsonMetalinkItem(String filePath, MetalinkItem item) {
		return Utils.toJsonFile(filePath, item);
	}

}

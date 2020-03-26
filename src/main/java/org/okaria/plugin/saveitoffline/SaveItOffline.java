package org.okaria.plugin.saveitoffline;

import java.util.LinkedList;
import java.util.List;

import org.terminal.console.log.Log;
import org.okaria.manager.Item;
import org.okaria.okhttp.client.Client;
import org.okaria.plugin.saveitoffline.OfflineObject.Quality;
import org.okaria.util.Utils;

import okhttp3.HttpUrl;
import okhttp3.Response;

public class SaveItOffline {
	
	/**
	 * https://www.saveitoffline.com/process/?url=http://example.com&type=json
	 */
	static String baseUrl = "http://www.saveitoffline.com/process/?type=json&url=";
	static String baseUrlHttps = "https://www.saveitoffline.com/process/?type=json&url=";
	String page;
	boolean https;
	
	Client client;
	public SaveItOffline(String page, Client client) {
		this(page, client, false);
	}
	public SaveItOffline(String page, Client client, boolean https) {
		this.page = page;
		this.https = https;
		this.client = client;
	}
	
	public List<Item> generateBuilder(String savePath) {
		try {
			
			Response response = client.get(getUrl());
			String json = response.body().string();
			response.close();
			Log.warn(json);
			OfflineObject object = Utils.json(json, OfflineObject.class);
			List<Item> list = new LinkedList<>();
			for (Quality quality : object.urls) {
				Item item = new Item();
				item.setUrl(quality.id);
				item.setFilename(object.title + ' ' + quality.filename() + ".mp4");
				list.add(item);
			}
			return list;
		} catch (Exception e) {
			Log.error(getClass(), e.getClass().getSimpleName(), e.getMessage());
		}
		return null;
	}
	private HttpUrl getUrl() {
		String url;
		if(https) {
			url = baseUrlHttps + page; 
		}else {
			url = baseUrl + page; 
		}
		return HttpUrl.parse(url);
	}
	
}

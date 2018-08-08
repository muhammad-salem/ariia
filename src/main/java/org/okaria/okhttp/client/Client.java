package org.okaria.okhttp.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.concurrent.Future;

import org.log.concurrent.Log;
import org.okaria.core.CookieJars;
import org.okaria.core.OkConfig;
import org.okaria.manager.Item;
import org.okaria.manager.ItemMetaData;
import org.okaria.okhttp.OkUtils;
import org.okaria.okhttp.queue.DownloadPlane;
import org.okaria.okhttp.request.ClientRequest;
import org.okaria.okhttp.request.StreamingClientRequest;
import org.okaria.okhttp.response.DownloadResponse;
import org.okaria.range.RangeInfo;
import org.okaria.speed.SpeedMonitor;

import okhttp3.CookieJar;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.ResponseBody;

public abstract class Client implements StreamingClientRequest, DownloadResponse, DownloadPlane {

	
	private OkHttpClient httpClient;
	
	public Client(OkConfig config) {
		createOkHttpClient(config);
	}
	public Client(OkHttpClient httpClient) {
		this.httpClient = httpClient;
	}
	public Client(OkHttpClient.Builder builder) {
		createOkHttpClient(builder);
	}

	
	public void createOkHttpClient(OkHttpClient.Builder builder) {
		httpClient = builder.build();
	}

	public void createOkHttpClient(OkConfig config) {
		if (config == null) {
			httpClient = new OkHttpClient();
		} else {
			OkHttpClient.Builder builder = new OkHttpClient.Builder();
			builder.cookieJar(config.cookieJar());
			builder.proxy(config.proxy());
			httpClient = builder.build();
		}
	}

	protected void defalutOkHttpClient() {
		httpClient = new OkHttpClient().newBuilder().cookieJar(CookieJars.CookieJarMap).build();
	}

	public void changeProxy(Proxy.Type type, String hostname, int port) {
		OkHttpClient.Builder builder = getHttpClient().newBuilder();
		builder.proxy(new Proxy(type, new InetSocketAddress(hostname, port)));
		httpClient = builder.build();
	}

	public void setCookieJar(CookieJar cookieJar) {
		OkHttpClient.Builder builder = getHttpClient().newBuilder();
		builder.cookieJar(cookieJar);
		httpClient = builder.build();
	}

	@Override
	public OkHttpClient getHttpClient() {
		if (httpClient == null)
			defalutOkHttpClient();
		return httpClient;
	}
		
	public void updateItemOnline(Item item) {
		boolean reGet = true;
		do {
			reGet = updateItemOnline(item, false);
			if (reGet)
				break;
			reGet = updateItemOnline(item, true);
		} while (!reGet);
	}

	private boolean updateItemOnline(Item item, boolean headOrGet) {
		Response response = null;
		try {
			if (headOrGet)
				response = head(item.url(), item.getCookies(), item.getHeaders());
			else
				response = get(item.url(), item.getCookies(), item.getHeaders());
		} catch (IOException e) {
			Log.warn(e.getClass(), "Exception", e.getMessage());
			return false;
		}
		
		if(response.code() == 404) {
			item.setFilename("404:Not:Found");
			return true;
		}

		// System.out.println(response);
		if (response.networkResponse() != null 
				&& ! response.networkResponse().request().url().toString()
					.equals(item.getUrl())) {
			item.setRedirect();
			item.setRedirectUrl(response.networkResponse().request().url().toString());
			Log.fine(getClass(), "redirect item to another location", 
					item.getUrl() + '\n' + item.getRedirectUrl());
		}
		ResponseBody body = response.body();
		try {
			long length = Long.parseLong(response.header("Content-Length")); // body.contentLength();

			RangeInfo rangeInfo = new RangeInfo(length);
			item.setRangeInfo(rangeInfo);
		} catch (NumberFormatException e) {
			RangeInfo rangeInfo = new RangeInfo();
			item.setRangeInfo(rangeInfo);
		} finally {
			body.close();
		}
		if (item.getFilename() == null) {
			
			String filename = OkUtils.Filename(item.updateUrl());
			String contentDisposition = response.header("Content-disposition", "filename=\"" + filename + "\"");
			if (contentDisposition.contains("filename")) {
				String[] split = contentDisposition.split("\"");
				filename = split[split.length - 1];
			}
			item.setFilename(filename);
		}

		return true;
	}
	@Override
	public ClientRequest getClientRequest() {
		return this;
	}
	
	
	public abstract Future<?> downloadPart(ItemMetaData placeHolder, int index,SpeedMonitor... monitors);
	
	
	public abstract boolean downloadTask(ItemMetaData placeHolder, int index,SpeedMonitor... monitors);



}

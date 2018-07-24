package org.okaria.okhttp;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.log.concurrent.Log;
import org.okaria.lunch.FilenameStore;
import org.okaria.manager.Item;
import org.okaria.manager.MetalinkItem;
import org.okaria.okhttp.request.ClientRequest;
import org.okaria.okhttp.request.StreamingClientRequest;
import org.okaria.okhttp.writer.ClientChannelWriter;
import org.okaria.okhttp.writer.ClinetWriter;
import org.okaria.queue.StreamDownloadPlane;
import org.okaria.range.RangeInfo;
import org.okaria.setting.AriaProperties;
import org.okaria.speed.SpeedMonitor;

import okhttp3.CookieJar;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class OkClient implements StreamingClientRequest, ClientResponse, StreamDownloadPlane {

	
	
	private FilenameStore store;
	private OkHttpClient httpClient;
	private ClinetWriter channelWriter = new ClientChannelWriter() {}; // ClientMappedWriter ClientChannelWriter

	@Override
	public ClinetWriter getClinetWriter() {
		return channelWriter;
	}

	ExecutorService executor =  Executors.newCachedThreadPool(); // Executors.newFixedThreadPool(32); //

	public OkClient(OkConfig config) {
		this.store = new FilenameStore();
		createOkHttpClient(config);
	}

	public OkClient(OkConfig config, FilenameStore store) {
		this.store = store;
		createOkHttpClient(config);
	}

	public OkClient(OkHttpClient.Builder builder) {
		this.store = new FilenameStore();
		createOkHttpClient(builder);
	}

	public OkClient(OkHttpClient httpClient, FilenameStore store) {
		this.httpClient = httpClient;
		this.store = store;
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
	public ClientRequest getClientRequest() {
		return this;
	}

	@Override
	public OkHttpClient getHttpClient() {
		if (httpClient == null)
			defalutOkHttpClient();
		return httpClient;
	}

	public void setSharedClient(OkHttpClient sharedClient) {
		this.httpClient = sharedClient;
	}
	

	public Future<?> downloadPart(Item item, int index, SpeedMonitor... monitors) {

		if (AriaProperties.RETRIES == 0) {
			return executor.submit(() -> {
				boolean finsh = false;
				while (!finsh) {
					finsh = downloadTask(item, index, monitors);
				}
			});
		}else {
			return executor.submit(() -> {
				for (int i = 0; i < AriaProperties.RETRIES; i++) {
					if(downloadTask(item, index, monitors)) {
						break;
					}
				}
			});
		}

	}

	private boolean updateItemOnline(Item item, boolean headOrGet) {
		Response response = null;
		try {
			if (headOrGet)
				response = head(item.getUrl(), item.getCookies(), item.getHeaders());
			else
				response = get(item.getUrl(), item.getCookies(), item.getHeaders());
		} catch (IOException e) {
			Log.warning(e.getClass(), "Exception", e.getMessage());
			return false;
		}

		// System.out.println(response);
//		if (response.isRedirect()) {
//			item.setRedirect();
//			item.setRedirectUrl(response.request().url());
//			Log.info(getClass(), "url is redirect", item.getRedirectUrl());
//		}
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
			String filename = store.get(item.url());
			String contentDisposition = response.header("Content-disposition", "filename=\"" + filename + "\"");
			if (contentDisposition.contains("filename")) {
				String[] split = contentDisposition.split("\"");
				filename = split[split.length - 1];
			}
			item.setFilename(filename);
		}
		store.put(item.url(), item.getFilename());

		return true;
	}

	private boolean updateItemOnline(MetalinkItem item, boolean headOrGet) {
		Response response = null;
		try {
			if (headOrGet)
				response = head(item.getUrl(), item.getCookies(), item.getHeaders());
			else
				response = get(item.getUrl(), item.getCookies(), item.getHeaders());
		} catch (IOException e) {
			Log.warning(e.getClass(), "Exception", e.getMessage());
			return false;
		}

		// System.out.println(response);
//		if (response.isRedirect()) {
//			item.setRedirect();
//			item.setRedirectUrl(response.request().url());
//			Log.info(getClass(), "url is redirect", item.getRedirectUrl());
//		}
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

		String filename = store.get(item.url());
		String contentDisposition = response.header("Content-disposition", "filename=\"" + filename + "\"");
		if (contentDisposition.contains("filename")) {
			String[] split = contentDisposition.split("\"");
			filename = split[split.length - 1];
		}
		item.setFilename(filename);
		store.put(item.url(), filename);
		return true;
	}

	public Item resolveItem(Item item) {
		boolean reGet = true;
		do {
			reGet = updateItemOnline(item, true);
			if (reGet)
				break;
			reGet = updateItemOnline(item, false);
		} while (!reGet);
		return item;
	}

	public MetalinkItem resolveItem(MetalinkItem item) {
		boolean reGet = true;
		do {
			reGet = updateItemOnline(item, true);
			if (reGet)
				break;
			reGet = updateItemOnline(item, false);
		} while (!reGet);
		return item;
	}

}

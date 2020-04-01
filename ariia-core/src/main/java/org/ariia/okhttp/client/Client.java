package org.ariia.okhttp.client;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.ariia.core.OkConfig;
import org.ariia.items.Item;
import org.ariia.logging.Log;
import org.ariia.manager.ItemMetaData;
import org.ariia.okhttp.OkUtils;
import org.ariia.okhttp.queue.DownloadPlane;
import org.ariia.okhttp.request.ClientRequest;
import org.ariia.okhttp.response.DownloadResponse;
import org.ariia.range.RangeInfo;
import org.ariia.setting.Properties;
import org.ariia.speed.SpeedMonitor;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Response;

public abstract class Client implements  ClientRequest /*StreamingClientRequest*/, DownloadResponse, DownloadPlane, ContentLength {

	
	private OkHttpClient httpClient;
	private OkConfig config;
	
	public Client(OkConfig config) {
		this.setConfig(config);
	}
	public Client(OkHttpClient httpClient) {
		this.setHttpClient(httpClient);
	}
	

	private void init(OkConfig config) {
		OkHttpClient.Builder builder = new OkHttpClient.Builder();
		builder
			.cookieJar(config.cookieJar())
			.proxy(config.proxy())
			.retryOnConnectionFailure(false);
		setHttpClient(builder.build());
	}

	
	private void init(OkHttpClient httpClient) {
		this.config = new OkConfig();
		this.config.updateCookieJar(httpClient.cookieJar());
		this.config.updaeProxy(httpClient.proxy());
	}
	
	public OkConfig getConfig() {
		return config;
	}
	
	public void setConfig(OkConfig config) {
		this.config = config;
		init(config);
	}

	@Override
	public OkHttpClient getHttpClient() {
		return httpClient;
	}
	public void setHttpClient(OkHttpClient httpClient) {
		this.httpClient = httpClient;
		this.init(httpClient);
	}
			
	public void updateItemOnline(Item item) {
		
		for (int i = 0; i < 5; i++) {
			try {
				updateItemOnline(item, false);
				break;
			} catch (IOException e) {
				try {
					updateItemOnline(item, true);
					break;
				} catch (IOException e1) { }
			}
		}
	}

	private void updateItemOnline(Item item, boolean headOrGet) throws IOException {
		
		try (Response response = headOrGet 
				? head(item.getUrl(), getHeaders(item))
				: get(item.getUrl(), getHeaders(item))) {
			
			if(response.code() == 404) {
				item.setFilename("404_Not_Found");
				return;
			}
			
			HttpUrl usedUrl;
			 if (response.networkResponse() != null 
					&& ! response.networkResponse().request().url().toString()
						.equals(item.getUrl())) {
				 usedUrl = response.networkResponse().request().url();
				Log.trace(getClass(), "redirect item to another location","base url:\t" + item.getUrl() 
						+ "\nredirect url: \t"+ usedUrl );
			} else {
				usedUrl = response.request().url();
			}
			long length = extracteLength(response);
			item.setRangeInfo(new RangeInfo(length, length > 0 ? Properties.RANGE_POOL_NUM : 1));
			
			
			if (item.getFilename() == null) {
				String filename = OkUtils.Filename(usedUrl);
				String contentDisposition = response.networkResponse().header("Content-disposition", "filename=\"" + filename + "\"");
				if (contentDisposition.contains("filename")) {
					String[] split = contentDisposition.split("=");
					filename = split[split.length - 1].trim();
					filename = filename.substring(
							filename.charAt(0) == '"' ? 1 : 0,
							filename.charAt(filename.length()-1) == '"'
									? filename.length()-1: filename.length());
				}
				item.setFilename(filename);
			}
		} catch (IOException e) {
			Log.warn(getClass(), e.getClass().getSimpleName(), e.getMessage());
			throw e;
		}
		
	}
	
	
	@Override
	public ClientRequest getClientRequest() {
		return this;
	}
	
	public abstract Future<?> downloadPart(ItemMetaData placeHolder, int index,SpeedMonitor... monitors);
	
	public abstract boolean downloadTask(ItemMetaData placeHolder, int index,SpeedMonitor... monitors);

	public abstract ExecutorService getExecutorService();
	
}

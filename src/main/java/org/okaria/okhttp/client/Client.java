package org.okaria.okhttp.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.log.concurrent.Log;
import org.okaria.core.CookieJars;
import org.okaria.core.OkConfig;
import org.okaria.manager.Item;
import org.okaria.manager.ItemMetaData;
import org.okaria.okhttp.OkUtils;
import org.okaria.okhttp.queue.DownloadPlane;
import org.okaria.okhttp.request.ClientRequest;
import org.okaria.okhttp.response.DownloadResponse;
import org.okaria.range.RangeInfo;
import org.okaria.setting.Properties;
import org.okaria.speed.SpeedMonitor;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Response;

public abstract class Client implements  ClientRequest /*StreamingClientRequest*/, DownloadResponse, DownloadPlane, ContentLength {

	
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
		httpClient = new OkHttpClient().newBuilder()
				.cookieJar(CookieJars.CookieJarMap)
				.build();
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
		int count = 0;
		do {
			try {
				 updateItemOnline(item, false);
				 count++;
				 break;
			} catch (Exception e) {
				try {
					updateItemOnline(item, true);
					 count++;
					break;
				} catch (IOException e1) {
					if (count >= 10) {
						break;
					}
				}
			}
		} while (true);
	}

	private void updateItemOnline(Item item, boolean headOrGet) throws IOException {
		if (item.getCookies().size() > 0) {
			getHttpClient().cookieJar().saveFromResponse(item.url(), item.getCookies());
		}
		
		try (Response response = headOrGet 
				? head(item.url(), getHeaders(item))
				: get(item.url(), getHeaders(item))) {
			
			List<Cookie> list =  getHttpClient().cookieJar().loadForRequest(item.url());
			item.addCookies(list);
			
			if(response.code() == 404) {
				item.setFilename("404_Not_Found");
				return;
			}
			
			HttpUrl usedUrl;
			 if (response.networkResponse() != null 
					&& ! response.networkResponse().request().url().toString()
						.equals(item.getUrl())) {
				 usedUrl = response.networkResponse().request().url();
				Log.fine(getClass(), "redirect item to another location","base url:\t" + item.getUrl() 
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

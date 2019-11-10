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
//			builder.addNetworkInterceptor(networkInterceptor);
			httpClient = builder.build();
		}
	}

	protected void defalutOkHttpClient() {
		httpClient = new OkHttpClient().newBuilder()
				.cookieJar(CookieJars.CookieJarMap)
//				.addNetworkInterceptor(networkInterceptor)
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
		boolean retrie = true;
		do {
			retrie = updateItemOnline(item, false);
			if (retrie)
				break;
			retrie = updateItemOnline(item, true);
		} while (!retrie);
	}

	private boolean updateItemOnline(Item item, boolean headOrGet) {
		Response response = null;
		getHttpClient().cookieJar().saveFromResponse(item.url(), item.getCookies());
		try {
			if (headOrGet)
				response = head(item.url(), item.getCookies(), item.getHeaders());
			else
				response = get(item.url(), item.getCookies(), item.getHeaders());
		} catch (IOException e) {
			Log.warn(e.getClass(), "Exception", e.getMessage());
			return false;
		}
		
		List<Cookie> list =  getHttpClient().cookieJar().loadForRequest(item.url());
		item.addCookies(list);
		
//		getHttpClient().cookieJar().saveFromResponse(url, jar);
		
		if(response.code() == 404) {
			item.setFilename("404_Not_Found");
			response.close();
			return true;
		}

		// System.out.println(response);
		
		HttpUrl usedUrl;
		 if (response.networkResponse() != null 
				&& ! response.networkResponse().request().url().toString()
					.equals(item.getUrl())) {
			 usedUrl = response.networkResponse().request().url();
			Log.fine(getClass(), "redirect item to another location","base url:\t" + item.getUrl() 
					+ "\n redirect url \t"+ usedUrl );
		}else {
			usedUrl = response.request().url();
		}
		
		long length = extracteLength(response);
		RangeInfo rangeInfo = new RangeInfo();

		if(length > 0) {
			//if (Properties.RANGE_POOL_NUM > 0){
			//	rangeInfo = new RangeInfo(length, Properties.RANGE_POOL_NUM);
			//}
			//else
			if (length > 104857600) {		// 100MB
				rangeInfo = RangeInfo.RangeInfo2M(length);
			}
			else if (length > 10485760) {	// 10MB
				rangeInfo = RangeInfo.RangeInfo1M(length);
			}
			else if (length > 5242880){		//5M
				rangeInfo = RangeInfo.RangeInfo512K(length);
			}else{
				rangeInfo = new RangeInfo(length);
			}
		}
		item.setRangeInfo(rangeInfo);
		response.close();
		
		if (item.getFilename() == null) {
			String filename = OkUtils.Filename(usedUrl);
			String contentDisposition = response.networkResponse().header("Content-disposition", "filename=\"" + filename + "\"");
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

	public abstract ExecutorService getExecutorService();
	
}

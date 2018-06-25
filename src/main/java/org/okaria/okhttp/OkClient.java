package org.okaria.okhttp;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.okaria.manager.Item;
import org.okaria.okhttp.writer.ClientChannelWriter;
import org.okaria.okhttp.writer.ClinetWriter;
import org.okaria.queue.StreamDownloadPlane;
import org.okaria.speed.SpeedMonitor;

import okhttp3.CookieJar;
import okhttp3.OkHttpClient;

public class OkClient implements ClientRequest, ClientResponse, StreamDownloadPlane {

	private OkHttpClient httpClient;
	private ClinetWriter channelWriter =  new ClientChannelWriter() {}; //  ClientMappedWriter ClientChannelWriter
	@Override
	public ClinetWriter getClinetWriter() {
		return channelWriter;
	}
	

	ExecutorService executor = Executors.newFixedThreadPool(32); // newCachedThreadPool();
	List<Future<?>> futures = new LinkedList<Future<?>>();

	public OkClient(OkConfig config) {
		createOkHttpClient(config);
	}

	public OkClient(OkHttpClient.Builder builder) {
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


//	public void downloadItem(Item item, SpeedMonitor... monitors) {
//		if(item.isStateEqual(ItemState.COMPLETE)) return;
//		item.setState(ItemState.DOWNLOAD);
//		
//		for (int index = 0; index < item.getRangeInfo().getRangeCount(); index++) {
//			downloadPart(item, index, monitors);
//		}
//	}
	

	public void downloadPart(Item item, int index, SpeedMonitor... monitors) {
		Future<?> future = executor.submit(() -> {
			boolean finsh = false;
			while (!finsh) {
				finsh = downloadTask(item, index, monitors);
			}
		});
		futures.add(future);
	}

}

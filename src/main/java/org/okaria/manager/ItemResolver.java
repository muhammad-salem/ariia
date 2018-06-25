package org.okaria.manager;

import static java.lang.System.currentTimeMillis;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.okaria.R;
import org.okaria.Utils;
import org.okaria.manager.Item.Builder;
import org.okaria.okhttp.ClientRequest;
import org.okaria.okhttp.ClientResponse;
import org.okaria.okhttp.writer.ClinetWriter;
import org.okaria.range.RangeInfo;

import okhttp3.Cookie;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class ItemResolver implements RequestResolver, ClientResponse, ClientRequest {

	OkHttpClient client;

	public ItemResolver(OkHttpClient client) {
		this.client = client;
	}

	@Override
	public ClientRequest getClientRequest() {
		return this;
	}

	@Override
	public OkHttpClient getHttpClient() {
		return client;
	}
	
	public Item resolveHttpUrl(HttpUrl url, Map<String, String> headers, List<Cookie> cookies) {
		client.cookieJar().saveFromResponse(url, cookies);
		Builder builder = new Builder();
		builder.id(currentTimeMillis());
		builder.url(url);
		builder.savepath();
		builder.cookies(cookies);
		builder.headers(headers);
		try {
			Response response = head(url, Headers.of(headers));
			if (response.isRedirect()) {
				builder.redirect();
				builder.redirectUrl(response.request().url());
			}
			ResponseBody body = response.body();
			long length = Long.parseLong(response.header("Content-Length")); // body.contentLength();
			// item.getRangeInfo().setFileLength(length);
			body.close();
			RangeInfo rangeInfo = new RangeInfo(length);
			builder.rangeInfo(rangeInfo);
			//rangeInfo.initRange();
			
			String filename = Utils.Filename(url);
			String contentDisposition = response.header("Content-disposition", "filename=\"" + filename + "\"");
			if (contentDisposition.contains("filename")) {
				String[] split = contentDisposition.split("\"");
				filename = split[split.length - 1];
			}
			builder.filename(filename);

		} catch (IOException e) {
			// ignore
			// return null;
		}

		return builder.build();
	}

	// -------------------------------------------------//

	public Item resolveLessItem(HttpUrl url, Map<String, String> headers, List<Cookie> cookies) {
		Builder builder = new Builder();
		builder.id(currentTimeMillis());
		builder.url(url);
		builder.savepath();
		builder.cookies(cookies);
		builder.headers(headers);
		builder.rangeInfo(new RangeInfo());
		
		//String filename = url.pathSegments().get(url.pathSegments().size() - 1);
		builder.filename(Utils.Filename(url));
		return builder.build();
	}

	private boolean updateItemOnline(Item item) {
		try {
			
			Response response = head(item.getUpdateHttpUrl(), item.getCookies(), item.getHeaders());
			//System.out.println(response);
			if (response.isRedirect()) {
				item.setRedirect();
				item.setRedirectUrl(response.request().url());
			}
			ResponseBody body = response.body();
			try {
				long length = Long.parseLong(response.header("Content-Length")); // body.contentLength();
				// item.getRangeInfo().setFileLength(length);
				
				RangeInfo rangeInfo = new RangeInfo(length);
				item.setRangeInfo(rangeInfo);
			} catch (NumberFormatException e) {
				RangeInfo rangeInfo = new RangeInfo(-1);
				item.setRangeInfo(rangeInfo);
			}finally {
				body.close();
			}
//			rangeInfo.initRange();
			String filename = Utils.Filename(item.getUrl());
			String contentDisposition = response.header("Content-disposition", "filename=\"" + filename + "\"");
			if (contentDisposition.contains("filename")) {
				String[] split = contentDisposition.split("\"");
				filename = split[split.length - 1];
			}
			item.setFilename(filename);
		} catch (IOException e) {
			// ignore
			//e.printStackTrace();
			return false;
			// return tryGetToUpdateItemOnline(item);
		}
		return true;
	}
	
	private boolean tryGetToUpdateItemOnline(Item item) {
		try {
			
			Response response = get(item);
			//System.out.println(response);
			if (response.isRedirect()) {
				item.setRedirect();
				item.setRedirectUrl(response.request().url());
			}
			ResponseBody body = response.body();
			try {
				long length = Long.parseLong(response.header("Content-Length")); // body.contentLength();
				// item.getRangeInfo().setFileLength(length);
				
				RangeInfo rangeInfo = new RangeInfo(length);
				item.setRangeInfo(rangeInfo);
			} catch (NumberFormatException e) {
				RangeInfo rangeInfo = new RangeInfo(-1);
				item.setRangeInfo(rangeInfo);
			}finally {
				body.close();
			}
//			rangeInfo.initRange();
			String filename = Utils.Filename(item.getUrl());
			String contentDisposition = response.header("Content-disposition", "filename=\"" + filename + "\"");
			if (contentDisposition.contains("filename")) {
				String[] split = contentDisposition.split("\"");
				filename = split[split.length - 1];
			}
			item.setFilename(filename);
		} catch (IOException e) {
			// ignore
			//e.printStackTrace();
			 return false;
		}
		return true;
	}
	public Item resolveItem(Item item) {
		boolean reGet = true;
		do {
			reGet = updateItemOnline(item);
			if(reGet)
				break;
			reGet = tryGetToUpdateItemOnline(item);	
		}while(! reGet);
		return item;
	}

	public List<Item> readUrlListFromFile(String filePath) {
		List<Item> items = new LinkedList<>();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(filePath));
			String line;
			Item item = null;
			Map<String, String> headers = null;
			while ((line = reader.readLine()) != null) {
				
				if (line.startsWith("#") 
//						|| line.startsWith(" ") 
						|| line.startsWith("\n") 
						|| line.startsWith("\r\n")) {
					continue;
				} else if (line.startsWith("http")) {
					if (item != null) {
						//add the last item the list
						items.add(item);
					}
					String filename = Utils.Filename(line);
					item = Utils.fromJson(R.getConfigFile(filename + ".json"), Item.class);
					
					if (item == null) {
						headers = new LinkedHashMap<>();
						item = resolveLessUrlWithHeaders(HttpUrl.parse(line), headers);
					}else {
						if(item.getRangeInfo().isFinish()) {
							continue;
						}
						else if (item.getUrl().equals(HttpUrl.parse(line)) ){
							if (item.getRangeInfo().getFileLength() == -1) {
								//System.out.println("info length: 0");
								headers = new LinkedHashMap<>();
								//RangeInfo info = item.getRangeInfo();
								item = resolveLessUrlWithHeaders(HttpUrl.parse(line), headers);
								//RangeInfo info2 = item.getRangeInfo();
								//info2.concateRange(info);
								
							} else {
								item.getRangeInfo().avoidMissedBytes();
								item.getRangeInfo().checkRanges();
								headers = item.getMapHeaders();
							}
						}else {
							item.setUrl(HttpUrl.parse(line));
							item.getRangeInfo().avoidMissedBytes();
							item.getRangeInfo().checkRanges();
							headers = item.getMapHeaders();
						}
					}
					
					

				} else if (line.startsWith("\t")) {
					String[] header = line.substring(1).split(": ");
					headers.put(header[0], header[1]);
				}
			}
			if (item != null) {
				//if(! item.getRangeInfo().isFinish())
					items.add(item);
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return items;
	}

	@Override
	public ClinetWriter getClinetWriter() {
		// TODO Auto-generated method stub
		return null;
	}

}

package org.okaria.manager;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.okaria.Utils;
import org.okaria.range.RangeInfo;

import okhttp3.Cookie;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.internal.http2.Header;

public class Item {
	
	protected String url;
	protected String redirectUrl;
	protected String referer;
	protected String useragent;
	protected String filename;
	protected String folder;
	protected String cacheFile;
	protected boolean redirect;
	protected Map<String, String> headers;
	protected List<Cookie> cookies;
	protected RangeInfo rangeInfo;
	

	public Item() {
		this.rangeInfo = new RangeInfo();
		this.headers   = new LinkedHashMap<>();
		this.cookies   = new LinkedList<>();
	}
	
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	public HttpUrl url() {
		return HttpUrl.parse(getUrl());
	}
	
	public void url(HttpUrl url) {
		this.url = url.toString();
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}
	

	public void setRedirectUrl(String redirectUr) {
		this.redirectUrl = redirectUr;
	}
	public String getRedirectUrl() {
		return redirectUrl;
	}
	
	public String updateUrl() {
		return redirect ? getRedirectUrl() : getUrl();
	}

	public HttpUrl getUpdateUrl() {
		return HttpUrl.parse(updateUrl());
	}


	public RangeInfo getRangeInfo() {
		return rangeInfo;
	}
	
	public boolean isFinish() {
        return rangeInfo.isFinish();
    }
	public boolean isStreaming() {
        return rangeInfo.isStreaming();
    }

	public void setRangeInfo(RangeInfo rangeInfo) {
		this.rangeInfo = rangeInfo;
	}

	public Map<String, String> getMapHeaders() {
		return headers;
	}

	public void setMapHeaders(Map<String, String> headers) {
		this.headers = headers;
	}

	public void updateHeaders() {
		if(useragent != null) 
			headers.put("User-Agent", useragent);
		if(referer != null) 
			headers.put("Referer", referer);
	}
	
	
	public String getUseragent() {
		return useragent;
	}
	
	public void setUseragent(String useragent) {
		this.useragent = useragent;
	}
	
	/**
	 * @return always new {@link Headers} object of the current header-map
	 */
	public Headers getHeaders() {
		return Headers.of(this.headers);
	}

	/**
	 * clear the current map then put new values of from headers object.
	 * 
	 * @param headers
	 */
	public void setHeaders(Headers headers) {
		this.headers.clear();
		for (String name : headers.names()) {
			this.headers.put(name, headers.get(name));
		}
	}

	public void setHeaders(Map<String, String> headers) {
		this.headers.clear();
		for (String name : headers.keySet()) {
			this.headers.put(name, headers.get(name));
		}
	}
	
	public void addHeaders(Headers headers) {
		for (String name : headers.names()) {
			this.headers.put(name, headers.get(name));
		}
	}
	
	public void addHeaders(Map<String, String> headers) {
		
		for (String name : headers.keySet()) {
			this.headers.put(name, headers.get(name));
		}
	}

	public void addHeaders(Header header) {
		this.headers.put(header.name.utf8(), header.value.utf8());
	}

	
	public List<Cookie> getCookies() {
		return cookies;
	}

	public void setCookies(List<Cookie> cookies) {
		this.cookies = cookies;
	}

	public void addCookies(Cookie cookie) {
		for (Cookie oldCookie : cookies) {
			if(oldCookie.equals(cookie))
				return;
		}
		this.cookies.add(cookie);
	}
	

	public void addCookies(List<Cookie> cookies) {
		cookies.forEach(cok->{
			addCookies(cok);
		});
		//this.cookies.addAll(cookies);
	}
	
	public String getFolder() {
		return folder;
	}

	public void setFolder(String folder) {
		this.folder = folder;
	}
	
	public String path() {
		char lastChar = folder.charAt(folder.length()-1);
		if( lastChar == '/' || lastChar == '\\') {
			return (folder + filename);
		}
		else {
			return (folder + File.separatorChar + filename);
		}
	}
	

	
	public String getCacheFile() {
		return cacheFile;
	}
	public void setCacheFile(String cacheFile) {
		this.cacheFile = cacheFile;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder(liteString());
		builder.append('\n');
		builder.append("Redirect : " + redirect);
		builder.append(",\tHeaders Size : " + headers.size() );
		builder.append(",\tCookies Size : " + cookies.size() );
		builder.append(",\tRange Count : " + rangeInfo.getRangeCount() );
		builder.append('\n');
		builder.append(rangeInfo.toString());
		return builder.toString();
	}
	
	public String liteString() {
		StringBuilder builder = new StringBuilder();
		builder.append(filename );
		builder.append('\n');
		builder.append( url());
		builder.append('\n');
		builder.append("Folder : " + folder );
		builder.append("\nCache File : " + cacheFile );
		builder.append('\n');
		builder.append("File Length : " + rangeInfo.getFileLengthMB() + " ( "  + rangeInfo.getFileLength() + " byte )");
		builder.append(",\tDownload : " + rangeInfo.getDownloadLengthMB());
		builder.append(",\tRemaining : " + rangeInfo.getRemainingLengthMB());
		return builder.toString();
	}

	
	@Override
	public boolean equals(Object obj) {
		Item item =  (Item) obj;
		return     this.url.equals(item.url)
				&& this.referer.equals(item.referer)
				&& this.useragent.equals(item.useragent)
				&& this.filename.equals(item.filename)
				&& this.folder.equals(item.folder)
				&& this.cacheFile.equals(item.cacheFile)
				&& this.redirect == item.redirect
				&& this.headers.equals(item.headers)
				&& this.cookies.equals(item.cookies)
				&& this.rangeInfo.equals(item.rangeInfo);
	}
	
	public static Item fromJsonFile(String filePath) {
		return Utils.fromJson(filePath, Item.class);
	}
	public static void toJsonFile(Item item) {
		if(item.cacheFile == null) return ;
		Utils.toJsonFile(item.cacheFile, item);
	}

	public String getReferer() {
		return referer;
	}
	
	public HttpUrl getRefererHttpUrl() {
		return HttpUrl.parse(referer);
	}

	public void setReferer(String referer) {
		this.referer = referer;
	}
	
	public boolean isRedirect() {
		return redirect;
	}

	public void setRedirect() {
		this.setRedirect(true);
	}
	public void setRedirect(boolean redirect) {
		this.redirect = redirect;
	}
	
	
	
	public Item copy() {
		Item item = new Item();
		if(url != null ) item.url = new String(this.url);
		if(redirectUrl != null ) item.redirectUrl = new String(this.redirectUrl);
		if(referer != null ) item.referer = new String(this.referer);
		if(useragent != null ) item.useragent = new String(this.useragent);
		if(filename != null ) item.filename = new String(this.filename);
		if(folder != null ) item.folder = new String(this.folder);
		if(cacheFile != null ) item.cacheFile = new String(this.cacheFile);
		item.redirect = redirect ? true : false;
		item.headers = new HashMap<String, String>(this.headers);
		item.cookies = new LinkedList<>(this.cookies);
		if(referer != null ) item.rangeInfo = new RangeInfo(this.rangeInfo.getFileLength());
		
		return item;
	}

}

package org.okaria.manager;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.okaria.R;
import org.okaria.Utils;
import org.okaria.range.RangeInfo;

import okhttp3.Cookie;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.internal.http2.Header;

public class Item {

	public static String SAVE_DIR_PATH = R.CurrentDirectory();
	
	protected Long id;
	protected String url;
	protected String redirectUrl;
	protected String referer;
	protected String useragent;
	protected String filename;
	protected String savepath;
	protected Boolean redirect;
	protected Map<String, String> headers;
	protected List<Cookie> cookies;
	protected RangeInfo rangeInfo;
	
//	transient protected Object attach;

	public Item() {
		this.id = -1l;
		this.rangeInfo = new RangeInfo();
		this.headers   = new LinkedHashMap<>();
		this.cookies   = new LinkedList<>();
		this.redirect  = false;
	}
	
	public Item(Builder builder) {
		this.id 		= builder.id;
		this.url 		= builder.url;
		this.redirectUrl= builder.redirectUrl;
		this.referer 	= builder.referer;
		this.useragent	= builder.useragent;
		this.filename 	= builder.filename;
		this.savepath 	= builder.savepath;
		this.redirect 	= builder.redirect;
		this.headers 	= builder.headers;
		this.cookies 	= builder.cookies;
		this.rangeInfo 	= builder.rangeInfo;
//		this.attach 	= builder.attach;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	
	public String url() {
		return url;
	}

	public HttpUrl getUrl() {
		return HttpUrl.parse(url);
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	public void setUrl(HttpUrl url) {
		this.url = url.toString();
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public RangeInfo getRangeInfo() {
		return rangeInfo;
	}
	
	public boolean isFinish() {
        return rangeInfo.isFinish();
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
	
	public String getSavepath() {
		return savepath;
	}

	public void setSavepath(String savepath) {
		this.savepath = savepath;
	}
	
	public String fullSavePath() {
		return savepath + File.separatorChar + filename;
	}

	
//	public void attach(Object attach) {
//		this.attach = attach;
//	}
//	
//	public Object attach() {
//		return attach;
//	}
	
	@Override
	public String toString() {
		return toLiteString();
	}
	public String toFullString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ID : " + id);
		builder.append("\t\t " + filename );
		builder.append("\n");
		for (int i = 0; i < 7 || i < rangeInfo.getRangeCount(); i++) {
			long[] ls = rangeInfo.getIndex(i);
			if(ls != null)
				builder.append("#" + (i+1) + "\t" + Arrays.toString(ls));
			switch (i) {
			case 0:
				builder.append("\t"+ ( (ls[0] < 2097152)? "\t":"") +" URL :\t\t" + url.toString());
				break;
			case 1:
				builder.append("\t Save Directory :\t" + savepath );
				break;
			case 2:
				builder.append("\t File Total Length:\t" + rangeInfo.getFileLengthMB() + " ( "  + rangeInfo.getFileLength() + " byte )");
				break;	
			case 3:
				builder.append("\t Downloaded :\t\t" + rangeInfo.getDownLengthMB());
				break;
			case 4:
				builder.append("\t Remaining :\t" + rangeInfo.getRengeLengthMB());
				break;
			case 5:
				builder.append("\t Is Redirect :\t\t" + redirect);
				break;
			case 6:
				
				builder.append("\t Headers Size :\t\t" + headers.size() );
				break;
			case 7:
				builder.append("\t Cookies Size :\t\t" + cookies.size() );
				break;	
			default:
				break;
			}
			builder.append('\n');
		}

		return builder.toString();
	}
	
	
	public String toLiteString() {
		StringBuilder builder = new StringBuilder(liteString());
		builder.append("\n");
		builder.append("Redirect : " + redirect);
		builder.append(",\tHeaders Size : " + headers.size() );
		builder.append(",\tCookies Size : " + cookies.size() );
		builder.append(",\tRange Count : " + rangeInfo.getRangeCount() );
		builder.append("\n");
		int i = 0;
		for( ; i < rangeInfo.getRangeCount()-1; i++){
			builder.append(Arrays.toString(rangeInfo.getIndex(i)));
			builder.append(", ");
			if(i%4 == 3) builder.append('\n');
		}
		builder.append(Arrays.toString(rangeInfo.getIndex(i)));

		return builder.toString();
	}
	
	public String liteString() {
		StringBuilder builder = new StringBuilder();
		builder.append(filename );
		builder.append(",\tID : " + id);
		builder.append("\n");
		builder.append( url);
		builder.append("\n");
		builder.append("Save Directory : " + savepath );
		builder.append("\n");
		builder.append("File Length : " + rangeInfo.getFileLengthMB() + " ( "  + rangeInfo.getFileLength() + " byte )");
		builder.append(",\tDownload : " + rangeInfo.getDownLengthMB());
		builder.append(",\tRemaining : " + rangeInfo.getRengeLengthMB());
		return builder.toString();
	}

	static Item jsonItem(String filePath) {
		return Utils.fromJson(filePath, Item.class);
	}
	static boolean toJsonItem(String filePath, Item item) {
		return Utils.toJsonFile(filePath, item);
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

	public String getRedirectUrl() {
		return redirectUrl;
	}

	public void setRedirectUrl(HttpUrl redirecturl) {
		this.redirectUrl = redirecturl.toString();
	}
	public void setRedirectUrl(String redirecturl) {
		this.redirectUrl = redirecturl;
	}
	
	public HttpUrl getRedirectHttpUrl() {
		return HttpUrl.parse(redirectUrl);
	}

	public HttpUrl getUpdateHttpUrl() {
		return HttpUrl.parse(getUpdateUrl());
	}
	
	public String getUpdateUrl() {
		if(redirect)
			return redirectUrl;
		else
			return url;
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

	public static class Builder {

		
		protected Long id;
		protected String url;
		protected String redirectUrl;
		protected String referer;
		protected String useragent;
		protected String filename;
		protected String savepath;
		protected Boolean redirect;
		protected Map<String, String> headers;
		protected List<Cookie> cookies;
		protected RangeInfo rangeInfo;
		
//		protected Object attach;
		
		public Builder() {
			this.id = System.currentTimeMillis();
			this.rangeInfo = new RangeInfo();
			this.headers   = new LinkedHashMap<>();
			this.cookies   = new LinkedList<>();
			this.redirect  = false;
			this.savepath();
		}
		
		public Builder id(long id) {
			this.id = id;
			return this;
		}
		
		public Builder url(String url) {
			this.url = url;
			return this;
		}
		public Builder url(HttpUrl url) {
			this.url = url.toString();
			return this;
		}
		
		public HttpUrl httpUrl() {
			return HttpUrl.parse(url);
		}
		
		public String url() {
			return url;
		}
		
		public Builder referer(String referer) {
			this.referer = referer;
			return this;
		}
		
		public Builder useragent(String useragent) {
			this.useragent = useragent;
			return this;
		}
		
		public Builder redirectUrl(HttpUrl redirectUrl) {
			this.redirectUrl = redirectUrl.toString();
			return this;
		}
		public Builder redirectUrl(String redirectUrl) {
			this.redirectUrl = redirectUrl;
			return this;
		}
		
		public Builder redirect() {
			return redirect(true);
		}
		
		public Builder redirect(boolean redirect) {
			this.redirect = redirect;
			return this;
		}
		
		public Builder savepath() {
			return savepath(SAVE_DIR_PATH);
		}
		
		public Builder savepath(String savepath) {
			this.savepath = savepath;
			return this;
		}
		
		public Builder filename(String filename) {
			this.filename = filename;
			return this;
		}
		public Builder headers(Map<String, String> headers) {
			this.headers = headers;
			return this;
		}
		public Builder addHeader(String key, String value) {
			this.headers.put(key, value);
			return this;
		}
		
		public Builder addHeaders(Map<String, String> headers) {
			this.headers.putAll(headers);
			return this;
		}
		public Builder cookies(List<Cookie> cookies) {
			this.cookies = cookies;
			return this;
		}
		
		public Builder addCookies(List<Cookie> cookies) {
			this.cookies.addAll(cookies);
			return this;
		}
		
		public Builder addCookie ( Cookie cookie) {
			this.cookies.add(cookie);
			return this;
		}
		
		public Builder rangeInfo(RangeInfo rangeInfo) {
			this.rangeInfo = rangeInfo;
			return this;
		}
		
//		public Builder attach(Object object) {
//			this.attach = object;
//			return this;
//		}
		
		public Builder json(String filePath) {
			Item item = jsonItem(filePath);
				id(item.id);
				url(item.url);
				redirectUrl(item.redirectUrl);
				referer(item.referer);
				useragent(item.useragent);
				filename(item.filename);
				savepath(item.savepath);
				redirect(item.redirect);
				headers(item.headers);
				cookies(item.cookies);
				rangeInfo(item.rangeInfo);
//				attach(item.attach);
			return this;
		}
		
		
		
		public Item build() {
			Item item = new Item();
				item.id 		= this.id;
				item.url 		= this.url;
				item.redirectUrl= this.redirectUrl;
				item.referer 	= this.referer;
				item.useragent	= this.useragent;
				item.filename 	= this.filename;
				item.savepath 	= this.savepath;
				item.redirect 	= this.redirect;
				item.headers 	= this.headers;
				item.cookies 	= this.cookies;
				item.rangeInfo 	= this.rangeInfo;
//				item.attach		= this.attach;
			return item;
		}
		
	}

}

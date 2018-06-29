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

	private long id;
	private String url;
	private String redirectUrl;
	private String referer;
	private String filename;
	private String savepath;
	private boolean redirect;
	private Map<String, String> headers;
	private List<Cookie> cookies;
	private RangeInfo rangeInfo;

	public Item() {
		this.id = -1;
		this.rangeInfo = new RangeInfo();
		this.headers   = new LinkedHashMap<>();
		this.cookies   = new LinkedList<>();
		this.redirect  = false;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
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
		this.cookies.add(cookie);
	}

	public void addCookies(List<Cookie> cookies) {
		this.cookies.addAll(cookies);
	}

	public String getSavepath() {
		return savepath;
	}

	public void setSavepath(String savepath) {
		this.savepath = savepath;
	}
	
	public String getSavepathFile() {
		return savepath + File.separatorChar + filename;
	}

	
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
		StringBuilder builder = new StringBuilder();
		builder.append("ID : " + id);
		builder.append("\t, " + filename );
		builder.append("\n");
		builder.append( url.toString());
		builder.append("\n");
		builder.append("Save Directory : " + savepath );
		builder.append("\n");
		builder.append("File Length : " + rangeInfo.getFileLengthMB() + " ( "  + rangeInfo.getFileLength() + " byte )");
		builder.append("\t, Download : " + rangeInfo.getDownLengthMB());
		builder.append("\t, Remaining : " + rangeInfo.getRengeLengthMB());
		builder.append("\n");
		builder.append("Is Redirect  : " + redirect);
		builder.append("\t, Headers Size : " + headers.size() );
		builder.append("\t, Cookies Size : " + cookies.size() );
		builder.append("\n");
		builder.append(Arrays.deepToString(rangeInfo.getRange()));

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

		private long id;
		private String url;
		private String redirectUrl;
		private String referer;
		private String filename;
		private String savepath;
		private boolean redirect;
		private Map<String, String> headers;
		private List<Cookie> cookies;
		private RangeInfo rangeInfo;

		public Builder() {
			
		}
		
		Builder id(long id) {
			this.id = id;
			return this;
		}
		
		Builder url(String url) {
			this.url = url;
			return this;
		}
		Builder url(HttpUrl url) {
			this.url = url.toString();
			return this;
		}
		
		HttpUrl url() {
			return HttpUrl.parse(url);
		}
		
		Builder referer(String referer) {
			this.referer = referer;
			return this;
		}
		
		Builder redirectUrl(HttpUrl redirectUrl) {
			this.redirectUrl = redirectUrl.toString();
			return this;
		}
		Builder redirectUrl(String redirectUrl) {
			this.redirectUrl = redirectUrl;
			return this;
		}
		
		Builder redirect() {
			return redirect(true);
		}
		Builder redirect(boolean redirect) {
			this.redirect = redirect;
			return this;
		}
		
		Builder savepath() {
			return savepath(R.getDownloadsFile());
		}
		Builder savepath(String savepath) {
			this.savepath = savepath;
			return this;
		}
		
		Builder filename(String filename) {
			this.filename = filename;
			return this;
		}
		Builder headers(Map<String, String> headers) {
			this.headers = headers;
			return this;
		}
		Builder cookies(List<Cookie> cookies) {
			this.cookies = cookies;
			return this;
		}
		
		Builder rangeInfo(RangeInfo rangeInfo) {
			this.rangeInfo = rangeInfo;
			return this;
		}
		
		Builder json(String filePath) {
			Item item = jsonItem(filePath);
				id(item.id);
				url(item.url);
				redirectUrl(item.redirectUrl);
				referer(item.referer);
				filename(item.filename);
				savepath(item.savepath);
				redirect(item.redirect);
				headers(item.headers);
				cookies(item.cookies);
				rangeInfo(item.rangeInfo);
			return this;
		}
		
		
		
		Item build() {
			Item item = new Item();
				item.id 		= this.id;
				item.url 		= this.url;
				item.redirectUrl= this.redirectUrl;
				item.referer 	= this.referer;
				item.filename 	= this.filename;
				item.savepath 	= this.savepath;
				item.redirect 	= this.redirect;
				item.headers 	= this.headers;
				item.cookies 	= this.cookies;
				item.rangeInfo 	= this.rangeInfo;
			return item;
		}
		
	}

}

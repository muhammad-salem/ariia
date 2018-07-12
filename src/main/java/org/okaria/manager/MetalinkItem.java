package org.okaria.manager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.okaria.Utils;

import okhttp3.HttpUrl;

public class MetalinkItem extends Item {

	private List<String> mirrors;
	public MetalinkItem() {
		super();
		mirrors = new ArrayList<>();
	}
	
	public MetalinkItem(Builder builder) {
		super(builder);
		mirrors = builder.mirrors;
	}
	
	
	transient int indexMark = 0;
	public String nextUrl() {
		if(indexMark > 100)
			indexMark %= mirrors.size();
		if(mirrors.isEmpty())
			return null;
		return mirrors.get( (indexMark++)% mirrors.size()  );
	}
	
	@Override
	public HttpUrl getUpdateHttpUrl() {
		return HttpUrl.parse(nextUrl());
	}
	
	
	public List<String> getMirrors() {
		return mirrors;
	}
	
	public void setMirrors(List<String> mirrors) {
		this.mirrors = mirrors;
	}
	
	
	public boolean addMirror(String e) {
		return mirrors.add(e);
	}

	public String getMirror(int index) {
		return mirrors.get(index);
	}

	static MetalinkItem jsonMetalinkItem(String filePath) {
		return Utils.fromJson(filePath, MetalinkItem.class);
	}
	static boolean toJsonMetalinkItem(String filePath, MetalinkItem item) {
		return Utils.toJsonFile(filePath, item);
	}
	
	public String toLiteString() {
		StringBuilder builder = new StringBuilder();
		builder.append(filename );
		builder.append(",\tID : " + id);
		builder.append("\n( "+mirrors.size() + " )\t");
		builder.append(getUrl());
		builder.append("\n");
		builder.append("Save Directory : " + savepath );
		builder.append("\n");
		builder.append("File Length : " + rangeInfo.getFileLengthMB() + " ( "  + rangeInfo.getFileLength() + " byte )");
		builder.append(",\tDownload : " + rangeInfo.getDownLengthMB());
		builder.append(",\tRemaining : " + rangeInfo.getRengeLengthMB());
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

	public static class Builder extends Item.Builder {
		
		private List<String> mirrors;
		public Builder() {
			super();
			mirrors = new ArrayList<>();
		}
		
		public Builder addUrl(String url) {
			return url(url);
		}
		
		public Builder url(String url) {
			this.mirrors.add(url);
			return this;
		}
		public Builder url(HttpUrl url) {
			return url(url.toString());
		}
		public Builder mirrors(List<String> urls) {
			this.mirrors = urls;
			return this;
		}
		
		
		
		public Builder json(String filePath) {
			MetalinkItem item = Utils.fromJson(filePath, MetalinkItem.class);
				id(item.id);
				url(item.url);
				mirrors(item.mirrors);
				redirect(item.redirect);
				redirectUrl(item.redirectUrl);
				referer(item.referer);
				filename(item.filename);
				savepath(item.savepath);
				headers(item.headers);
				cookies(item.cookies);
				rangeInfo(item.rangeInfo);
			return this;
		}
		
		
		
		public MetalinkItem build() {
			MetalinkItem item = new MetalinkItem();
				item.id 		= this.id;
				item.url 		= this.url;
				item.mirrors	= this.mirrors;
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

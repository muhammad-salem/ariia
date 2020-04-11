package org.ariia.items;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.ariia.config.Properties;
import org.ariia.range.RangeInfo;


public class Builder {
	protected String url;
	protected String saveDir;
	protected Map<String, List<String>> headers;
	protected RangeInfo rangeInfo;
	
	public Builder() { this.headers = new HashMap<>(0);}
	
	public Builder(String url) {
		this(url, new HashMap<>(0));
	}
	
	public Builder(String url, Map<String, List<String>> headers) {
		this.url = Objects.requireNonNull(url);
		this.headers = Objects.requireNonNull(headers);
	}
	
	public Builder url(String url) {
		this.url = Objects.requireNonNull(url);
		return this;
	}
	
	public Builder length(long length) {
		this.rangeInfo = new RangeInfo(length > 0 ? length : 0);
		return this;
	}
	
	public Builder saveDir(String saveDir) {
		this.saveDir = Objects.requireNonNull(saveDir);
		return this;
	}

	public Builder headers(Map<String, List<String>> headers) {
		this.headers = Objects.requireNonNull(headers);
		return this;
	}
	
	public Builder addHeaders(Map<String, List<String>> headers) {
		this.headers.putAll(headers);
		return this;
	}
	
	public Builder addHeaders(String name, List<String> headers) {
		this.headers.put(name, headers);
		return this;
	}
	
	public Builder addHeaders(String name , String header) {
		List<String> value = this.headers.getOrDefault(name, new ArrayList<>(1));
		value.add(header);
		return addHeaders(name, value);
	}

	public Builder rangeInfo(RangeInfo rangeInfo) {
		this.rangeInfo = Objects.requireNonNull(rangeInfo);
		return this;
	}
	
	public Item build() {
		Item item = new Item();
		
		item.url	   = this.url;
		item.filename  = this.getFileName();
		item.saveDir   = Objects.nonNull(saveDir) ? this.saveDir : Properties.Default_SAVE_DIR_PATH;
		item.headers   = this.headers.isEmpty() ? Collections.emptyMap() : this.headers;
		item.rangeInfo = Objects.nonNull(rangeInfo) ? this.rangeInfo : new RangeInfo();
		
		return item;
	} 
	
	private String getFileName() {
		File file = new File(url);
		String fileName = file.getName().split("\\?")[0];
		
		if ("".equals(fileName)) {
			String[] fileParts = url.split("/");
			fileName = fileParts[fileParts.length-2].split("\\?")[0];
		}
		return fileName;
	}
}
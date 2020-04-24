package org.ariia.items;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import org.ariia.range.RangeInfo;

public class Item {
	
	protected String id;
	
	protected String url;
	protected String redirectUrl;
	protected String filename;
	protected String saveDirectory;
	protected Map<String, List<String>> headers;
	protected RangeInfo rangeInfo;
	

	public Item() {
		this.rangeInfo = new RangeInfo();
		this.headers   = new HashMap<>(0);
		this.id = UUID.randomUUID().toString();
	}
	
	public String getId() {
		return id;
	}
	
//	public void setId(String id) {
//		this.id = id;
//	}
	
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	public boolean isRedirected() {
		return redirectUrl != null && !redirectUrl.equals(url);
	}
	
	public String getRedirectUrl() {
		return redirectUrl;
	}
	
	public void setRedirectUrl(String redirectUrl) {
		this.redirectUrl = redirectUrl;
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
	public boolean isStreaming() {
        return rangeInfo.isStreaming();
    }

	public void setRangeInfo(RangeInfo rangeInfo) {
		this.rangeInfo = rangeInfo;
	}

	public Map<String, List<String>> getHeaders() {
		return headers;
	}
	
	public void setHeaders(Map<String, List<String>> headers) {
		this.headers = headers;
	}
	
	public void addHeaders(Map<String, List<String>> headers) {
		this.headers.putAll(headers);
	}
	
	public void addHeader(String name, String value) {
		List<String> list = headers.getOrDefault(name, new ArrayList<>(1));
		list.add(value);
		this.headers.put(name, list);
	}
	
	public void addHeader(String name, List<String> value) {
		if (headers.containsKey(name)) {
			headers.get(name).addAll(value);
		} else {
			this.headers.put(name, value);
		}
	}
	
	public String path() {
		char lastChar = saveDirectory.charAt(saveDirectory.length()-1);
		if( lastChar == '/' || lastChar == '\\') {
			return (saveDirectory + filename);
		}
		else {
			return (saveDirectory + File.separatorChar + filename);
		}
	}
	
	public void setSaveDirectory(String saveDirectory) {
		this.saveDirectory = saveDirectory;
	}
	
	public String getSaveDirectory() {
		return saveDirectory;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder(liteString());
		builder.append('\n');
		builder.append("Headers Size : " + headers.size() );
		builder.append(",\tRange Count : " + rangeInfo.getRangeCount() );
		builder.append('\n');
		builder.append(rangeInfo.toString());
		return builder.toString();
	}
	
	public String liteString() {
		StringBuilder builder = new StringBuilder();
		builder.append( filename);
		builder.append( '\t' );
		builder.append( id );
		builder.append('\n' );
		builder.append( url );
		builder.append('\n' );
		if (Objects.nonNull(redirectUrl)) {
			builder.append( redirectUrl );
			builder.append( '\n' );
		}
		builder.append( "Directory : " );
		builder.append( saveDirectory );
		builder.append( '\n' );
		builder.append( "File Length : " + rangeInfo.getFileLengthMB() + " ( "  + rangeInfo.getFileLength() + " byte )");
		builder.append( ",\tDownload : " + rangeInfo.getDownloadLengthMB() );
		builder.append( ",\tRemaining : " + rangeInfo.getRemainingLengthMB() );
		return builder.toString();
	}

	
	@Override
	public boolean equals(Object obj) {
		Item item =  (Item) obj;
		return     this.url.equals(item.url)
				&& this.filename.equals(item.filename)
				&& this.saveDirectory.equals(item.saveDirectory)
				&& this.headers.equals(item.headers)
				&& this.rangeInfo.equals(item.rangeInfo)
				&& this.id == item.id;
	}
	
	public Item getCopy() {
		Item item = new Item();
		item.setUrl(url);
		item.setRedirectUrl(redirectUrl);
		item.setFilename(filename);
		item.setSaveDirectory(saveDirectory);
		item.headers = new HashMap<>(this.headers);
		item.rangeInfo = new RangeInfo(this.rangeInfo.getFileLength());
		return item;
	}
	
	public void copy(Item item) {
		this.id				= item.id;
		this.url			= item.url;
		this.filename		= item.filename;
		this.headers		= item.headers;
		this.rangeInfo		= item.rangeInfo;
		this.redirectUrl		= item.redirectUrl;
		this.saveDirectory	= item.saveDirectory;
	}
	


}

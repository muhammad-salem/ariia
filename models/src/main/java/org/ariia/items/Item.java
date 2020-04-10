package org.ariia.items;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.ariia.range.RangeInfo;

public class Item {
	
	protected String url;
	protected String filename;
	protected String saveDir;
	protected Map<String, List<String>> headers;
	protected RangeInfo rangeInfo;
	

	public Item() {
		this.rangeInfo = new RangeInfo();
		this.headers   = new LinkedHashMap<>();
	}
	
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
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
		for (String name : headers.keySet()) {
			this.headers.put(name, headers.get(name));
		}
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
		char lastChar = saveDir.charAt(saveDir.length()-1);
		if( lastChar == '/' || lastChar == '\\') {
			return (saveDir + filename);
		}
		else {
			return (saveDir + File.separatorChar + filename);
		}
	}
	

	
	public String getSaveDir() {
		return saveDir;
	}

	public void setSaveDir(String saveDir) {
		this.saveDir = saveDir;
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
		builder.append(filename );
		builder.append('\n');
		builder.append( url);
		builder.append('\n');
		builder.append("Folder : " + saveDir );
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
				&& this.filename.equals(item.filename)
				&& this.saveDir.equals(item.saveDir)
				&& this.headers.equals(item.headers)
				&& this.rangeInfo.equals(item.rangeInfo);
	}
	
	public Item getCopy() {
		Item item = new Item();
		if(url != null ) item.url = new String(this.url);
		if(filename != null ) item.filename = new String(this.filename);
		if(saveDir != null ) item.saveDir = new String(this.saveDir);
		item.headers = new HashMap<>(this.headers);
		if(rangeInfo != null ) item.rangeInfo = new RangeInfo(this.rangeInfo.getFileLength());
		return item;
	}
	
	public void copy(Item item) {
		this.url	   = item.url;
		this.filename  = item.filename;
		this.saveDir   = item.saveDir;
		this.headers   = item.headers;
		this.rangeInfo = item.rangeInfo;
	}

}

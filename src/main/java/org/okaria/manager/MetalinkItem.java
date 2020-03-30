package org.okaria.manager;

import java.util.ArrayList;
import java.util.List;

import org.okaria.util.Utils;

import okhttp3.HttpUrl;

public class MetalinkItem extends Item {

	private List<String> mirrors;
	public MetalinkItem() {
		super();
		mirrors = new ArrayList<>();
	}
	
	
	
	transient int indexMark = -1;
	public String nextUrl() {
		if(mirrors.isEmpty())
			return null;
		indexMark++;
		if(indexMark >= mirrors.size())
			indexMark = 0;
		return mirrors.get( indexMark );
	}
	
	@Override
	public String getUrl() {
		return nextUrl();
	}
	@Override
	public HttpUrl url() {
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

	public static MetalinkItem fromJsonFile(String filePath) {
		return Utils.fromJson(filePath, MetalinkItem.class);
	}
	public static boolean toJsonFile(String filePath, MetalinkItem item) {
		return Utils.toJsonFile(filePath, item);
	}
	
	@Override
	public String liteString() {
		StringBuilder builder = new StringBuilder();
		builder.append(filename );
		builder.append("\n(#URL: " + mirrors.size() + ")\t");
		builder.append( url());
		builder.append('\n');
		builder.append("Folder : " + saveDir );
		builder.append('\n');
		builder.append("File Length : " + rangeInfo.getFileLengthMB() + " ( "  + rangeInfo.getFileLength() + " byte )");
		builder.append(",\tDownload : " + rangeInfo.getDownloadLengthMB());
		builder.append(",\tRemaining : " + rangeInfo.getRemainingLengthMB());
		return builder.toString();
	}
}

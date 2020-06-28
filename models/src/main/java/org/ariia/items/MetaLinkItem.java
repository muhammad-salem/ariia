package org.ariia.items;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.ariia.util.Utils;

public class MetaLinkItem extends Item {

	protected List<String> mirrors;
	public MetaLinkItem() {
		super();
		mirrors = new ArrayList<>();
	}
	
	transient int indexMark = -1;
	public String nextUrl() {
		if(mirrors.isEmpty()){
			return null;
		}
		indexMark++;
		if(indexMark >= mirrors.size()){
			indexMark = 0;
		}
		return mirrors.get( indexMark );
	}
	
	@Override
	public String getUrl() {
		if (Objects.isNull(url)) {
			url = nextUrl();
			return url;
		}
		return nextUrl();
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

	public static MetaLinkItem fromJsonFile(String filePath) {
		return Utils.fromJson(filePath, MetaLinkItem.class);
	}
	public static boolean toJsonFile(String filePath, MetaLinkItem item) {
		return Utils.toJsonFile(filePath, item);
	}
	
	@Override
	public String liteString() {
		StringBuilder builder = new StringBuilder();
		builder.append(filename );
		builder.append("\n(#URL: " + mirrors.size() + ")\t");
		builder.append( mirrors.get(0));
		builder.append('\n');
		builder.append("Directory : " + saveDirectory );
		builder.append('\n');
		builder.append("File Length : " + rangeInfo.getFileLengthMB() + " ( "  + rangeInfo.getFileLength() + " byte )");
		builder.append(",\tDownload : " + rangeInfo.getDownloadLengthMB());
		builder.append(",\tRemaining : " + rangeInfo.getRemainingLengthMB());
		return builder.toString();
	}
}

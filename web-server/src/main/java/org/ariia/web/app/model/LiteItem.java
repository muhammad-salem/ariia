package org.ariia.web.app.model;

import org.ariia.core.api.writer.ItemMetaData;
import org.ariia.items.ItemState;
import org.ariia.monitors.RangeReport;
import org.ariia.range.RangeUtil;

public class LiteItem {
	
	public static LiteItem bind(ItemMetaData metaData) {
		return new LiteItem(metaData.getItem().getId(),
				metaData.getItem().getState(),
				metaData.getRangeInfo(), metaData.getRangeReport());
	}
	
	protected String itemId;
	protected ItemState state;
	protected long remainingTime;
	protected boolean downloading;
	protected long tcpDownloadSpeed;
	protected RangeUtil rangeInfo;
	
	public LiteItem(String itemId, ItemState state, RangeUtil rangeInfo, RangeReport rangeReport) {
		super();
		this.itemId = itemId;
		this.state = state;
		this.tcpDownloadSpeed = rangeReport.getMointor().getTcpDownloadSpeed();
		this.remainingTime = rangeReport.getRemainingTime();
		this.downloading = rangeReport.isDownloading();
		this.rangeInfo = rangeInfo;
	}
	
}

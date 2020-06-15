package org.ariia.web.app.model;

import org.ariia.core.api.writer.ItemMetaData;
import org.ariia.items.ItemState;
import org.ariia.monitors.RangeMonitor;
import org.ariia.range.RangeUtil;

public class LiteItem {
	
	public static LiteItem bind(ItemMetaData metaData) {
		return new LiteItem(metaData.getItem().getId(),metaData.getItem().getState(), metaData.getRangeInfo(), metaData.getRangeMointor());
	}
	
	protected String itemId;
	protected ItemState state;
	protected long remainingTime;
	protected long speedOfTCPReceive;
	protected RangeUtil rangeInfo;
	
	public LiteItem(String itemId, ItemState state, RangeUtil rangeInfo, RangeMonitor monitor) {
		super();
		this.itemId = itemId;
		this.state = state;
		this.speedOfTCPReceive = monitor.getSpeedReport().getMointor().getTcpDownloadSpeed();
		this.remainingTime = monitor.getRemainingTime();
		this.rangeInfo = rangeInfo;
	}
	
}

package org.ariia.web.app.model;

import org.ariia.core.api.writer.ItemMetaData;
import org.ariia.items.Item;
import org.ariia.items.ItemState;
import org.ariia.monitors.RangeReport;
import org.ariia.range.RangeUtil;

public class LiteItem {
	
	public static LiteItem bind(ItemMetaData metaData) {
		return new LiteItem(metaData.getItem(), metaData.getRangeReport());
	}
	
	protected String itemId;
	protected ItemState state;
	protected RangeReport report;
	protected RangeUtil rangeInfo;
	
	public LiteItem(Item item, RangeReport report) {
		super();
		this.itemId = item.getId();
		this.state = item.getState();
		this.rangeInfo = item.getRangeInfo();
		this.report = report;
	}
	
}

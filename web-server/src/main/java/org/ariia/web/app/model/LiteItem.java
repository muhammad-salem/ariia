package org.ariia.web.app.model;

import org.ariia.core.api.writer.ItemMetaData;
import org.ariia.items.ItemState;
import org.ariia.range.RangeUtil;

public class LiteItem {
	
	public static LiteItem bind(ItemMetaData metaData) {
		return new LiteItem(metaData.getItem().getId(),metaData.getItem().getState(), metaData.getRangeInfo());
	}
	
	protected String itemId;
	protected ItemState state;
	protected RangeUtil rangeInfo;
	public LiteItem(String itemId, ItemState state, RangeUtil rangeInfo) {
		super();
		this.itemId = itemId;
		this.state = state;
		this.rangeInfo = rangeInfo;
	}
	
	public String getItemId() {
		return itemId;
	}
	
	public ItemState getState() {
		return state;
	}
	
	public RangeUtil getRangeInfo() {
		return rangeInfo;
	}

}

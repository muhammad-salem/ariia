package org.okaria.mointors;

import org.okaria.Utils;
import org.okaria.manager.Item;
import org.okaria.range.RangeInfo;
import org.okaria.speed.SpeedMonitor;

public class SingleItemMointor extends SpeedMonitor {

	protected long timer = 0;
	private long totalLength = 0;
	private long downloadLength = 0;
	
	protected Item item;
	public Item getItem() {
		return item;
	}

	protected RangeInfo info;
	
	public SingleItemMointor(Item item) {
		this.item = item;
		this.info = item.getRangeInfo();
	}
	
	
	public void updateTotalLength() {
		totalLength = info.getFileLength();
	}

	public void updateDownloadLength() {
		downloadLength = info.getDownLength();
	}

	public long getTotalLength() {
		return totalLength;
	}

	public long getDownloadLength() {
		return downloadLength;
	}

	public String getTotalLengthMB() {
		return toUnitLength(totalLength);
	}

	public String getDownloadLengthMB() {
		return toUnitLength(downloadLength);
	}

	public long getReminningTime() {
		return (getRemainingLength() + 1) / (speedOfTCPReceive() + 1);
	}

	public String getReminningTimeString() {
		return Utils.timeformate(getReminningTime());
	}

	public long getRemainingLength() {
		return getTotalLength() - getDownloadLength();
	}

	public String getRemainingLengthMB() {
		return toUnitLength(getRemainingLength());
	}
	

	public String getPercent() {
		return Utils.percent(getDownloadLength(), getTotalLength());
	}

	public String getTimer() {
		return Utils.timeformate(timer++);
	}
	
	
}

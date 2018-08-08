package org.okaria.mointors;

import org.okaria.Utils;
import org.okaria.manager.Item;
import org.okaria.range.RangeInfo;
import org.okaria.speed.SpeedMonitor;

public class OneRangeMointor extends SpeedMonitor {

//	protected long timer = 0;
	protected Item item;
	
	protected RangeInfo info;
	
	public OneRangeMointor(Item item) {
		this.item = item;
		this.info = item.getRangeInfo();
	}
	
	public Item getItem() {
		return item;
	}
	public void updatedata() {
		info.oneCycleDataUpdate();
//		timer++;
	}

	public long getTotalLength() {
		return info.getFileLength();
	}

	public long getDownloadLength() {
		return info.getDownloadLength();
	}

	public String getTotalLengthMB() {
		return info.getFileLengthMB();
	}

	public String getDownloadLengthMB() {
		return info.getDownloadLengthMB();
	}

	public long getReminningTime() {
		return (getRemainingLength() + 1) / (speedOfTCPReceive() + 1);
	}

	public String getReminningTimeString() {
		return Utils.timeformate(getReminningTime());
	}

	public long getRemainingLength() {
		return info.getRemainingLength();
	}

	public String getRemainingLengthMB() {
		return info.getRemainingLengthMB();
	}
	
	public String getPercent() {
		return Utils.percent(getDownloadLength(), getTotalLength());
	}
	protected float percent() {
		return (float) getDownloadLength() /  getTotalLength();
	}
	
//	public String getTimer() {
//		return Utils.timeformate(timer);
//	}
	
}

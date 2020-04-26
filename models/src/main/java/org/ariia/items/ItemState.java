package org.ariia.items;

public enum ItemState {
	
	INIT, WAITING, DOWNLOADING, PAUSE, COMPLETE;
	
	@Override
	public String toString() {
		return name().toLowerCase();
	}

}

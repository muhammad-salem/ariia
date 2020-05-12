package org.ariia.items;

public enum ItemState {
	
	INIT, INIT_HTTP, INIT_FILE, WAITING, DOWNLOADING, PAUSE, COMPLETE;
	
	public String toLowerString() {
		return name().toLowerCase();
	}
	
	public boolean isComplete() {
		return this.equals(COMPLETE);
	}

}

package org.ariia.items;

public enum ItemState {
	
	INIT("Init"),
	INIT_HTTP("Init HTTP"),
	INIT_FILE("Init File"),
	WAITING("Wating"),
	DOWNLOADING("Downloading"),
	PAUSE("Pause"),
	COMPLETE("Complete");
	
	private final String state;
	private ItemState(String state) {
		this.state = state;
	}
	
	@Override
	public String toString() {
		return this.state;
	}
	
	public boolean isComplete() {
		return this.equals(COMPLETE);
	}

}

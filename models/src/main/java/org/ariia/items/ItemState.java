package org.ariia.items;

import java.util.Objects;

public enum ItemState {

    INIT("Init"),
    INIT_HTTP("Init HTTP"),
    INIT_FILE("Init File"),
    WAITING("Waiting"),
    DOWNLOADING("Downloading"),
    PAUSE("Pause"),
    COMPLETE("Complete"),
    DELETE("Delete");

    private final String state;

    ItemState(String state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return this.state;
    }

    public boolean isWaiting() {
        return this.equals(WAITING);
    }

    public boolean isDownloading() {
        return this.equals(DOWNLOADING);
    }

    public boolean isPause() {
        return this.equals(PAUSE);
    }

    public boolean isComplete() {
        return this.equals(COMPLETE);
    }

    public boolean isInit() {
        return this.toString().toLowerCase().startsWith("init");
    }

    public boolean canMoveToWaitState() {
        return this.isInit() || Objects.equals(PAUSE, this);
    }

//	public boolean canMoveToDownloadState() {
//		return canMoveToWaitState() || Objects.equals(WAITING, this);
//	}

}

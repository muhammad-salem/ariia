package org.ariia.web.app.model;

import lombok.Getter;
import lombok.Setter;
import org.ariia.range.RangeInfo;

@Getter
@Setter
public class WebRangeInfo {

    protected long fileLength;
    protected long downloadLength;
    protected long remainingLength;
    protected int maxRangePoolNum;
    protected boolean finish;
    protected boolean streaming;

    public WebRangeInfo(RangeInfo rangeInfo) {
        this.fileLength = rangeInfo.getFileLength();
        this.downloadLength = rangeInfo.getDownloadLength();
        this.remainingLength = rangeInfo.getRemainingLength();
        this.maxRangePoolNum = rangeInfo.getMaxRangePoolNum();
        this.finish = rangeInfo.isFinish();
        this.streaming = rangeInfo.isStreaming();
    }

}

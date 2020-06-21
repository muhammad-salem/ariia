package org.ariia.web.app.model;

import org.ariia.core.api.writer.ItemMetaData;
import org.ariia.items.Item;
import org.ariia.items.ItemState;
import org.ariia.monitors.RangeReport;
import org.ariia.range.RangeInfo;

import java.util.List;
import java.util.Map;

public class WebItem {

    protected String url;
    protected String redirectUrl;
    protected String filename;
    protected String saveDirectory;
    protected Map<String, List<String>> headers;

    protected ItemState state;
    protected WebRangeInfo rangeInfo;

    protected String uuid;
    protected Integer id;

    protected RangeReport report;

    public WebItem(ItemMetaData metaData){
        this(metaData.getItem(), metaData.getRangeReport());
    }

    public WebItem(Item item, RangeReport rangeReport) {
        this.uuid = item.getUuid();
        this.id = item.getId();
        this.state = item.getState();
        this.rangeInfo = new WebRangeInfo(item.getRangeInfo());
        this.report = rangeReport;
    }

    public WebItem(Item item){
        this.id = item.getId();
        this.uuid = item.getUuid();
        this.url = item.getUrl();
        this.redirectUrl = item.getRedirectUrl();
        this.filename = item.getFilename();
        this.state = item.getState();
        this.saveDirectory = item.getSaveDirectory();
        this.headers = item.getHeaders();
        this.rangeInfo = new WebRangeInfo(item.getRangeInfo());
    }

}

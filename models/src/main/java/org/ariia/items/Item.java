package org.ariia.items;

import lombok.Getter;
import lombok.Setter;
import org.ariia.range.RangeInfo;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;

@Setter
@Getter
public class Item {

    private static int ITEMS_COUNT = 1;

    protected Integer id;
    protected String uuid;
    protected String url;
    protected String redirectUrl;
    protected String filename;
    protected ItemState state;
    protected String saveDirectory;
    protected Map<String, List<String>> headers;
    protected RangeInfo rangeInfo;


    public Item() {
        this.rangeInfo = new RangeInfo();
        this.headers = new HashMap<>(0);
        this.uuid = UUID.randomUUID().toString();
        this.id = ITEMS_COUNT++;
        this.state = ItemState.INIT;
    }

    public boolean isRedirected() {
        return redirectUrl != null && !redirectUrl.equals(url);
    }

    public void setFilename(String filename) {
        this.setFilename(filename, "UTF-8");
    }

    public void setFilename(String filename, String enc) {
        try {
            this.filename = URLDecoder.decode(filename, enc);
        } catch (UnsupportedEncodingException e) {
            this.filename = filename;
        }
    }

    public boolean isFinish() {
        return rangeInfo.isFinish();
    }

    public boolean isStreaming() {
        return rangeInfo.isStreaming();
    }

    public void addHeaders(Map<String, List<String>> headers) {
        this.headers.putAll(headers);
    }

    public void addHeader(String name, String value) {
        var list = headers.getOrDefault(name, new ArrayList<>());
        list.add(value);
        this.headers.put(name, list);
    }

    public void addHeader(String name, List<String> value) {
        if (headers.containsKey(name)) {
            headers.get(name).addAll(value);
        } else {
            this.headers.put(name, value);
        }
    }

    public String path() {
        var lastChar = saveDirectory.charAt(saveDirectory.length() - 1);
        if (lastChar == '/' || lastChar == '\\') {
            return (saveDirectory + filename);
        } else {
            return (saveDirectory + File.separatorChar + filename);
        }
    }

    public String getSaveDirectory() {
        return saveDirectory;
    }

    public void setSaveDirectory(String saveDirectory) {
        this.saveDirectory = saveDirectory;
    }

    public String liteString() {
        var builder = new StringBuilder();
        builder.append(filename);
        builder.append('\t');
        builder.append(uuid);
        builder.append('\n');
        builder.append(url);
        builder.append('\n');
        if (Objects.nonNull(redirectUrl)) {
            builder.append(redirectUrl);
            builder.append('\n');
        }
        builder.append("Directory : ");
        builder.append(saveDirectory);
        builder.append('\n');
        builder.append("File Length : ");
        builder.append(rangeInfo.getFileLengthMB());
        builder.append(" ( ");
        builder.append(rangeInfo.getFileLength());
        builder.append(" byte )");
        builder.append(",\tDownload : ");
        builder.append(rangeInfo.getDownloadLengthMB());
        builder.append(",\tRemaining : ");
        builder.append(rangeInfo.getRemainingLengthMB());
        return builder.toString();
    }

    @Override
    public String toString() {
        var builder = new StringBuilder();
        builder.append(liteString());
        builder.append('\n');
        builder.append("Headers Size : ");
        builder.append(headers.size());
        builder.append(",\tRange Count : ");
        builder.append(rangeInfo.getRangeCount());
        builder.append(",\tState : ");
        builder.append(state);
        builder.append('\n');
        builder.append(rangeInfo.toString());
        return builder.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (Objects.isNull(obj)) {
            return false;
        }
        if (!(obj instanceof Item)) {
            return false;
        }
        var item = (Item) obj;
        return this.url.equals(item.url)
                && this.filename.equals(item.filename)
                && this.state.equals(item.state)
                && this.saveDirectory.equals(item.saveDirectory)
                && this.headers.equals(item.headers)
                && this.rangeInfo.equals(item.rangeInfo)
                && this.uuid.equals(item.uuid);
    }

    public Item getCopy() {
        var item = new Item();
        item.setUrl(url);
        item.setRedirectUrl(redirectUrl);
        item.setFilename(filename);
        item.setState(state);
        item.setSaveDirectory(saveDirectory);
        item.headers = new HashMap<>(this.headers);
        item.rangeInfo = new RangeInfo(this.rangeInfo.getFileLength());
        return item;
    }

    public void copy(Item item) {
        this.uuid = item.uuid;
        this.url = item.url;
        this.filename = item.filename;
        this.state = item.state;
        this.headers = item.headers;
        this.rangeInfo = item.rangeInfo;
        this.redirectUrl = item.redirectUrl;
        this.saveDirectory = item.saveDirectory;
    }

    public void update(Item item) {
        if (!this.url.equals(item.url) && item.url != null) {
            this.url = item.url;
        }
        if (!this.filename.equals(item.filename) && item.filename != null) {
            this.filename = item.filename;
        }
        if (!this.saveDirectory.equals(item.saveDirectory) && item.saveDirectory != null) {
            this.saveDirectory = item.saveDirectory;
        }
        if (this.headers!=null && !this.headers.equals(item.headers) && item.headers != null) {
            this.headers = item.headers;
        }
    }

}

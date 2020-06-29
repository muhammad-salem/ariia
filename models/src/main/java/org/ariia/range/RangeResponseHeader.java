package org.ariia.range;

/**
 * Content-Range: bytes 82400369-209923494/209923495
 * Content-Range: bytes * /209923495
 *
 * @author salem
 * <p>
 * <p>
 * HTTP/1.1 200 OK
 * Content-Type: binary/octet-stream
 * Content-Length: 9999999
 * Accept-Ranges: bytes
 */
/**
 * HTTP/1.1 200 OK
 * Content-Type: binary/octet-stream
 * Content-Length: 9999999
 * Accept-Ranges: bytes
 */

/**
 * HTTP/1.1 206 Partial Content
 * Content-Type: binary/octet-stream
 * Content-Length: 594109611
 * Accept-Ranges: bytes
 * Content-Range: bytes 189-594109799/594109800
 */
//

public class RangeResponseHeader {
    public Type type;
    public long start, end, length;
    public RangeResponseHeader(String contentRange) {
        init(contentRange);
    }

    public RangeResponseHeader(long start) {
        init(start, -1, -1);
    }

    public RangeResponseHeader(long start, long end) {
        init(start, end, -1);
    }

    private void init(long start, long end, long length) {
        this.start = start;
        this.end = end;
        this.length = length;
    }

    public void init(String contentRange) {
        if (contentRange == null) {
            start = -1;
            end = -1;
            length = -1;
            return;
        }
        String[] temp = contentRange.split("=");
        type = temp[0].equals("bytes") ? Type.bytes : Type.non;
        if (temp[1].equals("*")) {
            length = Long.valueOf(temp[2].substring(1));
            start = end = -1;
            return;
        }
        temp = temp[1].split("-");
        start = Long.valueOf(temp[0]);
        temp = temp[1].split("/");
        end = Long.valueOf(temp[0]);
        if (temp.length > 1) {
            length = Long.valueOf(temp[1]);
        }

    }

    @Override
    public String toString() {
        return type.name() + "=" + start + "-" + end + "/" + length;
    }

    public String toRequestRange() {
        StringBuilder builder = new StringBuilder();
        builder.append(type.name() + " ");
        builder.append(start);
        builder.append('-');
        if (end == -1)
            return builder.toString();
        builder.append(end);
        builder.append('-');
        if (length == -1)
            return builder.toString();
        builder.append(length);
        return builder.toString();
    }

    enum Type {
        bytes, non;
    }

}

package org.ariia.core.api.client;

import org.ariia.config.Properties;
import org.ariia.core.api.request.ClientRequest;
import org.ariia.core.api.writer.ClientWriter;

public class Clients {

    public static SegmentClient segmentClient(Properties properties, ClientRequest request) {
        return new SegmentClient(properties, request);
    }

    public static ChannelClient channelClient(Properties properties, ClientRequest request, ClientWriter clientWriter) {
        return new ChannelClient(properties, request, clientWriter);
    }

}

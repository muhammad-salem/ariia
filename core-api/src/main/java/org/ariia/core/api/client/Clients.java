package org.ariia.core.api.client;

import org.ariia.config.Properties;
import org.ariia.core.api.request.ClientRequest;
import org.ariia.core.api.writer.ClinetWriter;

public class Clients {

    public static SegmentClient segmentClient(Properties properties, ClientRequest request) {
        return new SegmentClient(properties, request);
    }

    public static ChannelClient channelClient(Properties properties, ClientRequest request, ClinetWriter clinetWriter) {
        return new ChannelClient(properties, request, clinetWriter);
    }

}

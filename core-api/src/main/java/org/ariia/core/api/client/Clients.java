package org.ariia.core.api.client;

import org.ariia.config.Properties;
import org.ariia.core.api.request.ClientRequest;
import org.ariia.core.api.writer.ClinetWriter;

public class Clients {
	
	public static SegmentClient segmentClient(ClientRequest request) {
		SegmentClient client = new SegmentClient(Properties.RETRIES, request);
		
		return client;
	}
	
	public static ChannelClient channelClient(ClientRequest request, ClinetWriter clinetWriter) {
		ChannelClient client = new ChannelClient(Properties.RETRIES,request, clinetWriter);
		return client;
	}
	
	
	

}

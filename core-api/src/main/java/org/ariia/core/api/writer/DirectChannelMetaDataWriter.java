package org.ariia.core.api.writer;

import org.ariia.config.Properties;
import org.ariia.core.api.client.Client;
import org.ariia.items.Item;
import org.ariia.segment.Segment;

public class DirectChannelMetaDataWriter extends ChannelMetaDataWriter {

	public DirectChannelMetaDataWriter(Item item, Client client, Properties properties) {
		super(item, client, properties);
	}
	
	@Override
	public synchronized void offerSegment(Segment segment) {
		if(writeSegment(segment)) {
			releaseSegment(segment);
			forceUpdate();
//			saveItem2CacheFile();
		} else {
			if(raf == null || !raf.getChannel().isOpen()) {
				initRandomAccessFile();
				initMetaData();
				offerSegment(segment);
			}
		}
	}
}

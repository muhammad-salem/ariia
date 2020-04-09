package org.ariia.core.api.response;

import org.ariia.core.api.writer.ItemMetaData;
import org.ariia.speed.SpeedMonitor;


public interface Downloader {
	
	boolean downloadTask(ItemMetaData metaData, int index, SpeedMonitor... monitors);
	
	
}

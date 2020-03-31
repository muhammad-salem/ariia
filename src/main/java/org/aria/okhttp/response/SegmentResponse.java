package org.aria.okhttp.response;

import java.io.IOException;
import java.util.Arrays;

import org.aria.logging.Log;
import org.aria.manager.Item;
import org.aria.manager.ItemMetaData;
import org.aria.okhttp.client.ContentLength;
import org.aria.okhttp.writer.SegmentWriter;
import org.aria.speed.SpeedMonitor;

import okhttp3.Response;

public interface SegmentResponse extends DownloadResponse, ContentLength{
	
	SegmentWriter getSegmentWriter();
	
	@Override
	default boolean downloadTask(ItemMetaData metaData, int index, SpeedMonitor... monitors) {
		Item item = metaData.getItem();
		if (item.getRangeInfo().isFinish(index)) return true;
		
		try (Response response = getClientRequest().get(item, index)) {
			if(metaData.getRangeInfo().isStreaming()) {
				updateLength(metaData.getRangeInfo(), response);
			}
			if (response.code() / 100 != 2) {
				Log.warn(getClass(),  item.getFilename(), 
					  "response.code = " + response.code() + ' ' + response.message()
					+ "\nurl = " + response.request().url().toString()
					+ "\nindex = " + index + "\t" + Arrays.toString(item.getRangeInfo().indexOf(index)));
				return false;
			}
//			else if(response.code() == 416) //416 Range Not Satisfiable
//			{
//				return true;
//			}
			
			getSegmentWriter()
				.writeResponse(response, 
						metaData,
						index, 
						item.getRangeInfo().limitOfIndex(index), 
						monitors);
			return true;
		} catch (IOException e) {
			return false;
		}
	}

}

package org.okaria.okhttp.response;

import java.util.Arrays;

import org.log.concurrent.Log;
import org.okaria.manager.Item;
import org.okaria.manager.ItemMetaData;
import org.okaria.okhttp.client.ContentLength;
import org.okaria.okhttp.writer.SegmentWriter;
import org.okaria.speed.SpeedMonitor;

import okhttp3.Response;

public interface SegmentResponse extends DownloadResponse, ContentLength {
	
	SegmentWriter getSegmentWriter();
	
	@Override
	default boolean downloadTask(ItemMetaData metaData, int index, SpeedMonitor... monitors) {
		Item item = metaData.getItem();
		if (item.getRangeInfo().isFinish(index)) return true;
		
		Response response = null;
		try {
			response = getClientRequest().get(item, index);
			if(metaData.getRangeInfo().isStreaming()) updateLength(metaData.getRangeInfo(), response);
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
			
			getSegmentWriter().writeResponse(response, metaData, index, item.getRangeInfo().limitOfIndex(index) , monitors);
		} catch (Exception e) {
			return false;
		} finally {
			try {
				response.close();
			} catch (Exception e) {
				Log.error(getClass(), e.getClass().getName(), e.getMessage());
			}
		}
		return true;
	}

}

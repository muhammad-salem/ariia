package org.okaria.okhttp.response;

import java.util.Arrays;

import org.log.concurrent.Log;
import org.okaria.manager.Item;
import org.okaria.manager.ItemMetaData;
import org.okaria.okhttp.writer.SegmentWriter;
import org.okaria.speed.SpeedMonitor;

import okhttp3.Response;

public interface SegmentResponse extends DownloadResponse{
	
	SegmentWriter getSegmentWriter();
	
	@Override
	default boolean downloadTask(ItemMetaData placeHolder, int index, SpeedMonitor... monitors) {
		Item item = placeHolder.getItem();
		if (item.getRangeInfo().isFinish(index)) return true;
		
		Response response = null;
		try {
			response = getClientRequest().get(item, index);
			//item.addCookies(getClientRequest().getHttpClient().cookieJar().loadForRequest(item.getUrl()));
			if (response.code() / 100 != 2) {
				Log.warn(getClass(),  item.getFilename(), 
					  "response.code = " + response.code() + ' ' + response.message()
					+ "\nurl = " + response.request().url().toString()
					+ "\nindex = " + index + "\t" + Arrays.toString(item.getRangeInfo().indexOf(index)));
				return false;
			}
			getSegmentWriter().writeResponse(response, placeHolder, index, item.getRangeInfo().limitOfIndex(index) , monitors);
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

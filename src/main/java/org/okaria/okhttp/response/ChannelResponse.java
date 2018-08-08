package org.okaria.okhttp.response;

import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import org.log.concurrent.Log;
import org.okaria.manager.Item;
import org.okaria.manager.ItemMetaData;
import org.okaria.okhttp.writer.ClinetWriter;
import org.okaria.speed.SpeedMonitor;

import okhttp3.Response;

public interface ChannelResponse extends DownloadResponse {
	
	ClinetWriter getClinetWriter();
	ExecutorService getReleaseResourcesExecutor();
	

	default boolean downloadTask(ItemMetaData placeHolder, int index, SpeedMonitor... monitors) {
		
		Item item = placeHolder.getItem();
		if (item.getRangeInfo().isFinish(index))
			return true;
		RandomAccessFile raf = null;
		Response response = null;
		try {
			response = getClientRequest().get(item, index);
			item.addCookies(getClientRequest().getHttpClient().cookieJar().loadForRequest(item.url()));
			if (response.code() / 100 != 2) {
				Log.warn(getClass(),  item.getFilename(), 
					  "response.code = " + response.code() + ' ' + response.message()
					+ "\nurl = " + response.request().url().toString()
					+ "\nindex = " + index + "\t" + Arrays.toString(item.getRangeInfo().indexOf(index)));
				return false;
			}
			raf = new RandomAccessFile(item.path(), "rwd");
			getClinetWriter().writeResponse(response, raf, item.getRangeInfo().indexOf(index), monitors);
		} catch (Exception e) {
			return false;
		} finally {
			relaseResources(raf, response);
		}
		return true;
	}

	default void relaseResources(RandomAccessFile raf, Response response) {
		getReleaseResourcesExecutor().execute(() -> {
			try {
				response.close();
				TimeUnit.SECONDS.sleep(2);
				raf.close();
			} catch (Exception e) {
				Log.info(getClass(), e.getClass().getName(), e.getMessage());
			}
//			{/* ignore */}
		});
	}
	
	

}

package org.ariia.okhttp.response;

import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import org.ariia.items.Item;
import org.ariia.logging.Log;
import org.ariia.okhttp.client.ContentLength;
import org.ariia.okhttp.writer.ClinetWriter;
import org.ariia.okhttp.writer.ItemMetaData;
import org.ariia.speed.SpeedMonitor;

import okhttp3.Response;

public interface ChannelResponse extends DownloadResponse, ContentLength {
	
	ClinetWriter getClinetWriter();
	ExecutorService getReleaseResourcesExecutor();
	

	default boolean downloadTask(ItemMetaData metaData, int index, SpeedMonitor... monitors) {
		
		Item item = metaData.getItem();
		if (item.getRangeInfo().isFinish(index))
			return true;
		RandomAccessFile raf = null;
		Response response = null;
		try {
			response = getClientRequest().get(item, index);
			if(index == 0 ) updateLength(metaData.getRangeInfo(), response);
			//item.addCookies(getClientRequest().getHttpClient().cookieJar().loadForRequest(item.url()));
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
//				
//			}
			
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
				if (Objects.nonNull(response)) {
					response.close();
				}
				TimeUnit.SECONDS.sleep(2);
				if (Objects.nonNull(raf)) {
					raf.close();
				}
			} catch (Exception e) {
				Log.info(getClass(), e.getClass().getName(), e.getMessage());
			}
//			{/* ignore */}
		});
	}
	
	

}

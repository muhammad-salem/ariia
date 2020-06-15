package org.ariia.core.api.response;

import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.ariia.core.api.request.ClientRequest;
import org.ariia.core.api.request.Response;
import org.ariia.core.api.writer.ClinetWriter;
import org.ariia.core.api.writer.ItemMetaData;
import org.ariia.items.Item;
import org.ariia.logging.Log;
import org.ariia.speed.report.SpeedMonitor;


public class ChannelDownloader implements Downloader, ContentLength {
	
	private ClientRequest clientRequest;
	private ClinetWriter clinetWriter;
	
	public ChannelDownloader(ClientRequest clientRequest, ClinetWriter clinetWriter) {
		super();
		this.clientRequest = clientRequest;
		this.clinetWriter = clinetWriter;
	}

	public boolean downloadTask(ItemMetaData metaData, int index, SpeedMonitor... monitors) {
		
		Item item = metaData.getItem();
		if (item.getRangeInfo().isFinish(index))
			return true;
		RandomAccessFile raf = null;
		Response response = null;
		try {
			response = clientRequest.get(item, index);
			
			if(index == 0 ) {
				Optional<String> contentLength = response.firstValue("Content-Length");
				if (contentLength.isPresent()) {
					updateLength(metaData.getRangeInfo(), contentLength.get());
				}
			}
			//item.addCookies(getClientRequest().getHttpClient().cookieJar().loadForRequest(item.url()));
			if (response.code() / 100 != 2) {
				Log.warn(getClass(),  item.getFilename(), 
					  "response.code = " + response.code() + ' ' + response.responseMessage()
					+ "\nurl = " + response.requestUrl()
					+ "\nindex = " + index + "\t" + Arrays.toString(item.getRangeInfo().indexOf(index)));
				return false;
			}
			
			
			raf = new RandomAccessFile(item.path(), "rwd");
			clinetWriter.writeRsponse(response.bodyBytes(), raf, item.getRangeInfo().indexOf(index), monitors);
		} catch (Exception e) {
			return false;
		} finally {
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
		}
		return true;
	}
	

}

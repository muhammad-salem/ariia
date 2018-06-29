package org.okaria.okhttp;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.log.Log;
import org.okaria.R;
import org.okaria.manager.Item;
import org.okaria.okhttp.writer.ClinetWriter;
import org.okaria.range.RangeResponseHeader;
import org.okaria.speed.SpeedMonitor;

import okhttp3.HttpUrl;
import okhttp3.Response;

public interface ClientResponse {
    ExecutorService releaseResources = Executors.newCachedThreadPool();
	
    
    
	//int RESPONSE_BUFFER = 8192;
	
	/*
	 * ---response begin--- HTTP/1.1 206 Partial Content Server: nginx Date: Thu, 19
	 * Apr 2018 15:18:36 GMT Content-Type: application/octet-stream Last-Modified:
	 * Tue, 20 Mar 2018 04:56:56 GMT ETag: "5ab09498-c832da7" Content-Range: bytes
	 * 82400369-209923494/209923495 Content-Length: 127523126
	 */

	/*
	 * HTTP/1.1 200 OK Server: nginx Date: Thu, 19 Apr 2018 15:34:35 GMT
	 * Content-Type: application/octet-stream Last-Modified: Sat, 24 Mar 2018
	 * 12:57:40 GMT ETag: "5ab64b44-3b8da4fe" Accept-Ranges: bytes Content-Length:
	 * 999138558
	 * 
	 */

	/*
	 * HTTP/1.1 416 Requested Range Not Satisfiable Server: nginx Date: Thu, 19 Apr
	 * 2018 20:32:21 GMT Content-Type: text/html Content-Range: bytes * /209923495
	 * Content-Length: 206
	 */


	/*
	* HTTP/1.1 200 OK
	* Content-Type: binary/octet-stream
	* Content-Length: 9999999
	* Accept-Ranges: bytes
	*/
	/*
	* HTTP/1.1 206 Partial Content
	* Content-Type: binary/octet-stream
	* Content-Length: 594109611
	* Accept-Ranges: bytes
	* Content-Range: bytes 189-594109799/594109800
	*/
	//

	default boolean isStateCodeAllowed( Response response, RandomAccessFile file) throws IOException {
		if (response.code() == 200) {
			file.seek(0);
			return  true;
		} else if (response.code() == 206) {
			RangeResponseHeader range = new RangeResponseHeader(response.header("Content-Range"));
			file.seek(range.start);
			return true;
		}else if (response.code() == 416) {			// error state
			return false;
		}
		return false;
	}

	default boolean downloadTask(HttpUrl httpUrl, long[] subrang, RandomAccessFile raf, SpeedMonitor... monitors) {
		if (subrang[0] - subrang[1] >= 0)
			return true;
		Response response = null;
		try {
			response = getClientRequest().get(httpUrl, subrang[0], subrang[1]);
			if (response.code() / 100 != 2) {
				return false;
			}
			getClinetWriter().writeResponse(response, raf, subrang, monitors);
//			writeResponse(raf, response, subrang, monitors);
		} catch (Exception e) {
			// if(subrang[0] - subrang[1] >= 0) return;
			// System.out.println(e.getMessage());
			// try {TimeUnit.SECONDS.sleep(1);}catch(Exception ignore){}
			return false;
			// downloadTask(httpUrl, subrang, filePath, monitors);
		} finally {
			relaseResources(null, response);
		}
		return true;
	}

	ClientRequest getClientRequest();

	default boolean downloadTask(HttpUrl httpUrl, long[] subrang, String filePath, SpeedMonitor... monitors) {
		if (subrang[0] - subrang[1] >= 0)
			return true;
		RandomAccessFile raf = null;
		Response response = null;
		try {
			response = getClientRequest().get(httpUrl, subrang[0], subrang[1]);
			if (response.code() / 100 != 2) {
				return false;
			}
			R.mkParentDir(filePath);
			raf = new RandomAccessFile(filePath, "rw");
			getClinetWriter().writeResponse(response, raf, subrang, monitors);
//			writeResponse(raf, response, subrang, monitors);
		} catch (Exception e) {
			// if(subrang[0] - subrang[1] >= 0) return;
			// System.out.println(e.getMessage());
			// try {TimeUnit.SECONDS.sleep(1);}catch(Exception ignore){}
			return false;
			// downloadTask(httpUrl, subrang, filePath, monitors);
		} finally {
			relaseResources(raf, response);
		}
		return true;
	}
	
	ClinetWriter getClinetWriter();
	
	default boolean downloadTask(Item item, int index, SpeedMonitor... monitors) {
		long[] subrang = item.getRangeInfo().getIndex(index);
		if (subrang[0] - subrang[1] >= 0)
			return true;
		RandomAccessFile raf = null;
		Response response = null;
		try {
			response = getClientRequest().get(item, index);
			
			
			item.addCookies(getClientRequest().getHttpClient().cookieJar().loadForRequest(item.getUpdateHttpUrl()));
			if (response.code() / 100 != 2) {
				Log.warning(getClass(), "response.code != 200", item.getFilename());
				return false;
			}
			R.mkParentDir(item.getSavepathFile());
			raf = new RandomAccessFile(item.getSavepathFile(), "rw");
			getClinetWriter().writeResponse(response, raf, subrang, monitors);
//			writeResponse(raf, response, subrang, monitors);
		} catch (Exception e) {
			// if(subrang[0] - subrang[1] >= 0) return;
			// System.out.println(e.getMessage());
			// try {TimeUnit.SECONDS.sleep(1);}catch(Exception ignore){}
			return false;
			// downloadTask(httpUrl, subrang, filePath, monitors);
		} finally {
			relaseResources(raf, response);
		}
		return true;
	}

	default void relaseResources(RandomAccessFile raf, Response response) {
		if (raf != null)
			releaseResources.execute(() -> {
				closeRAF(raf);
			});
		if (response != null)
			releaseResources.execute(() -> {
				if (response != null)
					response.close();
			});
	}

	default void closeRAF(RandomAccessFile raf) {
		if (raf == null)
			return;
		try {
			TimeUnit.SECONDS.sleep(2);
			raf.close();
		} catch (Exception ignore) {
			/* ignore */}
	}

}

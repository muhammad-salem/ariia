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

	/*
	default void writeResponse(RandomAccessFile file, Response response, SpeedMonitor... monitors) throws IOException {

		if (isStateCodeAllowed(response, file) == false) return;
		writeResponse(file, response.body().byteStream(), monitors);
		// InputStream in = MonitorInputStreamWrapper.wrap(response.body().byteStream(),
		// monitors);
		// writeResponse(in, file);
	}
	
	default void writeResponse(RandomAccessFile file, InputStream in, SpeedMonitor... monitors) throws IOException {
		in = MonitorInputStreamWrapper.wrap(in, monitors);
		writeResponse(file, in);
	}

//	default void writeResponse(RandomAccessFile file, InputStream in) throws IOException {
//		// Objects.requireNonNull(file, "file");
//		byte[] buffer = new byte[RESPONSE_BUFFER];
//		int read;
//		while ((read = in.read(buffer)) != -1) {
//			file.write(buffer, 0, read);
//		}
//	}

	default void writeResponse(RandomAccessFile file, Response response, long[] ranges, SpeedMonitor... monitors)
			throws IOException {
		if (response.code() == 200) {
			file.seek(0);
			if(ranges[0] != 0 ) return;
		} else if (response.code() == 206) {
			Range range = new Range(response.header("Content-Range"));
			file.seek(range.start);
		}else if (response.code() == 416) {			// error state
			return;
		}
		//if (isStateCodeAllowed(response, file) == false) return;
		writeResponse(file, response.body().byteStream(), ranges, monitors);
		// InputStream in = MonitorInputStreamWrapper.wrap(response.body().byteStream(),
		// monitors);
		// writeResponse(in, file);
	}

	default void writeResponse(RandomAccessFile file, InputStream in, long[] ranges, SpeedMonitor... monitors)
			throws IOException {
		in = MonitorInputStreamWrapper.wrap(in, monitors);
//		writeResponse(file, in, ranges);
		writeResponseRangeLimit(file, in, ranges);
	}

	default void writeResponse(RandomAccessFile file, InputStream in, long[] ranges) throws IOException {
		// Objects.requireNonNull(file, "file");
		byte[] buffer = new byte[RESPONSE_BUFFER];
		int read;
		while ((read = in.read(buffer)) != -1) {
			file.write(buffer, 0, read);
			addToRange(ranges, read);
		}
	}
	
	default void writeResponseRangeLimit(RandomAccessFile file, InputStream in, long[] ranges) throws IOException {
		// Objects.requireNonNull(file, "file");
		byte[] buffer = new byte[RESPONSE_BUFFER];
		int read;
		while ((read = in.read(buffer)) != -1) {
			file.write(buffer, 0, read);
			addToRange(ranges, read);
			
			/**
			 * stop read write operation intently
			 * in case of keep reading more than the given range
			 * when modify/update ranges 
			 * new will read until new modified range
			 * /
			if(ranges[0] - ranges[1] >= 0) break;
		}
	}
	
	

	default void addToRange(long[] ranges, long readed) {
		ranges[0] += readed;
	}
	*/
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
			Log.info(getClass(), "respnse", response.toString());
			Log.fine(getClass(), "respnse Line", response.protocol() + " " + response.code() + " " + response.message());
			Log.finer(getClass(), "headers", response.headers().toString());
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

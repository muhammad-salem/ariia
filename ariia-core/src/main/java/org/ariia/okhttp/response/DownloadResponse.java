package org.ariia.okhttp.response;

import org.ariia.okhttp.request.ClientRequest;
import org.ariia.okhttp.writer.ItemMetaData;
import org.ariia.speed.SpeedMonitor;

/*
 * ---response begin--- 
 * HTTP/1.1 206 Partial Content 
 * Server: nginx Date: Thu, 19
 * Apr 2018 15:18:36 GMT 
 * Content-Type: application/octet-stream 
 * Last-Modified: Tue, 20 Mar 2018 04:56:56 GMT 
 * ETag: "5ab09498-c832da7" 
 * Content-Range: bytes
 * 82400369-209923494/209923495 
 * Content-Length: 127523126
 */

/*
 * HTTP/1.1 200 OK 
 * Server: nginx 
 * Date: Thu, 19 Apr 2018 15:34:35 GMT
 * Content-Type: application/octet-stream 
 * Last-Modified: Sat, 24 Mar 2018 12:57:40 GMT 
 * ETag: "5ab64b44-3b8da4fe" 
 * Accept-Ranges: bytes 
 * Content-Length: 999138558
 * 
 */

/*
 * HTTP/1.1 416 Requested Range Not Satisfiable 
 * Server: nginx Date: Thu, 19 Apr 2018 20:32:21 GMT 
 * Content-Type: text/html 
 * Content-Range: bytes * /209923495
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


public interface DownloadResponse {
	
	ClientRequest getClientRequest();
	boolean downloadTask(ItemMetaData metaData, int index, SpeedMonitor... monitors);
	
	
	
	
}

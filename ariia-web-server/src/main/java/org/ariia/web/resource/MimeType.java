package org.ariia.web.resource;

import java.util.HashMap;
import java.util.Map;

public final class MimeType {
	
	private static Map<String, String> MIME_TYPE = new HashMap<>();
	static {
		String types = "css=text/css;htm=text/html;html=text/html;xml=text/xml;java=text/x-java-source, text/java;md=text/plain;txt=text/plain;asc=text/plain;gif=image/gif;jpg=image/jpeg;jpeg=image/jpeg;png=image/png;svg=image/svg+xml;mp3=audio/mpeg;m3u=audio/mpeg-url;mp4=video/mp4;ogv=video/ogg;flv=video/x-flv;mov=video/quicktime;swf=application/x-shockwave-flash;js=application/javascript;pdf=application/pdf;doc=application/msword;ogg=application/x-ogg;zip=application/octet-stream;exe=application/octet-stream;class=application/octet-stream;m3u8=application/vnd.apple.mpegurl;ts=video/mp2t;ico=image/x-icon";
		String[] mimes = types.split(";");
		for (String string : mimes) {
			String[] temp = string.split("=");
			MIME_TYPE.put(temp[0], temp[1]);
		}
	}
	
	public static String mime(String type) {
		return MIME_TYPE.get(type);
	}
	
	public static String getType(String fileName) {
		int lastIndexOf = fileName.lastIndexOf(".");
	    return fileName.substring(lastIndexOf + 1);
	}
	
	public static String getMimeForFileName(String fileName) {
	    return mime(getType(fileName));
	}

}

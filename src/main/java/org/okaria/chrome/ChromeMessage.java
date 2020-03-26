package org.okaria.chrome;

import java.util.Map;

import org.okaria.lunch.Argument;
import org.okaria.lunch.TerminalArgument;
import org.terminal.console.log.Level;

import com.google.gson.Gson;

public class ChromeMessage {

	private ChromeMessage() {
		
	}

	long date;
	int id;
	String mime;
	long fileSize;
	boolean list;
	boolean page;
	String url;
	String origUrl;
	String cookies = null;
	String referer;
	String filename;
	String post;
	String useragent;
	String proxy;

	public String getURL() {
		return url;
	}

	public String getOrigUrl() {
		return origUrl;
	}

	public String getReferer() {
		return referer;
	}

	public String getCookies() {
		return cookies;
	}

	public String getFilename() {
		return filename;
	}

	public String getPostData() {
		return post;
	}

	public String getUserAgent() {
		return useragent;
	}

	public String getMime() {
		return mime;
	}

	public int getID() {
		return id;
	}

	public long getFileSize() {
		return fileSize;
	}

	public long getDate() {
		return date;
	}

	public boolean isList() {
		return list;
	}

	public boolean isPage() {
		return page;
	}
	

	public static ChromeMessage CreateMessage(String json) {
		Gson gson = new Gson();
		ChromeMessage msg = gson.fromJson(json, ChromeMessage.class);
		return msg;
	}

	public Argument toArguments() {
		Argument arguments = new Argument();
		Map<TerminalArgument, String> map = arguments.getDictionary();
		if(url != null)
			map.put(TerminalArgument.Url, url);
		if(referer != null)
			map.put(TerminalArgument.Referer, referer);
		if(filename != null)
			map.put(TerminalArgument.FileName, filename);
		if(useragent != null)
			map.put(TerminalArgument.UserAgent, useragent);
		if(cookies != null & ! cookies.equals(""))
			map.put(TerminalArgument.Cookie, cookies);
		if(proxy != null)
			map.put(TerminalArgument.Proxy, proxy);
		
		map.put(TerminalArgument.Chrome, TerminalArgument.Chrome.toString());
		map.put(TerminalArgument.Debug, Level.off.toString());
		return arguments;
	}

	@Override
	public String toString() {
		String str = "";

		str += !getURL().equals("") ? ("url: " + getURL() + "\n") : "";
		str += !getOrigUrl().equals("") ? ("origUrl: " + getOrigUrl() + "\n") : "";
		str += !getFilename().equals("") ? ("filename: " + getFilename() + "\n") : "";
		str += !getMime().equals("") ? ("mime: " + getMime() + "\n") : "";
		str += !getReferer().equals("") ? ("referrer: " + getReferer() + "\n") : "";
		str += !getUserAgent().equals("") ? ("userAgent: " + getUserAgent() + "\n") : "";
		str +=  getID() != -1 ? ("id: " + getID() + "\n") : "";
		str +=  getDate() != -1 ? ("date: " + getDate() + "\n") : "";
		str +=  isPage() ? ("is Page \n") : "";
		str +=  isList() ? ("is list \n") : "";
		str += !getCookies().equals("") ? ("cookies: \n" + getCookies() + "\n") : "";

		return str;
	}

}

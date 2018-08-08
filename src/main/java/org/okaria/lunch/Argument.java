package org.okaria.lunch;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.okaria.okhttp.OkUtils;

import okhttp3.Cookie;
import okhttp3.HttpUrl;

public class Argument {

	transient private String[] args;
	private Map<TerminalArgument, String> dictionary;

	public Argument() {
		dictionary = new HashMap<TerminalArgument, String>();
	}
	public Argument(String... args) {
		this.args = args;
		dictionary = new HashMap<TerminalArgument, String>();
		initDictionary();
	}

	public String[] getArgs() {
		return args;
	}
	
	public Map<TerminalArgument, String> getDictionary() {
		return dictionary;
	}
	
	

	/**
	 * dictionary
	 */
	private void initDictionary() {
		for (int i = 0; i < args.length; i++) {
			String arg = args[i];
			if (arg.startsWith("-")) {
				String[] t = arg.split("=", 2);
				TerminalArgument argument = TerminalArgument.argument(t[0]);
				if (t.length == 1) {
					if(argument.isPair()) {
						addArgument(argument, args[++i]);
					}else {
						addArgument(argument, argument.getFull());
					}
				} else {
					addArgument(argument, t[1]);
				}
			} else if (arg.startsWith("http")){
				HttpUrl url = HttpUrl.parse(arg);
				if (url == null)
					continue;
				dictionary.put(TerminalArgument.Url, arg);
			}
		}
	}

	private void addArgument(TerminalArgument argument, String value) {
		if (argument == TerminalArgument.Header || argument == TerminalArgument.Cookie) {
			String header = dictionary.getOrDefault(argument, "");
			value = value + "\n" + header;
		}
		dictionary.put(argument, value);

	}

	public Proxy getProxy() {
		String value;
		Proxy proxy = Proxy.NO_PROXY;

		// http://127.0.0.1:8080/
		if (isProxy()) {
			value = get(TerminalArgument.Proxy);
			Proxy.Type type = Type.DIRECT;
			if (value.startsWith("http")) {
				type = Type.HTTP;

			} else if (value.startsWith("socks")) {
				type = Type.SOCKS;
			}

			try {
				URI uri = new URI(value);
				proxy = new Proxy(type, new InetSocketAddress(uri.getHost(), uri.getPort()));
			} catch (URISyntaxException e) {
				proxy = getProxy(value, type);
			}

		} else if (isHttpProxy()|| isHttpsProxy()) {
			value = get(TerminalArgument.HttpProxy);
			if(value == null)
				value = get(TerminalArgument.HttpsProxy);
			proxy = getProxy(value, Type.HTTP);
		} else if (isSocksProxy() 
				|| isSocks4Proxy()
				|| isSocks5Proxy()) {
			value = dictionary.get(TerminalArgument.SocksProxy);
			if(value == null)
				value = dictionary.get(TerminalArgument.Socks5Proxy);
			if(value == null)
				value = dictionary.get(TerminalArgument.Socks4Proxy);
			proxy = getProxy(value, Type.SOCKS);
		}
		return proxy;
	}

	/**
	 * @param value
	 * @param type
	 * @return
	 */
	protected Proxy getProxy(String value, Proxy.Type type) {
		Proxy proxy;
		String host = null;
		int port = 0;
		try {
			String[] temp = value.split(":");
			switch (temp.length) {
			case 3:
				// <->  http*//127.0.0.1*8080/
				
				/*
				switch (temp[0]) {
				case "http":
				case "https":
					type = Type.HTTP;
					break;
				case "ssh":	
				case "socks":
				case "socks4":
				case "socks5":	
					type = Type.SOCKS;
					break;
				}*/
				
				host = temp[1].substring(2);
				if(temp[2].lastIndexOf('/')  == -1)
					port= Integer.parseInt(temp[2]);
				else
					port= Integer.parseInt(temp[2].substring(0, temp[2].length()-2));
				break;
			case 2:
				/// 127.0.0.1:8080
				host = temp[0];
				port= Integer.parseInt(temp[1]);
				break;
			case 1:
				// 127.0.0.1	use 8080 as default
				host = temp[0];
				port= 8080;
				break;
			default:
				break;
			}
			proxy = new Proxy(type, new InetSocketAddress(host, port));
		} catch (Exception e) {
			return Proxy.NO_PROXY;
		}
		return proxy;
	}

	public String getUrl() {
		return dictionary.get(TerminalArgument.Url);
	}
	
	public String getInputFile() {
		return dictionary.get(TerminalArgument.InputFile);
	}
	public String getMetaLinkFile() {
		return dictionary.get(TerminalArgument.MetaLink);
	}
	
	public String getReferer() {
		return dictionary.get(TerminalArgument.Referer);
	}
	public String getFileName() {
		return dictionary.get(TerminalArgument.FileName);
	}
	
	public String getSavetoName() {
		return dictionary.get(TerminalArgument.FileName);
	}
	public String getUserAgent() {
		return dictionary.get(TerminalArgument.UserAgent);
	}
	
	public String getHeader() {
		return dictionary.get(TerminalArgument.Header);
	}
	
	public Map<String, String> getHeaders() {
		if(! isHeader()) return Collections.emptyMap();
		String value = dictionary.get(TerminalArgument.Header);
		Map<String, String> headers = new LinkedHashMap<>();
		if(value == null ) return headers; 
		String[] temp1 = value.split("\n");
		for (String string : temp1) {
			String[] temp2 = string.split(": ");
			headers.put(temp2[0], temp2[1]);
		}
		return headers;
	}
	
	public String getCookie() {
		return dictionary.get(TerminalArgument.Cookie);
	}
	
	public List<Cookie> getAllCookie() {
		List<Cookie> cookies = new LinkedList<>();
		if(isCookie())
			cookies.add(Cookie.parse(HttpUrl.parse(getUrl()), getCookie()));
		Map<String, String> headers = getHeaders();
		
		headers.forEach((k,v)->{
			if(k.equalsIgnoreCase("Set-Cookie")) {
				cookies.add(Cookie.parse(HttpUrl.parse(getUrl()), v));
			}
		});
		if(isCookieFile()) {
			List<Cookie> list = OkUtils.getCookies(getCookieFile());
			cookies.addAll(list);
		}
		return cookies;
	}
	
	public String getCookieFile() {
		return dictionary.get(TerminalArgument.CookieFile);
	}
	
	public List<Cookie> fileCookies() {
		return OkUtils.getCookies(dictionary.get(TerminalArgument.CookieFile));
	}
	
	public String getLogLevel() {
		return dictionary.get(TerminalArgument.Debug);
	}
	
	
	public int getNumberOfConnection() {
		return Integer.parseInt(dictionary.get(TerminalArgument.Connection));
	}
	
	public int getTries() {
		return Integer.parseInt(dictionary.get(TerminalArgument.Tries));
	}
	
	public int getMaxItem() {
		return Integer.parseInt(dictionary.get(TerminalArgument.MaxItem));
	}
	
	public String getSavePath() {
		return dictionary.get(TerminalArgument.SavePath);
	}
	
	public String getCheckFile() {
		return dictionary.get(TerminalArgument.CheckFile);
	}
	
	public String getMaven() {
		return dictionary.get(TerminalArgument.Maven);
	}
	public String getMavenGroupId() {
		return dictionary.get(TerminalArgument.GroupId);
	}
	public String getMavenArtifactId() {
		return dictionary.get(TerminalArgument.ArtifactId);
	}
	public String getMavenVersion() {
		return dictionary.get(TerminalArgument.MVersion);
	}
	public String getMavenRepository() {
		return dictionary.get(TerminalArgument.MavenRepository);
	}
	
	public String getGoogleDriveFileID() {
		return dictionary.get(TerminalArgument.GoogleDriveFileID);
	}
	
	
	private boolean is(TerminalArgument key) {
		return dictionary.containsKey(key);
	}
	
	public boolean isUrl() {
		return is(TerminalArgument.Url);
	}
	
	public boolean isInputFile() {
		return is(TerminalArgument.InputFile);
	}
	
	public boolean isMetaLink() {
		return is(TerminalArgument.MetaLink);
	}
	
	public boolean isReferer() {
		return is(TerminalArgument.Referer);
	}
	
	public boolean isFileName() {
		return is(TerminalArgument.FileName);
	}
	
	public boolean isUserAgent() {
		return is(TerminalArgument.UserAgent);
	}
	
	public boolean isHeader() {
		return is(TerminalArgument.Header);
	}
	
	public boolean isCookie() {
		return is(TerminalArgument.Cookie);
	}
	
	public boolean isCookieFile() {
		return is(TerminalArgument.CookieFile);
	}
	
	public boolean isProxy() {
		return is(TerminalArgument.Proxy);
	}
	
	public boolean isHttpProxy() {
		return is(TerminalArgument.HttpProxy);
	}
	
	public boolean isHttpsProxy() {
		return is(TerminalArgument.HttpsProxy);
	}
	
	public boolean isSocksProxy() {
		boolean proxy = false;
		if(isProxy()) {
			if(get(TerminalArgument.Proxy).startsWith("socks"))
				proxy = true;
			
		}
		return is(TerminalArgument.SocksProxy) 
				|| is(TerminalArgument.Socks4Proxy) 
				|| is(TerminalArgument.Socks5Proxy)
				|| proxy;
	}
	
	public boolean isSocks4Proxy() {
		return is(TerminalArgument.Socks4Proxy);
	}
	
	public boolean isSocks5Proxy() {
		return is(TerminalArgument.Socks5Proxy);
	}
	
	public boolean isSSH() {
		return is(TerminalArgument.SSH);
	}
	
	public boolean isSSHUser() {
		return is(TerminalArgument.SSHUser);
	}
	
	public boolean isSSHPass() {
		return is(TerminalArgument.SSHPass);
	}
	
	public boolean isVersion() {
		return is(TerminalArgument.Version);
	}
	

	public boolean isCheckFile() {
		return is(TerminalArgument.CheckFile);
	}
	
	public boolean isHelp() {
		return is(TerminalArgument.Help);
	}
	
	public boolean isLog() {
		return is(TerminalArgument.Debug);
	}
	public boolean isChrome() {
		return is(TerminalArgument.Chrome);
	}
	
	public boolean isConnection() {
		return is(TerminalArgument.Connection);
	}
	
	public boolean isTries() {
		return is(TerminalArgument.Tries);
	}
	
	public boolean isMaxItem() {
		return is(TerminalArgument.MaxItem);
	}
	
	public boolean isSavePath() {
		return is(TerminalArgument.SavePath);
	}
	
	public boolean isGoogleDrive() {
		return is(TerminalArgument.GoogleDriveFileID);
	}
	
	public boolean isMaven() {
		return is(TerminalArgument.GroupId)
				|| is(TerminalArgument.ArtifactId)
				|| is(TerminalArgument.MVersion)
				|| is(TerminalArgument.Maven) 
				|| is(TerminalArgument.MavenRepository);
	}

	public String get(TerminalArgument argument ){
		return dictionary.get(argument);
	}
	public String getOrDefault(TerminalArgument argument, String defaultValue) {
		if(is(argument)) {
			return get(argument);
		}
		return defaultValue;
	}
	
	public boolean isStream() {
		return is(TerminalArgument.Stream);
	}
	
	public String getStream(){
		return get(TerminalArgument.Stream);
	}
	
}

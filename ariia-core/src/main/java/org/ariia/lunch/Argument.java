package org.ariia.lunch;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.ariia.okhttp.OkUtils;
import org.ariia.util.R;

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

    public boolean isEmpty() {
        return dictionary.isEmpty();
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
				if(argument == null) {
					continue;
				}
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
				try {
					new URL(arg);
					dictionary.put(TerminalArgument.Url, arg);
				} catch (MalformedURLException e) {
					continue;
				}
			}
		}
	}

	private void addArgument(TerminalArgument argument, String value) {
		if (argument == TerminalArgument.Header) {
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
	
	public String getCookieFile() {
		return dictionary.get(TerminalArgument.CookieFile);
	}
	
	public Map<String, String> getCookies() {
		
		List<String> txtCookies = new LinkedList<>();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(getCookieFile()));
			String textCookie;
			while ((textCookie = reader.readLine()) != null) {
				txtCookies.add(textCookie);
			}
			reader.close();
		} catch (IOException e) {
			return Collections.emptyMap();
		}
		HashMap<String, String> cookies = new HashMap<>();
		// 0 1 2 3 4 5 6
		// .domain.com HTTPONLY / Secure ExpiryDate name value
		// .ubuntu.com TRUE / false 77777777 _ga GA1.2.86547
		for (String cookie : txtCookies) {

			String[] str = cookie.split("\t");
			cookies.put(str[5], str[6]);
		}
		return cookies;
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
		String sp = dictionary.get(TerminalArgument.SavePath);
		if(sp != null) {
			if(sp.startsWith("~"))
				sp = sp.replaceFirst("~", R.UserHome);
		}
		return sp;
	}
	
	public String getCheckFile() {
		return dictionary.get(TerminalArgument.CheckFile);
	}
	public String getChunkSize() {
		return dictionary.get(TerminalArgument.ChunkSize);
	}
	
	public int parseChunkSize() {
		try {
			return Integer.parseInt(getChunkSize());
		} catch (NumberFormatException e) {
			return -1;
		}
	}
	
	public String getDownloadPieces() {
		return dictionary.get(TerminalArgument.DownloadPieces);
	}
	public int[] parseDownloadPieces() {
		String pices = getDownloadPieces();
		if(pices.startsWith("/") || pices.startsWith("file://")) {
			List<String> list = OkUtils.readLines(pices);
			
//			List<Integer> indexs = new LinkedList<>();
//			Iterator<String> iterator =  list.iterator();
//			while (iterator.hasNext()) {
//				String num = (String) iterator.next();
//				try {
//					Integer i = Integer.parseInt(num);
//					indexs.add(i);
//				} catch (Exception e) {
//					continue;
//				}
//			}
//			return (Integer[]) indexs.toArray(new Integer[indexs.size()]);
			
			int[] indexs = new int[list.size()];
			for (int i = 0; i < list.size(); i++) {
				try {
					indexs[i] = Integer.parseInt(list.get(i));
				} catch (Exception e) {
					indexs[i] = -1;
				}
			}
			return indexs;
		}else {
			String temp [] = pices.split(" ");
			int[] indexs = new int[temp.length];
			for (int i = 0; i < temp.length; i++) {
				indexs[i] = Integer.parseInt(temp[i]);
			}
			return indexs;
		}
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
	public boolean isChunkSize() {
		return is(TerminalArgument.ChunkSize);
	}
	public boolean isDownloadPieces() {
		return is(TerminalArgument.DownloadPieces);
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

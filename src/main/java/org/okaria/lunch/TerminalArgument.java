package org.okaria.lunch;

public enum TerminalArgument{
		Url("-u", "--url"),
		InputFile("-i", "--input-file"),
		MetaLink("-m", "--metalink"),
		Referer("-r","--http-referer"),
		
		UserAgent("-ua", "--user-agent"),
		Header("-H", "--header"),
		Cookie("-C","--cookie"),
		CookieFile("-cf", "--cookie-file"),
		

		FileName("-o", "--file-name"),
		SavePath("-sp", "--save-path"),
		
		Tries("-t", "--tries"),
		Connection("-c", "--max-connection"),
		MaxItem("-n", "--num-download"),
		
		
		Proxy("-p", "--proxy"),
		
		HttpProxy("-http", "--http-proxy"),
		HttpsProxy("-https", "--https-proxy"),
		
		SocksProxy("-socks", "--socks-proxy"),
		Socks4Proxy("-socks4", "--socks4-proxy"),
		Socks5Proxy("-socks5", "--socks5-proxy"),
		
		SSH("-s", "--ssh"),
		SSHUser("-su", "--ssh-user"),
		SSHPass("-sp", "--ssh-pass"),
		
		Help("-h", "--help"),
		Log("-v", "--log-level"),
		Version("-V", "--version"), 
		Chrome("-ch","--chrome");
	
		
		
		private String mini; String full;
		private TerminalArgument(String mini, String full) {
			this.mini = mini;
			this.full = full;
		}
		
		public String getMini() {
			return mini;
		}
		
		public String getFull() {
			return full;
		}
		
		@Override
		public String toString() {
			return full;
		}
		
		public static TerminalArgument argument(String line) {
			for (TerminalArgument arg : TerminalArgument.values()) {
				if(line.startsWith(arg.mini) || line.startsWith(arg.full)) {
					return arg;
				}
			}
			return null;
		}
		
		public static String arg(String line) {
			for (TerminalArgument arg : TerminalArgument.values()) {
				if(line.startsWith(arg.mini)) {
					return arg.mini;
				}
				else if(line.startsWith(arg.full)) {
					return arg.full;
				}
			}
			return null;
		}
		
		public static String mini(String line) {
			for (TerminalArgument arg : TerminalArgument.values()) {
				if(line.startsWith(arg.mini)) {
					return arg.mini;
				}
			}
			return null;
		}
		
		public static String full(String line) {
			for (TerminalArgument arg : TerminalArgument.values()) {
				if(line.startsWith(arg.full)) {
					return arg.mini;
				}
			}
			return null;
		}

		public boolean isPair() {
			switch (this) {
			case Help:
			case Version:	
				return false;
			default:
				return true;
			}
		}
		public boolean isOne() {
			return !isPair();
		}
		
		public static String Help() {
			StringBuilder builder = new StringBuilder();
			builder.append("\n   OKaria (commend line) download manager\n");
			builder.append("\t-u	--url			link\n");
			builder.append("\t-i	--input-file		text file\n");
			builder.append("\t-m	--metalink		metalink text file\n");
			builder.append("\t-r	--http-referer		referer link\n");
			builder.append("\t-ua	--user-agent		user agent\n");
			builder.append("\t-H	--header		header\n");
			builder.append("\t-C	--cookie		cookie\n");
			builder.append("\t-cf	--cookie-file		cookie file\n");
			builder.append("\t-o	--file-name		file name\n");
			builder.append("\t-sp	--save-path		directory to save list file in\n");
			builder.append("\t-t	--tries			number of tries\n");
			builder.append("\t-n	--num-download			max current download items\n");
			builder.append("\t-c	--max-connection	max connection for current session\n");
			builder.append("\t-p	--proxy 		http://127.0.0.1:8080/\n");
			builder.append("\t-http	--http-proxy 		127.0.0.1:8080\n");
			builder.append("\t-https	--https-proxy 		127.0.0.1:8443\n");
			builder.append("\t-socks	--socks-proxy 		127.0.0.1:1080\n");
			builder.append("\t-socks4	--socks4-proxy	 	127.0.0.1:1080\n");
			builder.append("\t-socks5	--socks5-proxy 		127.0.0.1:1080\n");
			builder.append("\t-s	--ssh 			remotehost:port\n");
			builder.append("\t-su	--ssh-user 		remote login user name\n");
			builder.append("\t-sp	--ssh-pass 		remote login password, if non will be asked from terminal or gui will be used\n");
			builder.append("\t-h	--help			print this help message\n");
			builder.append("\t-V	--version		show current app version\n");
			builder.append("\t-v	--log-level		show logging info, debug, fine and all\n");
			return builder.toString();
		}
		
	}

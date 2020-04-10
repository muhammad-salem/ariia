package org.ariia.args;

import java.util.Arrays;

import org.terminal.console.log.Level;


public enum TerminalArgument{
		Url("-u", "--url"),
		InputFile("-i", "--input-file"),
		MetaLink("-m", "--metalink"),
		Referer("-r","--http-referer"),
		
		UserAgent("-ua", "--user-agent"),
		Header("-H", "--header"),
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
		
		CheckFile("-ch", "--check-file"),
		ChunkSize("-cs", "--chunk-size"),
		DownloadPieces("-dp", "--download-pieces"),
		
		
		Stream("-st", "--stream"),
		
		Help("-h", "--help"),
		Debug("-d", "--debug-level"),
		Version("-v", "--version"), 
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
				if(line.contentEquals(arg.mini) || line.contentEquals(arg.full)) {
					return arg;
				}
			}
			return null;
		}
		
		public static String GetStringArgument(String line) {
			for (TerminalArgument arg : TerminalArgument.values()) {
				if(line.contentEquals(arg.mini)) {
					return arg.mini;
				}
				else if(line.contentEquals(arg.full)) {
					return arg.full;
				}
			}
			return null;
		}
		
		public static String mini(String line) {
			for (TerminalArgument arg : TerminalArgument.values()) {
				if(line.contentEquals(arg.mini)) {
					return arg.mini;
				}
			}
			return null;
		}
		
		public static String full(String line) {
			for (TerminalArgument arg : TerminalArgument.values()) {
				if(line.contentEquals(arg.full)) {
					return arg.full;
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
		
		public static String help() {
			StringBuilder builder = new StringBuilder();
			builder.append("\n okaria commend line download manager\n");
			builder.append("\n java - jar okaria.jar [-u] URL\n");
			for (TerminalArgument argument : TerminalArgument.values()) {
				if(argument == TerminalArgument.Chrome) continue;
				builder.append('\t');
				builder.append(argument.mini);
				builder.append("\t");
				builder.append(argument.full);
				if(argument.full.length() > 14)
					builder.append("\t");
				else if(argument.full.length() > 7)
					builder.append("\t\t");
				else 
					builder.append("\t\t\t");
				builder.append(argument.doc());
				builder.append('\n');
			}
			return builder.toString();
		}
		
		public String doc() {
			switch (this) {
				case Url :
					return ("[-u] add new link/url to download manager");
				case InputFile:
					return ("downoload from text file - list of urls");
				case MetaLink:
					return ("downoload from  metalink text/xml file - list of urls on deffrient servers for the same daownloadable file");
				
				case Referer:
					return("set referer header for that link");
				case UserAgent:
					return("set user-agent header while download");
				case Header:
					return("set one/multiable different header(s) for that link\n\t\t\tadd cookie(s) while download");
				case CookieFile:
					return("add cookie(s) from standered cookie file");
				
				case FileName:
					return("save download link to file on hard-disk");
				case SavePath:
					return("set directory of download process");
				
				case Tries:
					return("number of tries when failler, then giveup (0 for keep-try )");
				case Connection:
					return("max connection for current session for each link");
				case MaxItem:
					return("number of download links in queue, if more links, will be in watting list");
				
				case Proxy:
					return("set proxy to http://host:port[8080]/, support protocols http, https ans socks4/5");
				case HttpProxy:
					return("use http proxy [host:port] format");
				case HttpsProxy:
					return("use https proxy [host:port] format");
				case SocksProxy:
					return("use socks proxy [host:port] format");
				case Socks5Proxy:
					return("use socks5 proxy [host:port] format");
				case Socks4Proxy:
					return("use socks4 proxy [host:port] format");
				
				case SSH:
					return("use ssh connection as proxy - [remotehost:port], not supported yet");
				case SSHUser:
					return("set ssh user name - remote login user name");
				case SSHPass:
					return("set remote login password, if non will be asked from terminal");
					
				case CheckFile:
					return ("check donload file if is complete, and try to complete it");
				case ChunkSize:
					return ("length of shunk/segment to check");
				case DownloadPieces:
					return ("index of pieces which need download. it could be in formate of string as \"2 52 22 783 \" or a file holding the indexs separited by '\\n'");
					
				case Stream:
					return ("stream URL One download connection");
					
				case Help:
					return("print this message");
				case Version:
					return("display the version of okaria");
				case Debug:
					return("display logging, Levels: " + Arrays.toString(Level.values()));
					

				default :
			}
			return "";
		}
		
	}

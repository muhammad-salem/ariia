package org.okaria;

import java.io.BufferedReader;
import java.io.FileReader;
import java.net.Proxy.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.log.Level;
import org.log.Log;
import org.okaria.okhttp.OkServiceManager;

public class OKAria {
	
	
	public static void main(String[] args) {
		
		if (args.length == 0) {
			printHelp();
			return;
		}
		
//		Logger.getLogger(OkHttpClient.class.getName()).setLevel(Level.ALL);
		Map<String, String> commends = new HashMap<>();
		// proxy 127.0.0.1:8999
		// proxy=127.0.0.1:8999
		for (int i = 0; i < args.length; i++) {
			String[] t = args[i].split("=", 2);
			if (t.length == 1) {
				commends.put(args[i], args[++i]);
			} else {
				commends.put(t[0], t[1]);
			}
		}

		// String proxyHost = "127.0.0.1";
		// int port = 8999;

		
		
		String log_level = commends.getOrDefault("--log-level", Level.info.name());
		Log.setLogLevel("aria", Level.valueOf(log_level));
		
		String proxyHost = null;
		int port = 0;
		String proxy = commends.get("proxy");
		if(proxy != null) {
			proxyHost = commends.get("proxy").split(":")[0];
			port = Integer.parseInt(commends.get("proxy").split(":")[1]);
		}
		

		if (commends.get("check") != null) {
			Log.info(Arrays.toString(args));
			// search google for "file hole"
//			 FileChecker checker = new FileChecker();
			//
			// long[][] ranges = OkUtils.subrange(1921843200, 10);
			//
			// LinkedHashMap<Long, Long> r = checker.getEmpityRange(args[1], ranges[6][0],
			// ranges[8][1], 512);
			//
			// Utils.toJsonFile(args[1] +"chek", r);
			return;
		} 
		OkServiceManager client = new OkServiceManager(Type.HTTP, proxyHost, port);
		if (commends.get("-u") != null) {				// download link
			client.downloadURL(commends.get("-u"));
			client.startScheduledService();
		} else if (commends.get("-i") != null) {		// list
			client.downloadFromFileAsList(commends.get("-i"));
			client.startScheduledService();
		}
//		else if (commends.get("-m") != null){		// metalink
//			List<String> urls = loadUrls(commends.get("-m"));
//			String filename = urls.get(0);
//			filename = filename.substring(filename.lastIndexOf('/') + 1);
//			System.out.println("\n    " + filename);
//			client.downloadMetaLink(urls, filename);
//			client.startScheduledService();
//
//		}
		Runtime.getRuntime().addShutdownHook(new Thread( ()-> System.out.println("\n\n\n")));
	}


	
	private static void printHelp() {
		StringBuilder builder = new StringBuilder();
		builder.append("-u link\n");
		builder.append("-i file\n");
		//builder.append("-m metalink file\n");
		builder.append("proxy 127.0.0.1:8080\n");
		Log.debug(OKAria.class, "Help", builder.toString());
	}
	
	protected static List<String> loadUrls(String file) {
		
		List<String> list = null;
		try {
			// FileInputStream in = new FileInputStream(file);
			BufferedReader reader = new BufferedReader(new FileReader(file));
			list = new ArrayList<>();
			String url;
			while ((url = reader.readLine()) != null) {
				if (url.startsWith("#") 
						|| url.startsWith(" ") 
						|| url.startsWith("\t") 
						|| url.startsWith("\n") 
						|| url.startsWith("\r\n") )
					continue;
				//System.out.println(url);
				list.add(url.trim());
			}
			reader.close();
		} catch (Exception e) {

		}
		return list;

	}
}

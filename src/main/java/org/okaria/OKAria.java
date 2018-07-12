package org.okaria;

import org.log.Level;
import org.log.Log;
import org.okaria.chrome.ChromeConnection;
import org.okaria.lunch.Argument;
import org.okaria.lunch.TerminalArgument;
import org.okaria.manager.Item;
import org.okaria.okhttp.OkClient;
import org.okaria.okhttp.OkServiceManager;
import org.okaria.range.RangeInfo;

public class OKAria {
	
	
	public static void main(String[] terminals) {
		
//		String[] testChrome = new String[terminals.length +1];
//		testChrome[terminals.length] = "gaogianbgnmoompbfkmgnefkbehmeijh";
//		for (int i = 0; i < terminals.length; i++) {
//			testChrome[i] = terminals[i];
//		}
		
		//String[] args = new String[] {"-http", "127.0.0.1:8999", "http://www.film2movie.us/content/uploads/A.nt_.Ma_.n.And_.Th_.e.Wa_.sp_.2018.hd_.cm_.Film2Movie_US.png" };		
		Argument arguments = null;
		ChromeConnection chrome = null;
		if( ChromeConnection.iSChromStream(terminals)) {
			
			chrome = new ChromeConnection();
			chrome.redChromeMessage(/*629*/);
			arguments = chrome.getArguments();
			//chrome.send(arguments.getDictionary());
			
		}else {
			arguments = new Argument(terminals);
		} 
		
		
		if(arguments.isHelp() ) {
			System.out.println(TerminalArgument.Help());
			return;
		}else if(arguments.isVersion()) {
			System.out.println("okaria version \"0.1.98\"");
			return;
		}
		
		if(arguments.isTries()) {
			OkClient.RETRIES = arguments.getTries();
		}
		
		if(arguments.isConnection()) {
			RangeInfo.RANGE_POOL_NUM = arguments.getNumberOfConnection();
		}
		
		if(arguments.isSavePath()) {
			Item.SAVE_DIR_PATH = arguments.getSavePath();
		}
		
		if(arguments.isMaxItem()) {
			OkServiceManager.MAX_ACTIVE_DOWNLOAD_POOL = arguments.getMaxItem();
		}
		
		
		
		String log_level = arguments.getOrDefault(TerminalArgument.Log, Level.info.name());
		Log.setLogLevel("aria", Level.valueOf(log_level));
		OkServiceManager manager = new OkServiceManager(arguments.getProxy());
		manager.download(arguments);
		manager.startScheduledService();
		
	}

	
}

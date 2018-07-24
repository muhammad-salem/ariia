package org.okaria.setting;

import org.okaria.R;
import org.okaria.lunch.Argument;

public final class AriaProperties {

	public static int RANGE_POOL_NUM = 8;
	
	public static int RETRIES = 0;
	
	public static String Default_SAVE_DIR_PATH = R.CurrentDirectory();
	
	public static int MAX_ACTIVE_DOWNLOAD_POOL = 4;
	
	
	private AriaProperties() {
		
	}


	public static void Config(Argument arguments) {
		if(arguments.isTries()) {
			RETRIES = arguments.getTries();
		}
		
		if(arguments.isConnection()) {
			RANGE_POOL_NUM = arguments.getNumberOfConnection();
		}
		
		if(arguments.isSavePath()) {
			Default_SAVE_DIR_PATH = arguments.getSavePath();
		}
		
		if(arguments.isMaxItem()) {
			MAX_ACTIVE_DOWNLOAD_POOL = arguments.getMaxItem();
		}
	}

}

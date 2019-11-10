package org.okaria.setting;

import org.okaria.lunch.Argument;
import org.okaria.util.R;

public final class Properties {
	private Properties() {}
	
	public static int RETRIES = 0;
	
	public static String Default_SAVE_DIR_PATH = R.CurrentDirectory();
	
	public static int MAX_ACTIVE_DOWNLOAD_POOL = 4;
	
	public static int RANGE_POOL_NUM = 8;
	
	public static int MAX_BUFFER_POOL = 64;		//	8 * 4 * 5


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
		R.MK_DIRS(Default_SAVE_DIR_PATH);
		
		
		if(arguments.isMaxItem()) {
			MAX_ACTIVE_DOWNLOAD_POOL = arguments.getMaxItem();
		}
		
		MAX_BUFFER_POOL = MAX_ACTIVE_DOWNLOAD_POOL * RANGE_POOL_NUM * 2;
	}

	
	
}

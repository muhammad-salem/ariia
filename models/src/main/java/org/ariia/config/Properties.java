package org.ariia.config;

import org.ariia.args.Argument;
import org.ariia.util.R;

public class Properties {
	
	public int retries = 0;
	public String defaultSaveDirectory = R.CurrentDirectory();
	public int maxActiveDownloadPool = 4;
	public int rangePoolNum = 8;
	public int maxBufferPool = 64;		//	8 * 4 * 2

	public Properties() {}
	
	public Properties(Argument arguments) {
		setupConfig(arguments);
	}
	
	public void setupConfig(Argument arguments) {
		if(arguments.isTries()) {
			retries = arguments.getTries();
		}
		if(arguments.isConnection()) {
			rangePoolNum = arguments.getNumberOfConnection();
		}
		if(arguments.isMaxItem()) {
			maxActiveDownloadPool = arguments.getMaxItem();
		}
		maxBufferPool = maxActiveDownloadPool * rangePoolNum * 2;
		if(arguments.isSavePath()) {
			defaultSaveDirectory = arguments.getSavePath();
		}
		R.MK_DIRS(defaultSaveDirectory);
	}
	public int getRetries() {
		return retries;
	}
	public String getDefaultSaveDirectory() {
		return defaultSaveDirectory;
	}
	public int getMaxActiveDownloadPool() {
		return maxActiveDownloadPool;
	}
	public int getRangePoolNum() {
		return rangePoolNum;
	}
	public int getMaxBufferPool() {
		return maxBufferPool;
	}

	
	
}

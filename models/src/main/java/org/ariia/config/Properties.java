package org.ariia.config;

import java.util.Objects;

import org.ariia.args.Argument;
import org.ariia.util.R;

public class Properties {
	
	private int retries = 0;
	private String defaultSaveDirectory = R.CurrentDirectory();
	private int maxActiveDownloadPool = 4;
	private int rangePoolNum = 8;
	private int maxBufferPool = 64;		//	8 * 4 * 2

	public Properties() {}
	
	public Properties(Argument arguments) {
		setupConfig(arguments);
	}
	
	public void updateProperties(Properties properties) {
		properties = Objects.requireNonNull(properties, "properties is null");
		R.MK_DIRS(properties.defaultSaveDirectory);
		this.defaultSaveDirectory = Objects.requireNonNull(properties.defaultSaveDirectory, "defaultSaveDirectory is null");
		this.maxActiveDownloadPool = Objects.requireNonNull(properties.maxActiveDownloadPool, "maxActiveDownloadPool is null");
		this.maxBufferPool = Objects.requireNonNull(properties.maxBufferPool, "maxBufferPool is null");
		this.rangePoolNum = Objects.requireNonNull(properties.rangePoolNum, "rangePoolNum is null");
		this.retries = Objects.requireNonNull(properties.retries, "retries is null");
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
	
	public void setRetries(int retries) {
		this.retries = retries;
	}

	public void setDefaultSaveDirectory(String defaultSaveDirectory) {
		this.defaultSaveDirectory = defaultSaveDirectory;
	}

	public void setMaxActiveDownloadPool(int maxActiveDownloadPool) {
		this.maxActiveDownloadPool = maxActiveDownloadPool;
	}

	public void setRangePoolNum(int rangePoolNum) {
		this.rangePoolNum = rangePoolNum;
	}

	public void setMaxBufferPool(int maxBufferPool) {
		this.maxBufferPool = maxBufferPool;
	}
	
}

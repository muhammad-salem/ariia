package org.okaria.manager;

import java.util.concurrent.Callable;

import org.okaria.okhttp.OkClient;

public interface Manager {

	OkClient getOkClient();
	
	int addDownloadTask(Callable<?> callable);
	Callable<?> remove(int index);
	
	void setMaxConnection(final int max);
	
	void startDownloadTask(int index);
	void stopDownloadTask(int index);
	
	/**
	 * try not read from socket
	 * @param index
	 */
	void puseDownloadTask(int index);
	
	
}

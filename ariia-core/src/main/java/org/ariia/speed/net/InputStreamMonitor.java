package org.ariia.speed.net;

/**
 * @author Youchao Feng
 * @version 1.0
 * @date Sep 21, 2015 2:46 PM
 */
public interface InputStreamMonitor {
	// void onRead(byte[] b);
	void onRead(int len);
}

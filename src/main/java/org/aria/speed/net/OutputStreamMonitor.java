package org.aria.speed.net;

/**
 * @author Youchao Feng
 * @version 1.0
 * @date Sep 21, 2015 2:44 PM
 */
public interface OutputStreamMonitor {
	// void onWrite(byte[] bytes);
	void onWrite(int len);
}

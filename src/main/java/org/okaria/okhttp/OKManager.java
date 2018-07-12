package org.okaria.okhttp;

import java.net.Proxy;

import org.okaria.range.RangeInfoMonitor;
import org.okaria.range.RangeUtils;
import org.okaria.speed.SpeedMonitor;

public class OKManager implements RangeUtils {

	OkConfig config;
	OkClient client;

	SpeedMonitor monitor;


	public OKManager(Proxy.Type type, String proxyHost, int port) {
		monitor = new RangeInfoMonitor();
		config = new OkConfig(CookieJars.CookieJarMap, type, proxyHost, port);
		client = new OkClient(config);
	}

}

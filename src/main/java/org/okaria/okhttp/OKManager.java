package org.okaria.okhttp;

import java.net.Proxy;

import org.okaria.range.RangeUtils;

public class OKManager implements RangeUtils {

	OkConfig config;
	OkClient client;

	SpeedReportMonitor monitor;


	public OKManager(Proxy.Type type, String proxyHost, int port) {
		monitor = new SpeedReportMonitor();
		config = new OkConfig(CookieJars.CookieJarMap, type, proxyHost, port);
		client = new OkClient(config);
	}




}

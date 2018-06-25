package org.okaria.core;

import java.net.URL;

public interface UrlProvider {

	/**
	 * aadd single URL to download start download from current URL
	 * 
	 * @param url
	 *            to be download
	 */
	void addUrl(String url);

	void addUrl(URL url);

	/**
	 * suppose you have one file on Internet with different / multiple links will
	 * download this file from these different urls in parallel to each others
	 * 
	 * @param url
	 *            url array to file on Internet
	 */
	void addUrl(String... url);

	void addUrl(URL... url);

	/**
	 * download multiple files not the same one
	 * 
	 * @param url
	 */
	void downloadFiles(String... url);

	void downloadFiles(URL... url);

}

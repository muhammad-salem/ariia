package org.okaria.okhttp;

import okhttp3.HttpUrl;

public interface CheckRedirect {

	void updateRedirect(HttpUrl url);

	void setProbrites(int id, HttpUrl url);

	void updateRedirect();

}
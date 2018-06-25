package org.okaria.okhttp;

import java.util.ArrayList;
import java.util.List;

import okhttp3.HttpUrl;
import okhttp3.Response;

public class RedirectUrl {

	enum LinkState {
		NeedCheck, Default, Redirect, NetworkError;
	}
	
	public static void updateRedirectUrl(List<RedirectUrl> list) {
		for (RedirectUrl redirectUrl : list) {
			redirectUrl.updateRedirectUrl();
		}
	}

	public static List<HttpUrl> toHttpUrl(List<RedirectUrl> list) {
		List<HttpUrl> httpUrls = new ArrayList<HttpUrl>();
		for (RedirectUrl redirectUrl : list) {
			redirectUrl.updateRedirectUrl();
			httpUrls.add(redirectUrl.getUrl());
		}
		return httpUrls;
	}


	private HttpUrl url;
	private LinkState state = LinkState.NeedCheck;
//	OkConfig config;
	
	ClientRequest client;
	//Response response;

	public RedirectUrl(HttpUrl url) {
		this.url = url;
		this.client = new OkClient(OkConfig.LessDirect); 
	}
	
	public RedirectUrl(Response response) {
		url = response.request().url();
		state = LinkState.Redirect;
	}
	public RedirectUrl(ClientRequest client, HttpUrl url) {
		this.url = url;
		this.client = client;
	}

	public void updateRedirectUrl() {
		try {
			Response response = client.head(url);
			HttpUrl redirectUrl = response.request().url();
			if (url.equals(redirectUrl)) {
				state = LinkState.Default;
			} else {
				state = LinkState.Redirect;
				url = redirectUrl;
			}
			response.close();
		} catch (Exception e) {
			state = LinkState.NetworkError;
		}
	}

	public HttpUrl getUrl() {
		return url;
	}

	public void updateRedirectUrl(HttpUrl url) {
		this.url = url;
		updateRedirectUrl();
	}

	public synchronized LinkState getState() {
		return state;
	}

	public void setState(LinkState state) {
		this.state = state;
	}

}

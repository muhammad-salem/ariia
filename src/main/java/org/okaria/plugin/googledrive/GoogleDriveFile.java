package org.okaria.plugin.googledrive;

import java.util.List;
import java.util.Map;

import org.log.concurrent.Log;

import okhttp3.Cookie;
import okhttp3.Headers;

// wget -dc --load-cookies /tmp/cookies.txt "https://docs.google.com/uc?export=download&confirm=$(wget --quiet --save-cookies /tmp/cookies.txt --keep-session-cookies --no-check-certificate 'https://docs.google.com/uc?export=download&id=FILEID' -O- | sed -rn 's/.*confirm=([0-9A-Za-z_]+).*/\1\n/p')&id=FILEID" -O FILENAME && rm -rf /tmp/cookies.txt


public class GoogleDriveFile {

	protected String fileID;
	protected String confirm;
	protected List<Cookie> cookies;
	
	public GoogleDriveFile(String googleDriveID) {
		this.fileID = googleDriveID;
		if(googleDriveID.startsWith("http")) {
			int i = googleDriveID.indexOf("id=") + 3;
			String id =  googleDriveID.substring(i);
			if(id.contains("&export=")) {
				id = id.substring(0, id.indexOf('&'));
			}
			this.fileID = id;
		}
	}
	
	public String confirm() {
		return confirm;
	}
	public String fileID() {
		return fileID;
	}
	
	public String setupRequestUrl() {
		return "https://docs.google.com/uc?export=download&id="+fileID;
	}
	
	public String url() {
		return "https://docs.google.com/uc?export=download&confirm=" + confirm + "&id=" + fileID;
	}
	
	public void setCookies(List<Cookie> cookies) {
		this.cookies = cookies;
	}
	public void confirm(String confirm) {
		this.confirm = confirm;
	}
	
	public void confirm(Map<String, String> headers) {
		this.confirm = headers.get("confirm");
	}

	public void confirm(Headers headers) {
		Log.debug(getClass(), "confirm headers", headers.toString());
		this.confirm = headers.get("confirm");
	}
	
	
	
	
}

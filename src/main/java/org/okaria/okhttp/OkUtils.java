package org.okaria.okhttp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import okhttp3.Cookie;

public class OkUtils {

	public static List<String> readLines(String filePath) {
		List<String> lines = new LinkedList<>();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(filePath));
			String textCookie;
			while ((textCookie = reader.readLine()) != null) {
				lines.add(textCookie);
			}
			reader.close();
		} catch (IOException e) {
			
		}
		return lines;
	}

	public static List<Cookie> getCookies(String cookiePath) {
		List<String> txtCookies = readLines(cookiePath);
		if (txtCookies == null)
			return Collections.emptyList();

		List<Cookie> listCookie = new ArrayList<>();
		Cookie.Builder builder;
		// 0 1 2 3 4 5 6
		// .domain.com HTTPONLY / Secure ExpiryDate name value
		// .ubuntu.com TRUE / false 77777777 _ga GA1.2.86547
		for (String cookie : txtCookies) {

			builder = new Cookie.Builder();
			String[] str = cookie.split("\t");
			builder.domain(str[0]);
			if (Boolean.valueOf(str[1]))
				builder.httpOnly();
			builder.path(str[2]);
			if (Boolean.valueOf(str[3]))
				builder.secure();
			builder.expiresAt(Long.valueOf(str[4]));
			builder.name(str[5]);
			builder.value(str[6]);

			listCookie.add(builder.build());
		}
		return listCookie;
	}

	/**
	 * check if the file exists, true will change the file name of the file false
	 * keep unchanged
	 * 
	 * @return true if file name had been changed
	 */
	public boolean checkExists(String filePath) {
		File file = new File(filePath);
		if (file.exists()) {
			return true;
		}
		return false;
	}

	public File solveName(File file) {
		return getNewName(file, 0);
	}

	public File getNewName(File file, int x) {
		String name = file.getName();
		String exetenation = name.substring(name.lastIndexOf('.'));
		name = name.substring(0, name.lastIndexOf('.'));

		File newFile = new File(file.getParent() + File.separator + name + "_" + x + "." + exetenation);
		if (newFile.exists()) {
			return getNewName(file, ++x);
		}
		return newFile;
	}

	public String getNameFor(String filePath, int overload) {
		String name = filePath.substring(filePath.lastIndexOf('/'));
		String exetenation = name.substring(name.lastIndexOf('.'));
		name = name.substring(0, name.lastIndexOf('.'));
		String file = filePath.substring(0, name.lastIndexOf('/')) + File.separator + name + "_" + overload + "."
				+ exetenation;
		if (checkExists(file)) {
			return getNameFor(filePath, ++overload);
		}
		return file;
	}

	public long getContentLengthFromContentRange(String contentRange) {
		int x = contentRange.indexOf("/");
		String length = contentRange.substring(x + 1, contentRange.length());
		long l = Long.valueOf(length);
		return l;
	}

}

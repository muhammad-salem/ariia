package org.ariia.core.api;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

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

	
	public static String getFileName(String url, String defaultName) {
	    try {
	        return getFileName(url);
	    } catch (StringIndexOutOfBoundsException e) {
	        return defaultName;
	    } 
	}
	public static String getFileName(String url) {
		URL url2 = null;
		try {
			url2 = new URL(url);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		String filename = url2.getFile();
		int lastslach = filename.lastIndexOf('/')+1;
		if(lastslach != filename.length())
			filename = filename.substring(lastslach);
		else {
			lastslach -= 2;
			filename = filename.substring(filename.lastIndexOf('/', lastslach)+1, lastslach);
		}
		
		lastslach = filename.lastIndexOf('=')+1;
		filename = filename.substring(lastslach);
		
		return filename;
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

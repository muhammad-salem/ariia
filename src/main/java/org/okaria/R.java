package org.okaria;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.log.Log;

public class R {

	private static char sprtr = File.separatorChar;
	public static String app_name = "okaria";
	public static String code_name = "و";
	public static String UserHome = System.getProperty("user.home");

	public static String DownloadsPath = UserHome + sprtr + "Downloads" + sprtr + app_name + sprtr;

	public static String ConfigPath = UserHome + sprtr + ".config" + sprtr + app_name + sprtr;

	public static String CachePath = UserHome + sprtr + ".cache" + sprtr + app_name + sprtr;

	public static String ConfigJsonFile;
	public static String LockFile = CachePath + sprtr + "lock";
	public static String TempDir = "/tmp/";

	static {
		String cacheFolder = "";
		if (PlatformUtil.isWin7OrLater()) {
			cacheFolder = UserHome + sprtr + "AppData" + sprtr + "Roaming" + sprtr + app_name + sprtr;
			CachePath = cacheFolder + "cache" + sprtr;
			ConfigPath = cacheFolder + "config" + sprtr;
		} else if (PlatformUtil.isWindows()) {
			cacheFolder = UserHome + sprtr + "Application Data" + sprtr + app_name + sprtr;
			TempDir = cacheFolder + "temp";
			CachePath = cacheFolder + "cache" + sprtr;
			ConfigPath = cacheFolder + "config" + sprtr;
		} else if (PlatformUtil.isMac()) { // Library/Application Support/
			cacheFolder = UserHome + sprtr + "Library" + sprtr + "Application Support" + sprtr + app_name + sprtr;
			TempDir = UserHome + "/Library/Caches/TemporaryItems/";
			CachePath = cacheFolder + "cache" + sprtr;
			ConfigPath = cacheFolder + "config" + sprtr;
		}
		LockFile = CachePath + sprtr + "lock";
		ConfigJsonFile = ConfigPath + "config.json";
	}

	/* ========================================================================= */
	/* ========================== Resources Methods Init ======================= */

	/**
	 * use <System.currentTimeMillis()> as timeMapId
	 * 
	 * @return string represent a
	 */

	public static String getConfigDirectory() {
		return ConfigPath;
	}
	public static String getConfigPath(String filename) {
		filename = ConfigPath + filename;
		mkParentDir(filename);
		return filename;
	}
	
	public static File getConfigFile(String filename) {
		return new File(getConfigPath(filename));
	}
	
	public static String getCompleteFile(String filename) {
		return getCompleteDir() + filename;
	}
	
	public static String getNewDownload(String name) {
		return ConfigPath + "download" + sprtr + name;
	}
	public static String getCompleteDir() {
		return ConfigPath + "complete" + sprtr;
	}
	
	public static String NewCacheDir() {
		return getNewCacheDir(System.currentTimeMillis());
	}
	public static String getNewCacheDir(long timeMapId) {
		return CachePath + timeMapId + sprtr;
	}
	
	public static String getCacheFile(String filename) {
		return CachePath + filename;
	}
	
	
	public static String getNewCacheDir(String dirname) {
		return CachePath + dirname + sprtr;
	}
	public static String NewCacheFile(String filename) {
		return CreateCacheFile(System.currentTimeMillis(), filename);
	}
	public static String CreateCacheFile(long timeMapId, String filename) {
		filename = CachePath + timeMapId + "-" + filename;
		mkParentDir(filename);
		return filename;
	}
	
	public static String getDownloadsFile() {
		return DownloadsPath;
	}
	public static String getDownloadsFile(String filename) {
		filename = DownloadsPath + filename;
		mkParentDir(filename);
		return filename;
	}
	public static void InitDirs() {

		File creator = new File(ConfigPath);
		mkdir(creator);
		creator = new File(CachePath);
		mkdir(creator);
	}

	public static void mkdir(File creator) {
		try {
			creator.mkdirs();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void mkParentDir(String creator) {
		mkParentDir(new File(creator));
	}

	public static void mkParentDir(File creator) {
		try {
			creator.getParentFile().mkdirs();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/* ========================================================================= */
	/* ======================== Resources Methods onSave ======================= */

	public static void DeleteTemp() {
		File temp = new File(ConfigPath);
		try {
			delete(temp);
		} catch (IOException e) {
			System.out.println("Can't delete Files");
			e.printStackTrace();
		}
	}

	public static void delete(File file) throws IOException {

		if (file.isDirectory()) {

			// directory is empty, then delete it
			if (file.list().length == 0) {
				file.delete();
				// System.out.println("Delete Directory : "+ file.getAbsolutePath());
			} else {

				// list all the directory contents
				String files[] = file.list();

				for (String temp : files) {
					// construct the file structure
					File fileDelete = new File(file, temp);

					// recursive delete
					delete(fileDelete);
				}

				// check the directory again, if empty then delete it
				if (file.list().length == 0) {
					file.delete();
				}
			}

		} else {
			// if file, then delete it
			file.delete();
		}
	}

	public static void openProcess(String str) {
		List<String> list = new ArrayList<String>();

		if (PlatformUtil.isLinux()) {
			list.add("xdg-open");
			list.add(str);
		} else if (PlatformUtil.isWindows()) {
			list.add("start");
			list.add(str);
		} else if (PlatformUtil.isMac()) {
			list.add("open");
			list.add(str);
		}
		ProcessBuilder builder = new ProcessBuilder(list);
		try {
			builder.start();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	public static String CurrentDirectory() {
		return  System.getProperty("user.dir") + File.separatorChar;	
	}
	
	/**
	 * 
	 * @return
	 */
	public static String getCurrentDirectory() {
		String path =  System.getProperty("user.dir") + File.separatorChar;
		System.out.println(path);
		try {
			System.getSecurityManager().checkWrite(path+"test");
			Log.info(R.class, "set default directory to [" + path + " ].");
			return path;
		} catch (Exception e) {
			e.printStackTrace();
			Log.info(R.class, "set default directory to [" + getDownloadsFile() + " ].");
			return getDownloadsFile();
		}
		
	}

}

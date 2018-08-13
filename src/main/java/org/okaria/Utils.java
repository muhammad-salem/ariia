package org.okaria;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;

import org.terminal.Ansi;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

public class Utils {
	
	public final static Ansi ANSI = new Ansi();

	private static DecimalFormat decimalFormat = new DecimalFormat("0.000");
	private static double kbyte = 1024;
	
	/**
	 * concate " " to string
	 * 
	 * @param str
	 *            the string to concate with " " wigth space
	 * @param count
	 *            the new length
	 * @return null if given string length greater than count
	 */
	public static String getStringWidth(String str, int count) {
		if (str.length() > count)
			return str;
		for (int i = str.length(); i < count; i++) {
			str += " ";
		}
		return str;
	}

	public static String getStringMiddle(String str, int count, char ch) {
		if (str.length() > count)
			return str;
		int apend = (count - str.length()) / 2;
		boolean reminder = ((count - str.length()) % 2) == 1;
		if (reminder)
			str = ch + str;
		for (int i = 0; i < apend; i++) {
			str = ch + str + ch;
		}
		return str;
	}

	public static String getStringMiddle(String str, int count) {
		return getStringMiddle(str, count, ' ');
	}

	public static String getRightString(String str, int count) {
		if (str.length() > count)
			return str;
		char[] cs = new char[count - str.length()];
		Arrays.fill(cs, ' ');
		StringBuilder builder = new StringBuilder();
		builder.append(cs);
		builder.append(str);
//		for (int i = str.length(); i < count; i++) {
//			str = " " + str;
//		}
		return builder.toString();
	}

	
	public static String middleMaxLength(final String string, int count) {
		if (string.length() > count)
			return string.substring(0, count-2).concat("..");
		char[] cs = new char[(count - string.length())/2];
		boolean reminder = ((count - string.length()) % 2) == 1;
		Arrays.fill(cs, ' ');
		StringBuilder builder = new StringBuilder();
		builder.append(cs);
		if(reminder)builder.append(' ');
		builder.append(string);
		builder.append(cs);
		return builder.toString();
	}
	
	
	public static String GetWidthBracts(String title, int l) {
		title = getStringWidth(title, l);
		title = '[' + title + ']';
		return title;
	}

	public static String GetWidthBractsRight(String title, int l) {
		title = getRightString(title, l);
		title = '[' + title + ']';
		return title;
	}

	public static String GetWidthBractsMiddle(String title, int l) {
		title = getStringMiddle(title, l);
		title = '[' + title + ']';
		return title;
	}

//	protected static Gson gsonPretty = getGson(true);
	private static Gson gson = getGson(true);

	public static Gson getGson(boolean pretty) {
		GsonBuilder builder = new GsonBuilder();
		if (pretty)
			builder.setPrettyPrinting();
		return builder.create();
	}

	public static String toJson(Object object) {
		return gson.toJson(object);
	}

	public static <T> T json(String json, Class<T> classOfT) throws JsonSyntaxException{
		return gson.fromJson(json, classOfT);
	}
	public static String toJson(Object object, Type type) {
		return gson.toJson(object, type);
	}

	public static boolean toJsonFile(String file, Object object) {
		return writeJson(file, toJson(object));
	}

	public static boolean writeJson(String filename, String json) {
		try {
			File file = new File(filename);
			file.getParentFile().mkdirs();
			FileWriter writer = new FileWriter(filename);
			writer.write(json);
			writer.close();
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	public static boolean toJsonFile(String filename, Object object, Type type) {
		return writeJson(filename, toJson(object, type));
	}

	public static boolean toJsonFile(File file, Object object) {
		return writeJson(file, toJson(object));
	}

	public static boolean writeJson(File file, String json) {
		try {
			if (!file.getParentFile().exists()) {
				file.getParentFile().mkdirs();
			}
			FileWriter writer = new FileWriter(file);
			writer.write(json);
			writer.flush();
			writer.close();
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	public static <T> T fromJson(File file, Class<T> classT) {
		return fromJson(file.getPath(), classT);
	}

	/**
	 *
	 * @param <T>
	 *            the type of the desired object
	 * @param file
	 *            file path to read from.
	 * @throws FileNotFoundException
	 */
	public static <T> T fromJson(Class<T> classT, String file) throws FileNotFoundException {
		BufferedReader reader = new BufferedReader(new FileReader(file));
		return gson.fromJson(reader, classT);
	}

	/**
	 *
	 * @param <T>
	 *            the type of the desired object
	 * @param file
	 *            file path to read from.
	 */
	public static <T> T fromJson(String file, Class<T> classT) {
		T t = null;
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			t = gson.fromJson(reader, classT);
		} catch (Exception e) {
			return null;
		}
		return t;
	}

	public static <T> T fromJson(String file, Class<T> classT, Type type) {
		T t = null;
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			t = gson.fromJson(reader, type);
			// System.out.println(url);
		} catch (Exception e) {
			System.err.println("no file found " + file);
			return null;
		}
		return t;
	}
	
	public static <T> T fromJsonGenric(String file, Class<T> classT) {
		T t = null;
		Type typeOfT = new TypeToken<T>(){}.getType();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			t = gson.fromJson(reader, typeOfT);
		} catch (Exception e) {
			System.err.println("no file found " + file);
			return null;
		}
		
		return t;
	}
	
	public static <T> List<T> jsonList( Class<T> classT, String file) {
		List<T> t = null;
		Type typeOfT = new TypeToken<List<T>>(){}.getType();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			t = gson.fromJson(reader, typeOfT);
		} catch (Exception e) {
			System.err.println("no file found " + file);
			return null;
		}
		
		return t;
	}

	/***
	 * return true only if it is a true URL
	 * 
	 * @param fileURL
	 *            {@link String} that represent that URL to test
	 * @return {@link Boolean}
	 */
	public static boolean verifyURL(String fileURL) {

		String str = fileURL.toLowerCase();
		// Allow FTP, HTTP and HTTPS URLs.
		if (str.startsWith("http://") || str.startsWith("https://") || str.startsWith("ftp://")) {
			// Verify format of URL.
			// URL verifiedUrl = null;
			try {
				/* verifiedUrl = */ new URL(fileURL);
			} catch (MalformedURLException e) {
				System.err.println("not valid url");
				return false;
			}

			/*
			 * // Make sure URL specifies a file. if (verifiedUrl.getFile().length() < 9) {
			 * return false; }
			 */
			// System.out.println(verifiedUrl.toString() + " >>");
		} else {
			return false;
		}
		return true;
	}

	public static String getStringFromInputStream(InputStream stream) {
		String string = "";
		BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

		try {
			String str = "";
			while ((str = reader.readLine()) != null) {
				string += str + "\n";
			}

			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return string;
	}

	/**
	 * @param length
	 *            file path to read from.
	 */
	public static String sizeLengthFormate0_0(double length) {
		return sizeLengthFormate(length, 1);
	}
	public static String sizeLengthFormate0_00(double length) {
		return sizeLengthFormate(length, 2);
	}
	
	public static String sizeLengthFormate(double length, int percentNum) {
		String hrSize = "";
		String percent = "0.";
		for (int i = 0; i < percentNum; i++) {
			percent += '0';
		}
		DecimalFormat dec = new DecimalFormat(percent);
		

		// use (10^3) instead of (2^10)
//		double kbyte = 1000.0; // = 1024;
		double b = length;
		double k = b / kbyte;
		double m = k / kbyte;
		double g = m / kbyte;
		double t = g / kbyte;
		

		if (t >= 1) {
			hrSize = dec.format(t).concat(" TB");
		} else if (g >= 1) {
			hrSize = dec.format(g).concat(" GB");
		} else if (m >= 1) {
			hrSize = dec.format(m).concat(" MB");
		} else if (k >= 1) {
			hrSize = dec.format(k).concat(" KB");
		} else {
			hrSize = dec.format(b).concat(" Bytes");
		}

		return hrSize;
	}

	/**
	 * @param length
	 *            file path to read from.
	 */
	public static String getLengthFor(double length) {
		return fileLengthUnite(length);
	}

	/**
	 * @param length
	 *            file path to read from.
	 */
	public static String fileLengthUnite(double length) {
		String hrSize = "";
		//DecimalFormat dec = new DecimalFormat("0.00");

		// use (10^3) instead of (2^10)
//		double kbyte = 1000.0; // = 1024;
		double b = length;
		double k = b / kbyte;
		double m = k / kbyte;
		double g = m / kbyte;
		double t = g / kbyte;

		if (t >= 1) {
			hrSize = decimalFormat.format(t).concat(" TB");
		} else if (g >= 1) {
			hrSize = decimalFormat.format(g).concat(" GB");
		} else if (m >= 1) {
			hrSize = decimalFormat.format(m).concat(" MB");
		} else if (k >= 1) {
			hrSize = decimalFormat.format(k).concat(" KB");
		} else {
			hrSize = decimalFormat.format(b).concat(" Bytes");
		}

		return hrSize;
	}

	public static double fileLength(long length) {
		// use (10^3) instead of (2^10)
//		double kbyte = 1000.0; // = 1024;
		double b = length;
		double k = b / kbyte;
		double m = k / kbyte;
		double g = m / kbyte;
		double t = g / kbyte;

		if (t > 1) {
			return t;
		} else if (g > 1) {
			return g;
		} else if (m > 1) {
			return m;
		} else if (k > 1) {
			return k;
		}
		return length;
	}

	public static int gesslChunkesNum(long length) {
		// double k = length / 1024.0;

		if (length <= 0) {
			return 1;
		}
		// use (10^3) instead of (2^10)
//		double kbyte = 1000.0; // = 1024;
		double b = length;
		double k = b / kbyte;
		double m = k / kbyte;
		double g = m / kbyte;
		double t = g / kbyte;

		if (t > 1) {
			return 32;
		} else if (g > 1) {
			return 16;
		} else if (m >= 700) {
			return 10;
		} else if (m < 700 && m >= 500) {
			return 8;
		} else if (m < 500 && m >= 250) {
			return 6;
		} else if (m < 250 && m >= 200) {
			return 5;
		} else if (m < 200 && m >= 100) {
			return 4;
		} else if (m < 100 && m >= 10) {
			return 3;
		} else if (m < 10 && m >= 4) {
			return 2;
		}
		/*
		 * else if (m < 5 && m >= 1 ) { return 1; } else if (k > 1) { return 1; }
		 */

		return 1;
	}

	static DecimalFormat decPercentage = new DecimalFormat("#0.00# %");
	public static String percent(long downloaded, long size) {
//		System.out.println(downloaded +", "+ size);
		return decPercentage.format(( (float) (downloaded+1) / (size+1) ));
	}

	public static double percent(double downloaded, double size) {
		return downloaded / size;
	}

	public static String fileLengthUnite(double progress, final long length) {
		return fileLengthUnite(progress * length);
	}

	public static String getTime(long date) {
		long rmnd = 0;
		long ss = date % 60;
		rmnd = date / 60; // minute
		long mm = rmnd % 60;
		rmnd /= 60; // hours
		long hh = rmnd % 60;
		rmnd /= 60; // days
		long dd = rmnd % 24;
		return dd + ":" + hh + ":" + mm + ":" + ss;
	}

	public static boolean isAnyNetWorkInterfaceUp() {
		try {
			Enumeration<NetworkInterface> networks = NetworkInterface.getNetworkInterfaces();
			while (networks.hasMoreElements()) {
				NetworkInterface networkInterface = networks.nextElement();
				if (networkInterface.isUp()) {
					if (networkInterface.isLoopback()) {
						continue;
					}
					if (networkInterface.getName().contains("pan")) {
						continue;
					}
					// System.out.println(networkInterface);
					return true;
				}
			}
		} catch (SocketException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static <T> InputStream getResourceAsStream(Class<T> class1, String res) {
		return class1.getResourceAsStream(res);
	}

	public static <T> URL getResourcem(Class<T> class1, String res) {
		return class1.getResource(res);
	}

	public static ArrayList<long[][]> fromJson(File file, Type type) {
		ArrayList<long[][]> t = null;
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			t = gson.fromJson(reader, type);
			// System.out.println(url);
		} catch (Exception e) {
			//System.err.println("no file found " + file);
			return null;
		}
		return t;
	}

	

	public static  String timeformate(final long millsecond) {
		long hh, mm, ss;
		hh = ((millsecond / 60) / 60) % 60;
		mm = (millsecond / 60) % 60;
		ss = millsecond % 60;

		String formate = (hh > 9 ? hh + "" : "0" + hh);
		formate += ':' + (mm > 9 ? mm + "" : "0" + mm);
		formate += ':' + (ss > 9 ? ss + "" : "0" + ((ss >=0 )? ss : "0"));

		return formate;
	}
	
	public static void Copy(Path source, File destination){
		try {
			Files.copy(source, new FileOutputStream(destination));
			
		} catch (IOException e) {
			
		}
	}
	
}

package org.ariia.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.*;

public class Utils {

    static DecimalFormat decPercentage = new DecimalFormat("#0.00# %");
    private static DecimalFormat decimalFormat = new DecimalFormat("0.000");
    /**
     * Base 2 (1024 bytes)
     * The kibibyte is a multiple of the unit byte for digital information.
     * The binary prefix kibi means 2^10, or 1024; therefore, 1 kibibyte is 1024 bytes.
     * The unit symbol for the kibibyte is KiB.
     */
    private static double kibibyte = 1024;
    /**
     * Base 10 (1000 bytes)
     * The prefix kilo means 1000 (103); therefore, one kilobyte is 1000 bytes.
     * The unit symbol is kB.
     */
    private static double kilobyte = 1000;
    //	protected static Gson gsonPretty = getGson(true);
    private static Gson gson = getGson(false);

    public static String unitLength(double length) {
        return unitLength(length, true, true);
    }

    public static String unitLength(double length, boolean isBinary, boolean isByte) {

        double kilo = isBinary ? kibibyte : kilobyte;
        double k = length / kilo;
        if (k < 1) {
            return length + (isBinary ? " B" : " b");
        }
        double m = k / kilo;
        if (m < 1) {
            if (isByte) {
                return decimalFormat.format(k).concat(" KB");
            } else {
                return decimalFormat.format(k * 8).concat(" Kb");
            }
        }
        double g = m / kilo;
        if (g < 1) {
            if (isByte) {
                return decimalFormat.format(m).concat(" MB");
            } else {
                return decimalFormat.format(m * 8).concat(" Mb");
            }
        }
        double t = g / kilo;
        if (t < 1) {
            if (isByte) {
                return decimalFormat.format(g).concat(" GB");
            } else {
                return decimalFormat.format(g * 8).concat(" Gb");
            }
        } else {
            if (isByte) {
                return decimalFormat.format(t).concat(" TB");
            } else {
                return decimalFormat.format(t * 8).concat(" Tb");
            }
        }
    }

    public static String percent(long downloaded, long size) {
        return decPercentage.format(((float) (downloaded + 1) / (size + 1)));
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

    /**
     * concate " " to string
     *
     * @param str   the string to concate with " " wigth space
     * @param count the new length
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
            return string.substring(0, count - 2).concat("..");
        char[] cs = new char[(count - string.length()) / 2];
        boolean reminder = ((count - string.length()) % 2) == 1;
        Arrays.fill(cs, ' ');
        StringBuilder builder = new StringBuilder();
        builder.append(cs);
        if (reminder) builder.append(' ');
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

    public static Gson getGson(boolean pretty) {
        GsonBuilder builder = new GsonBuilder();
        if (pretty)
            builder.setPrettyPrinting();
        return builder.create();
    }

    public static String toJson(Object object) {
        return gson.toJson(object);
    }

    public static <T> T json(String json, Class<T> classOfT) throws JsonSyntaxException {
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
     * @param <T>  the type of the desired object
     * @param file file path to read from.
     * @throws FileNotFoundException
     */
    public static <T> T fromJson(Class<T> classT, String file) throws FileNotFoundException {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        return gson.fromJson(reader, classT);
    }

    /**
     * @param <T>  the type of the desired object
     * @param file file path to read from.
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
        Type typeOfT = new TypeToken<T>() {
        }.getType();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            t = gson.fromJson(reader, typeOfT);
        } catch (Exception e) {
            System.err.println("no file found " + file);
            return null;
        }

        return t;
    }

    public static <T> List<T> jsonList(Class<T> classT, String file) {
        List<T> t = null;
        Type typeOfT = new TypeToken<ArrayList<T>>() {
        }.getType();
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
                /* verifiedUrl = */
                new URL(fileURL);
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


    public static String timeformate(final long millsecond) {
        long hh, mm, ss;
        hh = ((millsecond / 60) / 60) % 60;
        mm = (millsecond / 60) % 60;
        ss = millsecond % 60;

        String formate = (hh > 9 ? hh + "" : "0" + hh);
        formate += ':' + (mm > 9 ? mm + "" : "0" + mm);
        formate += ':' + (ss > 9 ? ss + "" : "0" + ((ss >= 0) ? ss : "0"));

        return formate;
    }

    public static void Copy(Path source, File destination) {
        try {
            Files.copy(source, new FileOutputStream(destination));

        } catch (IOException e) {

        }
    }

    public static List<String> readLines(String filePath) {
        List<String> txtCookies = new LinkedList<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            String text;
            while ((text = reader.readLine()) != null) {
                txtCookies.add(text);
            }
            reader.close();
            return txtCookies;
        } catch (IOException e) {
            return Collections.emptyList();
        }
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
        int lastslach = filename.lastIndexOf('/') + 1;
        if (lastslach != filename.length())
            filename = filename.substring(lastslach);
        else {
            lastslach -= 2;
            filename = filename.substring(filename.lastIndexOf('/', lastslach) + 1, lastslach);
        }

        lastslach = filename.lastIndexOf('=') + 1;
        filename = filename.substring(lastslach);

        return filename;
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

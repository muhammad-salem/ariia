/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.okaria.chrome;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.URL;
import java.nio.file.Paths;

import org.log.concurrent.Log;
import org.okaria.lunch.Argument;
import org.okaria.util.PlatformUtil;
import org.okaria.util.R;
import org.okaria.util.Utils;


/**
 *
 * @author moha
 */
public class ChromeConnection {
	
	public static String extensions_id = "gaogianbgnmoompbfkmgnefkbehmeijh";
	
	public static void CheckNetiveMessage() {
		if (PlatformUtil.isMac()) {
			try {
				URL source = ChromeConnection.class
						.getResource("osx/org.okaria.chrome.json");
				File parent = new File(R.ConfigPath).getParentFile()
						.getParentFile();
				// /Application Support/Google/Chrome/NativeMessagingHosts/org.javafx.ariafx.json
				parent = new File(parent, "Google/Chrome/NativeMessagingHosts/");
				parent.mkdir();
				File destination = new File(parent, "org.okaria.chrome.json");
				
				Utils.Copy(Paths.get(source.toURI()), destination);
			} catch (Exception e) {
				Log.info(ChromeConnection.class, "Google chrome : NativeMessagingHosts : ", e.getMessage());
			}
		}
	}

	
	public static boolean iSChromStream(String... args) {
		for (String string : args) {
			if(string.contains(extensions_id)) {
				return true;
			}
		}
		return false;
	}
	protected String json;
	protected ChromeMessage message;
	
	protected ChromeTerminal terminal;
	protected InputStream in = System.in;
	protected PrintStream out = System.out;
	
	public void setupTerminal() {
		terminal = new ChromeTerminal();
		terminal.defaultLifeCycle();
		
		in = terminal.getSystemInput();
		out = new PrintStream(terminal.getSystemOutput());
	}
	
	public void resetIO() {
		terminal.setDefaultSystemIO();
	}
	
	public String getJson() {
		return json;
	}
	
	public ChromeMessage getMessage() {
		return message;
	}
	
	public void redChromeMessage() {
		json = read();
		message = ChromeMessage.CreateMessage(json);
		System.out.println(json);
	}
	
	public void redChromeMessage(int length) {
		json = readMessage(length);
		message = ChromeMessage.CreateMessage(json);
		System.out.println(json);
	}
	
	
	public Argument getArguments() {
		return message.toArguments();
	}
	
	

	protected void sendLength(int len) throws IOException {
		byte[] bs = getBytes(len);
		
		out.write(bs);
	}

	protected void sendMessage(String msg) {
		try {
			out.write(msg.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void send(String message) {
		sendJson(Utils.toJson(message));
	}
	
	public void send(Object message) {
		sendJson(Utils.toJson(message));
	}
	
	public void sendJson(String json) {
		try {
			sendLength(json.length());
			sendMessage(json);
		} catch (IOException ex) {
			{
				// what you want to do if exception happen
			}
		}
	}

	protected int readLength() throws IOException {
		byte[] bs = new byte[4];
		int i = in.read(bs);
		if (i == 4)
			return getLength(bs);
		else
			return -1;
	}

	
	public String readMessage(int length) {
		String msg = "";
		try {
			
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			char[] cs = new char[length];
			br.read(cs);
			msg = new String(cs);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return msg;
	}

	protected String read() {
		int length;
		try {
			length = readLength();
			return readMessage(length).trim();
		} catch (IOException ex) {

		}
		return "";
	}

	public static byte[] getBytes(int length) {
		byte[] bytes = new byte[4];
		bytes[0] = (byte) (length & 0xFF);
		bytes[1] = (byte) ((length >> 8) & 0xFF);
		bytes[2] = (byte) ((length >> 16) & 0xFF);
		bytes[3] = (byte) ((length >> 24) & 0xFF);
		return bytes;
	}

	public static int getLength(byte[] bytes) {
		return (bytes[3] << 24) & 0xff000000 | (bytes[2] << 16) & 0x00ff0000
				| (bytes[1] << 8) & 0x0000ff00 | (bytes[0]) & 0x000000ff;
	}

	public static int getLength(char[] bytes) {
		return (bytes[3] << 24) & 0xff000000 | (bytes[2] << 16) & 0x00ff0000
				| (bytes[1] << 8) & 0x0000ff00 | (bytes[0]) & 0x000000ff;
	}

}

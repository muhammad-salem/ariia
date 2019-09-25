package org.okaria.okhttp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import okhttp3.Cookie;
import okhttp3.HttpUrl;

import org.junit.Test;

public class OkUtilsTest {

    @Test
	public void testFilename() {
		System.out.println("https://download-app.musixmatch.com/");
		System.out.println(org.okaria.okhttp.OkUtils.Filename("https://download-app.musixmatch.com/"));
	}

}

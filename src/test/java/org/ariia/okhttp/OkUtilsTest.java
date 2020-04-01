package org.ariia.okhttp;

import org.junit.Test;

public class OkUtilsTest {

    @Test
	public void testFilename() {
		System.out.println("https://download-app.musixmatch.com/");
		System.out.println(org.ariia.okhttp.OkUtils.Filename("https://download-app.musixmatch.com/"));
	}

}

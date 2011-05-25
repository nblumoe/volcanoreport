package com.nbapps.volcanoreport;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import org.apache.http.util.ByteArrayBuffer;

import android.util.Log;


public class ListDownloader {
	
	public void DownloadFromUrl(String fileURL, String fileName) {
		try {
			URL url = new URL(fileURL);
			File file = new File(fileName);
			
			
            Log.d("ImageManager", "download begining");
            Log.d("ImageManager", "download url:" + url);
            Log.d("ImageManager", "downloaded file name:" + fileName);

			
			URLConnection urlConnection = url.openConnection();
			/*
			 * Create Input Streams
			 */
			InputStream inputStream = urlConnection.getInputStream();
			BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
			
			ByteArrayBuffer byteArrayBuffer = new ByteArrayBuffer(50);
			int current = 0;
			while ((current = bufferedInputStream.read())!=-1) {
				byteArrayBuffer.append((byte) current);
			}
			
			FileOutputStream fileOutputStream = new FileOutputStream(file);
			fileOutputStream.write(byteArrayBuffer.toByteArray());
			fileOutputStream.close();
		} catch (IOException e) {
			Log.d("ListDownloader", "Error: " + e);
			
		}
	}

}

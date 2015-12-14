package com.ghostery.privacy.appnoticesdk.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


/*
 * Downlaods a file from a URL
 */
public class FileDownloader
{
    // download bitmap files
    public static Bitmap getBitmapFromUrl(String url) {
        Bitmap bitmap = null;
		URL urlObj;
        try {
			HttpURLConnection connection = null;
			urlObj = new URL(url);
			connection = (HttpURLConnection) urlObj.openConnection();
			connection.connect();
			int responseCode = connection.getResponseCode();
			if (responseCode == 200) {
				bitmap = BitmapFactory.decodeStream(connection.getInputStream());
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
		}
        return bitmap;
    }
}

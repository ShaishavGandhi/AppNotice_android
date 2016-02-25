package com.ghostery.privacy.appnoticesdk.utils;

/**
 * Created by Steven.Overson on 2/26/2015.
 */

import android.content.res.Resources;
import android.util.Log;

import com.ghostery.privacy.appnoticesdk.AppNotice;
import com.ghostery.privacy.appnoticesdk.R;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class ServiceHandler {

	private static final String TAG = "SDK_ServiceHandler";
	private static int httpRequestTimeout;	// In millis
	private static final int httpRequestTimeoutDefault = 15000;	// In millis

    public ServiceHandler() {
		try {
			httpRequestTimeout = AppNotice.getAppContext().getResources().getInteger(R.integer.ghostery_http_request_timeout);
		} catch (Resources.NotFoundException e) {
            Log.e(TAG, "Getting req timeout", e);
		}
	}

    public static String getRequest(String urlVal) {
        String Content = null;
        BufferedReader bufferedReader = null;
        try {
            URL url = new URL(urlVal);
            HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
            httpURLConnection.setConnectTimeout(httpRequestTimeout);
            bufferedReader = new BufferedReader(new InputStreamReader(
                    httpURLConnection.getInputStream()));
            StringBuilder stringBuilder = new StringBuilder();
            String line = null;

            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }

            Content = stringBuilder.toString();
        } catch (Exception e) {
			Log.e(TAG, "Error in http get request", e);
        } finally {
            try {
                bufferedReader.close();
            } catch (Exception e) {
                Log.e(TAG, "Closing reader", e);
            }
        }
        return Content;
    }

    public static String postRequest(String urlStr, String inputJson) {

        String result = null;
        try{
            URL url = new URL(urlStr);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setConnectTimeout(15000);
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setRequestProperty("Accept", "application/json");
            httpURLConnection.setRequestProperty("Content-type", "application/json");
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);
            httpURLConnection.connect();
            OutputStream outputStream = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(
                    new OutputStreamWriter(outputStream, "UTF-8"));
            bufferedWriter.write(inputJson);
            bufferedWriter.flush();
            bufferedWriter.close();
            outputStream.close();

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(
                    httpURLConnection.getInputStream()));
            StringBuilder stringBuilder = new StringBuilder();
            String line = null;

            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
            result = stringBuilder.toString();
        }catch(Exception e){
			Log.e(TAG, "Error in http post request", e);
        }
        return result;
    }

}

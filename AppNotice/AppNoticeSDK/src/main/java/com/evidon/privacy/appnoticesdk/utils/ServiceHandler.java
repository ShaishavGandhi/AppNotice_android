package com.evidon.privacy.appnoticesdk.utils;

/**
 * Created by Steven.Overson on 2/26/2015.
 */

import android.content.res.Resources;
import android.util.Log;

import com.evidon.privacy.appnoticesdk.R;
import com.evidon.privacy.appnoticesdk.model.AppNoticeData;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class ServiceHandler {

	private static final String TAG = "SDK_ServiceHandler";
    private static int httpConnectTimeout;	// In millis
    private static int httpReadTimeout;	// In millis
    private static final int httpConnectTimeoutDefault = 15000;	// In millis
    private static final int httpReadTimeoutDefault = 10000;	// In millis
    private static String appNoticeToken;

    public ServiceHandler() {
		try {
            httpConnectTimeout = AppNoticeData.appContext.getResources().getInteger(R.integer.evidon_http_connect_timeout);
            httpReadTimeout = AppNoticeData.appContext.getResources().getInteger(R.integer.evidon_http_read_timeout);
		} catch (Resources.NotFoundException e) {
            Log.e(TAG, "Getting req timeout", e);
		}
	}

    public static String getRequest(String urlVal, String appNoticeToken) {
        ServiceHandler.appNoticeToken = appNoticeToken;
        String result = getRequest(urlVal);
        return result;
    }

    public static String getRequest(String urlVal) {
        String Content = null;
        BufferedReader bufferedReader = null;
        try {

            URL url = new URL(urlVal);
            HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
            httpURLConnection.setConnectTimeout(httpConnectTimeout);
            httpURLConnection.setReadTimeout(httpReadTimeout);

            if (AppNoticeData.usingToken && appNoticeToken != null) {
//                String basicAuth = "token " + Base64.encodeToString(appNoticeToken.getBytes("UTF-8"), android.util.Base64.NO_WRAP);
                httpURLConnection.setRequestProperty("Authorization", "token " + appNoticeToken);
            }

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
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
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
            httpURLConnection.setConnectTimeout(httpConnectTimeout);
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

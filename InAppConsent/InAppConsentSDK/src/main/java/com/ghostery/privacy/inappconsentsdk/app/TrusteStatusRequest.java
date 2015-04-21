package com.ghostery.privacy.inappconsentsdk.app;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by ale on 10/13/14.
 */
public class TrusteStatusRequest extends AsyncTask<String, String, String> {
    private HashMap<String, String> mData = null;// post data
    Boolean downloaded;

    /**
     * constructor
     */
    public TrusteStatusRequest(HashMap<String, String> data) {
        mData = data;
        downloaded = false;
    }

    /**
     * background
     */
    @Override
    protected String doInBackground(String... params) {
        byte[] result = null;
        String str = "";
        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(params[0]);// in this case, params[0] is URL
        try {
            // set up post data
            ArrayList<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
            Iterator<String> it = mData.keySet().iterator();
            while (it.hasNext()) {
                String key = it.next();
                nameValuePair.add(new BasicNameValuePair(key, mData.get(key)));
            }

            post.setEntity(new UrlEncodedFormEntity(nameValuePair, "UTF-8"));
            HttpResponse response = client.execute(post);
            StatusLine statusLine = response.getStatusLine();
            if(statusLine.getStatusCode() == HttpURLConnection.HTTP_OK){
                result = EntityUtils.toByteArray(response.getEntity());
                str = new String(result, "UTF-8");
                Log.v("TAG", "Truste DATA Retrieved" + str);
                downloaded = true;
            }
            else if(statusLine.getStatusCode() == HttpURLConnection.HTTP_NO_CONTENT)
            {
                downloaded = true;
                Log.v("TAG", "truste Data Empty");
            }
            else
            {
                downloaded = true;
                Log.v("TAG", "truste Data Error");
            }
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        catch (Exception e) {
        }
        return str;
    }

    /**
     * on getting result
     */
    @Override
    protected void onPostExecute(String result) {

        ListActivity.updateListWithTrusteResult(result);

        Log.v("TAG", "Truste Transmission Finished!");



    }
}

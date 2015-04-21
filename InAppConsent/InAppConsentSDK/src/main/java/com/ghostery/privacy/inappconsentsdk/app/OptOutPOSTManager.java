package com.ghostery.privacy.inappconsentsdk.app;

import android.os.AsyncTask;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import org.apache.http.StatusLine;
import java.net.HttpURLConnection;
import org.apache.http.util.EntityUtils;
import java.io.UnsupportedEncodingException;
import android.util.Log;

/**
 * Created by ale on 10/13/14.
 */
public class OptOutPOSTManager extends AsyncTask<String, String, String> {
    private HashMap<String, String> mData = null;// post data
    OptOutManager.Status mStatus;
    OptOutManager oom;

    /**
     * constructor
     */
    public OptOutPOSTManager(HashMap<String, String> data, OptOutManager optOutManager) {
        mData = data;
        oom = optOutManager;
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
                Log.v("TAG", "POST CALL SUCCESS" + str);
                this.mStatus = OptOutManager.Status.SUCCESS;
            }
            else
            {
                this.mStatus = OptOutManager.Status.FAIL;
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

        oom.mStatus = mStatus;
        oom.executeResult();

    }
}

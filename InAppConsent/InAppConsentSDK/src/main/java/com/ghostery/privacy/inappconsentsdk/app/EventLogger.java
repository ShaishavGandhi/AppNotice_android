package com.ghostery.privacy.inappconsentsdk.app;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;

public class EventLogger extends AsyncTask<String, Integer, Long> {

    private static final String TAG = "AppChoices";
    static String individual = "1";
    static String u = "1";

    @Override
    protected Long doInBackground(String... args) {
        String urlString = args[0];

        try {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(urlString);
            httpClient.execute(httpGet);
            Log.v(TAG, "EventLogger Logged: " + urlString);
        } catch (IOException e) {
            Log.v(TAG, "EventLogger Exception: " + urlString);
            e.printStackTrace();
        }

        return null;
    }

    public void logOptOut(String companyId, String noticeId, String ocid, String s) {

        String queryString = "http://l.betrad.com/oo/p.gif?v=0.4&r=&c="+companyId+"&et=1&u="+u+"&i="+individual+"&s="+s+"&m=1&n="+noticeId+"&ocid="+ocid;
        individual = "0"; // this is one just the first call
        if(s.equals("1"))
            u = "0"; // u is one when calls are individual (single optouts),
                     // in the case of opt out all just the first call of the bundle is one and the rest in cero

        this.execute(queryString);
    }

    public void logAppInstall(String noticeId) {
        String queryString = "http://l.betrad.com/app/p.gif?v=0.4&et=2&p=drd&n="+noticeId;
        this.execute(queryString);
    }

    public void logAppOpen(String noticeId) {
        String queryString = "http://l.betrad.com/app/p.gif?v=0.4&et=1&p=drd&n="+noticeId;
        this.execute(queryString);
    }

    public void logCompanyOptOut(String companyId, String noticeId, String ocid, String manf, String model, String release, String msg)
    {
        String queryString = "http://l.betrad.com/app/p.gif?v=0.4&et=3&p=drd&n="+android.net.Uri.encode(noticeId)+"&mf="+android.net.Uri.encode(manf)+"&mod="+android.net.Uri.encode(model)+"&rel="+android.net.Uri.encode(release)+"&msg="+android.net.Uri.encode(msg);
        this.execute(queryString);
    }
}

package com.ghostery.privacy.inappconsentsdk.utils;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.util.Log;

import com.ghostery.privacy.inappconsentsdk.R;


/*
 * Check to see if there is an update for the application
 */
public class Version {

    private static final String TAG = "AppChoices";

    public Context context;
    public Integer verApp;
    public Integer verWeb;

    public Version(Context context) {
        this.context = context;
        DownloadVersionTask task = new DownloadVersionTask();
        task.execute();
    }

    private class DownloadVersionTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            // Get this application version
            try {
                verApp = Integer.parseInt(context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName.replace(".", ""));
            } catch (NumberFormatException e) {
                Log.v(TAG, "NumberFormatException: " + e);
            } catch (NameNotFoundException e) {
                Log.v(TAG, "NameNotFoundException: " + e);
            }

            // Get the version from the web
            if (Network.isNetworkAvailable(context)) {
                String str = FileDownloader.getDataFromUrl(context.getString(R.string.ghostery_url_app_version_check));
                if (null != str)
                    verWeb = Integer.parseInt(str.replace(".", ""));
                else
                    verWeb = verApp; // something wrong with web file, set as equal
            }

            // There was no network so just set them as equal
            if (verWeb == null) {
                verWeb = verApp;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (verWeb > verApp) {
                Notification notification = new Notification(context);
                notification.appUpdateAvailable();
            }
        }

    }
}

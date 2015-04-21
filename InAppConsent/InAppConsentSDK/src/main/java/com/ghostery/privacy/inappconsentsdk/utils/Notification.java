package com.ghostery.privacy.inappconsentsdk.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import com.ghostery.privacy.inappconsentsdk.app.OptOutManager;
import com.ghostery.privacy.inappconsentsdk.R;


/*
 * All notifications used in the application
 */
public class Notification {

    public Context mContext;

    public Notification(Context context) {
        mContext = context;
    }

    // Alert Dialog to notify user there is an update for the app
    public void appUpdateAvailable() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage(R.string.ghostery_notification_update_available);

        builder.setNeutralButton(R.string.ghostery_notification_click_to_dl, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // User wants to update app, take them to the Store
                // TODO: determine which store the app was DLed from and take them to that store (ie Amazon AppStore)
                try {
                    mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + mContext.getPackageName())));
                } catch (android.content.ActivityNotFoundException anfe) {
                    mContext.startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://play.google.com/store/apps/details?id=" + mContext.getPackageName())));
                }

            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // Toast to notify user there is no network connection
    public void noNetworkConnection() {
        Toast.makeText(mContext, R.string.ghostery_notification_no_network_connection, Toast.LENGTH_LONG).show();
    }

    // Toast to notify user of optout success/fail
    public void optoutNotification(OptOutManager.Status status) {
        if (status == OptOutManager.Status.SUCCESS) {
            Toast.makeText(mContext, R.string.ghostery_notification_optout_success, Toast.LENGTH_LONG).show();
        } else {
            if (Network.isNetworkAvailable(mContext)) {
                Toast.makeText(mContext, R.string.ghostery_notification_optout_fail, Toast.LENGTH_LONG).show();
            } else {
                noNetworkConnection();
            }
        }
    }

    // Alert dialog to notify user that AdvertisingId has changed and to opt out again
    public void advertisingIdHasChanged() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage(R.string.ghostery_notification_ad_id_reset);

        builder.setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // User clicked okay button
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}

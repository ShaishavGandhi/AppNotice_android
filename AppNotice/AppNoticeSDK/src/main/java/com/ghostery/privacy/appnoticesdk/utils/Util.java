package com.ghostery.privacy.appnoticesdk.utils;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;

import com.ghostery.privacy.appnoticesdk.AppNotice_Activity;
import com.ghostery.privacy.appnoticesdk.R;
import com.ghostery.privacy.appnoticesdk.callbacks.JSONGetterCallback;
import com.ghostery.privacy.appnoticesdk.fragments.ManagePreferences_Fragment;
import com.ghostery.privacy.appnoticesdk.model.AppNoticeData;

import java.util.regex.Pattern;

/**
 * Created by Steven.Overson on 2/25/2015.
 */
public class Util {
    private static final String TAG = "Util";
    public static final String THREAD_INITTRACKERLIST = "thread_initTrackerList";

    public static void showManagePreferences(final Activity activity) {
        // Get either a new or initialized tracker config object
        final AppNoticeData appNoticeData = AppNoticeData.getInstance(activity);

        if (appNoticeData.isTrackerListInitialized()) {
            if (AppNotice_Activity.consentStarted) {
                ManagePreferences_Fragment fragment = new ManagePreferences_Fragment();
                FragmentTransaction transaction = AppNotice_Activity.getInstance().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.appnotice_fragment_container, fragment, AppNotice_Activity.FRAGMENT_TAG_MANAGE_PREFERENCES);
                transaction.addToBackStack(AppNotice_Activity.FRAGMENT_TAG_MANAGE_PREFERENCES);
                transaction.commit();
            } else {
                Intent intent = new Intent(activity, AppNotice_Activity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                activity.startActivity(intent);
            }
        } else {
            // If not initialized yet, go get it
            Log.d(TAG, "Starting initTrackerList from Util.showManagePreferences init.");
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    appNoticeData.initTrackerList(new JSONGetterCallback() {

                        @Override
                        public void onTaskDone() {
                            Log.d(TAG, "Done with initTrackerList from Util.showManagePreferences init.");

                            // Send notice for this event
                            //AppNoticeData.sendNotice(AppNoticeData.NoticeType.PREF_DIRECT);

                            if (AppNotice_Activity.consentStarted) {
                                ManagePreferences_Fragment fragment = new ManagePreferences_Fragment();
                                FragmentTransaction transaction = AppNotice_Activity.getInstance().getSupportFragmentManager().beginTransaction();
                                transaction.replace(R.id.appnotice_fragment_container, fragment, AppNotice_Activity.FRAGMENT_TAG_MANAGE_PREFERENCES);
                                transaction.addToBackStack(AppNotice_Activity.FRAGMENT_TAG_MANAGE_PREFERENCES);
                                transaction.commit();
                            } else {
                                Intent intent = new Intent(activity, AppNotice_Activity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                activity.startActivity(intent);
                            }
                        }
                    });
                }
            }, THREAD_INITTRACKERLIST);
            thread.start();
        }

    }

    public static boolean checkURL(CharSequence input) {
        Boolean isValid = false;
        if (TextUtils.isEmpty(input)) {
            isValid = false;
        } else {
            Pattern URL_PATTERN = Patterns.WEB_URL;
            isValid = URL_PATTERN.matcher(input).matches();
        }
        return isValid;
    }

    public static void forceAppRestart(Activity activity) {
        // This handles a rare case where the app object has been killed, but the SDK activity continues to run.
        // This forces the app to restart in a way that the SDK gets properly initialized.
        // TODO: Should this be a callback to the host app?
        Intent i = activity.getBaseContext().getPackageManager()
                .getLaunchIntentForPackage(activity.getBaseContext().getPackageName());
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.finish();
        activity.startActivity(i);
    }

}


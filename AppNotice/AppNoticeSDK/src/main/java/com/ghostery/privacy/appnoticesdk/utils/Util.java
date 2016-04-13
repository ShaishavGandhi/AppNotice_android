package com.ghostery.privacy.appnoticesdk.utils;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Patterns;

import com.ghostery.privacy.appnoticesdk.app.TrackerListActivity;

import java.util.regex.Pattern;

/**
 * Created by Steven.Overson on 2/25/2015.
 */
public class Util {
    public static final int DIVIDER_ALPHA = 46;

    public static void showManagePreferences(final Activity activity) {
//        Intent intent = new Intent(fragmentActivity, ListActivity.class);
        Intent intent = new Intent(activity, TrackerListActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
    }

    public static boolean checkURL(CharSequence input) {
        boolean isValid = false;
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

//    public static int getContrastColor(int color, int alpha)
//    {
//        int d = 0;
//
//        // Counting the perceptive luminance - human eye favors green color...
//        double a = 1 - ( 0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255;
//
//        if (a < 0.5)
//            d = 0;          // bright colors - black font
//        else
//            d = 255;        // dark colors - white font
//
//        return Color.argb(alpha, d, d, d);
//    }

}


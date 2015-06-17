package com.ghostery.privacy.inappconsentsdk.utils;

import android.app.Activity;
import android.content.Intent;

import com.ghostery.privacy.inappconsentsdk.app.TrackerListActivity;

/**
 * Created by Steven.Overson on 2/25/2015.
 */
public class Util {
    public static final int DIVIDER_ALPHA = 46;

    public static void showManagePreferences(final Activity activity) {
//        Intent intent = new Intent(activity, ListActivity.class);
        Intent intent = new Intent(activity, TrackerListActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
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


package com.ghostery.privacy.appnoticesdk.utils;

import com.ghostery.privacy.appnoticesdk.AppNotice;

/*
 * Created by Steven.Overson on 2/6/2015.
 * Manage key/value pairs an an application level
 */
public class Session {

    private static final String TAG = "Common.Session";

    // == Keys ==========================================================================
    public static final String APPNOTICE_DATA = "appNotice_data";
    public static final String APPNOTICE_CALLBACK = "appNotice_callback";
    public static final String APPNOTICE_ALL_BTN_SELECT = "appNotice_selectAll";
    public static final String APPNOTICE_NONE_BTN_SELECT = "appNotice_selectNone";
    public static final String APPNOTICE_PREF_OPENED_FROM_DIALOG = "appNotice_prefOpenedFromDialog";  // Boolean

    // System Keys
//    public static final String SYS_SHAREDPREFERENCES = "sys_sharedPreferences"; 		// SharedPreferences object
    public static final String SYS_RIC_SESSION_COUNT = "sys_ric_session_count"; 		// int


    // == Getters and setters ===========================================================
    public static void set(String key, Object value) {
        AppNotice.getSessionMap().put(key, value);
    }

    public static Object get(String key) {
        return AppNotice.getSessionMap().get(key);
    }

    // Get...with default value
    public static Object get(String key, Object defaultVal) {
        Object val = AppNotice.getSessionMap().get(key);
        if (val == null)
            val =  defaultVal;

        return val;
    }

    public static void remove(String key) {
        if( AppNotice.getSessionMap().containsKey(key) ) {
            AppNotice.getSessionMap().remove(key);
        }
    }

    public static void reset() {
        AppNotice.getSessionMap().clear();
    }

    public static boolean isSet(String key) {
        return AppNotice.getSessionMap().containsKey(key);
    }
}

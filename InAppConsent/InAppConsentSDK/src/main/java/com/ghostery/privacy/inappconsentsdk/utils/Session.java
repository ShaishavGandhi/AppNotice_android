package com.ghostery.privacy.inappconsentsdk.utils;

import com.ghostery.privacy.inappconsentsdk.app.App;

/*
 * Created by Steven.Overson on 2/6/2015.
 * Manage key/value pairs an an application level
 */
public class Session {

    private static final String TAG = "Common.Session";

    // == Keys ==========================================================================
    public static final String INAPPCONSENT_DATA = "inAppConsent_data";
    public static final String INAPPCONSENT_CALLBACK = "inAppConsent_callback";

    // System Keys
//    public static final String SYS_SHAREDPREFERENCES = "sys_sharedPreferences"; 		// SharedPreferences object
    public static final String SYS_RIC_SESSION_COUNT = "sys_ric_session_count"; 		// int


    // == Getters and setters ===========================================================
    public static void set(String key, Object value) {
        App.getSessionMap().put(key, value);
    }

    public static Object get(String key) {
        return App.getSessionMap().get(key);
    }

    // Get...with default value
    public static Object get(String key, Object defaultVal) {
        Object val = App.getSessionMap().get(key);
        if (val == null)
            val =  defaultVal;

        return val;
    }

    public static void remove(String key) {
        if( App.getSessionMap().containsKey(key) ) {
            App.getSessionMap().remove(key);
        }
    }

    public static void reset() {
        App.getSessionMap().clear();
    }

    public static boolean isSet(String key) {
        return App.getSessionMap().containsKey(key);
    }
}

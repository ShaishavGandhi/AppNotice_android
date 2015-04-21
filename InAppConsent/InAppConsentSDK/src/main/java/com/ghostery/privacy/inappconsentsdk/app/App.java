package com.ghostery.privacy.inappconsentsdk.app;

/**
 * Created by Steven.Overson on 2/6/2015.
 * Give access to application-level features and functionality.
 */

import android.app.Application;
import android.content.Context;

import com.ghostery.privacy.inappconsentsdk.R;

import java.util.HashMap;

public class App extends Application {
private static Context mContext;
public static String appName;
private static final HashMap <String, Object> sessionMap = new HashMap<String, Object>();

@Override
public void onCreate() {
        super.onCreate();
        mContext = this;
        appName = this.getString(R.string.app_name);
        }

public static Context getContext(){
        return mContext;
        }

public static HashMap<String, Object> getSessionMap() {
        return sessionMap;
        }
        }

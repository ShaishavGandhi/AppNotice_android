package com.ghostery.privacy.appnoticesdk.app;

/**
 * Created by Steven.Overson on 2/6/2015.
 * Give access to application-level features and functionality.
 */

import android.app.Application;
import android.content.Context;

import java.util.HashMap;

public class App extends Application {
	private static Context mContext;
	private static final HashMap<String, Object> sessionMap = new HashMap<String, Object>();

	@Override
	public void onCreate() {
		super.onCreate();
		mContext = this;
	}

	public static Context getContext()
	{
		return mContext;
	}

	public static HashMap<String, Object> getSessionMap() {
		return sessionMap;
	}

}

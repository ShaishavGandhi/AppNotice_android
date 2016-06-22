package com.ghostery.privacy.appnoticesdk.callbacks;

import android.support.v4.app.Fragment;

import java.util.HashMap;

/**
 * Created by Steven.Overson on 3/4/2015.
 */
public interface AppNotice_Callback {
    public void onOptionSelected(boolean isAccepted, HashMap<Integer, Boolean> trackerHashMap);
    public void onNoticeSkipped(boolean isAccepted, HashMap<Integer, Boolean> trackerHashMap);
    public void onTrackerStateChanged(HashMap<Integer, Boolean> trackerHashMap);
    public Fragment onManagePreferencesClicked();
}

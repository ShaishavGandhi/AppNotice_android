package com.ghostery.privacy.inappconsentsdk.callbacks;

import java.util.HashMap;

/**
 * Created by Steven.Overson on 3/4/2015.
 */
public interface InAppConsent_Callback {
    public void onOptionSelected(boolean isAccepted, HashMap<Integer, Boolean> trackerHashMap);
    public void onNoticeSkipped();
    public void onTrackerStateChange(HashMap<Integer, Boolean> trackerHashMap);
}

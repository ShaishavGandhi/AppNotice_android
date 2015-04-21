package com.ghostery.privacy.inappconsentsdk.callbacks;

/**
 * Created by Steven.Overson on 3/4/2015.
 */
public interface InAppNotice_Callback {
    public void onOptionSelected(boolean isAccepted);
    public void onNoticeSkipped();
}

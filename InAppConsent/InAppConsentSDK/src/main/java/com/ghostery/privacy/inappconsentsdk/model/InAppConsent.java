package com.ghostery.privacy.inappconsentsdk.model;

import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.ghostery.privacy.inappconsentsdk.callbacks.InAppConsent_Callback;
import com.ghostery.privacy.inappconsentsdk.callbacks.TrackerConfigGetterCallback;
import com.ghostery.privacy.inappconsentsdk.fragments.ExplicitInfo_DialogFragment;
import com.ghostery.privacy.inappconsentsdk.fragments.ImpliedInfo_DialogFragment;
import com.ghostery.privacy.inappconsentsdk.utils.AppData;
import com.ghostery.privacy.inappconsentsdk.utils.Session;
import com.ghostery.privacy.inappconsentsdk.utils.TrackerConfig;
import com.ghostery.privacy.inappconsentsdk.utils.Util;

/**
 * Created by Steven.Overson on 2/4/2015.
 */
public class InAppConsent {
    private static final String INAPPNOTIVE_TRACKERCONFIG = "inAppConsent_TrackerConfig";

    private TrackerConfig trackerConfig;
//    private ShowMode showMode;
    private InAppConsent_Callback inAppConsent_callback;

//    private enum ShowMode {
//        SHOW_IMPLICIT_NOTICE, SHOW_EXPLICIT_NOTICE, SHOW_MANAGE_PREFERENCES
//    }

    /**
     * Starts the In-App Consent flow for implied consent. Must be called before your app begins tracking.
     *   - activity: Usually your start-up activity
     *   - company_id: The company ID assigned to you for In-App Consent
     *   - pub_notice_id: The Pub-notice ID of the configuration created for this app
     *   - use_remote_values:
     *        True = Try to use the In-App Consent dialog configuration parameters from the service;
     *        False = Use local resource values instead of calling the service
     *   - inAppConsent_callback: The InAppConsent_Callback method created in your class to handle the In-App Consent response
     */
    public void startConsentFlow(final FragmentActivity activity, int company_id, int pub_notice_id, boolean useRemoteValues, InAppConsent_Callback inAppConsent_callback) {
        this.inAppConsent_callback = inAppConsent_callback;
        init(activity, company_id, pub_notice_id, useRemoteValues);

        // Send notice for this event
        TrackerConfig.sendNotice(TrackerConfig.NoticeType.APP_LOAD);
    }

//    /**
//     * Starts the In-App Consent flow for explicit consent. Must be called before your app begins tracking.
//     *   - activity: Usually your start-up activity
//     *   - company_id: The company ID assigned to you by Ghostery
//     *   - pub_notice_id: The Pub-notice ID of the configuration created for this app
//     *   - use_remote_values:
//     *        True = Try to use the In-App Consent dialog configuration parameters from the service;
//     *        False = Use local resource values instead of calling the service
//     *   - inAppConsent_callback: The InAppConsent_Callback method created in your class to handle the In-App Consent response
//     */
//    public void startExplicitConsent(final FragmentActivity activity, int company_id, int pub_notice_id, boolean use_remote_values, InAppConsent_Callback inAppConsent_callback) {
//        this.inAppConsent_callback = inAppConsent_callback;
//        init(activity, company_id, pub_notice_id, ShowMode.SHOW_EXPLICIT_NOTICE, use_remote_values);
//
//        // Send notice for this event
//        TrackerConfig.sendNotice(TrackerConfig.NoticeType.APP_LOAD);
//    }

    /**
     * Resets the session and persistent values that InAppConsent SDK uses to manage the dialog display frequency.
     */
    public void resetSDK() {
        Session.set(Session.SYS_RIC_SESSION_COUNT, 0);
        AppData.setLong(AppData.APPDATA_IMPLICIT_LAST_DISPLAY_TIME, 0L);
        AppData.setInteger(AppData.APPDATA_IMPLICIT_DISPLAY_COUNT, 0);
        AppData.setBoolean(AppData.APPDATA_EXPLICIT_ACCEPTED, false);
    }

    /**
     * Shows the Manage Preferences screen. Can be called from your when the end user requests access to this screen (e.g., from a menu or button click).
     *   - activity: Usually your current activity
     */
    public void showManagePreferences(final FragmentActivity activity) {
        // Open the In-App Consent preferences activity
        Util.ShowAdPreferences(activity);

        // Send notice for this event
        TrackerConfig.sendNotice(TrackerConfig.NoticeType.PREF_DIRECT);
    }

    private void init(final FragmentActivity activity, int company_id, int pub_notice_id, final boolean useRemoteValues) {
        if ((company_id <= 0) || (pub_notice_id <= 0)) {
            throw(new IllegalArgumentException("Company ID and Pub-notice ID must both be valid identifiers."));
        }

        // Get either a new or initialized tracker config object
        trackerConfig = (TrackerConfig)Session.get(INAPPNOTIVE_TRACKERCONFIG, TrackerConfig.getInstance(activity));

        // Keep track of the company ID and the pub-notice ID
        trackerConfig.setCompany_id(company_id);
        trackerConfig.setPub_notice_id(pub_notice_id);

        if (trackerConfig.isInitialized()) {
            // If initialized, use what we have
            handleTrackerConfigInfoUpdate(activity, useRemoteValues);
        } else {
            // If not initialized yet, go get it
            trackerConfig.initTrackerConfig(new TrackerConfigGetterCallback() {

                @Override
                public void onTaskDone() {
                    // Save the tracker config object in the app session
                    Session.set(INAPPNOTIVE_TRACKERCONFIG, trackerConfig);

                    // Handle the response
                    handleTrackerConfigInfoUpdate(activity, useRemoteValues);
                }
            });
        }

    }

    private void handleTrackerConfigInfoUpdate(FragmentActivity activity, boolean useRemoteValues) {
        // trackerConfig should always be initialized at this point

        // Determine if we need to show this Implicit Notice dialog box
        boolean showNotice = true;
        if (trackerConfig.getBric()) {
            showNotice = trackerConfig.getExplicitNoticeDisplayStatus();
        } else {
            showNotice = trackerConfig.getImplicitNoticeDisplayStatus();
        }

        if (showNotice) {
            FragmentManager fm = activity.getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();

            // Create and show the dialog.
            if (trackerConfig.getBric()) {
                ExplicitInfo_DialogFragment explicitInfo_DialogFragment = ExplicitInfo_DialogFragment.newInstance(0);
                explicitInfo_DialogFragment.setTrackerConfig(trackerConfig);
                explicitInfo_DialogFragment.setInAppConsent_Callback(inAppConsent_callback);
                explicitInfo_DialogFragment.setUseRemoteValues(useRemoteValues);
                explicitInfo_DialogFragment.show(ft, "dialog_fragment_explicitInfo");

            } else {
                ImpliedInfo_DialogFragment impliedInfo_DialogFragment = ImpliedInfo_DialogFragment.newInstance(0);
                impliedInfo_DialogFragment.setTrackerConfig(trackerConfig);
                impliedInfo_DialogFragment.setInAppConsent_Callback(inAppConsent_callback);
                impliedInfo_DialogFragment.setUseRemoteValues(useRemoteValues);
                impliedInfo_DialogFragment.show(ft, "dialog_fragment_implicitIntro");

                // Remember that this Implicit Notice dialog box was displayed
                TrackerConfig.incrementImplicitNoticeDisplayCount();

            }
        } else {
            // If not showing a notice, return a true status to the
            inAppConsent_callback.onNoticeSkipped();
        }
    }
}

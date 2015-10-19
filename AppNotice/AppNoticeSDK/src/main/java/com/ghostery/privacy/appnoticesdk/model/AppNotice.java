package com.ghostery.privacy.appnoticesdk.model;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;

import com.ghostery.privacy.appnoticesdk.callbacks.AppNotice_Callback;
import com.ghostery.privacy.appnoticesdk.callbacks.JSONGetterCallback;
import com.ghostery.privacy.appnoticesdk.fragments.ExplicitInfo_DialogFragment;
import com.ghostery.privacy.appnoticesdk.fragments.ImpliedInfo_DialogFragment;
import com.ghostery.privacy.appnoticesdk.utils.AppData;
import com.ghostery.privacy.appnoticesdk.utils.Session;
import com.ghostery.privacy.appnoticesdk.utils.Util;

import java.util.HashMap;

/**
 * Created by Steven.Overson on 2/4/2015.
 */
public class AppNotice {

    private AppNoticeData appNoticeData;
//    private ShowMode showMode;
    private AppNotice_Callback appNotice_callback;
    private Activity extActivity = null;

//    private enum ShowMode {
//        SHOW_IMPLICIT_NOTICE, SHOW_EXPLICIT_NOTICE, SHOW_MANAGE_PREFERENCES
//    }

    public AppNotice(Activity activity) {
        extActivity = activity;
    }

    /**
     * Starts the In-App Consent flow. Must be called before your app begins tracking. The flow type
     *     (implied or explicit) is determined by the "bric" parameter in the JSON or the local
     *     ghostery_bric resource parameter.
     *   - fragmentActivity: Usually your start-up fragmentActivity
     *   - company_id: The company ID assigned to you for In-App Consent
     *   - pub_notice_id: The Pub-notice ID of the configuration created for this app
     *   - use_remote_values:
     *        True = Try to use the In-App Consent dialog configuration parameters from the service;
     *        False = Use local resource values instead of calling the service
     *   - appNotice_callback: The AppNotice_Callback method created in your class to handle the In-App Consent response
     */
    public void startConsentFlow(int company_id, int pub_notice_id, boolean useRemoteValues, AppNotice_Callback appNotice_callback) {
        this.appNotice_callback = appNotice_callback;
        Session.set(Session.APPNOTICE_CALLBACK, appNotice_callback);

        init(company_id, pub_notice_id, useRemoteValues, true);

        // Send notice for this event
        AppNoticeData.sendNotice(AppNoticeData.NoticeType.START_CONSENT_FLOW);
    }

//    /**
//     * Starts the In-App Consent flow for explicit consent. Must be called before your app begins tracking.
//     *   - fragmentActivity: Usually your start-up fragmentActivity
//     *   - company_id: The company ID assigned to you by Ghostery
//     *   - pub_notice_id: The Pub-notice ID of the configuration created for this app
//     *   - use_remote_values:
//     *        True = Try to use the In-App Consent dialog configuration parameters from the service;
//     *        False = Use local resource values instead of calling the service
//     *   - appNotice_callback: The AppNotice_Callback method created in your class to handle the In-App Consent response
//     */
//    public void startExplicitConsent(final FragmentActivity fragmentActivity, int company_id, int pub_notice_id, boolean use_remote_values, AppNotice_Callback appNotice_callback) {
//        this.appNotice_callback = appNotice_callback;
//        init(fragmentActivity, company_id, pub_notice_id, ShowMode.SHOW_EXPLICIT_NOTICE, use_remote_values);
//
//        // Send notice for this event
//        AppNoticeData.sendNotice(AppNoticeData.NoticeType.START_CONSENT_FLOW);
//    }

    /**
     * Resets the session and persistent values that AppNotice SDK uses to manage the dialog display frequency.
     */
    public void resetSDK() {
        Session.set(Session.SYS_RIC_SESSION_COUNT, 0);
        AppData.setLong(AppData.APPDATA_IMPLICIT_LAST_DISPLAY_TIME, 0L);
        AppData.setInteger(AppData.APPDATA_IMPLICIT_DISPLAY_COUNT, 0);
        AppData.setBoolean(AppData.APPDATA_EXPLICIT_ACCEPTED, false);
    }

    /**
     * Shows the Manage Preferences screen. Can be called from your when the end user requests access to this screen (e.g., from a menu or button click).
     *   - fragmentActivity: Usually your current fragmentActivity
     */
    public void showManagePreferences(int company_id, int pub_notice_id, boolean useRemoteValues, AppNotice_Callback appNotice_callback) {
        this.appNotice_callback = appNotice_callback;
        Session.set(Session.APPNOTICE_CALLBACK, appNotice_callback);

        init(company_id, pub_notice_id, useRemoteValues, false);
    }

    private void init(int company_id, int pub_notice_id, final boolean useRemoteValues, final boolean isConsentFlow) {
        if ((company_id <= 0) || (pub_notice_id <= 0)) {
            throw(new IllegalArgumentException("Company ID and Pub-notice ID must both be valid identifiers."));
        }

        // Get either a new or initialized tracker config object
        appNoticeData = AppNoticeData.getInstance(extActivity);
        appNoticeData.useRemoteValues = useRemoteValues;

        // Keep track of the company ID and the pub-notice ID
        appNoticeData.setCompany_id(company_id);
        appNoticeData.setPub_notice_id(pub_notice_id);

        if (appNoticeData.isInitialized()) {
            // If initialized, use what we have
            if (isConsentFlow) {
                startConsentFlow(useRemoteValues);
            } else {
                // Open the In-App Consent preferences fragmentActivity
                Util.showManagePreferences(extActivity);

                // Send notice for this event
                AppNoticeData.sendNotice(AppNoticeData.NoticeType.PREF_DIRECT);
            }
        } else {
            // If not initialized yet, go get it
            appNoticeData.init(new JSONGetterCallback() {

                @Override
                public void onTaskDone() {
                    // Save the tracker config object in the app session
                    Session.set(Session.APPNOTICE_DATA, appNoticeData);

                    if (isConsentFlow) {
                        // Handle the response
                        startConsentFlow(useRemoteValues);
                    } else {
                        // Open the In-App Consent preferences fragmentActivity
                        Util.showManagePreferences(extActivity);

                        // Send notice for this event
                        AppNoticeData.sendNotice(AppNoticeData.NoticeType.PREF_DIRECT);
                    }
                }
            });
        }

    }

    private void startConsentFlow(boolean useRemoteValues) {
        // appNoticeData should always be initialized at this point

        // Determine if we need to show this Implicit Notice dialog box
        boolean showNotice = true;
        if (appNoticeData.getBric()) {
            showNotice = appNoticeData.getExplicitNoticeDisplayStatus();
        } else {
            showNotice = appNoticeData.getImplicitNoticeDisplayStatus();
        }

        if (showNotice) {
            FragmentManager fm = extActivity.getFragmentManager();
            FragmentTransaction fragmentTransaction = fm.beginTransaction();

            // Create and show the dialog.
            if (appNoticeData.getBric()) {
                ExplicitInfo_DialogFragment explicitInfo_DialogFragment = ExplicitInfo_DialogFragment.newInstance(0);
                explicitInfo_DialogFragment.setUseRemoteValues(useRemoteValues);
                explicitInfo_DialogFragment.show(fragmentTransaction, "dialog_fragment_explicitInfo");

            } else {
                ImpliedInfo_DialogFragment impliedInfo_DialogFragment = ImpliedInfo_DialogFragment.newInstance(0);
                impliedInfo_DialogFragment.setUseRemoteValues(useRemoteValues);
                impliedInfo_DialogFragment.show(fragmentTransaction, "dialog_fragment_impliedInfo");

                // Remember that this Implicit Notice dialog box was displayed
                AppNoticeData.incrementImplicitNoticeDisplayCount();

            }
        } else {
            // If not showing a notice, return a true status to the
            appNotice_callback.onNoticeSkipped();
        }
    }

    public HashMap<Integer, Boolean> getTrackerPreferences() {
        return AppNoticeData.getTrackerPreferences();
    }

}

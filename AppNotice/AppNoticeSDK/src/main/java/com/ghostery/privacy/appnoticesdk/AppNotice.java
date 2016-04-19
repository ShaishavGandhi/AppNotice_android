package com.ghostery.privacy.appnoticesdk;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.util.Log;

import com.ghostery.privacy.appnoticesdk.callbacks.AppNotice_Callback;
import com.ghostery.privacy.appnoticesdk.fragments.ExplicitInfo_DialogFragment;
import com.ghostery.privacy.appnoticesdk.fragments.ImpliedInfo_DialogFragment;
import com.ghostery.privacy.appnoticesdk.model.AppNoticeData;
import com.ghostery.privacy.appnoticesdk.utils.AppData;
import com.ghostery.privacy.appnoticesdk.utils.Session;
import com.ghostery.privacy.appnoticesdk.utils.Util;

import java.util.HashMap;

/**
 * Created by Steven.Overson on 2/4/2015.
 */
public class AppNotice {

    private static final String TAG = "AppNotice";
    private AppNoticeData appNoticeData;
    private AppNotice_Callback appNotice_callback;
    private static Activity extActivity = null;
    private static Context appContext;
    private static final HashMap<String, Object> sessionMap = new HashMap<String, Object>();
    private static boolean isImpliedFlow = true;

    /**
     * AppNotice constructor
     * @param activity: Usually your start-up activity.
     * @param companyId: The company ID assigned to you for App Notice consent.
     * @param configId: The Configuration ID of the configuration created for this app.
     * @param appNotice_callback: An AppNotice_Callback object that handles the various callbacks from the SDK to the host app.
     */
    public AppNotice(Activity activity, int companyId, int configId, AppNotice_Callback appNotice_callback) {
        AppNotice(activity, companyId, configId, appNotice_callback);
    }

    /**
	 * AppNotice constructor (deprecated)
	 * @param activity: Usually your start-up activity
	 * @param companyId: The company ID assigned to you for App Notice consent
	 * @param configId: The Configuration ID of the configuration created for this app
     * @param useRemoteValues:
     *        True = Try to use the App Notice consent dialog configuration parameters from the service;
     *        False = Use local resource values instead of calling the service
     * @param appNotice_callback: An AppNotice_Callback object that handles the various callbacks from the SDK to the host app.
	 */
    public AppNotice(Activity activity, int companyId, int configId, boolean useRemoteValues, AppNotice_Callback appNotice_callback) {
        AppNotice(activity, companyId, configId, appNotice_callback);
	}

    /**
     * AppNotice method to combine the current and deprecated constructor functionality
     * @param activity: Usually your start-up activity.
     * @param companyId: The company ID assigned to you for App Notice consent.
     * @param configId: The Configuration ID of the configuration created for this app.
     * @param appNotice_callback: An AppNotice_Callback object that handles the various callbacks from the SDK to the host app.
     */
    private void AppNotice(Activity activity, int companyId, int configId, AppNotice_Callback appNotice_callback) {
        appContext = activity.getApplicationContext();
        extActivity = activity;
        if ((companyId <= 0) || (configId <= 0)) {
            throw(new IllegalArgumentException("Company ID and Config ID must both be valid identifiers."));
        }

        // Remember the provided callback
        this.appNotice_callback = appNotice_callback;
        Session.set(Session.APPNOTICE_CALLBACK, appNotice_callback);

        // Get either a new or initialized tracker config object
        appNoticeData = AppNoticeData.getInstance(extActivity);

        // Keep track of the company ID and the configuration ID
        appNoticeData.setCompanyId(companyId);
        appNoticeData.setConfigId(configId);
    }

    /**
     * Starts the App Notice Implied Consent flow. Must be called before your app begins any tracking activity.
     */
    public void startImpliedConsentFlow() {
        isImpliedFlow = true;
        init(true);

        // Send notice for this event
        AppNoticeData.sendNotice(AppNoticeData.NoticeType.START_CONSENT_FLOW);
    }

    /**
     * Starts the App Notice Explicit Consent flow. Must be called before your app begins any tracking activity.
     */
    public void startExplicitConsentFlow() {
        isImpliedFlow = false;
        init(true);

        // Send notice for this event
        AppNoticeData.sendNotice(AppNoticeData.NoticeType.START_CONSENT_FLOW);
    }

    /**
     * Resets the session and persistent values that AppNotice SDK uses to manage the dialog display frequency.
     */
    public void resetSDK() {
        Session.set(Session.SYS_CURRENT_SESSION_COUNT, 0);
        AppData.setLong(AppData.APPDATA_IMPLICIT_LAST_DISPLAY_TIME, 0L);
        AppData.setInteger(AppData.APPDATA_IMPLICIT_DISPLAY_COUNT, 0);
        AppData.setBoolean(AppData.APPDATA_EXPLICIT_ACCEPTED, false);
    }

    /**
     * Shows the Manage Preferences screen. Can be called from your when the end user requests access to this screen (e.g., from a menu or button click).
     *   - fragmentActivity: Usually your current fragmentActivity
     */
    public void showManagePreferences() {
        init(false);
    }

    private void init(final boolean isConsentFlow) {
        if (!appNoticeData.isInitialized()) {
            appNoticeData.init();
        }

        // If initialized, use what we have
        if (isConsentFlow) {
            openConsentFlowDialog();
        } else {
            // Open the App Notice Consent preferences fragmentActivity
            Util.showManagePreferences(extActivity);

            // Send notice for this event
            AppNoticeData.sendNotice(AppNoticeData.NoticeType.PREF_DIRECT);
        }

    }

    private void openConsentFlowDialog() {
        // appNoticeData should always be initialized at this point

        if (appNoticeData == null || !appNoticeData.isInitialized()) {
            // This handles a rare case where the app object has been killed, but the SDK activity continues to run.
            // This forces the app to restart in a way that the SDK gets properly initialized.
            // TODO: Should this be a callback to the host app?
            Log.d(TAG, "Force restart the host app to correctly init the SDK.");
            Util.forceAppRestart(extActivity);
        } else {
            // Determine if we need to show this Implicit Notice dialog box
            boolean showNotice = true;
            if (isImpliedFlow) {
                showNotice = appNoticeData.getImplicitNoticeDisplayStatus();
            } else {
                showNotice = appNoticeData.getExplicitNoticeDisplayStatus();
            }

            if (showNotice) {
                FragmentManager fm = extActivity.getFragmentManager();
                FragmentTransaction fragmentTransaction = fm.beginTransaction();

                // Create and show the dialog.
                if (isImpliedFlow) {
                    ImpliedInfo_DialogFragment impliedInfo_DialogFragment = ImpliedInfo_DialogFragment.newInstance(0);
                    impliedInfo_DialogFragment.show(fragmentTransaction, "dialog_fragment_impliedInfo");

                    // Remember that this Implicit Notice dialog box was displayed
                    AppNoticeData.incrementImplicitNoticeDisplayCount();

                } else {
                    ExplicitInfo_DialogFragment explicitInfo_DialogFragment = ExplicitInfo_DialogFragment.newInstance(0);
                    explicitInfo_DialogFragment.show(fragmentTransaction, "dialog_fragment_explicitInfo");

                }
            } else {
                // If not showing a notice, return a true status to the
                appNotice_callback.onNoticeSkipped();
            }
        }
    }

    public HashMap<Integer, Boolean> getTrackerPreferences() {
        return AppNoticeData.getTrackerPreferences();
    }

    public static HashMap<String, Object> getSessionMap() {
        return sessionMap;
    }

    public static Context getAppContext()
    {
        return appContext;
    }

    public static Activity getParentActivity()
    {
        return extActivity;
    }
}

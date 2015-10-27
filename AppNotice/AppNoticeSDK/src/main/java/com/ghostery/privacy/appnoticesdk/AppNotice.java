package com.ghostery.privacy.appnoticesdk;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;

import com.ghostery.privacy.appnoticesdk.callbacks.AppNotice_Callback;
import com.ghostery.privacy.appnoticesdk.callbacks.JSONGetterCallback;
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

    private AppNoticeData appNoticeData;
    private AppNotice_Callback appNotice_callback;
    private Activity extActivity = null;

	/**
	 * AppNotice constructor
	 * @param activity: Usually your start-up activity
	 * @param companyId: The company ID assigned to you for In-App Consent
	 * @param configId: The Configuration ID of the configuration created for this app
	 * @param useRemoteValues:
	 *        True = Try to use the In-App Consent dialog configuration parameters from the service;
	 *        False = Use local resource values instead of calling the service
	 */
    public AppNotice(Activity activity, int companyId, int configId, boolean useRemoteValues) {
        extActivity = activity;
		if ((companyId <= 0) || (configId <= 0)) {
			throw(new IllegalArgumentException("Company ID and Config ID must both be valid identifiers."));
		}

		// Get either a new or initialized tracker config object
		appNoticeData = AppNoticeData.getInstance(extActivity);
		appNoticeData.useRemoteValues = useRemoteValues;

		// Keep track of the company ID and the configuration ID
		appNoticeData.setCompanyId(companyId);
		appNoticeData.setConfigId(configId);
	}

    /**
     * Starts the In-App Consent flow. Must be called before your app begins tracking. The flow type
     *     (implied or explicit) is determined by the "bric" parameter in the JSON or the local
     *     ghostery_bric resource parameter.
     *   - appNotice_callback: The AppNotice_Callback method created in your class to handle the In-App Consent response
     */
    public void startConsentFlow(AppNotice_Callback appNotice_callback) {
        this.appNotice_callback = appNotice_callback;
        Session.set(Session.APPNOTICE_CALLBACK, appNotice_callback);

        init(true);

        // Send notice for this event
        AppNoticeData.sendNotice(AppNoticeData.NoticeType.START_CONSENT_FLOW);
    }

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
    public void showManagePreferences(AppNotice_Callback appNotice_callback) {
        this.appNotice_callback = appNotice_callback;
        Session.set(Session.APPNOTICE_CALLBACK, appNotice_callback);

        init(false);
    }

    private void init(final boolean isConsentFlow) {
        if (appNoticeData.isInitialized()) {
            // If initialized, use what we have
            if (isConsentFlow) {
                startConsentFlow(appNoticeData.useRemoteValues);
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
                        startConsentFlow(appNoticeData.useRemoteValues);
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

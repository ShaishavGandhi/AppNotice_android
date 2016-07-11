package com.ghostery.privacy.appnoticesdk;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.ghostery.privacy.appnoticesdk.callbacks.AppNotice_Callback;
import com.ghostery.privacy.appnoticesdk.callbacks.JSONGetterCallback;
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
    public static boolean isImpliedFlow = true;
    public static boolean usingToken = true;
    public static int implied30dayDisplayMax = 0;  // Default to mode-0. 0 displays on first start and every notice ID change. 1+ is the max number of times to display the consent screen on start up in a 30-day period.

    /**
     * AppNotice constructor
     * @param activity: Usually your start-up activity.
     * @param companyId: The company ID assigned to you for the App Notice SDK.
     * @param noticeId: The notice ID of the configuration created for this app.
     * @param appNotice_callback: An AppNotice_Callback object that handles the various callbacks from the SDK to the host app.
     */
    public AppNotice(Activity activity, int companyId, int noticeId, AppNotice_Callback appNotice_callback, boolean isImpliedFlow) {
        this.isImpliedFlow = isImpliedFlow;
        usingToken = false;
        appContext = activity.getApplicationContext();
        extActivity = activity;
        if ((companyId <= 0) || (noticeId <= 0)) {
            throw(new IllegalArgumentException("Company ID and notice ID must both be valid identifiers."));
        }

        // Remember the provided callback
        this.appNotice_callback = appNotice_callback;
        Session.set(Session.APPNOTICE_CALLBACK, appNotice_callback);

        // Get either a new or initialized tracker config object
        appNoticeData = AppNoticeData.getInstance(activity);

        // Keep track of the company ID and the notice ID
        appNoticeData.setCompanyId(companyId);
        appNoticeData.setCurrentNoticeId(noticeId);
    }

    /**
     * Starts the App Notice Implied Consent flow with an option to specify max displays in a 30-day period.
     * Should be called before your app begins any tracking activity.
     *   0 displays on first start and every notice ID change (recommended).
     *   1+ is the max number of times to display the consent screen on start up in a 30-day period.
     */
    public void startConsentFlow(int implied30dayDisplayMax) {
        this.implied30dayDisplayMax = implied30dayDisplayMax;
        startConsentFlow();
    }

    /**
     * Starts the App Notice Implied Consent flow. Must be called before your app begins any tracking activity.
     */
    public void startConsentFlow() {
        startConsentFlow(true);

        // Send notice for this event
        AppNoticeData.sendNotice(AppNoticeData.NoticeType.START_CONSENT_FLOW);
    }

    /**
     * Resets the session and persistent values that AppNotice SDK uses to manage the dialog display frequency.
     */
    public void resetSDK() {
        Session.reset();
        AppData.remove(AppData.APPDATA_IMPLIED_LAST_DISPLAY_TIME);
        AppData.remove(AppData.APPDATA_IMPLIED_DISPLAY_COUNT);
        AppData.remove(AppData.APPDATA_EXPLICIT_ACCEPTED);
        AppData.remove(AppData.APPDATA_TRACKERSTATES);
        AppData.remove(AppData.APPDATA_PREV_NOTICE_ID);
        AppData.remove(AppData.APPDATA_PREV_JSON);
    }

    /**
     * Shows the Manage Preferences screen. Can be called from your when the end user requests access to this screen (e.g., from a menu or button click).
     *   - fragmentActivity: Usually your current fragmentActivity
     */
    public void showManagePreferences() {
        startConsentFlow(false);
    }

    private void startConsentFlow(final boolean isConsentFlow) {
        if (!appNoticeData.isInitialized()) {
            appNoticeData.init();
        }

        // Start getting the tracker list before we display the consent dialog or the manage preferences screen
        if (appNoticeData.isTrackerListInitialized()) {
            // If initialized, use what we have
            if (isConsentFlow) {
                openConsentFlowDialog();
            } else {
                // Open the App Notice manage preferences fragment
                Util.showManagePreferences(extActivity);

                // Send notice for this event
                AppNoticeData.sendNotice(AppNoticeData.NoticeType.PREF_DIRECT);
            }
        } else {

            Log.d(TAG, "Starting initTrackerList from AppNotice startConsentFlow.");
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    appNoticeData.initTrackerList(new JSONGetterCallback() {

                        @Override
                        public void onTaskDone() {
                            // Do nothing
                            Log.d(TAG, "Done with initTrackerList from AppNotice startConsentFlow.");

                            // Now that it is initialized, use it
                            if (isConsentFlow) {
                                openConsentFlowDialog();
                            } else {
                                // Open the App Notice manage preferences fragment
                                Util.showManagePreferences(extActivity);

                                // Send notice for this event
                                AppNoticeData.sendNotice(AppNoticeData.NoticeType.PREF_DIRECT);
                            }
                        }
                    });
                }
            }, Util.THREAD_INITTRACKERLIST);
            thread.start();
        }

    }

    private void openConsentFlowDialog() {
        // appNoticeData should always be initialized at this point

        if (appNoticeData == null || !appNoticeData.isInitialized()) {
            // This handles a rare case where the app object has been killed, but the SDK activity continues to run.
            // This forces the app to restart in a way that the SDK gets properly initialized.
            // TODO: Should this be a callback to the host app?
            Log.d(TAG, "Force restart the host app to correctly startConsentFlow the SDK.");
            Util.forceAppRestart(extActivity);
        } else {
            // Determine if we need to show this Implied Notice dialog box
            Boolean showNotice = true;
            if (isImpliedFlow) {
                showNotice = appNoticeData.getImpliedNoticeDisplayStatus();
            } else {
                showNotice = appNoticeData.getExplicitNoticeDisplayStatus();
            }

            if (showNotice) {
                FragmentManager fm = extActivity.getFragmentManager();
                FragmentTransaction fragmentTransaction = fm.beginTransaction();

                // Create and show the dialog.
                if (isImpliedFlow) {
                    Intent intent = new Intent(extActivity, AppNotice_Activity.class);
                    intent.putExtra("FRAGMENT_TYPE", AppNotice_Activity.FRAGMENT_TAG_IMPLIED_CONSENT);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    extActivity.startActivity(intent);

                    // Count that this Implied Notice dialog box was displayed
                    AppNoticeData.incrementImpliedNoticeDisplayCount();

                } else {
                    Intent intent = new Intent(extActivity, AppNotice_Activity.class);
                    intent.putExtra("FRAGMENT_TYPE", AppNotice_Activity.FRAGMENT_TAG_EXPLICIT_CONSENT);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    extActivity.startActivity(intent);
                }

                // Remember that a notice has been shown for this notice ID
                appNoticeData.setPreviousNoticeId(appNoticeData.getNoticeId());

            } else {
                // If not showing a notice, let the host app know
                Boolean isAccepted = AppData.getBoolean(AppData.APPDATA_EXPLICIT_ACCEPTED, false);
                Log.d(TAG, "optionalTrackerArrayList size = " + appNoticeData.optionalTrackerArrayList.size());
                HashMap<Integer, Boolean> trackerHashMap = appNoticeData.getTrackerHashMap(true);
                Log.d(TAG, "trackerHashMap size = " + trackerHashMap.size());
                appNotice_callback.onNoticeSkipped(isAccepted, trackerHashMap);
            }
        }
    }

    public HashMap<Integer, Boolean> getTrackerPreferences() {
        return AppNoticeData.getTrackerPreferences();
    }

    public boolean getAcceptedState() {
        Boolean isAccepted = false;
        if (isImpliedFlow) {
            int displayCount = AppData.getInteger(AppData.APPDATA_IMPLIED_DISPLAY_COUNT, 0);
            isAccepted = displayCount > 0? true : false;
        } else {
            isAccepted = AppData.getBoolean(AppData.APPDATA_EXPLICIT_ACCEPTED, false);
        }
        return isAccepted;
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

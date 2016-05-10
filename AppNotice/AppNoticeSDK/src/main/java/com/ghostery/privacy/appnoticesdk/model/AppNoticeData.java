package com.ghostery.privacy.appnoticesdk.model;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.util.Log;

import com.ghostery.privacy.appnoticesdk.AppNotice;
import com.ghostery.privacy.appnoticesdk.R;
import com.ghostery.privacy.appnoticesdk.callbacks.JSONGetterCallback;
import com.ghostery.privacy.appnoticesdk.utils.AppData;
import com.ghostery.privacy.appnoticesdk.utils.ServiceHandler;
import com.ghostery.privacy.appnoticesdk.utils.Session;
import com.ghostery.privacy.appnoticesdk.utils.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;


/**
 * Created by Steven.Overson on 2/26/2015.
 */

// Never instantiate directly. Use getInstance() instead.
public class AppNoticeData {
    private static final String TAG = "AppNoticeData";

    private static AppNoticeData instance;
    private static Activity activity;
    private ProgressDialog progressDialog;
    private boolean isTrackerListInitialized = false;
    private boolean isInitialized = false;
    private static String appNotice_token;
    private static int companyId;
    private static int currentNoticeId;
    private static int previousNoticeId;
    private int implied_flow_30day_display_max_default = 3;
    private int implied_flow_session_display_max_default = 1;
    private int consent_flow_dialog_opacity_default = 100;
    private final Object waitObj = new Object();
    private boolean gettingTrackerList = false;


    private final static long ELAPSED_30_DAYS_MILLIS = 2592000000L;     // Number of milliseconds in 30 days

    // 0 = company ID; 1 = pub-notice ID
    private final static String URL_JSON_REQUEST = "https://c.betrad.com/pub/c/{0}/{1}.js";
    private final static String URL_JSON_REQUEST_VIA_TOKEN = "http://privacyapi.ghosterydev.com/api/v1/appnotice/configuration/{0}";

    // 0 = Publisher ID; 1 = Owner Company ID, 2 = trackerId; 3 = optOut; 4 = uniqueVisit; 5 = firstOptOut; 6 = selectAll
    private final static String URL_SDK_OPT_IN_OUT = "https://l.betrad.com/oo/p.gif?pid={0}&ocid={1}&c={2}&et={3}&u={4}&i={5}&s={6}&m=4";

    // 0 = Publisher ID; 1 = Owner Company ID
    private final static String URL_SDK_START_CONSENT_FLOW = "http://l.betrad.com/pub/p.gif?pid={0}&ocid={1}&ii=1&mb=4";
    private final static String URL_SDK_IMPLIED_INFO_PREF = "http://l.betrad.com/pub/p.gif?pid={0}&ocid={1}&nt=4&d=1&mb=4&ic=1";
    private final static String URL_SDK_EXPLICIT_INFO_PREF = "http://l.betrad.com/pub/p.gif?pid={0}&ocid={1}&ii=1&mb=4&nt=3&d=1";
    private final static String URL_SDK_EXPLICIT_INFO_ACCEPT = "http://l.betrad.com/pub/p.gif?pid={0}&ocid={1}&mb=4&nt=3&aa=1";
    private final static String URL_SDK_EXPLICIT_INFO_DECLINE = "http://l.betrad.com/pub/p.gif?pid={0}&ocid={1}&mb=4&nt=3&aa=0";
    private final static String URL_SDK_PREF_DIRECT = "http://l.betrad.com/pub/p.gif?pid={0}&ocid={1}&mb=4&aa=0&d=0";

    public enum NoticeType {
        START_CONSENT_FLOW,
        IMPLIED_INFO_PREF,
        EXPLICIT_INFO_PREF,
        EXPLICIT_INFO_ACCEPT,
        EXPLICIT_INFO_DECLINE,
        PREF_DIRECT
    }

    // Non-JSON outer characters that surround the JSON structure
    private static final String NON_JSON_PREFIX = "__ev_hover.s(";
    private static final String NON_JSON_POSTFIX = ");";
    private static final String FILE_NOT_FOUND = "File not found";

    // Field tags
    private static final String TAG_TRACKERS_VIA_TOKEN = "vendors";                                     // Tracker list
    private static final String TAG_TRACKERS = "trackers";                                              // Tracker list

    // Field values
    private int dialog_button_color; // Button background color for these buttons: Explicit Accept button on  Consent dialog
    private String dialog_button_consent; // Button text for Accept button on Explicit Consent dialog
    private int dialog_explicit_accept_button_text_color; // Button text color for Accept button on Explicit Consent dialog
    private int dialog_background_color; // Dialog background color for Consent dialog (missing in doc)
    private String dialog_explicit_message; // Message on Explicit Consent dialog
    private int dialog_explicit_decline_button_color; // Button background color for Decline button on Explicit Consent dialog
    private String dialog_button_decline; // Button text for Decline button on Explicit Consent dialog
    private int dialog_explicit_decline_button_text_color; // Button text color for Decline button on Explicit Consent dialog
    private String dialog_header_text; // Title on Consent dialog
    private int dialog_header_text_color; // Title color on Consent dialog
    private String dialog_button_close; // Button text for Close buttons on both Implied Consent dialogs
    private String preferences_description; // Text for the header of the Manage Privacy Preferences screen
    private String preferences_header; // Text for the description section of the Manage Privacy Preferences screen
    private String dialog_implicit_message; // Message on Implied Consent dialog
    private String dialog_button_preferences; // Button text for Manage Preferences button on the Consent dialog
    private int dialog_message_text_color; // Message text color for all dialogs
    private int implied_flow_30day_display_max; // Maximum number of times the Implied Consent dialog should be displayed in 30 days.
    private float consent_flow_dialog_opacity = 1F; // Opacity setting (scale 0 to 100) for all dialogs
    private int implied_flow_session_display_max; // Maximum number of times the Implied Consent dialog should be displayed in a session.

    public ArrayList<Tracker> trackerArrayList = new ArrayList<>();


    // Public getters and setters
    public Boolean isTrackerListInitialized() { return isTrackerListInitialized; }
    public Boolean isInitialized() { return isInitialized; }
    public int getCompanyId() { return companyId; }
    public void setAppNoticeToken(String appNotice_Token) { this.appNotice_token = appNotice_token; }
    public void setCompanyId(int companyId) { this.companyId = companyId; }
    public int getNoticeId() { return currentNoticeId; }
    public void setCurrentNoticeId(int currentNoticeId) { this.currentNoticeId = currentNoticeId; }
    public void setPreviousNoticeId(int previousNoticeId) {
        this.previousNoticeId = previousNoticeId;
        AppData.setInteger(AppData.APPDATA_PREV_NOTICE_ID, previousNoticeId);
    }

    public int getDialogButtonColor() { return dialog_button_color; }
    public String getDialogButtonConsent() { return dialog_button_consent; }
    public int getDialogExplicitAcceptButtonTextColor() { return dialog_explicit_accept_button_text_color; }
    public int getDialogBackgroundColor() { return dialog_background_color; }
    public String getDialogExplicitMessage() { return dialog_explicit_message; }
    public int getDialogExplicitDeclineButtonColor() { return dialog_explicit_decline_button_color; }
    public String getDialogButtonDecline() { return dialog_button_decline; }
    public int getDialogExplicitDeclineButtonTextColor() { return dialog_explicit_decline_button_text_color; }
    public String getDialogHeaderText() { return dialog_header_text; }
    public int getDialogHeaderTextColor() { return dialog_header_text_color; }
    public String getDialogButtonClose() { return dialog_button_close; }
    public String getPreferencesDescription() { return preferences_description; }
    public String getPreferencesHeader() { return preferences_header; }
    public String getDialogImplicitMessage() { return dialog_implicit_message; }
    public String getDialogButtonPreferences() { return dialog_button_preferences; }
    public int getDialogMessageTextColor() { return dialog_message_text_color; }
    public int getImpliedFlow30DayDisplayMax() { return implied_flow_30day_display_max; }
    public float getConsentFlowDialogOpacity() { return consent_flow_dialog_opacity / 100; }
    public int getImpliedFlowSessionDisplayMax() { return implied_flow_session_display_max; }


    // Single instance
    public static synchronized AppNoticeData getInstance(Activity _activity)
    {
        activity = _activity;

        // Ensure the app only uses one instance of this class.
        if (instance == null) {
            instance = (AppNoticeData) Session.get(Session.APPNOTICE_DATA, new AppNoticeData());

            // Save this instance object in the app session
            Session.set(Session.APPNOTICE_DATA, instance);
        }

        return instance;
    }

    // Constructor
    private AppNoticeData() {
        // Pre-populate the max values with defaults just in case the JSON object can't be retrieved
        implied_flow_30day_display_max = implied_flow_30day_display_max_default = activity.getResources().getInteger(R.integer.ghostery_implied_flow_30day_display_max);
        implied_flow_session_display_max = implied_flow_session_display_max_default = activity.getResources().getInteger(R.integer.ghostery_implied_flow_session_display_max);
        consent_flow_dialog_opacity = consent_flow_dialog_opacity_default = activity.getResources().getInteger(R.integer.ghostery_consent_flow_dialog_opacity);
    }

    public HashMap<Integer, Boolean> getTrackerHashMap(boolean useTrackerIdAsInt) {
        HashMap trackerHashMap = new HashMap();

        // Loop through the tracker list and add non-essential tracker IDs and their on/off state
        for (Tracker tracker : trackerArrayList) {
            if (!tracker.isEssential() && !isTrackerDuplicateOfEssentialTracker(tracker.getTrackerId())) {
                if (useTrackerIdAsInt)
                    trackerHashMap.put(tracker.getTrackerId(), tracker.isOn());
                else
                    trackerHashMap.put(Integer.toString(tracker.getTrackerId()), tracker.isOn());
            }
        }

        return trackerHashMap;
    }

    public ArrayList<Tracker> getTrackerListClone() {
        ArrayList<Tracker> trackerArrayListClone = new ArrayList<>();

        // Loop through the tracker list and add all tracker IDs and their on/off state
        for (Tracker tracker : trackerArrayList) {
            trackerArrayListClone.add(new Tracker(tracker));
        }
        return trackerArrayListClone;
    }

    // Returns requested tracker. If not found, returns null.
    public Tracker getTrackerById(int uId) {
        // Loop through the tracker list and add non-essential tracker IDs and their on/off state
        for (Tracker tracker : trackerArrayList) {
            if (tracker.uId == uId) {
                return tracker;
            }
        }
        return null;
    }

    // Sets all the specified non-essential tracker (and all duplicate non-essential trackers) on/off state to the specified value.
    public void setTrackerOnOffState(int uId, boolean isOn) {
        Tracker selectedTracker = getTrackerById(uId);  // Get the tracker object for the specified tracker
        if (!selectedTracker.isEssential() && !isTrackerDuplicateOfEssentialTracker(selectedTracker.getTrackerId())) {
            int selectedTrackerId = selectedTracker.getTrackerId();
            for (Tracker tracker : trackerArrayList) {
                if (tracker.getTrackerId() == selectedTrackerId) {
                    tracker.setOnOffState(isOn);
                }
            }
        }
    }

    // Sets all non-essential tracker on/off states to the specified value.
    public void setTrackerOnOffState(boolean isOn) {
        for (Tracker tracker : trackerArrayList) {
            if (!tracker.isEssential() && !isTrackerDuplicateOfEssentialTracker(tracker.getTrackerId())) {
                tracker.setOnOffState(isOn);
            }
        }
    }

    // Returns 1 if all on; 0 if some on and some off; -1 if all off
    public int getTrackerOnOffStates() {
        int trackerCount = 0;
        int trackerOnCount = 0;
        for (Tracker tracker : trackerArrayList) {
            if (!tracker.isEssential() && !isTrackerDuplicateOfEssentialTracker(tracker.getTrackerId())) {
                trackerCount++;
                if (tracker.isOn()) {
                    trackerOnCount++;
                }
            }
        }

        int trackerOnOffStates = trackerOnCount == trackerCount ? 1 : trackerOnCount == 0 ? -1 : 0;
        return trackerOnOffStates;
    }

    // Returns the number of non-essential trackers
    public int getNonEssentialTrackerCount() {
        int nonEssentialTrackerCount = 0;    // Assume no changes

        // Count non-essential trackers
        for (int i = 0; i < trackerArrayList.size(); i++) {
            Tracker tracker = trackerArrayList.get(i);

            // If the tracker is non-essential...
            if (!tracker.isEssential() && !isTrackerDuplicateOfEssentialTracker(tracker.getTrackerId())) {
                nonEssentialTrackerCount++;
            }
        }

        return nonEssentialTrackerCount;
    }

    // Returns the number of non-essential trackers
    public boolean isTrackerDuplicateOfEssentialTracker(int trackerId) {
        Boolean isTrackerDuplicateOfEssentialTracker = false;    // Assume not a duplicate

        // Look for duplicate essential trackers
        for (int i = 0; i < trackerArrayList.size(); i++) {
            Tracker tracker = trackerArrayList.get(i);

            // If the tracker is essential...
            if (tracker.isEssential()) {
                // If the tracker is a duplicate...
                if (tracker.getTrackerId() == trackerId) {
                    isTrackerDuplicateOfEssentialTracker = true;
                    break;
                }
            } else {
                // All essential trackers should be listed first, so when we get to a non-essential tracker, we can stop looking
                break;
            }
        }

        return isTrackerDuplicateOfEssentialTracker;
    }

    // Returns the number of non-essential trackers that have changed on/off state since the original tracker was captured
    public int getTrackerStateChangeCount(ArrayList<Tracker> originalTrackerArrayList) {
        int changeCount = 0;    // Assume no changes

        // Send opt-in/out ping-back for each changed non-essential tracker
        if (trackerArrayList != null && originalTrackerArrayList != null &&
                trackerArrayList.size() == originalTrackerArrayList.size()) {

            for (int i = 0; i < trackerArrayList.size(); i++) {
                Tracker tracker = trackerArrayList.get(i);
                Tracker originalTracker = originalTrackerArrayList.get(i);

                // If the tracker is non-essential and is changed...
                if (!tracker.isEssential() && (tracker.isOn() != originalTracker.isOn())) {
                    changeCount++;
                }
            }
        }

        return changeCount;
    }

    // Sends an opt-in/out ping back through the opt-in-out chanel
    public static void sendOptInOutNotice(final int trackerId, final boolean optOut, final boolean uniqueVisit, final boolean firstOptOut, final boolean selectAll) {
        // Use a non-UI thread
        new Thread(){
            public void run(){
                Object[] urlParams = new Object[7];
                urlParams[0] = String.valueOf(currentNoticeId);	// 0
                urlParams[1] = String.valueOf(companyId);		// 1
                urlParams[2] = String.valueOf(trackerId);		// 2
                urlParams[3] = optOut ? "1" : "0";  		    // 3
                urlParams[4] = uniqueVisit ? "1" : "0";	    	// 4
                urlParams[5] = firstOptOut ? "1" : "0";		    // 5
                urlParams[6] = selectAll ? "1" : "0";	    	// 6

                String uRL = MessageFormat.format(URL_SDK_OPT_IN_OUT, urlParams);

                if (uRL != null && uRL.length() > 0) {
                    Log.d(TAG, "Sending notice beacon: (type=OptInOut) " + uRL);
                    try{
                        ServiceHandler serviceHandler = new ServiceHandler();
                        String temp = serviceHandler.getRequest(uRL);
                    }catch(Exception e){
                        Log.e(TAG, "Error sending notice beacon: (type=OptInOut) " + uRL, e);
                    }
                } else {
                    Log.e(TAG, "No URL found for Opt-In/Out.");
                }
            }
        }.start();
    }

    // Sends a report back through the Site Notice Channel
    public static void sendNotice(final NoticeType type) {
        // Use a non-UI thread
        new Thread(){
            public void run(){
                Object[] urlParams = new Object[2];
                urlParams[0] = String.valueOf(currentNoticeId);	// 0
                urlParams[1] = String.valueOf(companyId);		// 1

                String uRL = "";

                switch (type) {
                    case START_CONSENT_FLOW:
                        uRL = MessageFormat.format(URL_SDK_START_CONSENT_FLOW, urlParams);
                        break;
                    case IMPLIED_INFO_PREF:
                        uRL = MessageFormat.format(URL_SDK_IMPLIED_INFO_PREF, urlParams);
                        break;
                    case EXPLICIT_INFO_PREF:
                        uRL = MessageFormat.format(URL_SDK_EXPLICIT_INFO_PREF, urlParams);
                        break;
                    case EXPLICIT_INFO_ACCEPT:
                        uRL = MessageFormat.format(URL_SDK_EXPLICIT_INFO_ACCEPT, urlParams);
                        break;
                    case EXPLICIT_INFO_DECLINE:
                        uRL = MessageFormat.format(URL_SDK_EXPLICIT_INFO_DECLINE, urlParams);
                        break;
                    case PREF_DIRECT:
                        uRL = MessageFormat.format(URL_SDK_PREF_DIRECT, urlParams);
                        break;
                }

                if (uRL != null && uRL.length() > 0) {
                    Log.d(TAG, "Sending notice beacon: (type=" + type + ") " + uRL);
                    try{
//                        if (Network.isNetworkAvailable(fragmentActivity)) {
//                            // Split the supplied URL into usable parts for the service call
//                            String[] uRlParts = uRL.split("\\?");
//                            String[] params = uRlParts[1].split("\\&");
//                            List paramList = new ArrayList();
//                            for (String param : params) {
//                                String[] paramParts = param.split("\\=");
//                                BasicNameValuePair nameValuePair = new BasicNameValuePair(paramParts[0], paramParts[1]);
//                                paramList.add(nameValuePair);
//                            }
//
//                            ServiceHandler sh = new ServiceHandler();
//                            String temp = sh.makeServiceCall(uRlParts[0], ServiceHandler.POST, paramList);
//                        } else {
//                            Log.e(TAG, "No network connection for sending notice beacon: (type=" + type + ")" + uRL);
//                        }
                        ServiceHandler serviceHandler = new ServiceHandler();
                        String temp = serviceHandler.getRequest(uRL);
                    }catch(Exception e){
                        Log.e(TAG, "Error sending notice beacon: (type=" + type + ")" + uRL, e);
                    }
                } else {
                    Log.e(TAG, "No URL found due to incorrect notification type.");
                }
            }
        }.start();
    }

    // Init
    public void init() {
        Resources resources = activity.getResources();

        dialog_button_color = resources.getColor(R.color.ghostery_dialog_button_color);
        dialog_button_consent = resources.getString(R.string.ghostery_dialog_button_consent);
        dialog_explicit_accept_button_text_color = resources.getColor(R.color.ghostery_dialog_explicit_accept_button_text_color);
        dialog_background_color = resources.getColor(R.color.ghostery_dialog_background_color);
        dialog_explicit_message = resources.getString(R.string.ghostery_dialog_explicit_message);
        dialog_explicit_decline_button_color = resources.getColor(R.color.ghostery_dialog_explicit_decline_button_color);
        dialog_button_decline = resources.getString(R.string.ghostery_dialog_button_decline);
        dialog_explicit_decline_button_text_color = resources.getColor(R.color.ghostery_dialog_explicit_decline_button_text_color);
        dialog_header_text = resources.getString(R.string.ghostery_dialog_header_text);
        dialog_header_text_color = resources.getColor(R.color.ghostery_dialog_header_text_color);
        dialog_button_close = resources.getString(R.string.ghostery_dialog_button_close);
        preferences_description = resources.getString(R.string.ghostery_preferences_description);
        preferences_header = resources.getString(R.string.ghostery_preferences_header);
        dialog_implicit_message = resources.getString(R.string.ghostery_dialog_implicit_message);
        dialog_button_preferences = resources.getString(R.string.ghostery_dialog_button_preferences);
        dialog_message_text_color = resources.getColor(R.color.ghostery_dialog_message_text_color);
        implied_flow_30day_display_max = implied_flow_30day_display_max_default;
        consent_flow_dialog_opacity = consent_flow_dialog_opacity_default;
        implied_flow_session_display_max = implied_flow_session_display_max_default;

        previousNoticeId = AppData.getInteger(AppData.APPDATA_PREV_NOTICE_ID, 0);

        isInitialized = true;
    }

    // Init
    public void initTrackerList(final JSONGetterCallback mJSONGetterCallback) {
        assert(Thread.currentThread().getName().equals(Util.THREAD_INITTRACKERLIST));
        synchronized(waitObj) {
            while (gettingTrackerList) {
                try {
                    Log.d(TAG, "initTrackerList: Waiting for tracker list to be filled.");
                    waitObj.wait();
                } catch (InterruptedException e) {
                    // Do nothing
                    Log.d(TAG, "initTrackerList: Wait interrupted while filling tracker list.");
                }
            }
            Log.d(TAG, "initTrackerList: gettingTrackerList = " + Boolean.toString(gettingTrackerList));

            // Check to see if we have a current cached tracker list
            String previousJson = AppData.getString(AppData.APPDATA_PREV_JSON, "");
            if (currentNoticeId == previousNoticeId && !previousJson.isEmpty()) {
                fillTrackerList(previousJson);

                // Restore the selection states of the trackers
                restoreTrackerStates();

                // Let the specified callback know it finished...
                if (mJSONGetterCallback != null) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mJSONGetterCallback.onTaskDone();
                        }
                    });
                }

                // Save the tracker states
                saveTrackerStates();
            } else {
                // Start the call to get the AppNoticeData data from the service...from the UI thread
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        gettingTrackerList = true;
                        JSONGetter mJSONGetter = new JSONGetter(mJSONGetterCallback);
                        mJSONGetter.execute();
                    }
                });
            }
        }
    }

    // Determine if the Implicit notice should be shown. True = show notice; False = don't show notice.
    public boolean getImplicitNoticeDisplayStatus() {
        Boolean showNotice = true;     // Assume we need to show the notice

        if (implied_flow_30day_display_max <= 0) {
            // If the notice ID has changed, we need to show the notice again
            if (currentNoticeId == previousNoticeId) {
                showNotice = false;
            } else {
                showNotice = true;
            }
        } else {
            long currentTime = System.currentTimeMillis();
            int implicit_display_count = (int) AppData.getInteger(AppData.APPDATA_IMPLICIT_DISPLAY_COUNT, 0);
            long implicit_last_display_time = (long) AppData.getLong(AppData.APPDATA_IMPLICIT_LAST_DISPLAY_TIME, 0L);
            int impliedFlow_SessionCount = (int) Session.get(Session.SYS_CURRENT_SESSION_COUNT, 0);

            if (implicit_last_display_time == 0L) {     // If this is the first pass...
                implicit_last_display_time = currentTime;
                AppData.setLong(AppData.APPDATA_IMPLICIT_LAST_DISPLAY_TIME, implicit_last_display_time);
            }

            if (impliedFlow_SessionCount >= implied_flow_session_display_max) { // If displayed enough in this session...
                showNotice = false; // don't display it now
            } else {
                if (currentTime <= implicit_last_display_time + ELAPSED_30_DAYS_MILLIS) { // If displayed less than 30 days ago...
                    if (implicit_display_count >= implied_flow_30day_display_max) { // If displayed enough in last 30 days...
                        showNotice = false; // don't display it now
                    }
                } else {
                    // If it's been more than 30 days...
                    AppData.setInteger(AppData.APPDATA_IMPLICIT_DISPLAY_COUNT, 0); // Reset the display count to 0
                    AppData.setLong(AppData.APPDATA_IMPLICIT_LAST_DISPLAY_TIME, currentTime); // And reset the last display time
                }
            }
        }

        return showNotice;
    }

    // Determine if the Explicit notice should be shown. True = show notice; False = don't show notice.
    public boolean getExplicitNoticeDisplayStatus() {
        Boolean displayStatus = true;     // Assume we need to show the notice
        if (AppNotice.usingExplicitStrictMode) {
            Boolean isExplicitAccepted = (boolean) AppData.getBoolean(AppData.APPDATA_EXPLICIT_ACCEPTED, false);
            displayStatus = !isExplicitAccepted;     // If not accepted, display notice; and vice-versa
        } else {
            // If the notice ID has changed, we need to show the notice again
            if (currentNoticeId == previousNoticeId) {
                displayStatus = false;
            }
        }
        return displayStatus;
    }

    public static void incrementImplicitNoticeDisplayCount() {
        long implicit_last_display_time = (long) AppData.getLong(AppData.APPDATA_IMPLICIT_LAST_DISPLAY_TIME, 0L);

        // Increment the implicit display count
        int currentDisplayCount = AppData.getInteger(AppData.APPDATA_IMPLICIT_DISPLAY_COUNT, 0);
        AppData.setInteger(AppData.APPDATA_IMPLICIT_DISPLAY_COUNT, currentDisplayCount + 1);

        // Increment the implicit session display count
        int currentSessionCount = (int)Session.get(Session.SYS_CURRENT_SESSION_COUNT, 0);
        Session.set(Session.SYS_CURRENT_SESSION_COUNT, currentSessionCount + 1);

        long currentTime = System.currentTimeMillis();
        if (currentDisplayCount == 0) {             // If this is the first time being displayed in this 30-day period...
            AppData.setLong(AppData.APPDATA_IMPLICIT_LAST_DISPLAY_TIME, currentTime);   //   reset the last display time to now
        }
    }

    public void saveTrackerStates() {
        HashMap trackerHashMap = getTrackerHashMap(false);     // Use tracker ID as a string
        if (!trackerHashMap.isEmpty()) {    // Don't override last saved states if we don't have any trackers in the map
            JSONObject trackerStateJSONObject = new JSONObject(trackerHashMap);
            String trackerStatesString = trackerStateJSONObject.toString();
            AppData.setString(AppData.APPDATA_TRACKERSTATES, trackerStatesString);
        }
    }

    public void restoreTrackerStates() {
        HashMap trackerHashMap = getTrackerPreferences();

        // Look for a non-essential tracker with the same ID. If found, set its state.
        for (Tracker tracker : trackerArrayList) {
            if (!tracker.isEssential()) {
                Boolean trackerState = (Boolean)trackerHashMap.get(tracker.getTrackerId());

                if (trackerState != null)
                    tracker.setOnOffState(trackerState);
            }
        }
    }

    // Gets the tracker preferences directly from AppData just in case the AppNoticeData object hasn't been inite'ed yet.
    public static HashMap getTrackerPreferences() {
        HashMap trackerHashMap = new HashMap();

        String trackerStatesString = AppData.getString(AppData.APPDATA_TRACKERSTATES);

        if (trackerStatesString.length() > 2) {
            // Strip off the brackets
            trackerStatesString = trackerStatesString.substring(1, trackerStatesString.length() - 1);
            String[] trackerStatesArray = trackerStatesString.split(",");

            for (String trackerStatePairString : trackerStatesArray) {
                String[] trackerStateArray = trackerStatePairString.split(":");
                String trackerIdString = trackerStateArray[0];
                String trackerStateString = trackerStateArray[1];

                // Strip off the quote marks
                trackerIdString = trackerIdString.substring(1, trackerIdString.length() - 1);

                // Get the actual key/value pair
                int trackerId = Integer.parseInt(trackerIdString);
                Boolean trackerState = Boolean.parseBoolean(trackerStateString);

                // Add this tracker state to the hashmap
                trackerHashMap.put(trackerId, trackerState);

                Log.d(TAG, trackerIdString + " : " + trackerStatePairString);
            }
        }

        return trackerHashMap;
    }

    public void fillTrackerList(String jsonStr) {
        try {
            JSONObject jsonObj = null;

            if (jsonStr != null && jsonStr.length() > 20 && !jsonStr.startsWith(FILE_NOT_FOUND)){
                // Strip off the not-JSON outer characters
                if (jsonStr.startsWith(NON_JSON_PREFIX))
                    jsonStr = jsonStr.substring(NON_JSON_PREFIX.length());
                if (jsonStr.endsWith(NON_JSON_POSTFIX))
                    jsonStr = jsonStr.substring(0, jsonStr.length() - NON_JSON_POSTFIX.length());

                jsonObj = new JSONObject(jsonStr);
            }

            if (jsonObj != null) {
                try {
                    String trackerJSONString;
                    if (AppNotice.usingToken) {
                        trackerJSONString = jsonObj.isNull(TAG_TRACKERS_VIA_TOKEN)? null : jsonObj.getString(TAG_TRACKERS_VIA_TOKEN);
                    } else {
                        trackerJSONString = jsonObj.isNull(TAG_TRACKERS)? null : jsonObj.getString(TAG_TRACKERS);
                    }

                    if (trackerJSONString != null) {
                        JSONArray trackerJSONArray = new JSONArray(trackerJSONString);
                        trackerArrayList.clear();

                        int id;
                        for (int i = 0; i < trackerJSONArray.length(); i++) {
                            JSONObject trackerJSONObject = trackerJSONArray.getJSONObject(i);
                            Tracker tracker = new Tracker(trackerJSONObject);
                            trackerArrayList.add(tracker);
                            Log.d(TAG, "Add tracker: " + tracker.getTrackerId() + " (" + tracker.getName() + ")");
                        }

                        // Sort by category and then by name within category
                        Collections.sort(trackerArrayList, new Comparator<Tracker>() {
                            @Override
                            public int compare(Tracker tracker1, Tracker tracker2) {
                                int result = 0;

                                // Sort first by category...keeping "Essential" at the top
                                if (tracker1.isEssential() && tracker2.isEssential()) {
                                    result = 0;
                                } else if (tracker1.isEssential()) {
                                    result = -1;
                                } else if (tracker2.isEssential()) {
                                    result = 1;
                                } else {
                                    // Sort by non-essential category
                                    String tracker1_category = tracker1.getCategory().toUpperCase();
                                    String tracker2_category = tracker2.getCategory().toUpperCase();

                                    //ascending order
                                    result = tracker1_category.compareTo(tracker2_category);
//                                    result = tracker1.getCategory().compareToIgnoreCase(tracker2.getCategory());
                                }

                                // If it's in the same category, then sort by tracker name
                                if (result == 0) {
                                    String tracker1_name = tracker1.getName().toUpperCase();
                                    String tracker2_name = tracker2.getName().toUpperCase();

                                    //ascending order
                                    result = tracker1_name.compareTo(tracker2_name);
//                                    result = tracker1.getName().compareToIgnoreCase(tracker2.getName());
                                }
                                return result;
                            }
                        });

                        // Set header bit for first tracker in each category
                        String categoryName = "";
                        for (int i = 0; i < trackerArrayList.size(); i++) {
                            Tracker tracker = trackerArrayList.get(i);
                            tracker.uId = i;        // Set the tracker's unique ID

                            // Flag tracker as having a header if this is the first tracker or if the category name is new
                            if (i == 0 || !tracker.getCategory().equalsIgnoreCase(categoryName)) {
                                tracker.setHasHeader();
                            }
                            categoryName = tracker.getCategory();
                        }
                    }

                    isTrackerListInitialized = true;
                } catch (JSONException e) {
                    Log.e(TAG, "JSONException while parsing the JSON object.", e);
                }
            }

        } catch (NumberFormatException e) {
            Log.e(TAG, "Exception while parsing elements of the JSON object", e);
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "NotFoundException while getting the JSON object", e);
        } catch (Exception e) {
            Log.e(TAG, "Exception while getting or parsing the JSON object.", e);
        }
    }


    // =================================================================================
    // =================================================================================

    // Async task to get AppNoticeData data from a URL
    private class JSONGetter extends AsyncTask<Void, Void, Void> {

        private JSONGetterCallback mJSONGetterCallback;

        public JSONGetter(JSONGetterCallback mJSONGetterCallback) {
            this.mJSONGetterCallback = mJSONGetterCallback;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if(!activity.isFinishing())
            {
                // Make sure we don't have two dialogs created
                if((progressDialog != null) && progressDialog.isShowing() ) {
                    Log.d(TAG, "Wait dialog already showing.");
                } else {
                    // Showing progress dialog
                    progressDialog = new ProgressDialog(activity);
                    progressDialog.setMessage(activity.getResources().getString(R.string.ghostery_dialog_pleaseWait));
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                }

            }
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            trackerArrayList.clear();       // Start with an empty tracker array

            // Creating service handler class instance
            ServiceHandler serviceHandler = new ServiceHandler();

            // Make a request to url for the AppNoticeData info
            String url = getFormattedJSONUrl();









            String jsonStr = serviceHandler.getRequest(url);
            fillTrackerList(jsonStr);

            // If this JSON string was parsed successfully, cache it for later use
            if (isTrackerListInitialized) {
                AppData.setString(AppData.APPDATA_PREV_JSON, jsonStr);
            } else {
                Log.d(TAG, "Failed to fill tracker list.");
//                // If fillTrackerList didn't initialize the tracker list, we need to end the wait-object here
//                gettingTrackerList = false;
//                synchronized(waitObj) {
//                    Log.d(TAG, "Finished filling tracker list.");
//                    waitObj.notifyAll();
//                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            // Restore the selection states of the trackers
            restoreTrackerStates();

            // Save the tracker states
            saveTrackerStates();

            // Dismiss the progress dialog
            if((progressDialog != null) && progressDialog.isShowing() ){
                try {
                    progressDialog.dismiss();
                } catch (Exception e) {}
            }

            // End the wait-object
            gettingTrackerList = false;
            synchronized(waitObj) {
                Log.d(TAG, "Finished filling tracker list.");
                waitObj.notifyAll();
            }

            // Let the specified callback know it finished...
            if (mJSONGetterCallback != null) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mJSONGetterCallback.onTaskDone();
                    }
                });
            }
        }

        protected String getFormattedJSONUrl() {
            String formattedJSONUrl = "";
            if (AppNotice.usingToken) {
                Object[] urlParams = new Object[1];
                urlParams[0] = String.valueOf(appNotice_token);			// 0
                formattedJSONUrl = MessageFormat.format(URL_JSON_REQUEST_VIA_TOKEN, urlParams);
            } else {
                Object[] urlParams = new Object[2];
                urlParams[0] = String.valueOf(companyId);			// 0
                urlParams[1] = String.valueOf(currentNoticeId);		// 1
                formattedJSONUrl = MessageFormat.format(URL_JSON_REQUEST, urlParams);
            }
            return formattedJSONUrl;
        }

    }
}

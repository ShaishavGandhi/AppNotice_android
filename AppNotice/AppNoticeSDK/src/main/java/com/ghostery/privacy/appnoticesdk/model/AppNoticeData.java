package com.ghostery.privacy.appnoticesdk.model;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;

import com.ghostery.privacy.appnoticesdk.R;
import com.ghostery.privacy.appnoticesdk.callbacks.JSONGetterCallback;
import com.ghostery.privacy.appnoticesdk.utils.AppData;
import com.ghostery.privacy.appnoticesdk.utils.ServiceHandler;
import com.ghostery.privacy.appnoticesdk.utils.Session;

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
    public boolean useRemoteValues = true;

    private static AppNoticeData instance;
    private static Activity activity;
    private ProgressDialog progressDialog;
    private boolean initialized = false;
    private static int companyId;
    private static int configId;
    private int ric_max_default = 3;
    private int ric_session_max_default = 1;
    private int ric_opacity_default = 100;

    private final static String TAG_APPNOTICEDATA = "appnoticedata";
    private final static long ELAPSED_30_DAYS_MILLIS = 2592000000L;     // Number of milliseconds in 30 days

    // 0 = company ID; 1 = pub-notice ID
    private final static String URL_JSON_REQUEST = "https://c.betrad.com/pub/c/{0}/{1}.js";

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
    private static final String TAG_BRIC = "bric";                                                      // If true, display the Explicit Consent dialog; If false, display the Implied Consent dialog
    private static final String TAG_BRIC_ACCESS_BUTTON_COLOR = "bric_access_button_color";              // Button background color for these buttons: Explicit Accept button on  Consent dialog
    private static final String TAG_BRIC_ACCESS_BUTTON_TEXT = "bric_access_button_text";                // Button text for Accept button on Explicit Consent dialog
    private static final String TAG_BRIC_ACCESS_BUTTON_TEXT_COLOR = "bric_access_button_text_color";    // Button text color for Accept button on Explicit Consent dialog
    private static final String TAG_BRIC_BG = "bric_bg";                                                // Dialog background color for Explicit Consent dialog (missing in doc)
    private static final String TAG_BRIC_CONTENT1 = "bric_content1";                                    // Message on Explicit Consent dialog (Combine bric_content1, bric_content2 and bric_content3 into paragraphs of this message)
    private static final String TAG_BRIC_DECLINE_BUTTON_COLOR = "bric_decline_button_color";            // Button background color for Decline button on Explicit Consent dialog
    private static final String TAG_BRIC_DECLINE_BUTTON_TEXT = "bric_decline_button_text";              // Button text for Decline button on Explicit Consent dialog
    private static final String TAG_BRIC_DECLINE_BUTTON_TEXT_COLOR = "bric_decline_button_text_color";  // Button text color for Decline button on Explicit Consent dialog
    private static final String TAG_BRIC_HEADER_TEXT = "bric_header_text";                              // Title on Explicit Consent dialog
    private static final String TAG_BRIC_HEADER_TEXT_COLOR = "bric_header_text_color";                  // Title color on Explicit Consent dialog
    private static final String TAG_CLOSE_BUTTON = "close_button";                                      // Button text for Close buttons on both Implied Consent and Explicit Consent dialogs
    private static final String TAG_MANAGE_PREFERENCES_DESCRIPTION = "manage_preferences_description";  // Text for the header of the Manage Privacy Preferences screen
    private static final String TAG_MANAGE_PREFERENCES_HEADER = "manage_preferences_header";            // Text for the description section of the Manage Privacy Preferences screen
    private static final String TAG_RIC = "ric";                                                        // Message on Implied Consent dialog
    private static final String TAG_RIC_BG = "ric_bg";                                                  // Dialog background color for Implied Consent dialog
    private static final String TAG_RIC_CLICK_MANAGE_SETTINGS = "ric_click_manage_settings";            // Button text for Manage Preferences button on Explicit Consent dialog
    private static final String TAG_RIC_COLOR = "ric_color";                                            // Message text color for all dialogs
    private static final String TAG_RIC_MAX = "ric_max";                                                // Maximum number of times the Implied Consent dialog should be displayed in 30 days.
    private static final String TAG_RIC_OPACITY = "ric_opacity";                                        // Opacity setting (scale 0 to 100) for all dialogs
    private static final String TAG_RIC_SESSION_MAX = "ric_session_max";                                // Maximum number of times the Implied Consent dialog should be displayed in a session.
    private static final String TAG_RIC_TITLE = "ric_title";                                            // Title on Implied Consent dialog
    private static final String TAG_RIC_TITLE_COLOR = "ric_title_color";                                // Title color on Implied Consent dialog
    private static final String TAG_TRACKERS = "trackers";                                              // Tracker list

    // Field values
    private Boolean bric = false;
    private int bric_access_button_color;
    private String bric_access_button_text;
    private int bric_access_button_text_color;
    private int bric_bg;
    private String bric_content1;
    private int bric_decline_button_color;
    private String bric_decline_button_text;
    private int bric_decline_button_text_color;
    private String bric_header_text;
    private int bric_header_text_color;
    private String close_button;
    private String manage_preferences_description;
    private String manage_preferences_header;
    private String ric;
    private int ric_bg;
    private String ric_click_manage_settings;
    private int ric_color;
    private int ric_max;
//    private String ric_maxString;
    private float ric_opacity = 1F;
//    private String ric_opacityString;
    private int ric_session_max;
//    private String ric_session_maxString;
    private String ric_title;
    private int ric_title_color;

    public ArrayList<Tracker> trackerArrayList = new ArrayList<>();


    // Public getters and setters
    public Boolean isInitialized() { return initialized; }
    public int getCompanyId() { return companyId; }
    public void setCompanyId(int companyId) { this.companyId = companyId; }
    public int getConfigId() { return configId; }
    public void setConfigId(int configId) { this.configId = configId; }

    public Boolean getBric() { return bric != null ? bric : true; }
    public int getBric_access_button_color() { return bric_access_button_color; }
    public String getBric_access_button_text() { return bric_access_button_text; }
    public int getBric_access_button_text_color() { return bric_access_button_text_color; }
    public int getBric_bg() { return bric_bg; }
    public String getBric_content1() { return bric_content1; }
    public int getBric_decline_button_color() { return bric_decline_button_color; }
    public String getBric_decline_button_text() { return bric_decline_button_text; }
    public int getBric_decline_button_text_color() { return bric_decline_button_text_color; }
    public String getBric_header_text() { return bric_header_text; }
    public int getBric_header_text_color() { return bric_header_text_color; }
    public String getClose_button() { return close_button; }
    public String getManage_preferences_description() { return manage_preferences_description; }
    public String getManage_preferences_header() { return manage_preferences_header; }
    public String getRic() { return ric; }
    public int getRic_bg() { return ric_bg; }
    public String getRic_click_manage_settings() { return ric_click_manage_settings; }
    public int getRic_color() { return ric_color; }
    public int getRic_max() { return ric_max; }
    public float getRic_opacity() { return ric_opacity / 100; }
    public int getRic_session_max() { return ric_session_max; }
    public String getRic_title() { return ric_title; }
    public int getRic_title_color() { return ric_title_color; }
//    public ArrayList<Tracker> getTrackerArrayList() { return trackerArrayList; }


    // Single instance
    public static synchronized AppNoticeData getInstance(Activity _activity)
    {
        activity = _activity;

        // Ensure the app only uses one instance of this class.
        if (instance == null)
            instance = (AppNoticeData) Session.get(Session.APPNOTICE_DATA, new AppNoticeData());

        return instance;
    }

    // Constructor
    private AppNoticeData() {
        // Pre-populate the max values with defaults just in case the JSON object can't be retrieved
        ric_max = ric_max_default = activity.getResources().getInteger(R.integer.ghostery_implied_flow_30day_display_max);
        ric_session_max = ric_session_max_default = activity.getResources().getInteger(R.integer.ghostery_implied_flow_session_display_max);
        ric_opacity = ric_opacity_default = activity.getResources().getInteger(R.integer.ghostery_consent_flow_dialog_opacity);
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

        // Loop through the tracker list and add non-essential tracker IDs and their on/off state
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
        boolean isTrackerDuplicateOfEssentialTracker = false;    // Assume not a duplicate

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
        for (int i = 0; i < trackerArrayList.size(); i++) {
            Tracker tracker = trackerArrayList.get(i);
            Tracker originalTracker = originalTrackerArrayList.get(i);

            // If the tracker is non-essential and is changed...
            if (!tracker.isEssential() && (tracker.isOn() != originalTracker.isOn())) {
                changeCount++;
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
                urlParams[0] = String.valueOf(configId);	// 0
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
                        ServiceHandler sh = new ServiceHandler();
                        String temp = sh.makeServiceCall(uRL, ServiceHandler.POST);
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
                urlParams[0] = String.valueOf(configId);	// 0
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
                        ServiceHandler sh = new ServiceHandler();
                        String temp = sh.makeServiceCall(uRL, ServiceHandler.POST);
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
    public void init(JSONGetterCallback mJSONGetterCallback) {
        // Start the call to get the AppNoticeData data from the service
        JSONGetter mJSONGetter = new JSONGetter(mJSONGetterCallback);
        mJSONGetter.execute();
    }

    // Determine if the Implicit notice should be shown. True = show notice; False = don't show notice.
    public boolean getImplicitNoticeDisplayStatus() {
        boolean showNotice = true;     // Assume we need to show the notice
        long currentTime = System.currentTimeMillis();
        int implicit_display_count = (int) AppData.getInteger(AppData.APPDATA_IMPLICIT_DISPLAY_COUNT, 0);
        long implicit_last_display_time = (long) AppData.getLong(AppData.APPDATA_IMPLICIT_LAST_DISPLAY_TIME, 0L);
        int ric_session_count = (int) Session.get(Session.SYS_RIC_SESSION_COUNT, 0);

        if (implicit_last_display_time == 0L) {     // If this is the first pass...
            implicit_last_display_time = currentTime;
            AppData.setLong(AppData.APPDATA_IMPLICIT_LAST_DISPLAY_TIME, implicit_last_display_time);
        }

        if (ric_session_count >= ric_session_max) {                 // If displayed enough in this session...
            showNotice = false;                                     //    don't display it now
        } else {
            if (currentTime <= implicit_last_display_time + ELAPSED_30_DAYS_MILLIS) {     // If displayed less than 30 days ago...
                if (implicit_display_count >= ric_max) {                                  // If displayed enough in last 30 days...
                    showNotice = false;                                                   //    don't display it now
                }
            } else {
                // If it's been more than 30 days...
                AppData.setInteger(AppData.APPDATA_IMPLICIT_DISPLAY_COUNT, 0);    // Reset the display count to 0
                AppData.setLong(AppData.APPDATA_IMPLICIT_LAST_DISPLAY_TIME, currentTime);    // And reset the last display time
            }
        }

        return showNotice;
    }

    // Determine if the Explicit notice should be shown. True = show notice; False = don't show notice.
    public boolean getExplicitNoticeDisplayStatus() {
        boolean isExplicitAccepted = (boolean) AppData.getBoolean(AppData.APPDATA_EXPLICIT_ACCEPTED, false);
        return !isExplicitAccepted;     // If not accepted, display notice; and vice-versa
    }

    public static void incrementImplicitNoticeDisplayCount() {
        long implicit_last_display_time = (long) AppData.getLong(AppData.APPDATA_IMPLICIT_LAST_DISPLAY_TIME, 0L);

        // Increment the implicit display count
        int currentDisplayCount = AppData.getInteger(AppData.APPDATA_IMPLICIT_DISPLAY_COUNT, 0);
        AppData.setInteger(AppData.APPDATA_IMPLICIT_DISPLAY_COUNT, currentDisplayCount + 1);

        // Increment the implicit session display count
        int currentSessionCount = (int)Session.get(Session.SYS_RIC_SESSION_COUNT, 0);
        Session.set(Session.SYS_RIC_SESSION_COUNT, currentSessionCount + 1);

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
                // Showing progress dialog
                progressDialog = new ProgressDialog(activity);
                progressDialog.setMessage(activity.getResources().getString(R.string.ghostery_dialog_pleaseWait));
                progressDialog.setCancelable(false);
                progressDialog.show();
            }
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            trackerArrayList.clear();       // Start with an empty tracker array

            // Creating service handler class instance
            ServiceHandler sh = new ServiceHandler();

            try {
                // Make a request to url for the AppNoticeData info
                String url = getFormattedJSONUrl();
                String jsonStr = sh.makeServiceCall(url, ServiceHandler.GET);
                JSONObject jsonObj = null;
                Resources resources = activity.getResources();

                if (jsonStr != null && jsonStr.length() > 20 && !jsonStr.startsWith(FILE_NOT_FOUND)){
                    // Strip off the not-JSON outer characters
                    if (jsonStr.startsWith(NON_JSON_PREFIX))
                        jsonStr = jsonStr.substring(NON_JSON_PREFIX.length());
                    if (jsonStr.endsWith(NON_JSON_POSTFIX))
                        jsonStr = jsonStr.substring(0, jsonStr.length() - NON_JSON_POSTFIX.length());

                    jsonObj = new JSONObject(jsonStr);
                }

                // Parse the returned JSON string
                if (useRemoteValues && jsonObj != null){
                    Log.d(TAG, "Response: " + jsonStr);

                    try {
                        bric = jsonObj.isNull(TAG_BRIC)? resources.getBoolean(R.bool.ghostery_consent_flow_type) : jsonObj.getBoolean(TAG_BRIC);
                        bric_access_button_color = jsonObj.isNull(TAG_BRIC_ACCESS_BUTTON_COLOR)? resources.getColor(R.color.ghostery_dialog_button_color) : Color.parseColor(jsonObj.getString(TAG_BRIC_ACCESS_BUTTON_COLOR));
                        bric_access_button_text = jsonObj.isNull(TAG_BRIC_ACCESS_BUTTON_TEXT)? resources.getString(R.string.ghostery_dialog_button_consent) : jsonObj.getString(TAG_BRIC_ACCESS_BUTTON_TEXT);
                        bric_access_button_text_color = jsonObj.isNull(TAG_BRIC_ACCESS_BUTTON_TEXT_COLOR)? resources.getColor(R.color.ghostery_dialog_explicit_accept_button_text_color) : Color.parseColor(jsonObj.getString(TAG_BRIC_ACCESS_BUTTON_TEXT_COLOR));
                        bric_bg = jsonObj.isNull(TAG_BRIC_BG)? resources.getColor(R.color.ghostery_dialog_background_color) : Color.parseColor(jsonObj.getString(TAG_BRIC_BG));
                        bric_content1 = jsonObj.isNull(TAG_BRIC_CONTENT1)? resources.getString(R.string.ghostery_dialog_explicit_message) : jsonObj.getString(TAG_BRIC_CONTENT1);
                        bric_decline_button_color = jsonObj.isNull(TAG_BRIC_DECLINE_BUTTON_COLOR)? resources.getColor(R.color.ghostery_dialog_explicit_decline_button_color) : Color.parseColor(jsonObj.getString(TAG_BRIC_DECLINE_BUTTON_COLOR));
                        bric_decline_button_text = jsonObj.isNull(TAG_BRIC_DECLINE_BUTTON_TEXT)? resources.getString(R.string.ghostery_dialog_button_decline) : jsonObj.getString(TAG_BRIC_DECLINE_BUTTON_TEXT);
                        bric_decline_button_text_color = jsonObj.isNull(TAG_BRIC_DECLINE_BUTTON_TEXT_COLOR)? resources.getColor(R.color.ghostery_dialog_explicit_decline_button_text_color) : Color.parseColor(jsonObj.getString(TAG_BRIC_DECLINE_BUTTON_TEXT_COLOR));
                        bric_header_text = jsonObj.isNull(TAG_BRIC_HEADER_TEXT)? resources.getString(R.string.ghostery_dialog_header_text) : jsonObj.getString(TAG_BRIC_HEADER_TEXT);
                        bric_header_text_color = jsonObj.isNull(TAG_BRIC_HEADER_TEXT_COLOR)? resources.getColor(R.color.ghostery_dialog_header_text_color) : Color.parseColor(jsonObj.getString(TAG_BRIC_HEADER_TEXT_COLOR));
                        close_button = jsonObj.isNull(TAG_CLOSE_BUTTON)? resources.getString(R.string.ghostery_dialog_button_close) : jsonObj.getString(TAG_CLOSE_BUTTON);
                        manage_preferences_description = jsonObj.isNull(TAG_MANAGE_PREFERENCES_DESCRIPTION)? resources.getString(R.string.ghostery_preferences_description) : jsonObj.getString(TAG_MANAGE_PREFERENCES_DESCRIPTION);
                        manage_preferences_header = jsonObj.isNull(TAG_MANAGE_PREFERENCES_HEADER)? resources.getString(R.string.ghostery_preferences_header) : jsonObj.getString(TAG_MANAGE_PREFERENCES_HEADER);
                        ric = jsonObj.isNull(TAG_RIC)? resources.getString(R.string.ghostery_dialog_implicit_message) : jsonObj.getString(TAG_RIC);
                        ric_bg = jsonObj.isNull(TAG_RIC_BG)? resources.getColor(R.color.ghostery_dialog_background_color) : Color.parseColor(jsonObj.getString(TAG_RIC_BG));
                        ric_click_manage_settings = jsonObj.isNull(TAG_RIC_CLICK_MANAGE_SETTINGS)? resources.getString(R.string.ghostery_dialog_button_preferences) : jsonObj.getString(TAG_RIC_CLICK_MANAGE_SETTINGS);
                        ric_color = jsonObj.isNull(TAG_RIC_COLOR)? resources.getColor(R.color.ghostery_dialog_message_text_color) : Color.parseColor(jsonObj.getString(TAG_RIC_COLOR));
                        ric_max = jsonObj.isNull(TAG_RIC_MAX)? ric_max_default : jsonObj.getInt(TAG_RIC_MAX);
                        ric_opacity = jsonObj.isNull(TAG_RIC_OPACITY)? ric_opacity_default : jsonObj.getInt(TAG_RIC_OPACITY);
                        ric_session_max = jsonObj.isNull(TAG_RIC_SESSION_MAX) ? ric_session_max_default : jsonObj.getInt(TAG_RIC_SESSION_MAX);
                        ric_title = jsonObj.isNull(TAG_RIC_TITLE)? resources.getString(R.string.ghostery_dialog_header_text) : jsonObj.getString(TAG_RIC_TITLE);
                        ric_title_color = jsonObj.isNull(TAG_RIC_TITLE_COLOR)? resources.getColor(R.color.ghostery_dialog_header_text_color) : Color.parseColor(jsonObj.getString(TAG_RIC_TITLE_COLOR));

                        initTrackerList(jsonObj);

                        initialized = true;
                    } catch (JSONException e) {
                        Log.e(TAG, "JSONException while parsing the JSON object.", e);
                    }
                } else {
                    if (useRemoteValues)
                        Log.d(TAG, "Using local values because configuration JSON could not be retrieved.");
                    else
                        Log.d(TAG, "Using local values as requested.");

                    bric = resources.getBoolean(R.bool.ghostery_consent_flow_type);
                    bric_access_button_color = resources.getColor(R.color.ghostery_dialog_button_color);
                    bric_access_button_text = resources.getString(R.string.ghostery_dialog_button_consent);
                    bric_access_button_text_color = resources.getColor(R.color.ghostery_dialog_explicit_accept_button_text_color);
                    bric_bg = resources.getColor(R.color.ghostery_dialog_background_color);
                    bric_content1 = resources.getString(R.string.ghostery_dialog_explicit_message);
                    bric_decline_button_color = resources.getColor(R.color.ghostery_dialog_explicit_decline_button_color);
                    bric_decline_button_text = resources.getString(R.string.ghostery_dialog_button_decline);
                    bric_decline_button_text_color = resources.getColor(R.color.ghostery_dialog_explicit_decline_button_text_color);
                    bric_header_text = resources.getString(R.string.ghostery_dialog_header_text);
                    bric_header_text_color = resources.getColor(R.color.ghostery_dialog_header_text_color);
                    close_button = resources.getString(R.string.ghostery_dialog_button_close);
                    manage_preferences_description = resources.getString(R.string.ghostery_preferences_description);
                    manage_preferences_header = resources.getString(R.string.ghostery_preferences_header);
                    ric = resources.getString(R.string.ghostery_dialog_implicit_message);
                    ric_bg = resources.getColor(R.color.ghostery_dialog_background_color);
                    ric_click_manage_settings = resources.getString(R.string.ghostery_dialog_button_preferences);
                    ric_color = resources.getColor(R.color.ghostery_dialog_message_text_color);
                    ric_max = ric_max_default;
                    ric_opacity = ric_opacity_default;
                    ric_session_max = ric_session_max_default;
                    ric_title = resources.getString(R.string.ghostery_dialog_header_text);
                    ric_title_color = resources.getColor(R.color.ghostery_dialog_header_text_color);

                    if (jsonObj != null)
                        initTrackerList(jsonObj);

                }
            } catch (NumberFormatException e) {
                Log.e(TAG, "Exception while parsing elements of the JSON object", e);
            } catch (Resources.NotFoundException e) {
                Log.e(TAG, "NotFoundException while getting the JSON object", e);
            } catch (Exception e) {
                Log.e(TAG, "Exception while getting or parsing the JSON object.", e);
            }

            return null;
        }

        private void initTrackerList(JSONObject jsonObj) {
            try {
                String trackerJSONString = jsonObj.isNull(TAG_TRACKERS)? null : jsonObj.getString(TAG_TRACKERS);
                if (trackerJSONString != null) {
                    JSONArray trackerJSONArray = new JSONArray(trackerJSONString);

                    int id;
                    for (int i = 0; i < trackerJSONArray.length(); i++) {
                        JSONObject trackerJSONObject = trackerJSONArray.getJSONObject(i);
                        Tracker tracker = new Tracker(trackerJSONObject);
                        trackerArrayList.add(tracker);
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

                initialized = true;
            } catch (JSONException e) {
                Log.e(TAG, "JSONException while parsing the JSON object.", e);
            }
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

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

            // Dismiss the progress dialog
            if (progressDialog.isShowing())
                progressDialog.dismiss();
        }

        protected String getFormattedJSONUrl() {
            Object[] urlParams = new Object[2];
            urlParams[0] = String.valueOf(companyId);			// 0
            urlParams[1] = String.valueOf(configId);		// 1
            return MessageFormat.format(URL_JSON_REQUEST, urlParams);
        }

    }
}

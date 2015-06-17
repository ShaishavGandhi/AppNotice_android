package com.ghostery.privacy.inappconsentsdk.model;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.util.Log;

import com.ghostery.privacy.inappconsentsdk.R;
import com.ghostery.privacy.inappconsentsdk.callbacks.JSONGetterCallback;
import com.ghostery.privacy.inappconsentsdk.utils.AppData;
import com.ghostery.privacy.inappconsentsdk.utils.ServiceHandler;
import com.ghostery.privacy.inappconsentsdk.utils.Session;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * Created by Steven.Overson on 2/26/2015.
 */

// Never instantiate directly. Use getInstance() instead.
public class InAppConsentData {
    private static final String TAG = "InAppConsentData";

    private static InAppConsentData instance;
    private static Activity _activity;
    private ProgressDialog pDialog;
    private boolean initialized = false;
    private static int company_id;
    private static int pub_notice_id;
    private int ric_max_default = 3;
    private int ric_session_max_default = 1;
    private int ric_opacity_default = 100;

    private final static String TAG_INAPPCONSENTDATA = "inappconsentdata";
    private final static long ELAPSED_30_DAYS_MILLIS = 2592000000L;     // Number of milliseconds in 30 days

    // 0 = company ID; 1 = pub-notice ID
    private final static String URL_JSON_REQUEST = "https://c.betrad.com/pub/c/{0}/{1}.js";

    // 0 = Publisher ID; 1 = Owner Company ID
    private final static String URL_NOTICE_APP_LOAD = "http://l.betrad.com/pub/p.gif?pid={0}&ocid={1}&ii=1&mb=4";
    private final static String URL_NOTICE_IMPLICIT_INTRO_LEARN = "http://l.betrad.com/pub/p.gif?pid={0}&ocid={1}&nt=2&mb=4&d=1";
    private final static String URL_NOTICE_IMPLICIT_INFO_PREF = "http://l.betrad.com/pub/p.gif?pid={0}&ocid={1}&nt=4&mb=4&ic=1";
    private final static String URL_NOTICE_EXPLICIT_INFO_PREF = "http://l.betrad.com/pub/p.gif?pid={0}&ocid={1}&ii=1&mb=4&nt=3&d=1";
    private final static String URL_NOTICE_EXPLICIT_INFO_ACCEPT = "http://l.betrad.com/pub/p.gif?pid={0}&ocid={1}&mb=4&nt=3&aa=1";
    private final static String URL_NOTICE_EXPLICIT_INFO_DECLINE = "http://l.betrad.com/pub/p.gif?pid={0}&ocid={1}&mb=4&nt=3&aa=0";
    private final static String URL_NOTICE_PREF_DIRECT = "http://l.betrad.com/pub/p.gif?pid={0}&ocid={1}&mb=4&nt=3&aa=0";

    public enum NoticeType {
        APP_LOAD,
        IMPLICIT_INTRO_LEARN,
        IMPLICIT_INFO_PREF,
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
    private boolean bric = false;
    private String bric_access_button_color;
    private String bric_access_button_text;
    private String bric_access_button_text_color;
    private String bric_bg;
    private String bric_content1;
    private String bric_decline_button_color;
    private String bric_decline_button_text;
    private String bric_decline_button_text_color;
    private String bric_header_text;
    private String bric_header_text_color;
    private String close_button;
    private String manage_preferences_description;
    private String manage_preferences_header;
    private String ric;
    private String ric_bg;
    private String ric_click_manage_settings;
    private String ric_color;
    private int ric_max;
//    private String ric_maxString;
    private float ric_opacity = 1F;
//    private String ric_opacityString;
    private int ric_session_max;
//    private String ric_session_maxString;
    private String ric_title;
    private String ric_title_color;

    private ArrayList<Tracker> trackerArrayList = new ArrayList<>();


    // Public getters and setters
    public Boolean isInitialized() { return initialized; }
    public int getCompany_id() { return company_id; }
    public void setCompany_id(int company_id) { this.company_id = company_id; }
    public int getPub_notice_id() { return pub_notice_id; }
    public void setPub_notice_id(int pub_notice_id) { this.pub_notice_id = pub_notice_id; }

    public boolean getBric() { return bric; }
    public String getBric_access_button_color() { return bric_access_button_color; }
    public String getBric_access_button_text() { return bric_access_button_text; }
    public String getBric_access_button_text_color() { return bric_access_button_text_color; }
    public String getBric_bg() { return bric_bg; }
    public String getBric_content1() { return bric_content1; }
    public String getBric_decline_button_color() { return bric_decline_button_color; }
    public String getBric_decline_button_text() { return bric_decline_button_text; }
    public String getBric_decline_button_text_color() { return bric_decline_button_text_color; }
    public String getBric_header_text() { return bric_header_text; }
    public String getBric_header_text_color() { return bric_header_text_color; }
    public String getClose_button() { return close_button; }
    public String getManage_preferences_description() { return manage_preferences_description; }
    public String getManage_preferences_header() { return manage_preferences_header; }
    public String getRic() { return ric; }
    public String getRic_bg() { return ric_bg; }
    public String getRic_click_manage_settings() { return ric_click_manage_settings; }
    public String getRic_color() { return ric_color; }
    public int getRic_max() { return ric_max; }
    public float getRic_opacity() { return ric_opacity; }
    public int getRic_session_max() { return ric_session_max; }
    public String getRic_title() { return ric_title; }
    public String getRic_title_color() { return ric_title_color; }
    public ArrayList<Tracker> getTrackerArrayList() { return trackerArrayList; }
    public HashMap<Integer, Boolean> getTrackerHashMap() {
        HashMap trackerHashMap = new HashMap();

        // Loop through the tracker list and add non-essential tracker IDs and their on/off state
        for (Tracker tracker : trackerArrayList) {
            if (!tracker.getCategory().equalsIgnoreCase("Essential")) {
                trackerHashMap.put(tracker.getTrackerId(), tracker.isOn());
            }
        }
        return trackerHashMap;
    }


    // Single instance
    public static synchronized InAppConsentData getInstance(Activity activity)
    {
        _activity = activity;

        // Ensure the app only uses one instance of this class.
        if (instance == null)
            instance = new InAppConsentData();

        return instance;
    }

    // Constructor
    public InAppConsentData() {
        // Pre-populate the max values with defaults just in case the JSON object can't be retrieved
        ric_max = ric_max_default = _activity.getResources().getInteger(R.integer.ghostery_ric_max_default);
        ric_session_max = ric_session_max_default = _activity.getResources().getInteger(R.integer.ghostery_ric_session_max_default);
        ric_opacity = ric_opacity_default = _activity.getResources().getInteger(R.integer.ghostery_ric_session_max_default);
    }

    // Sends a report back through the Site Notice Channel
    public static void sendNotice(final NoticeType type) {
        // Use a non-UI thread
        new Thread(){
            public void run(){
                Object[] urlParams = new Object[2];
                urlParams[0] = String.valueOf(pub_notice_id);	// 0
                urlParams[1] = String.valueOf(company_id);		// 1

                String uRL = "";

                switch (type) {
                    case APP_LOAD:
                        uRL = MessageFormat.format(URL_NOTICE_APP_LOAD, urlParams);
                        break;
                    case IMPLICIT_INTRO_LEARN:
                        uRL = MessageFormat.format(URL_NOTICE_IMPLICIT_INTRO_LEARN, urlParams);
                        break;
                    case IMPLICIT_INFO_PREF:
                        uRL = MessageFormat.format(URL_NOTICE_IMPLICIT_INFO_PREF, urlParams);
                        break;
                    case EXPLICIT_INFO_PREF:
                        uRL = MessageFormat.format(URL_NOTICE_EXPLICIT_INFO_PREF, urlParams);
                        break;
                    case EXPLICIT_INFO_ACCEPT:
                        uRL = MessageFormat.format(URL_NOTICE_EXPLICIT_INFO_ACCEPT, urlParams);
                        break;
                    case EXPLICIT_INFO_DECLINE:
                        uRL = MessageFormat.format(URL_NOTICE_EXPLICIT_INFO_DECLINE, urlParams);
                        break;
                    case PREF_DIRECT:
                        uRL = MessageFormat.format(URL_NOTICE_PREF_DIRECT, urlParams);
                        break;
                }

                if (uRL != null && uRL.length() > 0) {
                    Log.d(TAG, "Sending notice beacon: (type=" + type + ")" + uRL);
                    try{
//                        if (Network.isNetworkAvailable(_activity)) {
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
    public void inti(JSONGetterCallback mJSONGetterCallback) {
        // Start the call to get the InAppConsentData data from the service
        JSONGetter mJSONGetter = new JSONGetter(mJSONGetterCallback);
        mJSONGetter.execute();
    }

    // Determine if the Implicit notice should be shown. True = show notice; False = don't show notice.
    public boolean getImplicitNoticeDisplayStatus() {
        boolean status = true;     // Assume we need to show the notice
        int implicit_display_count = (int) AppData.getInteger(AppData.APPDATA_IMPLICIT_DISPLAY_COUNT, 0);
        long implicit_last_display_time = (long) AppData.getLong(AppData.APPDATA_IMPLICIT_LAST_DISPLAY_TIME, 0L);
        int ric_session_count = (int) Session.get(Session.SYS_RIC_SESSION_COUNT, 0);

        if (ric_session_count >= ric_session_max) {                 // If displayed enough in this session...
            status = false;                                         //    don't display it now
        } else {
            long currentTime = System.currentTimeMillis();
            if (currentTime <= implicit_last_display_time + ELAPSED_30_DAYS_MILLIS) {     // If displayed less than 30 days ago...
                if (implicit_display_count >= ric_max) {                                  // If displayed enough in last 30 days...
                    status = false;                                                       //    don't display it now
                }
            } else {
                AppData.setInteger(AppData.APPDATA_IMPLICIT_DISPLAY_COUNT, 0);            // If it's been more than 30 days, reset the display count to 0
            }
        }

        return status;
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




    // =================================================================================
    // =================================================================================

    // Async task to get InAppConsentData data from a URL
    private class JSONGetter extends AsyncTask<Void, Void, Void> {

        private JSONGetterCallback mJSONGetterCallback;

        public JSONGetter(JSONGetterCallback mJSONGetterCallback) {
            this.mJSONGetterCallback = mJSONGetterCallback;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(_activity);
            pDialog.setMessage(_activity.getResources().getString(R.string.ghostery_dialog_pleaseWait));
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            trackerArrayList.clear();       // Start with an empty tracker array

            // Creating service handler class instance
            ServiceHandler sh = new ServiceHandler();

            try {
                // Make a request to url for the InAppConsentData info
                String url = getFormattedJSONUrl();
                String jsonStr = sh.makeServiceCall(url, ServiceHandler.GET);

                // Parse the returned JSON string
                if (jsonStr == null || jsonStr.length() <= 20 || jsonStr.startsWith(FILE_NOT_FOUND)) {
                    // No string to parse...
                    Log.e(TAG, "Configuration info could not be retrieved.");

                } else {
                    Log.d(TAG, "Response: " + jsonStr);

                    try {
                        // Strip off the not-JSON outer characters
                        if (jsonStr.startsWith(NON_JSON_PREFIX))
                            jsonStr = jsonStr.substring(NON_JSON_PREFIX.length());
                        if (jsonStr.endsWith(NON_JSON_POSTFIX))
                            jsonStr = jsonStr.substring(0, jsonStr.length() - NON_JSON_POSTFIX.length());

                        JSONObject jsonObj = new JSONObject(jsonStr);

                        bric = jsonObj.isNull(TAG_BRIC)? null : jsonObj.getBoolean(TAG_BRIC);
                        bric_access_button_color = jsonObj.isNull(TAG_BRIC_ACCESS_BUTTON_COLOR)? null : jsonObj.getString(TAG_BRIC_ACCESS_BUTTON_COLOR);
                        bric_access_button_text = jsonObj.isNull(TAG_BRIC_ACCESS_BUTTON_TEXT)? null : jsonObj.getString(TAG_BRIC_ACCESS_BUTTON_TEXT);
                        bric_access_button_text_color = jsonObj.isNull(TAG_BRIC_ACCESS_BUTTON_TEXT_COLOR)? null : jsonObj.getString(TAG_BRIC_ACCESS_BUTTON_TEXT_COLOR);
                        bric_bg = jsonObj.isNull(TAG_BRIC_BG)? null : jsonObj.getString(TAG_BRIC_BG);
                        bric_content1 = jsonObj.isNull(TAG_BRIC_CONTENT1)? null : jsonObj.getString(TAG_BRIC_CONTENT1);
                        bric_decline_button_color = jsonObj.isNull(TAG_BRIC_DECLINE_BUTTON_COLOR)? null : jsonObj.getString(TAG_BRIC_DECLINE_BUTTON_COLOR);
                        bric_decline_button_text = jsonObj.isNull(TAG_BRIC_DECLINE_BUTTON_TEXT)? null : jsonObj.getString(TAG_BRIC_DECLINE_BUTTON_TEXT);
                        bric_decline_button_text_color = jsonObj.isNull(TAG_BRIC_DECLINE_BUTTON_TEXT_COLOR)? null : jsonObj.getString(TAG_BRIC_DECLINE_BUTTON_TEXT_COLOR);
                        bric_header_text = jsonObj.isNull(TAG_BRIC_HEADER_TEXT)? null : jsonObj.getString(TAG_BRIC_HEADER_TEXT);
                        bric_header_text_color = jsonObj.isNull(TAG_BRIC_HEADER_TEXT_COLOR)? null : jsonObj.getString(TAG_BRIC_HEADER_TEXT_COLOR);
                        close_button = jsonObj.isNull(TAG_CLOSE_BUTTON)? null : jsonObj.getString(TAG_CLOSE_BUTTON);
                        manage_preferences_description = jsonObj.isNull(TAG_MANAGE_PREFERENCES_DESCRIPTION)? null : jsonObj.getString(TAG_MANAGE_PREFERENCES_DESCRIPTION);
                        manage_preferences_header = jsonObj.isNull(TAG_MANAGE_PREFERENCES_HEADER)? null : jsonObj.getString(TAG_MANAGE_PREFERENCES_HEADER);
                        ric = jsonObj.isNull(TAG_RIC)? null : jsonObj.getString(TAG_RIC);
                        ric_bg = jsonObj.isNull(TAG_RIC_BG)? null : jsonObj.getString(TAG_RIC_BG);
                        ric_click_manage_settings = jsonObj.isNull(TAG_RIC_CLICK_MANAGE_SETTINGS)? null : jsonObj.getString(TAG_RIC_CLICK_MANAGE_SETTINGS);
                        ric_color = jsonObj.isNull(TAG_RIC_COLOR)? null : jsonObj.getString(TAG_RIC_COLOR);
                        ric_max = jsonObj.isNull(TAG_RIC_MAX)? ric_max_default : jsonObj.getInt(TAG_RIC_MAX);
                        ric_opacity = jsonObj.isNull(TAG_RIC_OPACITY)? ric_opacity_default : jsonObj.getInt(TAG_RIC_OPACITY);
                        ric_session_max = jsonObj.isNull(TAG_RIC_SESSION_MAX) ? ric_session_max_default : jsonObj.getInt(TAG_RIC_SESSION_MAX);
                        ric_title = jsonObj.isNull(TAG_RIC_TITLE)? null : jsonObj.getString(TAG_RIC_TITLE);
                        ric_title_color = jsonObj.isNull(TAG_RIC_TITLE_COLOR)? null : jsonObj.getString(TAG_RIC_TITLE_COLOR);

                        String trackerJSONString = jsonObj.isNull(TAG_TRACKERS)? null : jsonObj.getString(TAG_TRACKERS);
                        JSONArray trackerJSONArray = new JSONArray(trackerJSONString);

                        int id;
                        for (int i = 0; i < trackerJSONArray.length(); i++) {
                            JSONObject trackerJSONObject = trackerJSONArray.getJSONObject(i);
                            Tracker tracker = new Tracker(trackerJSONObject);
                            trackerArrayList.add(tracker);
                        }

//                        // Convert the opacity string (value "0" to "100") to a float (value 0.0 to 1.0)
//                        if (ric_opacityString != null) {
//                            int opacityInt = Integer.parseInt(ric_opacityString);
//                            ric_opacity = ((float)opacityInt) / 100;
//                        }
//
//                        // Convert the ric_max value from either the retrieved JSON parameter or the default value
//                        if (ric_maxString == null || ric_maxString.length() == 0)
//                            ric_maxString = _activity.getResources().getString(R.string.ghostery_ric_max_default);
//
//                        if (ric_maxString != null) {
//                            Log.d(TAG, "ric_maxString = " + ric_maxString);
//                            ric_max = Integer.parseInt(ric_maxString);
//                        } else {
//                            throw(new MissingResourceException("A default value for ric_max is missing as a string resource.", TAG, "ric_max"));
//                        }
//
//                        // Convert the ric_session_max value from either the retrieved JSON parameter or the default value
//                        if (ric_session_maxString == null || ric_session_maxString.length() == 0)
//                            ric_session_maxString = _activity.getResources().getString(R.string.ghostery_ric_session_max_default);
//
//                        if (ric_session_maxString != null) {
//                            ric_session_max = Integer.parseInt(ric_session_maxString);
//                        } else {
//                            throw(new MissingResourceException("A default value for ric_session_max is missing as a string resource.", TAG, "ric_session_max"));
//                        }

                        initialized = true;
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

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            // Let the specified callback know it finished...
            if (mJSONGetterCallback != null) {
                _activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mJSONGetterCallback.onTaskDone();
                    }
                });
            }

            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
        }

        protected String getFormattedJSONUrl() {
            Object[] urlParams = new Object[2];
            urlParams[0] = String.valueOf(company_id);			// 0
            urlParams[1] = String.valueOf(pub_notice_id);		// 1
            return MessageFormat.format(URL_JSON_REQUEST, urlParams);
        }

    }
}

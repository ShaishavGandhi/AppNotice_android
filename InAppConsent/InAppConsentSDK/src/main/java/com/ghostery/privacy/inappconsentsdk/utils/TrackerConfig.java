package com.ghostery.privacy.inappconsentsdk.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.util.Log;

import com.ghostery.privacy.inappconsentsdk.R;
import com.ghostery.privacy.inappconsentsdk.callbacks.TrackerConfigGetterCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.MessageFormat;
import java.util.MissingResourceException;


/**
 * Created by Steven.Overson on 2/26/2015.
 */

// Never instantiate directly. Use getInstance() instead.
public class TrackerConfig {
    private static final String TAG = "TrackerConfig";

    private static TrackerConfig instance;
    private static Activity _activity;
    private ProgressDialog pDialog;
    private boolean initialized = false;
    private static int company_id;
    private static int pub_notice_id;

    private final static String TAG_TRACKERCONFIG = "trackerconfig";
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
    private static final String TAG_RIC_BG = "ric_bg";
    private static final String TAG_RIC_OPACITY = "ric_opacity";
    private static final String TAG_RIC_TITLE = "ric_title";
    private static final String TAG_RIC_TITLE_COLOR = "ric_title_color";
    private static final String TAG_RIC = "ric";
    private static final String TAG_RIC_INTRO = "ric_intro";
    private static final String TAG_RIC_COLOR = "ric_color";
    private static final String TAG_RIC_LEARN_MORE = "ric_learn_more";
    private static final String TAG_CLOSE_BUTTON = "close_button";

    private static final String TAG_BRIC_BG = "bric_bg";
    private static final String TAG_BRIC_HEADER_TEXT = "bric_header_text";
    private static final String TAG_BRIC_HEADER_TEXT_COLOR = "bric_header_text_color";
    private static final String TAG_RIC_CLICK_MANAGE_SETTINGS = "ric_click_manage_settings";
    private static final String TAG_BRIC_CONTENT1 = "bric_content1";
    private static final String TAG_BRIC_CONTENT2 = "bric_content2";
    private static final String TAG_BRIC_CONTENT3 = "bric_content3";
    private static final String TAG_BRIC_ACCESS_BUTTON_TEXT = "bric_access_button_text";
    private static final String TAG_BRIC_ACCESS_BUTTON_TEXT_COLOR = "bric_access_button_text_color";
    private static final String TAG_BRIC_ACCESS_BUTTON_COLOR = "bric_access_button_color";
    private static final String TAG_BRIC_DECLINE_BUTTON_TEXT = "bric_decline_button_text";
    private static final String TAG_BRIC_DECLINE_BUTTON_TEXT_COLOR = "bric_decline_button_text_color";
    private static final String TAG_BRIC_DECLINE_BUTTON_COLOR = "bric_decline_button_color";
    private static final String TAG_RIC_MAX = "ric_max";
    private static final String TAG_RIC_SESSION_MAX = "ric_session_max";

    // Field values
    private String ric_bg;
    private String ric_opacityString;
    private float ric_opacity = 1F;
    private String ric_title;
    private String ric_title_color;
    private String ric;
    private String ric_intro;
    private String ric_color;
    private String ric_learn_more;
    private String close_button;

    private String bric_bg;
    private String bric_header_text;
    private String bric_header_text_color;
    private String ric_click_manage_settings;
    private String bric_content;
    private String bric_content1;
    private String bric_content2;
    private String bric_content3;
    private String bric_access_button_text;
    private String bric_access_button_text_color;
    private String bric_access_button_color;
    private String bric_decline_button_text;
    private String bric_decline_button_text_color;
    private String bric_decline_button_color;
    private String ric_maxString;
    private int ric_max;
    private String ric_session_maxString;
    private int ric_session_max;

    // Public getters and setters
    public Boolean isInitialized() { return initialized; }
    public int getCompany_id() { return company_id; }
    public void setCompany_id(int company_id) { this.company_id = company_id; }
    public int getPub_notice_id() { return pub_notice_id; }
    public void setPub_notice_id(int pub_notice_id) { this.pub_notice_id = pub_notice_id; }

    public String getRic_bg() { return ric_bg; }
    public float getRic_opacity() { return ric_opacity; }
    public String getRic_title() { return ric_title; }
    public String getRic_title_color() { return ric_title_color; }
    public String getRic() { return ric; }
    public String getRic_intro() { return ric_intro; }
    public String getRic_color() { return ric_color; }
    public String getRic_learn_more() { return ric_learn_more; }
    public String getClose_button() { return close_button; }

    public String getBric_bg() { return bric_bg; }
    public String getBric_header_text() { return bric_header_text; }
    public String getBric_header_text_color() { return bric_header_text_color; }
    public String getRic_click_manage_settings() { return ric_click_manage_settings; }
    public String getBric_content() { return bric_content; }
    public String getBric_access_button_text() { return bric_access_button_text; }
    public String getBric_access_button_text_color() { return bric_access_button_text_color; }
    public String getBric_access_button_color() { return bric_access_button_color; }
    public String getBric_decline_button_text() { return bric_decline_button_text; }
    public String getBric_decline_button_text_color() { return bric_decline_button_text_color; }
    public String getBric_decline_button_color() { return bric_decline_button_color; }
    public int getRic_max() { return ric_max; }
    public int getRic_session_max() { return ric_session_max; }


    // Single instance
    public static synchronized TrackerConfig getInstance(Activity activity)
    {
        _activity = activity;

        // Ensure the app only uses one instance of this class.
        if (instance == null)
            instance = new TrackerConfig();

        return instance;
    }

    // Constructor
    public TrackerConfig() {
        // Pre-populate the max values with defaults just in case the JSON object can't be retrieved
        ric_maxString = _activity.getResources().getString(R.string.ghostery_ric_max_default);
        ric_max = Integer.parseInt(ric_maxString);
        ric_session_maxString = _activity.getResources().getString(R.string.ghostery_ric_session_max_default);
        ric_session_max = Integer.parseInt(ric_session_maxString);
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
    public void initTrackerConfig(TrackerConfigGetterCallback TrackerConfigGetterCallback) {
        // Start the call to get the TrackerConfig data from the service
        TrackerConfigGetter trackerConfigGetter = new TrackerConfigGetter(TrackerConfigGetterCallback);
        trackerConfigGetter.execute();
    }

    // Determine if the Implicit notice should be shown. True = show notice; False = don't show notice.
    public boolean getImplicitNoticeDisplayStatus() {
        boolean status = true;     // Assume we need to show the notice
        int implicit_display_count = (int) AppData.getInteger(AppData.APPDATA_IMPLICIT_DISPLAY_COUNT, 0);
        long implicit_last_display_time = (long) AppData.getLong(AppData.APPDATA_IMPLICIT_LAST_DISPLAY_TIME, 0L);
        int ric_session_count = (int)Session.get(Session.SYS_RIC_SESSION_COUNT, 0);

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

    // Async task to get TrackerConfig data from a URL
    private class TrackerConfigGetter extends AsyncTask<Void, Void, Void> {

        private TrackerConfigGetterCallback mTrackerConfigGetterCallback;

        public TrackerConfigGetter(TrackerConfigGetterCallback TrackerConfigGetterCallback) {
            mTrackerConfigGetterCallback = TrackerConfigGetterCallback;
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
            // Creating service handler class instance
            ServiceHandler sh = new ServiceHandler();

            try {
                // Make a request to url for the TrackerConfig info
                String url = getFormattedTrackerConfigUrl();
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

                        ric_bg = jsonObj.isNull(TAG_RIC_BG)? null : jsonObj.getString(TAG_RIC_BG);
                        ric_opacityString = jsonObj.isNull(TAG_RIC_OPACITY)? null : jsonObj.getString(TAG_RIC_OPACITY);
                        ric = jsonObj.isNull(TAG_RIC)? null : jsonObj.getString(TAG_RIC);
                        ric_intro = jsonObj.isNull(TAG_RIC_INTRO)? null : jsonObj.getString(TAG_RIC_INTRO);
                        ric_color = jsonObj.isNull(TAG_RIC_COLOR)? null : jsonObj.getString(TAG_RIC_COLOR);
                        ric_title = jsonObj.isNull(TAG_RIC_TITLE)? null : jsonObj.getString(TAG_RIC_TITLE);
                        ric_title_color = jsonObj.isNull(TAG_RIC_TITLE_COLOR)? null : jsonObj.getString(TAG_RIC_TITLE_COLOR);
                        ric_learn_more = jsonObj.isNull(TAG_RIC_LEARN_MORE)? null : jsonObj.getString(TAG_RIC_LEARN_MORE);
                        close_button = jsonObj.isNull(TAG_CLOSE_BUTTON)? null : jsonObj.getString(TAG_CLOSE_BUTTON);

                        bric_bg = jsonObj.isNull(TAG_BRIC_BG)? null : jsonObj.getString(TAG_BRIC_BG);
                        bric_header_text = jsonObj.isNull(TAG_BRIC_HEADER_TEXT)? null : jsonObj.getString(TAG_BRIC_HEADER_TEXT);
                        bric_header_text_color = jsonObj.isNull(TAG_BRIC_HEADER_TEXT_COLOR)? null : jsonObj.getString(TAG_BRIC_HEADER_TEXT_COLOR);
                        ric_click_manage_settings = jsonObj.isNull(TAG_RIC_CLICK_MANAGE_SETTINGS)? null : jsonObj.getString(TAG_RIC_CLICK_MANAGE_SETTINGS);
                        bric_content1 = jsonObj.isNull(TAG_BRIC_CONTENT1)? null : jsonObj.getString(TAG_BRIC_CONTENT1);
                        bric_content2 = jsonObj.isNull(TAG_BRIC_CONTENT2)? null : jsonObj.getString(TAG_BRIC_CONTENT2);
                        bric_content3 = jsonObj.isNull(TAG_BRIC_CONTENT3)? null : jsonObj.getString(TAG_BRIC_CONTENT3);
                        bric_access_button_text = jsonObj.isNull(TAG_BRIC_ACCESS_BUTTON_TEXT)? null : jsonObj.getString(TAG_BRIC_ACCESS_BUTTON_TEXT);
                        bric_access_button_text_color = jsonObj.isNull(TAG_BRIC_ACCESS_BUTTON_TEXT_COLOR)? null : jsonObj.getString(TAG_BRIC_ACCESS_BUTTON_TEXT_COLOR);
                        bric_access_button_color = jsonObj.isNull(TAG_BRIC_ACCESS_BUTTON_COLOR)? null : jsonObj.getString(TAG_BRIC_ACCESS_BUTTON_COLOR);
                        bric_decline_button_text = jsonObj.isNull(TAG_BRIC_DECLINE_BUTTON_TEXT)? null : jsonObj.getString(TAG_BRIC_DECLINE_BUTTON_TEXT);
                        bric_decline_button_text_color = jsonObj.isNull(TAG_BRIC_DECLINE_BUTTON_TEXT_COLOR)? null : jsonObj.getString(TAG_BRIC_DECLINE_BUTTON_TEXT_COLOR);
                        bric_decline_button_color = jsonObj.isNull(TAG_BRIC_DECLINE_BUTTON_COLOR)? null : jsonObj.getString(TAG_BRIC_DECLINE_BUTTON_COLOR);
                        ric_maxString = jsonObj.isNull(TAG_RIC_MAX)? null : jsonObj.getString(TAG_RIC_MAX);
                        ric_session_maxString = jsonObj.isNull(TAG_RIC_SESSION_MAX)? null : jsonObj.getString(TAG_RIC_SESSION_MAX);

                        // Convert the opacity string (value "0" to "100") to a float (value 0.0 to 1.0)
                        if (ric_opacityString != null) {
                            int opacityInt = Integer.parseInt(ric_opacityString);
                            ric_opacity = ((float)opacityInt) / 100;
                        }

                        // Combine the content strings into one
                        if (bric_content1 != null || bric_content2 != null || bric_content3 != null) {
                            if (bric_content1 != null)          // If content1 has content,
                                bric_content = bric_content1;   //   use it
                            if (bric_content2 != null) {        // If content2 has content,
                                if (bric_content.length() > 0)  //   and if content already has content,
                                    bric_content += "\n\n";     //     add vertical white space
                                bric_content += bric_content2;  //   tack on content2
                            }
                            if (bric_content3 != null) {        // If content3 has content,
                                if (bric_content.length() > 0)  //   and if content already has content,
                                    bric_content += "\n\n";     //     add vertical white space
                                bric_content += bric_content3;  //   tack on content3
                            }
                        }

                        // Convert the ric_max value from either the retrieved JSON parameter or the default value
                        if (ric_maxString == null || ric_maxString.length() == 0)
                            ric_maxString = _activity.getResources().getString(R.string.ghostery_ric_max_default);

                        if (ric_maxString != null) {
                            Log.d(TAG, "ric_maxString = " + ric_maxString);
                            ric_max = Integer.parseInt(ric_maxString);
                        } else {
                            throw(new MissingResourceException("A default value for ric_max is missing as a string resource.", TAG, "ric_max"));
                        }

                        // Convert the ric_session_max value from either the retrieved JSON parameter or the default value
                        if (ric_session_maxString == null || ric_session_maxString.length() == 0)
                            ric_session_maxString = _activity.getResources().getString(R.string.ghostery_ric_session_max_default);

                        if (ric_session_maxString != null) {
                            ric_session_max = Integer.parseInt(ric_session_maxString);
                        } else {
                            throw(new MissingResourceException("A default value for ric_session_max is missing as a string resource.", TAG, "ric_session_max"));
                        }

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
            if (mTrackerConfigGetterCallback != null) {
                _activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mTrackerConfigGetterCallback.onTaskDone();
                    }
                });
            }

            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
        }

        protected String getFormattedTrackerConfigUrl() {
            Object[] urlParams = new Object[2];
            urlParams[0] = String.valueOf(company_id);			// 0
            urlParams[1] = String.valueOf(pub_notice_id);		// 1
            return MessageFormat.format(URL_JSON_REQUEST, urlParams);
        }

    }
}

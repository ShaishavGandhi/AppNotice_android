package com.ghostery.privacy.inappconsentsdk.model;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Steven.Overson on 5/6/2015.
 */
public class Tracker {
    private static final String TAG = "Tracker";
    private final static String CAT_ESSENTIAL = "Essential";

    // Field tags
    private static final String TAG_CATEGORY = "category";
    private static final String TAG_TRACKERID = "id";
    private static final String TAG_NAME = "name";
    private static final String TAG_LOGO_URL = "logo-url";
    private static final String TAG_DESCRIPTION = "description";
    private static final String TAG_PRIVACY_URL = "privacy-url";

    // Field values
    private String category;
    private int trackerId;
    private String name;
    private String logo_url;
    private String description;
    private String privacy_url;
    private boolean on = true;                  // Not in JSON. Defaults to true and is managed within the SDK
    private boolean hasHeader = false;      // Not in JSON. Defaults to false and is set after the tracker list is first loaded

    // Public getters and setters
    public String getCategory() { return category; }
    public int getTrackerId() { return trackerId; }
    public String getName() { return name; }
    public String getLogo_url() { return logo_url; }
    public String getDescription() { return description; }
    public String getPrivacy_url() { return privacy_url; }
    public boolean isOn() { return on; }
    public void setOnOffState(boolean isOn) { on = isOn; };
    public boolean hasHeader() { return hasHeader; }
    public void setHasHeader() { this.hasHeader = true; };

    // Constructor
    public Tracker(JSONObject trackerJSONObject) {
        try {

            category = trackerJSONObject.getString(TAG_CATEGORY);
            trackerId = trackerJSONObject.getInt(TAG_TRACKERID);
            name = trackerJSONObject.getString(TAG_NAME);
            logo_url = trackerJSONObject.getString(TAG_LOGO_URL);
            description = trackerJSONObject.getString(TAG_DESCRIPTION);
            privacy_url = trackerJSONObject.getString(TAG_PRIVACY_URL);
            on = true;
        } catch (JSONException e) {
            Log.e(TAG, "JSONException while parsing the Tracker object.", e);
        }

    }

    public boolean isEssential() {
        return (category.equalsIgnoreCase(CAT_ESSENTIAL));
    }
}

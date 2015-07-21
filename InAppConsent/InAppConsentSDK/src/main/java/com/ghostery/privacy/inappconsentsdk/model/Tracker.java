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
    private boolean isOn = true;              // Not in JSON. Defaults to true and is managed within the SDK
    private boolean hasHeader = false;      // Not in JSON. Defaults to false and is set after the tracker list is first loaded

    // Public getters and setters
    public String getCategory() { return category; }
    public int getTrackerId() { return trackerId; }
    public String getName() { return name; }
    public String getLogo_url() { return logo_url; }
    public String getDescription() { return description; }
    public String getPrivacy_url() { return privacy_url; }
    public boolean isOn() { return isOn; }
    public void setOnOffState(boolean isOn) { this.isOn = isOn; };
    public boolean hasHeader() { return hasHeader; }
    public void setHasHeader() { this.hasHeader = true; };

    // Constructors
    public Tracker(Tracker tracker) {
        category = tracker.category;
        trackerId = tracker.trackerId;
        name = tracker.name;
        logo_url = tracker.logo_url;
        description = tracker.description;
        privacy_url = tracker.privacy_url;
        isOn = tracker.isOn;
        hasHeader = tracker.hasHeader;
    }

    public Tracker(JSONObject trackerJSONObject) {
        try {
            category = trackerJSONObject.isNull(TAG_CATEGORY)? null : trackerJSONObject.getString(TAG_CATEGORY);
            trackerId = trackerJSONObject.isNull(TAG_TRACKERID)? null : trackerJSONObject.getInt(TAG_TRACKERID);
            name = trackerJSONObject.isNull(TAG_NAME)? null : trackerJSONObject.getString(TAG_NAME);
            logo_url = trackerJSONObject.isNull(TAG_LOGO_URL)? null : trackerJSONObject.getString(TAG_LOGO_URL);
            description = trackerJSONObject.isNull(TAG_DESCRIPTION)? null : trackerJSONObject.getString(TAG_DESCRIPTION);
            privacy_url = trackerJSONObject.isNull(TAG_PRIVACY_URL)? null : trackerJSONObject.getString(TAG_PRIVACY_URL);
            isOn = true;
        } catch (JSONException e) {
            Log.e(TAG, "JSONException while parsing the Tracker object.", e);
        } catch (Exception e) {
            Log.e(TAG, "Exception while parsing the Tracker object.", e);
        }

    }

    public boolean isEssential() {
        return (category.equalsIgnoreCase(CAT_ESSENTIAL));
    }

}

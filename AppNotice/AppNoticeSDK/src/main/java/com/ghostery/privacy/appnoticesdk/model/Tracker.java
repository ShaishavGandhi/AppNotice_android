package com.ghostery.privacy.appnoticesdk.model;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Steven.Overson on 5/6/2015.
 */
public class Tracker {
    private static final String TAG = "Tracker";
    private final static String CAT_ESSENTIAL = "Essential";

    // Field tags for token-based notice list
    private static final String TAG_CATEGORY_VIA_TOKEN = "category";
    private static final String TAG_TRACKERID_VIA_TOKEN = "id";
    private static final String TAG_NAME_VIA_TOKEN = "name";
    private static final String TAG_LOGO_URL_VIA_TOKEN = "logoUrl";
    private static final String TAG_DESCRIPTION_VIA_TOKEN = "description";
    private static final String TAG_PRIVACY_URL_VIA_TOKEN = "privacyUrl";

    // Field tags
    private static final String TAG_CATEGORY = "category";
    private static final String TAG_TRACKERID = "id";
    private static final String TAG_NAME = "name";
    private static final String TAG_LOGO_URL = "logo-url";
    private static final String TAG_DESCRIPTION = "description";
    private static final String TAG_PRIVACY_URL = "privacy-url";

    // Field values
    public int uId;             // Unique identifier for this tracker (local only)
    private String category;
    private int trackerId;
    private String name;
    private String logo_url;
    private String description;
    private String privacy_url;
    private Boolean isOn = null;              // Not in JSON. Defaults to null (uninitialized, but true) and is managed within the SDK

    // Public getters and setters
    public String getCategory() { return category; }
    public int getTrackerId() { return trackerId; }
    public String getName() { return name; }
    public String getLogo_url() { return logo_url; }
    public String getDescription() { return description; }
    public String getPrivacy_url() { return privacy_url; }
    public boolean isOn() { return isOn == null? true : isOn; }
    public boolean isNull() { return isOn == null; }
    public void setOnOffState(boolean isOn) { this.isOn = isOn; };

    // Constructors
    public Tracker(Tracker tracker) {
        category = tracker.category;
        trackerId = tracker.trackerId;
        name = tracker.name;
        logo_url = tracker.logo_url;
        description = tracker.description;
        privacy_url = tracker.privacy_url;
        isOn = tracker.isOn;
    }

    public Tracker(JSONObject trackerJSONObject) {
        try {
            if (AppNoticeData.usingToken) {
                category = trackerJSONObject.isNull(TAG_CATEGORY_VIA_TOKEN)? null : trackerJSONObject.getString(TAG_CATEGORY_VIA_TOKEN);
                trackerId = trackerJSONObject.isNull(TAG_TRACKERID_VIA_TOKEN)? null : trackerJSONObject.getInt(TAG_TRACKERID_VIA_TOKEN);
                name = trackerJSONObject.isNull(TAG_NAME_VIA_TOKEN)? null : trackerJSONObject.getString(TAG_NAME_VIA_TOKEN);
                logo_url = trackerJSONObject.isNull(TAG_LOGO_URL_VIA_TOKEN)? null : trackerJSONObject.getString(TAG_LOGO_URL_VIA_TOKEN);
                description = trackerJSONObject.isNull(TAG_DESCRIPTION_VIA_TOKEN)? null : trackerJSONObject.getString(TAG_DESCRIPTION_VIA_TOKEN);
                privacy_url = trackerJSONObject.isNull(TAG_PRIVACY_URL_VIA_TOKEN)? null : trackerJSONObject.getString(TAG_PRIVACY_URL_VIA_TOKEN);
                isOn = null;    // Default to null (uninitialized, but on)
            } else {
                category = trackerJSONObject.isNull(TAG_CATEGORY)? null : trackerJSONObject.getString(TAG_CATEGORY);
                trackerId = trackerJSONObject.isNull(TAG_TRACKERID)? null : trackerJSONObject.getInt(TAG_TRACKERID);
                name = trackerJSONObject.isNull(TAG_NAME)? null : trackerJSONObject.getString(TAG_NAME);
                logo_url = trackerJSONObject.isNull(TAG_LOGO_URL)? null : trackerJSONObject.getString(TAG_LOGO_URL);
                description = trackerJSONObject.isNull(TAG_DESCRIPTION)? null : trackerJSONObject.getString(TAG_DESCRIPTION);
                privacy_url = trackerJSONObject.isNull(TAG_PRIVACY_URL)? null : trackerJSONObject.getString(TAG_PRIVACY_URL);
                isOn = null;    // Default to null (uninitialized, but on)
            }
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

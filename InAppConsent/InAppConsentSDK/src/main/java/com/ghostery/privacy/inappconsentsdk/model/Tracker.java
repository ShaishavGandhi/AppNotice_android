package com.ghostery.privacy.inappconsentsdk.model;

/**
 * Created by Steven.Overson on 5/6/2015.
 */
public class Tracker {

    // Field tags
    private static final String TAG_CATEGORY = "category";
    private static final String TAG_NAME = "name";
    private static final String TAG_LOGO_URL = "logo-url";
    private static final String TAG_DESCRIPTION = "description";
    private static final String TAG_PRIVACY_URL = "privacy-url";

    // Field values
    private String category;
    private String name;
    private String logo_url;
    private String description;
    private String privacy_url;

    // Public getters and setters
    public String getCategory() { return category; }
    public String getName() { return name; }
    public String getLogo_url() { return logo_url; }
    public String getDescription() { return description; }
    public String getPrivacy_url() { return privacy_url; }


}

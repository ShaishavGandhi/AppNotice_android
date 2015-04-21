package com.ghostery.privacy.inappconsentsdk.utils;

import android.content.Context;
import android.util.Log;
import android.util.Xml;

import com.ghostery.privacy.inappconsentsdk.R;
import com.ghostery.privacy.inappconsentsdk.model.Company;
import com.ghostery.privacy.inappconsentsdk.model.Optout;
import com.ghostery.privacy.inappconsentsdk.model.OwnerCompany;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;


public class XMLParser {

    private static final String TAG = "AppChoices";
    Boolean goToSite;
    String method;
    String requetsBodyPOST;

    public void parse(Context context) {
        // Download the XML file when network is available. Write the XML to the device storage.
        if (Network.isNetworkAvailable(context)) {
            String xml = FileDownloader.getDataFromUrl(context.getString(R.string.ghostery_url_mobile_opt_out_providers));
            FileWriter.writeFile(context, context.getString(R.string.ghostery_file_mobile_opt_out_providers), xml);
        }

        // Read XML file from device. Parse XML and store to Company model.
        try {
            parseXml(FileReader.readReader(context, context.getString(R.string.ghostery_file_mobile_opt_out_providers)));
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    /*
     * Setup XmlPullParser for parsing XML data
     */
    private void parseXml(StringReader reader) {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(reader);
            parser.nextTag();
            parseXmlData(parser);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        } finally {
            reader.close();
        }
    }

    private void parseXmlData(XmlPullParser parser) throws XmlPullParserException, IOException {
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG)
                continue;
            String tag = parser.getName();
            // Starts by looking for the company tag
            if (tag.equalsIgnoreCase("company")) {
                parseCompanyXml(parser);
            } else {
                skipTag(parser);
            }
        }
    }

    private void parseCompanyXml(XmlPullParser parser) throws XmlPullParserException, IOException {
        // tags to save from XML file
        Integer companyId = null;
        String companyName = null;
        String companyLogoUrl = null;
        String companyDesc = null;
        String companyCollectionPolicy = null;
        String companyWebsiteUrl = null;
        String companyAboutUrl = null;
        String companyPrivacyUrl = null;
        String companyOptOutUrl = null;
        String companyDaaDescription = null;
        String companyDaaLink = null;
        this.requetsBodyPOST = null;
        this.method = null;
        ArrayList<String> companyCategory = new ArrayList<String>();
        ArrayList<String> companyAdnotice = new ArrayList<String>();
        Company company = new Company();
        Optout optout = new Optout();
        OwnerCompany owner = new OwnerCompany();

        parser.require(XmlPullParser.START_TAG, null, "company");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG)
                continue;

            String tag = parser.getName();
            if (tag.equalsIgnoreCase("company-about-us-url")) {
                companyAboutUrl = readTag(parser, "company-about-us-url");
            } else if (tag.equalsIgnoreCase("company-id")) {
                companyId = Integer.parseInt(readTag(parser, "company-id"));
            } else if (tag.equalsIgnoreCase("company-in-their-own-words")) {
                companyDesc = readTag(parser, "company-in-their-own-words");
            } else if (tag.equalsIgnoreCase("company-logo-url")) {
                companyLogoUrl = readTag(parser, "company-logo-url");
            } else if (tag.equalsIgnoreCase("company-name")) {
                companyName = readTag(parser, "company-name");
            } else if (tag.equalsIgnoreCase("company-privacy-url")) {
                companyPrivacyUrl = readTag(parser, "company-privacy-url");
            } else if (tag.equalsIgnoreCase("company-website-url")) {
                companyWebsiteUrl = readTag(parser, "company-website-url");
            } else if (tag.equalsIgnoreCase("collection-policies-text")) {
                companyCollectionPolicy = readTag(parser, "collection-policies-text");
            } else if (tag.equalsIgnoreCase("categories")) {
                companyCategory = parseArrayTag(parser, "categories", "category", "category-title");
            } else if (tag.equalsIgnoreCase("ad-notices")) {
                companyAdnotice = parseArrayTag(parser, "ad-notices", "ad-notice", "an-id");
            } else if (tag.equalsIgnoreCase("mobile-opt-out")) {
                companyOptOutUrl = parseOptOutUrl(parser);
            } else if (tag.equalsIgnoreCase("company-daa-description")) {
                companyDaaDescription = readTag(parser, "company-daa-description");
            } else if (tag.equalsIgnoreCase("company-daa-link")) {
                companyDaaLink = readTag(parser, "company-daa-link");
            } else {
                skipTag(parser);
            }
        }

        // Only add company if there is an opt out for Android
        if (companyOptOutUrl.length() != 0) {
            company.addCompany(new Company.CompanyData(companyId, companyName, companyLogoUrl, companyDesc, companyCollectionPolicy, companyWebsiteUrl,
                    companyAboutUrl, companyPrivacyUrl, companyOptOutUrl, companyCategory, goToSite, companyDaaDescription, companyDaaLink, this.method, this.requetsBodyPOST));
            optout.addOptout(new Optout.OptoutData(companyId));
            owner.addOwnerCompany(new OwnerCompany.OwnerCompanyData(companyId, companyAdnotice));
            companyCategory = new ArrayList<String>();
            companyAdnotice = new ArrayList<String>();
        }
    }

    /*
     * Reads the value from an XML tag and returns a String
     */
    private String readTag(XmlPullParser parser, String tag) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, tag);
        String value = readTagValue(parser);
        parser.require(XmlPullParser.END_TAG, null, tag);
        return value;
    }

    /*
     * Called from readTag to get the text from a tag and returns a String
     */
    private String readTagValue(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    /*
     * Parse through nested tags to get the value from the tag we seek
     *
     * @param startTag - the starting xml tag
     *
     * @param seekTag - the tag in which we want to extract the value/contents
     */
    private String parseNestedTag(XmlPullParser parser, String startTag, String seekTag) throws IOException, XmlPullParserException {
        String tag = "";
        parser.require(XmlPullParser.START_TAG, null, startTag);
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG)
                continue;

            String xml = parser.getName();
            if (xml.equalsIgnoreCase(seekTag)) {
                tag = readTag(parser, seekTag);
            } else {
                skipTag(parser);
            }
        }
        return tag;
    }

    /*
     * Parse and XML array of tags
     */
    private ArrayList<String> parseArrayTag(XmlPullParser parser, String startTag, String nextTag, String seekTag) throws IOException, XmlPullParserException {
        ArrayList<String> list = new ArrayList<String>();
        parser.require(XmlPullParser.START_TAG, null, startTag);
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG)
                continue;

            String xml = parser.getName();
            if (xml.equalsIgnoreCase(nextTag)) {
                list.add(parseNestedTag(parser, nextTag, seekTag));
            } else {
                skipTag(parser);
            }
        }
        return list;
    }

    /*
     * Parse the opt out url and keep track if the url is a 'go to site' url
     */
    private String parseOptOutUrl(XmlPullParser parser) throws IOException, XmlPullParserException {
        String url = "";
        parser.require(XmlPullParser.START_TAG, null, "mobile-opt-out");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG)
                continue;

            String xml = parser.getName();
            if (xml.equalsIgnoreCase("moo-android-url")) {
                url = readTag(parser, "moo-android-url");
                this.goToSite = false;
            } else if (url.length() == 0 && xml.equalsIgnoreCase("moo-go-to-site-android-url")) {
                url = readTag(parser, "moo-go-to-site-android-url");
                this.goToSite = true;
            } else if (xml.equalsIgnoreCase("moo-request-method")){
                this.method = readTag(parser, "moo-request-method");
            } else if (xml.equalsIgnoreCase("moo-ios-android-data")){
                this.requetsBodyPOST = readTag(parser, "moo-ios-android-data");
            } else {
                skipTag(parser);
            }
        }
        return url;
    }

    /*
     * Skips tags the parser isn't interested in. Skips nested tags.
     */
    private void skipTag(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG)
            throw new IllegalStateException();

        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }
}

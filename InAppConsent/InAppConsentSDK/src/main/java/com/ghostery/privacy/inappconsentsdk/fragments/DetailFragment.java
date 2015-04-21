package com.ghostery.privacy.inappconsentsdk.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ghostery.privacy.inappconsentsdk.R;
import com.ghostery.privacy.inappconsentsdk.model.Company;
import com.ghostery.privacy.inappconsentsdk.utils.FileReader;
import com.ghostery.privacy.inappconsentsdk.utils.ImageDownloader;


/**
 * A fragment representing a single Company detail screen.
 */
public class DetailFragment extends Fragment {

    private static final String TAG = "AppChoices";

    /**
     * The fragment argument representing the application description that this fragment represents
     */
    public Boolean ARG_APP_DESC = false;

    /**
     * The fragment argument representing the legal doc that this fragment represents
     */
    public String ARG_LEGAL_FILE = null;

    /**
     * The fragment argument representing the company id that this fragment represents.
     */
    public static final String ARG_COMPANY_ID = "id";

    /**
     * The company this fragment is presenting.
     */
    private Company.CompanyData mCompany;

//    private Typeface arial;
//    private Typeface arial_bold;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the fragment (e.g. upon screen orientation
     * changes).
     */
    public DetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_COMPANY_ID)) {
            mCompany = Company.COMPANY_MAP.get(getArguments().getString(ARG_COMPANY_ID));
        } else if (getArguments().containsKey("ARG_LEGAL_FILE")) {
            ARG_LEGAL_FILE = getArguments().getString("ARG_LEGAL_FILE");
        } else if (getArguments().containsKey("ARG_APP_DESC")) {
            ARG_APP_DESC = getArguments().getBoolean("ARG_APP_DESC");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.ghostery_company_detail, container, false);
        ListFragment.adapter.notifyDataSetChanged();
        // Show the content
        if (mCompany != null) {
            rootView = inflater.inflate(R.layout.ghostery_company_detail, container, false);
            displayCompanyDetails(rootView);
        } else if (ARG_APP_DESC) {
            rootView = inflater.inflate(R.layout.ghostery_app_detail, container, false);
            displayApplicationDescription(rootView);
        } else if (ARG_LEGAL_FILE != null) {
            displayLegalDocument(rootView);
        }

        return rootView;
    }

    private void displayApplicationDescription(View rootView) {
//        arial = Typeface.createFromAsset(rootView.getContext().getAssets(),
//                "fonts/arial.ttf");
//
//        arial_bold = Typeface.createFromAsset(rootView.getContext().getAssets(),
//                "fonts/arial_bold.ttf");

        LinearLayout layout = (LinearLayout) rootView.findViewById(R.id.item_detail);


        WebView desc = (WebView) rootView.findViewById(R.id.app_desc);
        String text;
        text = "<html><body><p style=\"font-family:Arial\">";
        text+= getString(R.string.ghostery_app_desc_1);
        text+="<b> "+getString(R.string.ghostery_app_desc_2)+"</b>";
        text+="<br/><br/>"+getString(R.string.ghostery_app_desc_3);
        text+= "</p></body></html>";
        desc.loadData(text, "text/html", "utf-8");

        layout.removeAllViews();
        layout.addView(desc);

    }

    private void displayLegalDocument(View rootView) {

        LinearLayout layout = (LinearLayout) rootView.findViewById(R.id.item_detail);

        WebView legal = (WebView) rootView.findViewById(R.id.legal_doc);
        //legal.loadDataWithBaseURL(null, FileReader.readString(getActivity(), ARG_LEGAL_FILE), "text/html", "UTF-8", null);

        String pish = "<html><head><style type=\"text/css\">@font-face {font-family: MyFont;src: url(\"file:///android_asset/fonts/arial.ttf\")}body {font-family: MyFont;font-size: small;}</style></head><body>";
        String pas = "</body></html>";
        String myHtmlString = pish + FileReader.readString(getActivity(), ARG_LEGAL_FILE) + pas;
        legal.loadDataWithBaseURL(null,myHtmlString, "text/html", "UTF-8", null);

        layout.removeAllViews();
        layout.addView(legal);

    }

    private void displayCompanyDetails(View rootView) {
//        arial = Typeface.createFromAsset(rootView.getContext().getAssets(),
//                "fonts/arial.ttf");
//
//        arial_bold = Typeface.createFromAsset(rootView.getContext().getAssets(),
//                "fonts/arial_bold.ttf");

        LinearLayout layout = (LinearLayout) rootView.findViewById(R.id.item_detail);

        // company logo
        ImageView comp_logo = (ImageView) rootView.findViewById(R.id.comp_logo);
        ImageDownloader imgManager = new ImageDownloader();
        imgManager.download(mCompany.logo, comp_logo);

        // company name (if image not available)
        TextView comp_name = (TextView) rootView.findViewById(R.id.comp_name);
        comp_name.setText(mCompany.name);
//        comp_name.setTypeface(arial);

        // company website
        TextView comp_website = (TextView) rootView.findViewById(R.id.comp_website_url);
        comp_website.setText(Html.fromHtml(
                        "<a href=\""+mCompany.website+"\">"+mCompany.website+"</a> "
        ));
        comp_website.setMovementMethod(LinkMovementMethod.getInstance());

        // company daa description
        TextView comp_info = (TextView) rootView.findViewById(R.id.comp_info);
        comp_info.setText(mCompany.daaDescription);
//        comp_info.setTypeface(arial);

        // company daa link
        TextView comp_daa_link = (TextView) rootView.findViewById(R.id.comp_to_learn_more_url);
        comp_daa_link.setText(Html.fromHtml(
                "<a href=\""+mCompany.daaLink+"\">"+mCompany.daaLink+"</a> "
        ));
//        comp_daa_link.setTypeface(arial);
        comp_daa_link.setMovementMethod(LinkMovementMethod.getInstance());

        // Determine if logo or text should be shown
        if (comp_logo.getDrawable().getIntrinsicHeight() <= 0) {
            layout.removeView(comp_logo);
        } else {
            layout.removeView(comp_name);
        }

        //setting proper font to all view parts
        TextView comp_website_title = (TextView) rootView.findViewById(R.id.comp_website_title);
//        comp_website_title.setTypeface(arial_bold);

        TextView comp_website_url = (TextView) rootView.findViewById(R.id.comp_website_url);
//        comp_website_url.setTypeface(arial);

        TextView comp_info_title = (TextView) rootView.findViewById(R.id.comp_info_title);
//        comp_info_title.setTypeface(arial_bold);

        TextView comp_to_learn_more = (TextView) rootView.findViewById(R.id.comp_to_learn_more);
//        comp_to_learn_more.setTypeface(arial);

        TextView comp_to_learn_more_url = (TextView) rootView.findViewById(R.id.comp_to_learn_more_url);
//        comp_to_learn_more_url.setTypeface(arial);

    }

    public String getStringResourceByName(String str) {
        return getString(getResources().getIdentifier(str.replace(" ", "_").toLowerCase(), "string", getActivity().getPackageName()));
    }

}
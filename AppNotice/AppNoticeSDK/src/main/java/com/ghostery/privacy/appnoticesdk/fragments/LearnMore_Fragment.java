package com.ghostery.privacy.appnoticesdk.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.ghostery.privacy.appnoticesdk.R;
import com.ghostery.privacy.appnoticesdk.model.AppNoticeData;
import com.ghostery.privacy.appnoticesdk.model.Tracker;

/**
 */
public class LearnMore_Fragment extends Fragment {

//    private OnFragmentInteractionListener mListener;

    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    /**
     * The content this fragment is presenting.
     */
    private Tracker tracker;

    public LearnMore_Fragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get either a new or initialized tracker config object
        AppNoticeData appNoticeData = AppNoticeData.getInstance(getActivity());

        if (appNoticeData.isTrackerListInitialized()) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            tracker = appNoticeData.getTrackerById(getArguments().getInt(ARG_ITEM_ID));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.ghostery_fragment_learn_more, container, false);

        WebView learnmore_webview = (WebView) view.findViewById(R.id.learnmore_webview);
        learnmore_webview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url){
                // Used to load the end-result of a redirected URL into the web view
                view.loadUrl(url);
                return false; // Allow this webview to handle loading the specified URL
            }
        });
        learnmore_webview.loadUrl(tracker.getPrivacy_url());

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        getActivity().setTitle(R.string.ghostery_tracker_learnmore_title);
    }

    public void onBackPressed() {
        getActivity().getSupportFragmentManager().popBackStack();
    }
}

package com.ghostery.privacy.inappconsentsdk.fragments;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.ghostery.privacy.inappconsentsdk.R;
import com.ghostery.privacy.inappconsentsdk.app.TrackerDetailActivity;
import com.ghostery.privacy.inappconsentsdk.model.InAppConsentData;
import com.ghostery.privacy.inappconsentsdk.model.Tracker;

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
        InAppConsentData inAppConsentData = InAppConsentData.getInstance(getActivity());

        if (inAppConsentData.isInitialized()) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            tracker = inAppConsentData.getTrackerById(getArguments().getInt(ARG_ITEM_ID));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.ghostery_fragment_learn_more, container, false);

        WebView learnmore_webview = (WebView) view.findViewById(R.id.learnmore_webview);
        learnmore_webview.loadUrl(tracker.getPrivacy_url());

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        ((TrackerDetailActivity)getActivity()).setActionBarTitle(R.string.ghostery_tracker_learnmore_title);
    }

    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
    }

//    @Override
//    public void onAttach(Activity fragmentActivity) {
//        super.onAttach(fragmentActivity);
//        try {
//            mListener = (OnFragmentInteractionListener) fragmentActivity;
//        } catch (ClassCastException e) {
//            throw new ClassCastException(fragmentActivity.toString() + " must implement OnFragmentInteractionListener");
//        }
//    }

    @Override
    public void onDetach() {
        super.onDetach();
//        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the fragmentActivity and potentially other fragments contained in that
     * fragmentActivity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
//    public interface OnFragmentInteractionListener {
//        // TODO: Update argument type and name
//        public void onFragmentInteraction(Uri uri);
//    }

}

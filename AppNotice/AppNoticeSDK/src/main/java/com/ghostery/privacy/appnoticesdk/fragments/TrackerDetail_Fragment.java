package com.ghostery.privacy.appnoticesdk.fragments;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Switch;

import com.ghostery.privacy.appnoticesdk.AppNotice_Activity;
import com.ghostery.privacy.appnoticesdk.R;
import com.ghostery.privacy.appnoticesdk.callbacks.LogoDownload_Callback;
import com.ghostery.privacy.appnoticesdk.model.AppNoticeData;
import com.ghostery.privacy.appnoticesdk.model.Tracker;
import com.ghostery.privacy.appnoticesdk.utils.ImageDownloader;
import com.ghostery.privacy.appnoticesdk.utils.Util;

/**
 * A fragment representing a single Tracker detail screen.
 */
public class TrackerDetail_Fragment extends Fragment {

    private AppNoticeData appNoticeData;

    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    /**
     * The content this fragment is presenting.
     */
    private Tracker tracker;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public TrackerDetail_Fragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.show();
        }

        // Get either a new or initialized tracker config object
        appNoticeData = AppNoticeData.getInstance(getActivity());

        if (appNoticeData.isTrackerListInitialized()) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            Bundle bundle = getArguments();
            int itemId = 0;
            if (bundle != null)
                itemId = bundle.getInt(ARG_ITEM_ID);
            tracker = appNoticeData.getTrackerById(itemId);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.ghostery_fragment_tracker_detail, container, false);
        final ImageView imageView_trackerLogo = (ImageView) rootView.findViewById(R.id.imageView_trackerLogo);
        final AppCompatTextView textView_TrackerName = (AppCompatTextView) rootView.findViewById(R.id.textView_TrackerName);


        // Show the dummy content as text in a TextView.
        if (tracker != null) {
            ImageDownloader imageDownloader = new ImageDownloader(getActivity(), tracker.uId, new LogoDownload_Callback() {

                @Override
                public void onDownloaded(int position) {
                    Drawable logoDrawable = imageView_trackerLogo.getDrawable();
					if (logoDrawable == null || logoDrawable.getIntrinsicHeight() <= 0) {
						imageView_trackerLogo.setVisibility(View.GONE);
						textView_TrackerName.setVisibility(View.VISIBLE);
						textView_TrackerName.setText(tracker.getName());
                    } else {                                                // Else use the name text
						imageView_trackerLogo.setVisibility(View.VISIBLE);
						textView_TrackerName.setVisibility(View.GONE);
                    }
                }
            });

            imageDownloader.download(tracker.getLogo_url(), imageView_trackerLogo);

            // Determine if logo or tracker name should be shown
            Drawable logoDrawable = imageView_trackerLogo.getDrawable();
            if (logoDrawable == null || logoDrawable.getIntrinsicHeight() <= 0) {
                imageView_trackerLogo.setVisibility(View.GONE);
                textView_TrackerName.setVisibility(View.VISIBLE);
                textView_TrackerName.setText(tracker.getName());
            } else {
                imageView_trackerLogo.setVisibility(View.VISIBLE);
                textView_TrackerName.setVisibility(View.GONE);
            }

            CheckBox opt_in_out_checkbox = ((CheckBox) rootView.findViewById(R.id.opt_in_out_checkbox));
            if (tracker.isEssential()) {
                opt_in_out_checkbox.setEnabled(false);
            } else {
                opt_in_out_checkbox.setEnabled(true);

                // If this tracker is a duplicate of an essential tracker, disable it
                if (appNoticeData.isTrackerDuplicateOfEssentialTracker(tracker.getTrackerId())){
                    opt_in_out_checkbox.setChecked(true);     // Make sure it is checked
                    opt_in_out_checkbox.setEnabled(false);    // Disable the switch
                } else {
                    opt_in_out_checkbox.setChecked(tracker.isOn());
                    opt_in_out_checkbox.setEnabled(true);     // Enable the switch
                    opt_in_out_checkbox.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Boolean isOn = ((Switch)v).isChecked();
                            AppNoticeData appNoticeData = AppNoticeData.getInstance(getActivity());

                            if (appNoticeData != null && appNoticeData.isTrackerListInitialized()) {
                                if (tracker != null) {
                                    appNoticeData.setTrackerOnOffState(tracker.uId, isOn);
                                }
                            }
                        }
                    });
                }
            }

            ((AppCompatTextView) rootView.findViewById(R.id.textView_trackerDescription)).setText(tracker.getDescription());

            AppCompatTextView textView_learn_more = ((AppCompatTextView) rootView.findViewById(R.id.textView_learn_more));
            AppCompatTextView textView_learn_more_url = ((AppCompatTextView) rootView.findViewById(R.id.textView_learn_more_url));
            String learnMoreUrl = tracker.getPrivacy_url();
            if (textView_learn_more_url != null) {
                Boolean isUrlValid = Util.checkURL(learnMoreUrl);
                if (isUrlValid) {
                    textView_learn_more_url.setVisibility(View.VISIBLE);
                    textView_learn_more_url.setText(learnMoreUrl);
                } else {
                    textView_learn_more.setText(R.string.ghostery_preferences_detail_learnmore_not_provided);
                    textView_learn_more_url.setVisibility(View.GONE);
                }

                textView_learn_more_url.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle bundle = new Bundle();
                        bundle.putInt(LearnMore_Fragment.ARG_ITEM_ID, getActivity().getIntent().getIntExtra(TrackerDetail_Fragment.ARG_ITEM_ID, 0));
                        LearnMore_Fragment fragment = new LearnMore_Fragment();

                        fragment.setArguments(bundle);
                        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.appnotice_fragment_container, fragment, AppNotice_Activity.FRAGMENT_TAG_LEARN_MORE);
                        transaction.addToBackStack(AppNotice_Activity.FRAGMENT_TAG_LEARN_MORE);
                        transaction.commit();
                    }
                });
            }
        }

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        getActivity().setTitle(R.string.ghostery_tracker_detail_title);
    }

    public void onBackPressed() {
        // Do nothing
    }

}

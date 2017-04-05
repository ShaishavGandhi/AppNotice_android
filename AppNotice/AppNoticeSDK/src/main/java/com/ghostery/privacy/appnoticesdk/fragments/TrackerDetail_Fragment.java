package com.ghostery.privacy.appnoticesdk.fragments;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.ghostery.privacy.appnoticesdk.AppNotice;
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

    private static final String TAG = "TrackerDetail_Fragment";
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

            RelativeLayout relativeLayout_allow_technology = ((RelativeLayout) rootView.findViewById(R.id.relativeLayout_allow_technology));
            CheckBox opt_in_out_checkbox = ((CheckBox) rootView.findViewById(R.id.opt_in_out_checkbox));
            if (tracker.isEssential()) {
                relativeLayout_allow_technology.setVisibility(View.GONE);
            } else {
                // If this tracker is a duplicate of an essential tracker, disable it
                if (appNoticeData.isTrackerDuplicateOfEssentialTracker(tracker.getTrackerId())){
                    relativeLayout_allow_technology.setVisibility(View.GONE);
                } else {
                    opt_in_out_checkbox.setChecked(tracker.isOn());
                    opt_in_out_checkbox.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View checkBoxView) {
                            Boolean isOn = ((CheckBox)checkBoxView).isChecked();
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

            Boolean isUrlValid = false;
            try {
                isUrlValid = Util.checkURL(learnMoreUrl);
            } catch (Exception e) {
                isUrlValid = false;
                Log.e(TAG, "Determining if URL is valid", e);
            }

            if (textView_learn_more_url != null && isUrlValid) {
                textView_learn_more.setVisibility(View.VISIBLE);
                textView_learn_more.setText(getResources().getString(R.string.ghostery_tracker_detail_learnmore));
                textView_learn_more_url.setVisibility(View.VISIBLE);
                textView_learn_more_url.setText(learnMoreUrl);
                textView_learn_more_url.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(tracker.getPrivacy_url()));
                        startActivity(i);
                    }
                });
            } else {
                textView_learn_more.setText(getResources().getString(R.string.ghostery_tracker_detail_learnmore_not_provided));
                textView_learn_more_url.setVisibility(View.GONE);
            }
        }

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        LinearLayout explicitButtonLayout = (LinearLayout)getView().findViewById(R.id.explicit_button_layout);
        final AppNotice_Activity appNotice_activity = (AppNotice_Activity) getActivity();

        if (AppNotice_Activity.isConsentActive) {
            if (AppNotice.isImpliedMode) {
                // If implied mode, show the snackbar
                CoordinatorLayout coordinatorlayout = (CoordinatorLayout)getView().findViewById(R.id.coordinatorLayout);
                Snackbar snackbar = Snackbar
                        .make(coordinatorlayout, R.string.ghostery_preferences_ready_message, Snackbar.LENGTH_INDEFINITE)
                        .setAction(R.string.ghostery_preferences_continue_button, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Send notice for this event
                                AppNoticeData.sendNotice(AppNoticeData.pingEvent.IMPLIED_CONTINUE);
                                appNotice_activity.handleTrackerStateChanges();

                                // Let the calling class know the selected option
                                AppNoticeData appNoticeData = AppNoticeData.getInstance(appNotice_activity);

                                if (AppNotice_Activity.appNotice_callback != null) {
                                    AppNotice_Activity.appNotice_callback.onOptionSelected(true, appNoticeData.getTrackerHashMap(true));
                                }

                                // Close this fragment
                                AppNotice_Activity.isConsentActive = false;
                                appNotice_activity.finish();
                            }
                        });

                snackbar.show();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        getActivity().setTitle(tracker.getName());  // (R.string.ghostery_tracker_detail_title);
    }

    public void onBackPressed() {
        // Do nothing
    }

}

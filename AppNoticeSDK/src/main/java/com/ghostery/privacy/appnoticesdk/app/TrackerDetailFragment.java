package com.ghostery.privacy.appnoticesdk.app;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.ghostery.privacy.appnoticesdk.R;
import com.ghostery.privacy.appnoticesdk.callbacks.LogoDownload_Callback;
import com.ghostery.privacy.appnoticesdk.model.AppNoticeData;
import com.ghostery.privacy.appnoticesdk.model.Tracker;
import com.ghostery.privacy.appnoticesdk.utils.ImageDownloader;
import com.ghostery.privacy.appnoticesdk.utils.Util;

/**
 * A fragment representing a single Tracker detail screen.
 * This fragment is either contained in a {@link TrackerListActivity}
 * in two-pane mode (on tablets) or a {@link TrackerDetailActivity}
 * on handsets.
 */
public class TrackerDetailFragment extends Fragment {

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
    public TrackerDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get either a new or initialized tracker config object
        appNoticeData = AppNoticeData.getInstance(getActivity());

        if (appNoticeData.isInitialized()) {
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
        final TextView textView_TrackerName = ((TextView) rootView.findViewById(R.id.textView_TrackerName));


        // Show the dummy content as text in a TextView.
        if (tracker != null) {
            ImageDownloader imageDownloader = new ImageDownloader(getActivity(), tracker.uId, new LogoDownload_Callback() {

                @Override
                public void onDownloaded(int position) {
                    if (imageView_trackerLogo.getHeight() > 0) {            // If the logo is substantial, use it,
                        imageView_trackerLogo.setVisibility(View.VISIBLE);
                        textView_TrackerName.setVisibility(View.GONE);
                    } else {                                                // Else use the name text
                        imageView_trackerLogo.setVisibility(View.GONE);
                        textView_TrackerName.setVisibility(View.VISIBLE);
                        textView_TrackerName.setText(tracker.getName());
                    }
                }
            });

            imageDownloader.download(tracker.getLogo_url(), imageView_trackerLogo);

            // Determine if logo or tracker name should be shown
            Drawable trackerLogo = imageView_trackerLogo.getDrawable();
            if (trackerLogo == null || trackerLogo.getIntrinsicHeight() <= 0) {
                imageView_trackerLogo.setVisibility(View.GONE);
                textView_TrackerName.setVisibility(View.VISIBLE);
                textView_TrackerName.setText(tracker.getName());
            } else {
                imageView_trackerLogo.setVisibility(View.VISIBLE);
                textView_TrackerName.setVisibility(View.GONE);
            }

            Switch opt_in_out_switch = ((Switch) rootView.findViewById(R.id.opt_in_out_switch));
            if (tracker.isEssential()) {
                opt_in_out_switch.setVisibility(View.INVISIBLE);
            } else {
                opt_in_out_switch.setVisibility(View.VISIBLE);

                // If this tracker is a duplicate of an essential tracker, disable it
                if (appNoticeData.isTrackerDuplicateOfEssentialTracker(tracker.getTrackerId())){
                    opt_in_out_switch.setChecked(true);     // Make sure it is checked
                    opt_in_out_switch.setEnabled(false);    // Disable the switch
                } else {
                    opt_in_out_switch.setChecked(tracker.isOn());
                    opt_in_out_switch.setEnabled(true);     // Enable the switch
                }
            }

            ((TextView) rootView.findViewById(R.id.textView_trackerDescription)).setText(tracker.getDescription());

            TextView textView_learn_more = ((TextView) rootView.findViewById(R.id.textView_learn_more));
            TextView textView_learn_more_url = ((TextView) rootView.findViewById(R.id.textView_learn_more_url));
            String learnMoreUrl = tracker.getPrivacy_url();
            boolean isUrlValid = Util.checkURL(learnMoreUrl);
            if (isUrlValid) {
                textView_learn_more_url.setVisibility(View.VISIBLE);
                textView_learn_more_url.setText(learnMoreUrl);
            } else {
                textView_learn_more.setText(R.string.ghostery_manage_preferences_detail_learnmore_not_provided);
                textView_learn_more_url.setVisibility(View.GONE);
            }
        }

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        ((TrackerDetailActivity) getActivity()).setActionBarTitle(R.string.ghostery_tracker_detail_title);
    }

}

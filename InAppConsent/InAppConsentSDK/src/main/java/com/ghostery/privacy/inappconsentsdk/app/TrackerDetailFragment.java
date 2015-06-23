package com.ghostery.privacy.inappconsentsdk.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ghostery.privacy.inappconsentsdk.R;
import com.ghostery.privacy.inappconsentsdk.model.InAppConsentData;
import com.ghostery.privacy.inappconsentsdk.model.Tracker;
import com.ghostery.privacy.inappconsentsdk.utils.ImageDownloader;
import com.ghostery.privacy.inappconsentsdk.utils.Session;

/**
 * A fragment representing a single Tracker detail screen.
 * This fragment is either contained in a {@link TrackerListActivity}
 * in two-pane mode (on tablets) or a {@link TrackerDetailActivity}
 * on handsets.
 */
public class TrackerDetailFragment extends Fragment {

    private InAppConsentData inAppConsentData;

    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    /**
     * The dummy content this fragment is presenting.
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
        inAppConsentData = (InAppConsentData) Session.get(Session.INAPPCONSENT_DATA, InAppConsentData.getInstance(getActivity()));

        if (inAppConsentData.isInitialized()) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            tracker = inAppConsentData.getTrackerById(getArguments().getInt(ARG_ITEM_ID));

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_tracker_detail, container, false);


        // Show the dummy content as text in a TextView.
        if (tracker != null) {
            ImageDownloader imageDownloader = new ImageDownloader();
            ImageView imageView_trackerLogo = (ImageView) rootView.findViewById(R.id.imageView_trackerLogo);
            imageDownloader.download(tracker.getLogo_url(), imageView_trackerLogo);

            TextView textView_TrackerName = ((TextView) rootView.findViewById(R.id.textView_TrackerName));

            // Determine if logo or tracker name should be shown
            if (imageView_trackerLogo.getDrawable().getIntrinsicHeight() <= 0) {
                imageView_trackerLogo.setVisibility(View.GONE);
                textView_TrackerName.setVisibility(View.VISIBLE);
                textView_TrackerName.setText(tracker.getName());
            } else {
                imageView_trackerLogo.setVisibility(View.VISIBLE);
                textView_TrackerName.setVisibility(View.GONE);
            }

            ((TextView) rootView.findViewById(R.id.textView_trackerDescription)).setText(tracker.getDescription());
            ((TextView) rootView.findViewById(R.id.textView_learn_more_url)).setText(tracker.getPrivacy_url());
        }

        return rootView;
    }

    public void onLinkClick() {

    }
}

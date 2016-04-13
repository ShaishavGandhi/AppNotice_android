package com.ghostery.privacy.appnoticesdk.app;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Switch;

import com.ghostery.privacy.appnoticesdk.R;
import com.ghostery.privacy.appnoticesdk.fragments.LearnMore_Fragment;
import com.ghostery.privacy.appnoticesdk.model.AppNoticeData;
import com.ghostery.privacy.appnoticesdk.model.Tracker;
import com.ghostery.privacy.appnoticesdk.utils.Session;

/**
 * An fragmentActivity representing a single Tracker detail screen. This
 * fragmentActivity is only used on handset devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link TrackerListActivity}.
 * <p/>
 * This fragmentActivity is mostly just a 'shell' fragmentActivity containing nothing
 * more than a {@link TrackerDetailFragment}.
 */
public class TrackerDetailActivity extends AppCompatActivity {

    private int trackerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ghostery_activity_tracker_detail);

        // Show the Up button in the action bar.
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.ghostery_header_background_color)));
        }

        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this fragmentActivity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the fragmentActivity
            // using a fragment transaction.
            Bundle arguments = new Bundle();
            trackerId = getIntent().getIntExtra(TrackerDetailFragment.ARG_ITEM_ID, 0);
            arguments.putInt(TrackerDetailFragment.ARG_ITEM_ID, trackerId);

            final TrackerDetailFragment fragment = new TrackerDetailFragment();
            fragment.setArguments(arguments);
            final FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.tracker_detail_container, fragment);
//            transaction.replace(R.id.tracker_detail_container, fragment);
            transaction.addToBackStack(null);
            transaction.commit();

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // fragmentActivity, the Up button is shown. Use NavUtils to allow users
            // to navigate up one level in the application structure. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.tracker_detail_container);
            if (currentFragment instanceof TrackerDetailFragment) {
                NavUtils.navigateUpTo(this, new Intent(this, TrackerListActivity.class));
            } else {
                getSupportFragmentManager().popBackStack();
            }

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.tracker_detail_container);
        if (currentFragment instanceof TrackerDetailFragment) {
            this.finish();
        } else {
            getSupportFragmentManager().popBackStack();
        }
    }

    public void onLinkClick(View view) {
        Bundle bundle = new Bundle();
        bundle.putInt(LearnMore_Fragment.ARG_ITEM_ID, getIntent().getIntExtra(TrackerDetailFragment.ARG_ITEM_ID, 0));
        LearnMore_Fragment fragment = new LearnMore_Fragment();

        fragment.setArguments(bundle);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.tracker_detail_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void onOptInOutClick(View view) {
        Boolean isOn = ((Switch)view).isChecked();
        AppNoticeData appNoticeData = (AppNoticeData) Session.get(Session.APPNOTICE_DATA);
        Session.set(Session.APPNOTICE_ALL_BTN_SELECT, false);   // If they changed the state of a tracker, remember that "All" wasn't the last set state.
        Session.set(Session.APPNOTICE_NONE_BTN_SELECT, false);  // If they changed the state of a tracker, remember that "None" wasn't the last set state.

        if (appNoticeData != null && appNoticeData.isInitialized()) {
            Tracker tracker = appNoticeData.getTrackerById(trackerId);

            if (tracker != null) {
                appNoticeData.setTrackerOnOffState(tracker.uId, isOn);
            }
        }
    }

    public void setActionBarTitle(int titleId){
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(titleId);
        }
    }
}

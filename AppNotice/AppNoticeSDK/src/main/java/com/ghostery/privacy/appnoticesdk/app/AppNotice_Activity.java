package com.ghostery.privacy.appnoticesdk.app;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatCallback;
import android.view.View;
import android.widget.Switch;

import com.ghostery.privacy.appnoticesdk.R;
import com.ghostery.privacy.appnoticesdk.fragments.ManagePreferences_Fragment;
import com.ghostery.privacy.appnoticesdk.model.AppNoticeData;
import com.ghostery.privacy.appnoticesdk.model.Tracker;
import com.ghostery.privacy.appnoticesdk.utils.Session;

import java.util.ArrayList;

/**
 * An fragmentActivity representing a list of Trackers. This fragmentActivity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the fragmentActivity presents a list of items, which when touched,
 * lead to a {@link TrackerDetailActivity} representing
 * item details. On tablets, the fragmentActivity presents the list of items and
 * item details side-by-side using two vertical panes.
 * <p/>
 * The fragmentActivity makes heavy use of fragments. The list of items is a
 * {@link TrackerListFragment} and the item details
 * (if present) is a {@link TrackerDetailFragment}.
 * <p/>
 * This fragmentActivity also implements the required
 * {@link TrackerListFragment.Callbacks} interface
 * to listen for item selections.
 */
public class AppNotice_Activity extends AppCompatActivity implements AppCompatCallback, TrackerListFragment.Callbacks  {

    private ArrayList<Tracker> trackerArrayList;
    private ArrayList<Tracker> trackerArrayListClone;
    private AppNoticeData appNoticeData;
    private static AppCompatActivity activity;
    private FragmentManager fragmentManager;

    /**
     * Whether or not the fragmentActivity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        activity = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ghostery_activity_appnotice);
        fragmentManager = getSupportFragmentManager();

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.ghostery_header_background_color)));

            // If there is header text in the JSON, use it. Else use the default.
            if (appNoticeData != null)
                actionBar.setTitle(appNoticeData.getPreferencesHeader());
        }

        ManagePreferences_Fragment fragment = new ManagePreferences_Fragment();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.appnotice_fragment_container, fragment);
        ft.addToBackStack(null);
        ft.commit();
    }

    @Override
    public void onResume()
    {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        // ToDo: call current fragment's onBackPressed method

        super.onBackPressed();
    }

    /**
     * Callback method from {@link TrackerListFragment.Callbacks}
     * indicating that the item with the given ID was selected.
     */
    @Override
    public void onItemSelected(int uId) {
//        if (mTwoPane) {
        // In two-pane mode, show the detail view in this fragmentActivity by
        // adding or replacing the detail fragment using a
        // fragment transaction.
        Bundle arguments = new Bundle();
        arguments.putInt(TrackerDetailFragment.ARG_ITEM_ID, uId);
        TrackerDetailFragment fragment = new TrackerDetailFragment();
        fragment.setArguments(arguments);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.tracker_detail_container, fragment)
                .commit();

//        } else {
//            // In single-pane mode, simply start the detail fragmentActivity
//            // for the selected item ID.
//            Intent detailIntent = new Intent(this, TrackerDetailActivity.class);
//            detailIntent.putExtra(TrackerDetailFragment.ARG_ITEM_ID, uId);
//            startActivityForResult(detailIntent, 0);
//        }
    }

    public void onOptInOutClick(View view) {
        Boolean isOn = ((Switch)view).isChecked();
        int uId = (int)view.getTag();
        if (appNoticeData == null) {
            appNoticeData = (AppNoticeData)Session.get(Session.APPNOTICE_DATA);
        }

        if (appNoticeData != null) {
            appNoticeData.setTrackerOnOffState(uId, isOn);
        }
        Session.set(Session.APPNOTICE_ALL_BTN_SELECT, false);   // If they changed the state of a tracker, remember that "All" wasn't the last set state.
        Session.set(Session.APPNOTICE_NONE_BTN_SELECT, false);  // If they changed the state of a tracker, remember that "None" wasn't the last set state.

        TrackerListFragment trackerListFragment = (TrackerListFragment)getSupportFragmentManager().findFragmentById(R.id.tracker_list);
        if (trackerListFragment != null) {
            trackerListFragment.refresh();
        }

        ManagePreferences_Fragment managePreferences_fragment = (ManagePreferences_Fragment) getSupportFragmentManager().findFragmentById(R.id.appnotice_fragment_container);
        if (managePreferences_fragment != null && managePreferences_fragment.getClass().equals(ManagePreferences_Fragment.class)) {
            managePreferences_fragment.setAllNoneControlState();
        }
    }

//    @Override
//    public void onSupportActionModeStarted(ActionMode mode) {
//        //let's leave this empty, for now
//    }
//
//    @Override
//    public void onSupportActionModeFinished(ActionMode mode) {
//        // let's leave this empty, for now
//    }
}

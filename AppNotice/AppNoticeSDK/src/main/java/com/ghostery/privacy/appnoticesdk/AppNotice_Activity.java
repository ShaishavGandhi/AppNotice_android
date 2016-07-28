package com.ghostery.privacy.appnoticesdk;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatCallback;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;

import com.ghostery.privacy.appnoticesdk.callbacks.AppNotice_Callback;
import com.ghostery.privacy.appnoticesdk.fragments.ExplicitConsent_Fragment;
import com.ghostery.privacy.appnoticesdk.fragments.ImpliedConsent_Fragment;
import com.ghostery.privacy.appnoticesdk.fragments.ManagePreferences_Fragment;
import com.ghostery.privacy.appnoticesdk.fragments.TrackerDetail_Fragment;
import com.ghostery.privacy.appnoticesdk.model.AppNoticeData;
import com.ghostery.privacy.appnoticesdk.model.Tracker;

import java.util.ArrayList;

/**
 * AppNotice_Activity
 */
public class AppNotice_Activity extends AppCompatActivity implements AppCompatCallback, AdapterView.OnItemClickListener {

    private static AppNotice_Activity instance;
    private FragmentManager fragmentManager;
    public static AppNotice_Callback appNotice_callback;
    public static boolean isConsentActive = false;
    public static boolean isImpliedMode = true;
    public static AppNoticeData appNoticeData;
    public static ArrayList<Tracker> optionalTrackerArrayListClone;

    // Fragment tags
    public static final String FRAGMENT_TAG_IMPLIED_CONSENT = "IMPLIED_CONSENT";
    public static final String FRAGMENT_TAG_EXPLICIT_CONSENT = "EXPLICIT_CONSENT";
    public static final String FRAGMENT_TAG_MANAGE_PREFERENCES = "MANAGE_PREFERENCES";
    public static final String FRAGMENT_TAG_TRACKER_DETAIL = "TRACKER_DETAIL";
    public static final String FRAGMENT_TAG_EXPLICIT_DECLINE = "EXPLICIT_DECLINE";


    public static AppNotice_Activity getInstance() {
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        setContentView(R.layout.ghostery_activity_appnotice);
        fragmentManager = getSupportFragmentManager();

        Bundle extras = getIntent().getExtras();
        String fragmentType = FRAGMENT_TAG_MANAGE_PREFERENCES;
        if (extras != null) {
            fragmentType = extras.getString("FRAGMENT_TYPE");
            isImpliedMode = extras.getBoolean("ISIMPLIEDMODE");
        }

        if (fragmentType.equals(FRAGMENT_TAG_MANAGE_PREFERENCES)) {
            ManagePreferences_Fragment fragment = new ManagePreferences_Fragment();
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.replace(R.id.appnotice_fragment_container, fragment, fragmentType);
            ft.addToBackStack(fragmentType);
            ft.commit();
        } else if (fragmentType.equals(FRAGMENT_TAG_IMPLIED_CONSENT)) {

            ImpliedConsent_Fragment fragment = new ImpliedConsent_Fragment();
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.replace(R.id.appnotice_fragment_container, fragment, fragmentType);
            ft.addToBackStack(fragmentType);
            ft.commit();
        } else if (fragmentType.equals(FRAGMENT_TAG_EXPLICIT_CONSENT)) {

            ExplicitConsent_Fragment fragment = new ExplicitConsent_Fragment();
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.replace(R.id.appnotice_fragment_container, fragment, fragmentType);
            ft.addToBackStack(fragmentType);
            ft.commit();
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.getBackStackEntryCount() > 0) {
            String tag = fragmentManager.getBackStackEntryAt(fragmentManager.getBackStackEntryCount() - 1).getName();

            // Let each fragment handle its own back click
            Fragment fragment = fragmentManager.findFragmentByTag(tag);
            if (fragment != null) {
                switch (tag) {
                    case FRAGMENT_TAG_IMPLIED_CONSENT:
                        ((ImpliedConsent_Fragment) fragment).onBackPressed();
                        this.finish();
                        break;
                    case FRAGMENT_TAG_EXPLICIT_CONSENT:
                        ((ExplicitConsent_Fragment) fragment).onBackPressed();
                        break;
                    case FRAGMENT_TAG_MANAGE_PREFERENCES:
                        ((ManagePreferences_Fragment) fragment).onBackPressed();
                        if (isConsentActive) {
                            getSupportFragmentManager().popBackStack();
                        } else {
                            this.finish();
                        }
                        break;
                    case FRAGMENT_TAG_TRACKER_DETAIL:
                        ((TrackerDetail_Fragment) fragment).onBackPressed();
                        getSupportFragmentManager().popBackStack();
                        break;
                    case FRAGMENT_TAG_EXPLICIT_DECLINE:
                        getSupportFragmentManager().popBackStack();
                        break;
                    default:
                        super.onBackPressed();
                }
            }
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int uId, long arg3) {
        Bundle arguments = new Bundle();
        int id = view.getId();
        arguments.putInt(TrackerDetail_Fragment.ARG_ITEM_ID, id);

        TrackerDetail_Fragment fragment = new TrackerDetail_Fragment();
        fragment.setArguments(arguments);
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.appnotice_fragment_container, fragment, FRAGMENT_TAG_TRACKER_DETAIL);
        ft.addToBackStack(FRAGMENT_TAG_TRACKER_DETAIL);
        ft.commit();
    }

    public void onClick_OptInOut(View view) {
        Boolean isOn = ((CheckBox)view).isChecked();
        int uId = (int)view.getTag();
        if (appNoticeData == null) {
            appNoticeData = AppNoticeData.getInstance(this);
        }

        if (appNoticeData != null) {
            appNoticeData.setTrackerOnOffState(uId, isOn);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    public void handleTrackerStateChanges() {
        saveTrackerStates();
        sendOptInOutNotices();
    }

    public void sendOptInOutNotices() {
        // Opt-in/out ping-back parameters
        int pingBackCount = 0;      // Count the ping-backs
        AppNoticeData appNoticeData = AppNoticeData.getInstance(this);

        // Send opt-in/out ping-back for each changed non-essential tracker
        if (appNoticeData.optionalTrackerArrayList != null && AppNotice_Activity.optionalTrackerArrayListClone != null &&
                appNoticeData.optionalTrackerArrayList.size() == AppNotice_Activity.optionalTrackerArrayListClone.size()) {

            for (int i = 0; i < appNoticeData.optionalTrackerArrayList.size(); i++) {
                Tracker tracker = appNoticeData.optionalTrackerArrayList.get(i);
                Tracker trackerClone = AppNotice_Activity.optionalTrackerArrayListClone.get(i);

                // If the tracker is non-essential and is changed...
                if (!tracker.isEssential() && (tracker.isOn() != trackerClone.isOn())) {
                    Boolean optOut = tracker.isOn() == false;
                    Boolean uniqueVisit = false;//((allBtnSelected == false && noneBtnSelected == false) || pingBackCount == 0);
                    Boolean firstOptOut = pingBackCount == 0;
                    Boolean selectAll = false;//((allBtnSelected == true || noneBtnSelected == true) && pingBackCount == 0);

                    // TODO: Get correct values for uniqueVisit and selectAll
                    AppNoticeData.sendOptInOutNotice(tracker.getTrackerId(), optOut, uniqueVisit, firstOptOut, selectAll);    // Send opt-in/out ping-back
                    pingBackCount++;
                }
            }
        }
    }

    public void saveTrackerStates() {
        if (AppNotice_Activity.appNoticeData != null) {
            AppNotice_Activity.appNoticeData.saveTrackerStates();

            // If trackers have been changed and a consent dialog is not showing, send an updated tracker state hashmap to the calling app
            int trackerStateChangeCount = AppNotice_Activity.appNoticeData.getTrackerStateChangeCount(AppNotice_Activity.optionalTrackerArrayListClone);
            if (trackerStateChangeCount > 0 && !AppNotice_Activity.isConsentActive) {
                AppNotice_Activity.appNotice_callback.onTrackerStateChanged(AppNotice_Activity.appNoticeData.getTrackerHashMap(true));
            }
        }
    }

}
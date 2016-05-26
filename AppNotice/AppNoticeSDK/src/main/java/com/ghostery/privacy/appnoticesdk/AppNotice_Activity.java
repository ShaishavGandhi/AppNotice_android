package com.ghostery.privacy.appnoticesdk;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatCallback;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Switch;

import com.ghostery.privacy.appnoticesdk.fragments.LearnMore_Fragment;
import com.ghostery.privacy.appnoticesdk.fragments.ManagePreferences_Fragment;
import com.ghostery.privacy.appnoticesdk.fragments.TrackerDetail_Fragment;
import com.ghostery.privacy.appnoticesdk.model.AppNoticeData;
import com.ghostery.privacy.appnoticesdk.utils.Session;

/**
 * AppNotice_Activity
 */
public class AppNotice_Activity extends AppCompatActivity implements AppCompatCallback, AdapterView.OnItemClickListener {

    private AppNoticeData appNoticeData;
    private FragmentManager fragmentManager;

    // Fragment tags
//    public static final String FRAGMENT_TAG_IMPLIED_CONSENT = "IMPLIED_CONSENT";
//    public static final String FRAGMENT_TAG_EXPLICIT_CONSENT = "EXPLICIT_CONSENT";
    public static final String FRAGMENT_TAG_MANAGE_PREFERENCES = "MANAGE_PREFERENCES";
//    public static final String FRAGMENT_TAG_TRACKER_LIST = "TRACKER_LIST";
    public static final String FRAGMENT_TAG_TRACKER_DETAIL = "TRACKER_DETAIL";
    public static final String FRAGMENT_TAG_LEARN_MORE = "LEARN_MORE";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ghostery_activity_appnotice);
        fragmentManager = getSupportFragmentManager();

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.ghostery_header_background_color)));
        }

        ManagePreferences_Fragment fragment = new ManagePreferences_Fragment();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.appnotice_fragment_container, fragment, FRAGMENT_TAG_MANAGE_PREFERENCES);
        ft.addToBackStack(FRAGMENT_TAG_MANAGE_PREFERENCES);
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
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.getBackStackEntryCount() > 0) {
            String tag = fragmentManager.getBackStackEntryAt(fragmentManager.getBackStackEntryCount() - 1).getName();

            // Let each fragment handle its own back click
            Fragment fragment = fragmentManager.findFragmentByTag(tag);
            if (fragment != null) {
                switch (tag) {
                    case FRAGMENT_TAG_MANAGE_PREFERENCES:
                        ((ManagePreferences_Fragment) fragment).onBackPressed();
                        this.finish();
                        break;
                    case FRAGMENT_TAG_TRACKER_DETAIL:
                        ((TrackerDetail_Fragment) fragment).onBackPressed();
                        getSupportFragmentManager().popBackStack();
                        break;
                    case FRAGMENT_TAG_LEARN_MORE:
                        ((LearnMore_Fragment) fragment).onBackPressed();
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
        arguments.putInt(TrackerDetail_Fragment.ARG_ITEM_ID, uId);

        TrackerDetail_Fragment fragment = new TrackerDetail_Fragment();
        fragment.setArguments(arguments);
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.appnotice_fragment_container, fragment, FRAGMENT_TAG_TRACKER_DETAIL);
        ft.addToBackStack(FRAGMENT_TAG_TRACKER_DETAIL);
        ft.commit();
    }

    public void onClick_OptInOut(View view) {
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

        ManagePreferences_Fragment managePreferences_fragment = (ManagePreferences_Fragment) getSupportFragmentManager().findFragmentById(R.id.appnotice_fragment_container);
        if (managePreferences_fragment != null && managePreferences_fragment.getClass().equals(ManagePreferences_Fragment.class)) {
            managePreferences_fragment.setAllNoneControlState();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            getSupportFragmentManager().popBackStack();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}

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
import android.widget.Switch;

import com.ghostery.privacy.appnoticesdk.fragments.ExplicitConsent_Fragment;
import com.ghostery.privacy.appnoticesdk.fragments.ImpliedConsent_Fragment;
import com.ghostery.privacy.appnoticesdk.fragments.LearnMore_Fragment;
import com.ghostery.privacy.appnoticesdk.fragments.ManagePreferences_Fragment;
import com.ghostery.privacy.appnoticesdk.fragments.TrackerDetail_Fragment;
import com.ghostery.privacy.appnoticesdk.model.AppNoticeData;
import com.ghostery.privacy.appnoticesdk.utils.Session;

/**
 * AppNotice_Activity
 */
public class AppNotice_Activity extends AppCompatActivity implements AppCompatCallback, AdapterView.OnItemClickListener {

    private static AppNotice_Activity instance;
    private AppNoticeData appNoticeData;
    private FragmentManager fragmentManager;
    public static boolean isConsentActive = false;

    // Fragment tags
    public static final String FRAGMENT_TAG_IMPLIED_CONSENT = "IMPLIED_CONSENT";
    public static final String FRAGMENT_TAG_EXPLICIT_CONSENT = "EXPLICIT_CONSENT";
    public static final String FRAGMENT_TAG_MANAGE_PREFERENCES = "MANAGE_PREFERENCES";
//    public static final String FRAGMENT_TAG_TRACKER_LIST = "TRACKER_LIST";
    public static final String FRAGMENT_TAG_TRACKER_DETAIL = "TRACKER_DETAIL";
    public static final String FRAGMENT_TAG_LEARN_MORE = "LEARN_MORE";
    public static final String FRAGMENT_TAG_HOST_SETTINGS = "HOST_SETTINGS";


    public static AppNotice_Activity getInstance() {
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        setContentView(R.layout.ghostery_activity_appnotice);
        fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        String fragmentType = (String)Session.get(Session.APPNOTICE_CURRENT_FRAGMENT_TAG, FRAGMENT_TAG_MANAGE_PREFERENCES);

        if (savedInstanceState == null) {
            switch (fragmentType) {
                case FRAGMENT_TAG_IMPLIED_CONSENT:
                    ImpliedConsent_Fragment impliedConsent_fragment = new ImpliedConsent_Fragment();
                    ft.replace(R.id.appnotice_fragment_container, impliedConsent_fragment, fragmentType);
                    break;
                case FRAGMENT_TAG_EXPLICIT_CONSENT:
                    ExplicitConsent_Fragment explicitConsent_fragment = new ExplicitConsent_Fragment();
                    ft.replace(R.id.appnotice_fragment_container, explicitConsent_fragment, fragmentType);
                    break;
                case FRAGMENT_TAG_MANAGE_PREFERENCES:
                    ManagePreferences_Fragment managePreferences_fragment = new ManagePreferences_Fragment();
                    ft.replace(R.id.appnotice_fragment_container, managePreferences_fragment, fragmentType);
                    break;
                case FRAGMENT_TAG_TRACKER_DETAIL:
                    TrackerDetail_Fragment trackerDetail_fragment = new TrackerDetail_Fragment();
                    ft.replace(R.id.appnotice_fragment_container, trackerDetail_fragment, fragmentType);
                    break;
                case FRAGMENT_TAG_LEARN_MORE:
                    LearnMore_Fragment learnMore_fragment = new LearnMore_Fragment();
                    ft.replace(R.id.appnotice_fragment_container, learnMore_fragment, fragmentType);
                    break;
                case FRAGMENT_TAG_HOST_SETTINGS:
                    ManagePreferences_Fragment fragment = new ManagePreferences_Fragment();
                    ft.replace(R.id.appnotice_fragment_container, fragment, fragmentType);
                    break;
                default:
            }
            ft.addToBackStack(fragmentType);
            ft.commit();
        } else {
            switch (fragmentType) {
                case FRAGMENT_TAG_IMPLIED_CONSENT:
                    ImpliedConsent_Fragment impliedConsent_fragment = (ImpliedConsent_Fragment) getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG_IMPLIED_CONSENT);
                    ft.replace(R.id.appnotice_fragment_container, impliedConsent_fragment, fragmentType);
                    break;
                case FRAGMENT_TAG_EXPLICIT_CONSENT:
                    ExplicitConsent_Fragment explicitConsent_fragment = new ExplicitConsent_Fragment();
                    ft.replace(R.id.appnotice_fragment_container, explicitConsent_fragment, fragmentType);
                    break;
                case FRAGMENT_TAG_MANAGE_PREFERENCES:
                    ManagePreferences_Fragment managePreferences_fragment = new ManagePreferences_Fragment();
                    ft.replace(R.id.appnotice_fragment_container, managePreferences_fragment, fragmentType);
                    break;
                case FRAGMENT_TAG_TRACKER_DETAIL:
                    TrackerDetail_Fragment trackerDetail_fragment = new TrackerDetail_Fragment();
                    ft.replace(R.id.appnotice_fragment_container, trackerDetail_fragment, fragmentType);
                    break;
                case FRAGMENT_TAG_LEARN_MORE:
                    LearnMore_Fragment learnMore_fragment = new LearnMore_Fragment();
                    ft.replace(R.id.appnotice_fragment_container, learnMore_fragment, fragmentType);
                    break;
                case FRAGMENT_TAG_HOST_SETTINGS:
                    ManagePreferences_Fragment fragment = new ManagePreferences_Fragment();
                    ft.replace(R.id.appnotice_fragment_container, fragment, fragmentType);
                    break;
                default:
            }
            getFragmentManager().popBackStack();
        }


//        if (fragmentType.equals(FRAGMENT_TAG_MANAGE_PREFERENCES)) {
//            ManagePreferences_Fragment fragment = new ManagePreferences_Fragment();
//            FragmentTransaction ft = fragmentManager.beginTransaction();
//            ft.replace(R.id.appnotice_fragment_container, fragment, fragmentType);
//            ft.addToBackStack(fragmentType);
//            ft.commit();
//        } else if (fragmentType.equals(FRAGMENT_TAG_IMPLIED_CONSENT)) {
//
//            ImpliedConsent_Fragment fragment = new ImpliedConsent_Fragment();
//            FragmentTransaction ft = fragmentManager.beginTransaction();
//            ft.replace(R.id.appnotice_fragment_container, fragment, fragmentType);
//            ft.addToBackStack(fragmentType);
//            ft.commit();
//        } else if (fragmentType.equals(FRAGMENT_TAG_EXPLICIT_CONSENT)) {
//
//            ExplicitConsent_Fragment fragment = new ExplicitConsent_Fragment();
//            FragmentTransaction ft = fragmentManager.beginTransaction();
//            ft.replace(R.id.appnotice_fragment_container, fragment, fragmentType);
//            ft.addToBackStack(fragmentType);
//            ft.commit();
//        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
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
                        getSupportFragmentManager().popBackStack();
                        this.finish();
                        break;
                    case FRAGMENT_TAG_EXPLICIT_CONSENT:
                        ((ExplicitConsent_Fragment) fragment).onBackPressed();
                        getSupportFragmentManager().popBackStack();
                        this.finish();
                        break;
                    case FRAGMENT_TAG_MANAGE_PREFERENCES:
                        ((ManagePreferences_Fragment) fragment).onBackPressed();
                        getSupportFragmentManager().popBackStack();
                        if (!isConsentActive) {
                            this.finish();
                        }
                        break;
                    case FRAGMENT_TAG_TRACKER_DETAIL:
                        ((TrackerDetail_Fragment) fragment).onBackPressed();
                        getSupportFragmentManager().popBackStack();
                        break;
                    case FRAGMENT_TAG_LEARN_MORE:
                        ((LearnMore_Fragment) fragment).onBackPressed();
                        getSupportFragmentManager().popBackStack();
                        break;
                    case FRAGMENT_TAG_HOST_SETTINGS:
                        getSupportFragmentManager().popBackStack();
                        break;
                    default:
                        super.onBackPressed();
                }
            }
        } else {
            super.onBackPressed();
        }

        // Get the tag of the new current fragment and remember it as the current fragment
        int backStackCount = fragmentManager.getBackStackEntryCount();
        if (backStackCount > 0) {
            String tag = fragmentManager.getBackStackEntryAt(backStackCount - 1).getName();
            Session.set(Session.APPNOTICE_CURRENT_FRAGMENT_TAG, tag);
        } else {
            Session.set(Session.APPNOTICE_CURRENT_FRAGMENT_TAG, "");
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int uId, long arg3) {
        Bundle arguments = new Bundle();
        Session.set(Session.APPNOTICE_SELECTED_ITEM_ID, uId);

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
            onBackPressed();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

}

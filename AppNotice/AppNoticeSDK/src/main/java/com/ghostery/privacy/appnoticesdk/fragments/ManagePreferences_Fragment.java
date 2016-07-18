package com.ghostery.privacy.appnoticesdk.fragments;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.ghostery.privacy.appnoticesdk.AppNotice_Activity;
import com.ghostery.privacy.appnoticesdk.R;
import com.ghostery.privacy.appnoticesdk.adapter.ManagePreferences_ViewPager_Adapter;
import com.ghostery.privacy.appnoticesdk.model.AppNoticeData;
import com.ghostery.privacy.appnoticesdk.model.Tracker;
import com.ghostery.privacy.appnoticesdk.utils.AppData;

/**
 *
 */
public class ManagePreferences_Fragment extends Fragment {
    ManagePreferences_ViewPager_Adapter managePreferences_viewPager_adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.ghostery_fragment_manage_preferences, container, false);

        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.show();
        }

        TabLayout tab_layout = (TabLayout) view.findViewById(R.id.tab_layout);
        Resources resources = getActivity().getResources();
        tab_layout.addTab(tab_layout.newTab().setText(resources.getString(R.string.ghostery_preferences_optional_title)));
        tab_layout.addTab(tab_layout.newTab().setText(resources.getString(R.string.ghostery_preferences_essential_title)));
        tab_layout.addTab(tab_layout.newTab().setText(resources.getString(R.string.ghostery_preferences_webbased_title)));

        final ViewPager view_pager = (ViewPager) view.findViewById(R.id.view_pager);

        managePreferences_viewPager_adapter = new ManagePreferences_ViewPager_Adapter(getChildFragmentManager(), tab_layout.getTabCount());

        view_pager.setAdapter(managePreferences_viewPager_adapter);

        view_pager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tab_layout));

        tab_layout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                view_pager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        LinearLayout explicitButtonLayout = (LinearLayout)getView().findViewById(R.id.explicit_button_layout);

        if (AppNotice_Activity.isConsentActive) {
            if (AppNotice_Activity.isImpliedMode) {
                // If implied mode, hide the explicit button layout
                explicitButtonLayout.setVisibility(View.GONE);

                // If implied mode, show the snackbar
                CoordinatorLayout coordinatorlayout = (CoordinatorLayout)getView().findViewById(R.id.coordinatorLayout);
                Snackbar snackbar = Snackbar
                        .make(coordinatorlayout, R.string.ghostery_preferences_ready_message, Snackbar.LENGTH_INDEFINITE)
                        .setAction(R.string.ghostery_preferences_continue_button, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                handleTrackerStateChanges();

                                // Let the calling class know the selected option
                                AppNoticeData appNoticeData = AppNoticeData.getInstance(getActivity());

                                if (AppNotice_Activity.appNotice_callback != null) {
                                    AppNotice_Activity.appNotice_callback.onOptionSelected(true, appNoticeData.getTrackerHashMap(true));
                                }

                                // Close this fragment
                                AppNotice_Activity.isConsentActive = false;
                                getActivity().finish();
                            }
                        });

                snackbar.show();
            } else {
                // If explicit mode, show the explicit button layout
                explicitButtonLayout.setVisibility(View.VISIBLE);

                // Watch for button clicks.
                AppCompatButton preferences_button_accept = (AppCompatButton)getView().findViewById(R.id.preferences_button_accept);
                preferences_button_accept.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        // Send notice for this event
                        AppNoticeData.sendNotice(AppNoticeData.NoticeType.EXPLICIT_INFO_ACCEPT);

                        // Remember in a persistent way that the explicit notice has been accepted
                        AppData.setBoolean(AppData.APPDATA_EXPLICIT_ACCEPTED, true);

                        handleTrackerStateChanges();

                        // Let the calling class know the selected option
                        AppNoticeData appNoticeData = AppNoticeData.getInstance(getActivity());
                        if (AppNotice_Activity.appNotice_callback != null && !getActivity().isFinishing()) {
                            AppNotice_Activity.appNotice_callback.onOptionSelected(true, appNoticeData.getTrackerHashMap(true));
                        }

                        // Close this fragment
                        AppNotice_Activity.isConsentActive = false;
                        getActivity().finish();
                    }
                });

                AppCompatButton preferences_button_decline = (AppCompatButton)getView().findViewById(R.id.preferences_button_decline);
                preferences_button_decline.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        ExplicitDecline_Fragment fragment = new ExplicitDecline_Fragment();
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        FragmentTransaction ft = fragmentManager.beginTransaction();
                        ft.replace(R.id.appnotice_fragment_container, fragment, AppNotice_Activity.FRAGMENT_TAG_EXPLICIT_DECLINE);
                        ft.addToBackStack(AppNotice_Activity.FRAGMENT_TAG_EXPLICIT_DECLINE);
                        ft.commit();
                    }
                });
            }
        } else {
            // If not in a consent flow, hide the explicit button layout
            explicitButtonLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();

        getActivity().setTitle(R.string.ghostery_preferences_header);
    }

    public void onBackPressed() {
        handleTrackerStateChanges();
    }

    public void handleTrackerStateChanges() {
        saveTrackerStates();
        sendOptInOutNotices();
    }

    public void sendOptInOutNotices() {
        // Opt-in/out ping-back parameters
        int pingBackCount = 0;      // Count the ping-backs
        AppNoticeData appNoticeData = AppNoticeData.getInstance(getActivity());

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

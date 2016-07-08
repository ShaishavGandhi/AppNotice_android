package com.ghostery.privacy.appnoticesdk.fragments;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ghostery.privacy.appnoticesdk.AppNotice_Activity;
import com.ghostery.privacy.appnoticesdk.R;
import com.ghostery.privacy.appnoticesdk.adapter.ManagePreferences_ViewPager_Adapter;
import com.ghostery.privacy.appnoticesdk.callbacks.AppNotice_Callback;
import com.ghostery.privacy.appnoticesdk.model.AppNoticeData;
import com.ghostery.privacy.appnoticesdk.utils.Session;

/**
 *
 */
public class ManagePreferences_Fragment extends Fragment {
    CoordinatorLayout coordinatorlayout;
    ManagePreferences_ViewPager_Adapter managePreferences_viewPager_adapter;

    /**
     * Whether or not the fragmentActivity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

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

        coordinatorlayout = (CoordinatorLayout)getView().findViewById(R.id.coordinatorlayout);
        Snackbar snackbar = Snackbar
                .make(coordinatorlayout, R.string.ghostery_preferences_ready_message, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.ghostery_preferences_continue_button, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        handleTrackerStateChanges();

                        // Let the calling class know the selected option
                        AppNotice_Callback appNotice_callback = (AppNotice_Callback) Session.get(Session.APPNOTICE_CALLBACK);
                        AppNoticeData appNoticeData = AppNoticeData.getInstance(getActivity());

                        if (appNotice_callback != null) {
                            appNotice_callback.onOptionSelected(true, appNoticeData.getTrackerHashMap(true));
                        }

                        // Close this fragment
                        AppNotice_Activity.isConsentActive = false;
                        getActivity().finish();
                    }
                });

        snackbar.show();
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
        ManagePreferences_TrackerList_Fragment managePreferences_trackerList_fragment = (ManagePreferences_TrackerList_Fragment)managePreferences_viewPager_adapter.getItem(0);
        managePreferences_trackerList_fragment.saveTrackerStates();
        managePreferences_trackerList_fragment.sendOptInOutNotices();
    }

}

package com.ghostery.privacy.appnoticesdk.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.ghostery.privacy.appnoticesdk.fragments.ManagePreferences_TrackerList_Fragment;
import com.ghostery.privacy.appnoticesdk.fragments.ManagePreferences_WebBased_Fragment;

/**
 * Created by Steven.Overson on 7/1/2016.
 */
public class ManagePreferences_ViewPager_Adapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;
    ManagePreferences_TrackerList_Fragment tab1;
    ManagePreferences_TrackerList_Fragment tab2;
    ManagePreferences_WebBased_Fragment tab3;

    public ManagePreferences_ViewPager_Adapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                if (tab1 == null) {
                    tab1 = new ManagePreferences_TrackerList_Fragment();
                    Bundle args = new Bundle();
                    args.putBoolean("isEssential", false);
                    tab1.setArguments(args);
                }
                return tab1;
            case 1:
                if (tab2 == null) {
                    tab2 = new ManagePreferences_TrackerList_Fragment();
                    Bundle args = new Bundle();
                    args.putBoolean("isEssential", true);
                    tab2.setArguments(args);
                }
                return tab2;
            case 2:
                if (tab3 == null) {
                    tab3 = new ManagePreferences_WebBased_Fragment();
                }
                return tab3;

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}

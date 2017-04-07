package com.evidon.privacy.appnoticesdk.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.evidon.privacy.appnoticesdk.fragments.ManagePreferences_TrackerList_Fragment;
import com.evidon.privacy.appnoticesdk.fragments.ManagePreferences_WebBased_Fragment;

/**
 * Created by Steven.Overson on 7/1/2016.
 */
public class ManagePreferences_ViewPager_Adapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;
    private static boolean showOptionalTab = false;
    private static boolean showEssentailTab = false;
    private static boolean showWebTab = false;
    ManagePreferences_TrackerList_Fragment optionalFragment;
    ManagePreferences_TrackerList_Fragment essentialFragment;
    ManagePreferences_WebBased_Fragment webFragment;

    public ManagePreferences_ViewPager_Adapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                if (showOptionalTab) {
                    return getOptionalFragment();
                } else if (showEssentailTab) {
                    return getEssentialFragment();
                } else if (showWebTab) {
                    return getWebFragment();
                }
            case 1:
                if (showOptionalTab) {
                    if (showEssentailTab) {
                        return getEssentialFragment();
                    } else if (showWebTab) {
                        return getWebFragment();
                    }
                } else if (showEssentailTab) {
                    if (showWebTab) {
                        return getWebFragment();
                    }
                }
            case 2:
                if (showOptionalTab && showEssentailTab && showWebTab) {
                    return getWebFragment();
                }

            default:
                return null;
        }
    }

    private ManagePreferences_TrackerList_Fragment getOptionalFragment() {
        if (optionalFragment == null) {
            optionalFragment = new ManagePreferences_TrackerList_Fragment();
            Bundle args = new Bundle();
            args.putBoolean("isEssential", false);
            optionalFragment.setArguments(args);
        }
        return optionalFragment;
    }

    private ManagePreferences_TrackerList_Fragment getEssentialFragment() {
        if (essentialFragment == null) {
            essentialFragment = new ManagePreferences_TrackerList_Fragment();
            Bundle args = new Bundle();
            args.putBoolean("isEssential", true);
            essentialFragment.setArguments(args);
        }
        return essentialFragment;
    }

    private ManagePreferences_WebBased_Fragment getWebFragment() {
        if (webFragment == null) {
            webFragment = new ManagePreferences_WebBased_Fragment();
        }
        return webFragment;
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }

    public static void setActiveTabs(boolean showOptionalTab, boolean showEssentailTab, boolean showWebTab) {
        ManagePreferences_ViewPager_Adapter.showOptionalTab = showOptionalTab;
        ManagePreferences_ViewPager_Adapter.showEssentailTab = showEssentailTab;
        ManagePreferences_ViewPager_Adapter.showWebTab = showWebTab;
    }
}

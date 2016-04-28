package com.ghostery.privacy.appnoticesdk.app;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.ghostery.privacy.appnoticesdk.R;

public class AppNoticeActivity extends AppCompatActivity implements AppNoticeFragment.OnFragmentInteractionListener {
    android.support.v4.app.Fragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ghostery_activity_app_notice);

        FragmentManager fm = getSupportFragmentManager();
        fragment = fm.findFragmentByTag("AppNoticeFragment");
        if (fragment == null) {
            android.support.v4.app.FragmentTransaction ft = fm.beginTransaction();
            fragment = new AppNoticeFragment();
            ft.add(android.R.id.content, fragment, "AppNoticeFragment_tag");
            ft.commit();

        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}

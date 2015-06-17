package com.ghostery.privacy.use_sdk_module;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ghostery.privacy.inappconsentsdk.callbacks.InAppConsent_Callback;
import com.ghostery.privacy.inappconsentsdk.model.InAppConsent;

import java.util.HashMap;


public class MainActivity extends AppCompatActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    private InAppConsent_Callback inAppConsent_Callback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        // Start the In-App Consent SDK
//        InAppConsent inAppConsent = new InAppConsent();
////        inAppConsent.startConsentFlow(this);
//        inAppConsent.initExplicitConsent(this);
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                .commit();
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment implements OnClickListener, InAppConsent_Callback {

        private Button btn_consent_flow;
        private Button btn_manage_preferences;
        private Button btn_reset_sdk;
        private Button btn_close_app;

        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);

            // If there are saved IDs, use them
            String companyIdString = Util.getSharedPreference(getActivity(), Util.SP_COMPANY_ID, "");
            String pubNoticeIdString = Util.getSharedPreference(getActivity(), Util.SP_PUB_NOTICE_ID, "");

            EditText companyIdEditText = (EditText)rootView.findViewById(R.id.editText_companyId);
            EditText pubNoticeIdEditText = (EditText)rootView.findViewById(R.id.editText_pubNoticeId);

            companyIdEditText.setText(companyIdString);
            pubNoticeIdEditText.setText(pubNoticeIdString);

            btn_consent_flow = (Button) rootView.findViewById(R.id.btn_consent_flow) ;
            btn_manage_preferences = (Button) rootView.findViewById(R.id.btn_manage_preferences) ;
            btn_reset_sdk = (Button) rootView.findViewById(R.id.btn_reset_sdk) ;
            btn_close_app = (Button) rootView.findViewById(R.id.btn_close_app) ;

            btn_consent_flow.setOnClickListener(this);
            btn_manage_preferences.setOnClickListener(this);
            btn_reset_sdk.setOnClickListener(this);
            btn_close_app.setOnClickListener(this);

            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MainActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }

        @Override
        public void onClick(View view) {

            String companyIdString = "";
            String pubNoticeIdString = "";
            int companyId = 0;
            int pubNoticeId = 0;
            Boolean useRemoteValues = true;

            TextView tv = (TextView)getActivity().findViewById(R.id.editText_companyId);
            if (tv != null)
                companyIdString = tv.getText().toString();

            tv = (TextView)getActivity().findViewById(R.id.editText_pubNoticeId);
            if (tv != null)
                pubNoticeIdString = tv.getText().toString();

            // Save these values as defaults for next session
            Util.setSharedPreference(getActivity(), Util.SP_COMPANY_ID, companyIdString);
            Util.setSharedPreference(getActivity(), Util.SP_PUB_NOTICE_ID, pubNoticeIdString);

            if (view == btn_reset_sdk) {
                InAppConsent inAppConsent = new InAppConsent();
                inAppConsent.resetSDK();

                Toast.makeText(getActivity(), "SDK was reset.", Toast.LENGTH_SHORT).show();

            } else if (view == btn_close_app) {
                // Close the app
                getActivity().finish();
                System.exit(0);

            }

            else if (companyIdString.length() == 0 || pubNoticeIdString.length() == 0) {
                Toast.makeText(getActivity(), "You must supply a Company ID and Pub-notice ID.", Toast.LENGTH_LONG).show();
            } else {
                companyId = Integer.valueOf(companyIdString);
                pubNoticeId = Integer.valueOf(pubNoticeIdString);

                CheckBox cb = (CheckBox)getActivity().findViewById(R.id.checkBox_useRemoteValues);
                if (cb != null)
                    useRemoteValues = cb.isChecked();

                InAppConsent inAppConsent = new InAppConsent();

                if (view == btn_manage_preferences) {
                    inAppConsent.showManagePreferences(this.getActivity(), companyId, pubNoticeId, useRemoteValues, this);

                } else if (view == btn_consent_flow) {
                    inAppConsent.startConsentFlow(this.getActivity(), companyId, pubNoticeId, useRemoteValues, this);

                }
            }
        }

        @Override
        public void onOptionSelected(boolean isAccepted, HashMap<Integer, Boolean> trackerHashMap) {
            // Handle your response
            if (isAccepted) {
                Toast.makeText(getActivity(), "Tracking accepted", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getActivity(), "Tracking declined", Toast.LENGTH_LONG).show();

                // Close the app
//                getApplication().finish();
                System.exit(0);
            }
        }

        @Override
        public void onNoticeSkipped() {
            // Handle your response
            Toast.makeText(getActivity(), "Dialog skipped: Tracking accepted", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onTrackerStateChange(HashMap<Integer, Boolean> trackerHashMap) {

        }

    }

}

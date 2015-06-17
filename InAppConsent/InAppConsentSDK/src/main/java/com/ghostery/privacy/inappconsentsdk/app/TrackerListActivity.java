package com.ghostery.privacy.inappconsentsdk.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatCallback;
import android.support.v7.view.ActionMode;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.ghostery.privacy.inappconsentsdk.R;
import com.ghostery.privacy.inappconsentsdk.model.InAppConsentData;

/**
 * An activity representing a list of Trackers. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link TrackerDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 * <p/>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link TrackerListFragment} and the item details
 * (if present) is a {@link TrackerDetailFragment}.
 * <p/>
 * This activity also implements the required
 * {@link TrackerListFragment.Callbacks} interface
 * to listen for item selections.
 */
public class TrackerListActivity extends AppCompatActivity implements TrackerListFragment.Callbacks, AppCompatCallback {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracker_list);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            actionBar.setCustomView(R.layout.ghostery_action_bar_layout);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }

//        Toolbar toolbar = (Toolbar) findViewById(R.id.ghostery_tracker_list_toolbar);
//        toolbar.setTitle(R.string.title_tracker_list);
////        toolbar.setLogo(R.drawable.ghostery_header_logo);
//
//        setSupportActionBar(toolbar);
//
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setTitle(R.string.title_tracker_detail);


//        final ActionBar actionBar = getSupportActionBar();
//
////        actionBar.setCustomView(R.layout.ghostery_tracker_list_header);
//        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
//        actionBar.setDisplayHomeAsUpEnabled(true);
//        actionBar.setDisplayShowHomeEnabled(true);

        TextView manage_preferences_description = (TextView)findViewById(R.id.manage_preferences_description);
        if (manage_preferences_description != null) {
            InAppConsentData inAppConsentData = InAppConsentData.getInstance(this);
            String manage_preferences_description_text = inAppConsentData.getManage_preferences_description();
            if (manage_preferences_description_text != null && manage_preferences_description_text.length() > 0)
                manage_preferences_description.setText(manage_preferences_description_text);
        }

        if (findViewById(R.id.tracker_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-large and
            // res/values-sw600dp). If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;

            // In two-pane mode, list items should be given the
            // 'activated' state when touched.
            ((TrackerListFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.tracker_list))
                    .setActivateOnItemClick(true);
        }

        // TODO: If exposing deep links into your app, handle intents here.
    }

    /**
     * Callback method from {@link TrackerListFragment.Callbacks}
     * indicating that the item with the given ID was selected.
     */
    @Override
    public void onItemSelected(String id) {
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putString(TrackerDetailFragment.ARG_ITEM_ID, id);
            TrackerDetailFragment fragment = new TrackerDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.tracker_detail_container, fragment)
                    .commit();

        } else {
            // In single-pane mode, simply start the detail activity
            // for the selected item ID.
            Intent detailIntent = new Intent(this, TrackerDetailActivity.class);
            detailIntent.putExtra(TrackerDetailFragment.ARG_ITEM_ID, id);
            startActivity(detailIntent);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Bundle arguments = new Bundle();

        int i = item.getItemId();
        if (item.getItemId() == android.R.id.home) {
            // do something here, such as start an Intent to the parent activity.
            Toast.makeText(this, "Actionbar Home", Toast.LENGTH_SHORT).show();
            this.finish();  // Or onBackPressed();
        }

//        DetailFragment fragment = new DetailFragment();
//        fragment.setArguments(arguments);
//        getSupportFragmentManager().beginTransaction().replace(R.id.item_detail_container, fragment).commit();

        return true;
    }

    @Override
    public void onSupportActionModeStarted(ActionMode mode) {
        //let's leave this empty, for now
    }

    @Override
    public void onSupportActionModeFinished(ActionMode mode) {
        // let's leave this empty, for now
    }
}

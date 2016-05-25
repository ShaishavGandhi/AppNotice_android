//package com.ghostery.privacy.appnoticesdk.app;
//
//import android.graphics.drawable.ColorDrawable;
//import android.os.Bundle;
//import android.support.v4.app.FragmentTransaction;
//import android.support.v7.app.ActionBar;
//import android.support.v7.app.AppCompatActivity;
//
//import com.ghostery.privacy.appnoticesdk.R;
//
///**
// * An fragmentActivity representing a single Tracker detail screen. This
// * fragmentActivity is only used on handset devices. On tablet-size devices,
// * item details are presented side-by-side with a list of items
// * in a {@link AppNotice_Activity}.
// * <p/>
// * This fragmentActivity is mostly just a 'shell' fragmentActivity containing nothing
// * more than a {@link TrackerDetailFragment}.
// */
//public class TrackerDetailActivity extends AppCompatActivity {
//
//    private int trackerId;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.ghostery_activity_tracker_detail);
//
//        // Show the Up button in the action bar.
////        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        ActionBar actionBar = getSupportActionBar();
//        if (actionBar != null) {
//            actionBar.setDisplayHomeAsUpEnabled(true);
//            actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.ghostery_header_background_color)));
//        }
//
//        // savedInstanceState is non-null when there is fragment state
//        // saved from previous configurations of this fragmentActivity
//        // (e.g. when rotating the screen from portrait to landscape).
//        // In this case, the fragment will automatically be re-added
//        // to its container so we don't need to manually add it.
//        // For more information, see the Fragments API guide at:
//        //
//        // http://developer.android.com/guide/components/fragments.html
//        //
//        if (savedInstanceState == null) {
//            // Create the detail fragment and add it to the fragmentActivity
//            // using a fragment transaction.
//            Bundle arguments = new Bundle();
//            trackerId = getIntent().getIntExtra(TrackerDetailFragment.ARG_ITEM_ID, 0);
//            arguments.putInt(TrackerDetailFragment.ARG_ITEM_ID, trackerId);
//
//            final TrackerDetailFragment fragment = new TrackerDetailFragment();
//            fragment.setArguments(arguments);
//            final FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//            transaction.add(R.id.tracker_detail_container, fragment);
////            transaction.replace(R.id.tracker_detail_container, fragment);
//            transaction.addToBackStack(null);
//            transaction.commit();
//
//        }
//    }
//
//    public void setActionBarTitle(int titleId){
//        ActionBar actionBar = getSupportActionBar();
//        if (actionBar != null) {
//            actionBar.setTitle(titleId);
//        }
//    }
//}

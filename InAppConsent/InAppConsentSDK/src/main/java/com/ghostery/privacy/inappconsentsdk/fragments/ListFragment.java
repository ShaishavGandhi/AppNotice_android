//package com.ghostery.privacy.inappconsentsdk.fragments;
//
//import android.app.Activity;
//import android.app.ProgressDialog;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.View;
//import android.widget.Button;
//import android.widget.CompoundButton;
//import android.widget.ListView;
//import android.widget.TextView;
//
//import com.ghostery.privacy.inappconsentsdk.R;
//import com.ghostery.privacy.inappconsentsdk.adapter.TrackerArrayAdapter;
//import com.ghostery.privacy.inappconsentsdk.app.CustomDialogClass;
//import com.ghostery.privacy.inappconsentsdk.app.OptOutManager;
//import com.ghostery.privacy.inappconsentsdk.model.Company;
//import com.ghostery.privacy.inappconsentsdk.model.Optout;
//import com.ghostery.privacy.inappconsentsdk.utils.Network;
//import com.ghostery.privacy.inappconsentsdk.utils.Notification;
//
//import java.text.MessageFormat;
//import java.util.Collections;
//import java.util.Map;
//
//
//public class ListFragment extends android.support.v4.app.ListFragment {
//
//    public static TrackerArrayAdapter adapter = null;
//    private static final String TAG = "AppChoices";
//    private ProgressDialog progress;
//
//
//    /**
//     * The fragment's current callback object, which is notified of list item clicks.
//     */
//    private Callbacks mCallbacks = sCompanyCallbacks;
//
//    /**
//     * A callback interface that all activities containing this fragment must implement. This mechanism allows
//     * activities to be notified of item selections.
//     */
//    public interface Callbacks {
//        /**
//         * Callback for when an item has been selected.
//         */
//        public void onItemSelected(String id);
//
//
//
//    }
//
//    /**
//     * A dummy implementation of the Callbacks interface that does nothing. Used only when this fragment is not attached
//     * to an activity.
//     */
//    private static Callbacks sCompanyCallbacks = new Callbacks() {
//        @Override
//        public void onItemSelected(String id) {
//        }
//    };
//
//    /**
//     * Mandatory empty constructor for the fragment manager to instantiate the fragment (e.g. upon screen orientation
//     * changes).
//     */
//    public ListFragment() {
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        Collections.sort(Company.COMPANY);
//        adapter = new TrackerArrayAdapter(getActivity(), Company.COMPANY);
//        progress = new ProgressDialog(getActivity(), R.style.ghostery_loading_dialog_theme);
//        setListAdapter(adapter);
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        /*
//        boolean network = Network.isNetworkAvailable(getActivity());
//        OptOutManager oom = new OptOutManager(getActivity());
//        View listview = getActivity().findViewById(R.id.opt_out_button);
//
//        if(network)
//        {
//            for (Map.Entry<String, Company.CompanyData> entry : Company.COMPANY_MAP.entrySet()) {
//                Company.CompanyData company = Company.COMPANY_MAP.get(entry.getKey());
//                Optout.OptoutData optout = Optout.OPTOUT_MAP.get(entry.getKey());
//
//                if(optout.storeOptoutAll)
//                {
//                    Log.v(TAG, "OptOutAll request detected!");
//                    optOutAll();
//                    break;
//                }
//
//                Log.v(TAG, "company="+company.name+" optout="+optout.storeOptout);
//                if(optout.storeOptout)
//                {
//                    CompoundButton toggle = (CompoundButton) listview.findViewWithTag(company.id);
//                    oom.processOptOutInBackground(company, "", "", (CompoundButton) toggle, true);
//                }
//
//
//            }
//
//        }*/
//    }
//
//    @Override
//    public void onAttach(Activity activity) {
//        super.onAttach(activity);
//
//        // Activities containing this fragment must implement its callbacks.
//        if (!(activity instanceof Callbacks)) {
//            throw new IllegalStateException("Activity must implement fragment's callbacks.");
//        }
//
//        mCallbacks = (Callbacks) activity;
//    }
//
//    @Override
//    public void onDetach() {
//        super.onDetach();
//        // Reset the active callbacks interface to the dummy implementation.
//        mCallbacks = sCompanyCallbacks;
//    }
//
//    @Override
//    public void onListItemClick(ListView listView, View view, int position, long id) {
//        super.onListItemClick(listView, view, position, id);
//
//        // Notify the active callbacks interface (the activity, if the
//        // fragment is attached to one) that an item has been selected.
//        mCallbacks.onItemSelected(Company.COMPANY.get(position).id.toString());
//        adapter.notifyDataSetChanged();
//    }
//
//
//    public void optOutAll() {
//
//        OptOutManager.mOptoutGTS = 0;
//        OptOutManager.mOptoutSucess = 0;
//        OptOutManager.mOptoutFail = 0;
//
//        Button disable_all = (Button)getActivity().findViewById(R.id.choose_all_companies);
//        disable_all.setEnabled(false);
//
//        progress.setCancelable(false);
//        progress.setProgressStyle(android.R.style.Widget_ProgressBar_Large);
//        progress.show();
//
//        disable_all.setBackgroundResource(R.drawable.ghostery_rounded_corner_gray);
//        View listview = getActivity().findViewById(R.id.opt_out_button);
//
//        boolean network = Network.isNetworkAvailable(getActivity());
//
//        for (Map.Entry<String, Optout.OptoutData> entry : Optout.OPTOUT_MAP.entrySet()) {
//            Company.CompanyData company = Company.COMPANY_MAP.get(entry.getKey());
//
//            Optout.OptoutData optout = Optout.OPTOUT_MAP.get(entry.getKey());
//
//            OptOutManager oom = new OptOutManager(getActivity());
//
//            // to discard companies that require to go to site and the ones that were already opted out
//            if(!company.goToSite && optout.optoutStatus == false && network)
//            {
//                Log.v(TAG, "Opting out "+ company.name);
//                oom.lf = this;
//                oom.mOptoutTotal = Company.COMPANY_MAP.size();
//                CompoundButton toggle = (CompoundButton) listview.findViewWithTag(company.id);
//                oom.processOptOutInBackground(company, "", "", (CompoundButton) toggle, true);
//            }
//            else if(company.goToSite)
//            {
//                OptOutManager.mOptoutGTS++;
//            }else if(optout.optoutStatus == true)
//            {
//                OptOutManager.mOptoutSucess++;
//            }else if(!network)
//            {
//                Log.v(TAG, "Store optout for " + company.name);
//                optout.storeOptout = true;
//                Optout.OPTOUT_MAP.get(entry.getKey()).storeOptout = true;
//                optout.storeOptoutAll = true;
//            }
//        }
//
//        if(!network)
//        {
//            progress.dismiss();
//            Notification notification = new Notification(getActivity());
//            notification.noNetworkConnection();
//
//        }
//
//    }
//
//    public void optOutResults()
//    {
//        Log.v(TAG, "=====TOTAL: "+ Company.COMPANY_MAP.size());
//        Log.v(TAG, "=====Sucess: "+ OptOutManager.mOptoutSucess);
//        Log.v(TAG, "=====Fail: "+ OptOutManager.mOptoutFail);
//        Log.v(TAG, "=====Go to site: "+ OptOutManager.mOptoutGTS);
//
//        if((OptOutManager.mOptoutSucess + OptOutManager.mOptoutFail + OptOutManager.mOptoutGTS) == Company.COMPANY_MAP.size())
//        {
//            progress.dismiss();
//            CustomDialogClass cd = new CustomDialogClass(getActivity(), "dialog_result");
//            cd.show();
//
//            TextView result_statistics = (TextView)cd.findViewById(R.id.result_statistics);
//            String result_reset_statistics = getResources().getString(R.string.ghostery_result_reset_statistics);
//            Object[] formatParams = {Integer.toString(OptOutManager.mOptoutSucess), Integer.toString(Company.COMPANY_MAP.size())};
//            String result_reset_statistics_formatted;
//            result_reset_statistics_formatted = MessageFormat.format(result_reset_statistics, formatParams);
//            result_statistics.setText(result_reset_statistics_formatted);
//
//            if(OptOutManager.mOptoutFail != 0)
//            {
//                Button disable_all = (Button)getActivity().findViewById(R.id.choose_all_companies);
//                disable_all.setEnabled(true);
//                disable_all.setBackgroundResource(R.drawable.ghostery_rounded_corner_blue);
//            }
//
//        }
//    }
//
//}
//

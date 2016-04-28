package com.ghostery.privacy.appnoticesdk.app;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ghostery.privacy.appnoticesdk.R;
import com.ghostery.privacy.appnoticesdk.model.AppNoticeData;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AppNoticeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AppNoticeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AppNoticeFragment extends Fragment {
    private static final String TAG = "AppNoticeFragment";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public AppNoticeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AppNoticeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AppNoticeFragment newInstance(String param1, String param2) {
        AppNoticeFragment fragment = new AppNoticeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.ghostery_fragment_app_notice, container, false);

        AppCompatButton decline_button = (AppCompatButton)view.findViewById(R.id.decline_button);
        decline_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // User cancelled the dialog...negating consent
                Context context = getContext();
                Activity activity = getActivity();
                String hostAppMainActivity;

                try {
                    ApplicationInfo ai = activity.getPackageManager().getApplicationInfo(activity.getPackageName(), PackageManager.GET_META_DATA);
                    Bundle bundle = ai.metaData;
                    hostAppMainActivity = bundle.getString("ghostery_hostApp_mainActivity");
                } catch (NameNotFoundException e) {
                    Log.e(TAG, "The ghostery_hostApp_mainActivity parameter is missing", e);
                    throw new ActivityNotFoundException("The ghostery_hostApp_mainActivity parameter is not configured correctly in your manifest.");
                } catch (NullPointerException e) {
                    Log.e(TAG, "The ghostery_hostApp_mainActivity parameter is missing", e);
                    throw new ActivityNotFoundException("The ghostery_hostApp_mainActivity parameter is missing in your manifest.");
                }

                if (hostAppMainActivity != null && !hostAppMainActivity.isEmpty()) {
                    try {
                        Intent intent = new Intent();
                        String packageName = context.getPackageName();
                        intent.setClassName(context,hostAppMainActivity);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        context.startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        Log.e(TAG, "", e);
                        throw new ActivityNotFoundException("The ghostery_hostApp_mainActivity value is not valid. Make sure it contains the full package name (e.g., com.yourpackage.YourClass).");
                    }
                } else {
                    Log.d(TAG, "The ghostery_hostApp_mainActivity parameter is missing");
                    throw new ActivityNotFoundException("The ghostery_hostApp_mainActivity parameter is missing or not configured correctly in your manifest.");

                }

//                // Send notice for this event
//                AppNoticeData.sendNotice(AppNoticeData.NoticeType.EXPLICIT_INFO_DECLINE);
//
//                // Let the calling class know the selected option
//                if (appNotice_callback != null && !getActivity().isFinishing())
//                    appNotice_callback.onOptionSelected(false, null);    // Don't pass back a tracker hashmap if consent not given
//
//                // Close this dialog
//                dismiss();
            }
        });

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}

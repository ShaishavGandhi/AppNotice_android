package com.ghostery.privacy.fireball_module;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

public class Settings_Fragment extends Fragment {

    public Settings_Fragment() {
        super();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        LinearLayout pref_in_app_privacy_layout = (LinearLayout)view.findViewById(R.id.pref_in_app_privacy_layout);
        pref_in_app_privacy_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(App.getContext(), "Manage Privacy Preferences from hybrid.", Toast.LENGTH_LONG).show();
                MainActivity.getAppNotice().showManagePreferences();
            }
        });

//        LinearLayout pref_web_privacy_layout = (LinearLayout)view.findViewById(R.id.pref_web_privacy_layout);
//        pref_web_privacy_layout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent myIntent = new Intent(getActivity(), MainActivity.class);
//                getActivity().startActivity(myIntent);
//            }
//        });

        return view;
    }
}

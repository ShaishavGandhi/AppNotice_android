package com.ghostery.privacy.fireball_module;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

public class HybridPrivacySettings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!MainActivity.isInitialized) {  // If this activity started before MainActivity...
            Intent i = new Intent(this, MainActivity.class);
            finish();  //Kill this activity
            startActivity(i);  // Start MainActivity
            return;
        }

        setContentView(R.layout.activity_hybrid_privacy_settings);

        LinearLayout pref_in_app_privacy_layout = (LinearLayout)findViewById(R.id.pref_in_app_privacy_layout);
        pref_in_app_privacy_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(App.getContext(), "Manage Privacy Preferences from hybrid.", Toast.LENGTH_LONG).show();
                MainActivity.getAppNotice().showManagePreferences();
            }
        });
    }
}

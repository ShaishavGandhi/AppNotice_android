package com.ghostery.privacy.inappconsentsdk.app;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.ghostery.privacy.inappconsentsdk.R;

public class CustomDialogClass extends Dialog implements
        android.view.View.OnClickListener {

    public Activity c;
    public Dialog d;
    public Button no;
    public String layout;

    public CustomDialogClass(Activity a, String l) {
        super(a);
        // TODO Auto-generated constructor stub
        this.c = a;
        this.layout = l;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        if(this.layout == "dialog_understand_these_choices")
//        {
//            setContentView(R.layout.ghostery_dialog_understand_these_choices);
//        }
//        else
//        {
            setContentView(R.layout.ghostery_dialog_results);
//        }

        no = (Button) findViewById(R.id.btn_no);
        no.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_no) {
            dismiss();

        } else {
        }
        dismiss();
    }
}
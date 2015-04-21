package com.ghostery.privacy.inappconsentsdk.utils;



import android.content.Context;

import com.ghostery.privacy.inappconsentsdk.R;

import java.io.InputStream;


/*
 * Copies packaged files in /raw to the device storage
 */
public class CopyToDevice {

	public CopyToDevice(Context context) {

		String xml = null;
		String legal = null;

		xml = convertStreamToString(context.getResources().openRawResource(R.raw.ghostery_mobile_opt_out_providers));
		FileWriter.writeFile(context, context.getString(R.string.ghostery_file_mobile_opt_out_providers), xml);

		legal = convertStreamToString(context.getResources().openRawResource(R.raw.ghostery_mobile_app_terms));
		FileWriter.writeFile(context, context.getString(R.string.ghostery_file_terms_of_use), legal);

		legal = convertStreamToString(context.getResources().openRawResource(R.raw.ghostery_mobile_app_privacy_statement));
		FileWriter.writeFile(context, context.getString(R.string.ghostery_file_privacy_statement), legal);

	}

	public static String convertStreamToString(InputStream is) {
		String text = null;
		try {
			int size = is.available();
			byte[] buffer = new byte[size];
			is.read(buffer);
			is.close();
			text = new String(buffer);
		} catch (Exception e) {
		}
		return text;
	}
}

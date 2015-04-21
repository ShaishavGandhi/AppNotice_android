package com.ghostery.privacy.inappconsentsdk.Identifiers;

/**
 * Created by jdonohoo on 6/6/14.
 */

import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

/**
        * This import java.security.NoSuchAlgorithmException;

class knows how to fetch and encode values from telephonyManager.getDeviceId
        * Gets the Phone ID hashed with MD5 and formatted as a 32 byte hexadecimal number. Gets the Phone ID hashed with SHA1
        * and formatted as a 40 byte hexadecimal number.
        * Usage of this class requires adding the following to the AndroidManifest.xml
        * <uses-permission android:name="android.permission.READ_PHONE_STATE"/>
        */
public class PhoneId implements IdentifierSource
{

    private static final String TAG = "AppChoices";

    @Override
    public List<TypedIdentifier> get(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String phoneId = telephonyManager.getDeviceId();
        List<TypedIdentifier> ids = new ArrayList<TypedIdentifier>();
        if (phoneId != null) {
            try {
                ids.add(new TypedIdentifier(TypedIdentifier.TYPE_PHONE_ID_MD5, DigestUtil.md5Hash(phoneId)));
            } catch (NoSuchAlgorithmException nsae) {
                Log.v(TAG, "Error hashing PHONE_ID - MD5 not supported");
            }
            try {
                ids.add(new TypedIdentifier(TypedIdentifier.TYPE_PHONE_ID_SHA1, DigestUtil.sha1Hash(phoneId)));
            } catch (NoSuchAlgorithmException nsae) {
                Log.v(TAG, "Error hashing PHONE_ID - SHA1 not supported");
            }
        } else {
            try {
                ids.add(new TypedIdentifier(TypedIdentifier.TYPE_PHONE_ID_MD5, DigestUtil.md5Hash("")));
            } catch (NoSuchAlgorithmException nsae) {
                Log.v(TAG, "Error hashing PHONE_ID - MD5 not supported");
            }
            try {
                ids.add(new TypedIdentifier(TypedIdentifier.TYPE_PHONE_ID_SHA1, DigestUtil.sha1Hash("")));
            } catch (NoSuchAlgorithmException nsae) {
                Log.v(TAG, "Error hashing PHONE_ID - SHA1 not supported");
            }
            //Log.v(TAG, "Error retrieving PHONE_ID.");
        }
        return (ids);
    }
}

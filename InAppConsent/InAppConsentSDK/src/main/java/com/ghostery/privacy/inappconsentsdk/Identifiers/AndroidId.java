package com.ghostery.privacy.inappconsentsdk.Identifiers;

/**
 * Created by jdonohoo on 6/6/14.
 */

import android.content.Context;
import android.provider.Settings;
import android.util.Log;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class knows how to fetch and encode values from Settings.Secure.ANDROID_ID
 *
 * Gets the Android system ID hashed with MD5 and formatted as a 32 byte hexadecimal number. Gets the Android system ID
 * hashed with SHA1 and formatted as a 40 byte hexadecimal number.
 */
public class AndroidId implements IdentifierSource
{

    private static final String TAG = "AppChoices";

    @Override
    public List<TypedIdentifier> get(Context context) {
        String androidId = Settings.System.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        List<TypedIdentifier> ids = new ArrayList<TypedIdentifier>();
        if (androidId != null) {
            ids.add(new TypedIdentifier(TypedIdentifier.TYPE_ANDROID_ID, androidId));
            try {
                ids.add(new TypedIdentifier(TypedIdentifier.TYPE_ANDROID_ID_MD5, DigestUtil.md5Hash(androidId)));
            } catch (NoSuchAlgorithmException nsae) {
                Log.v(TAG, "Error hashing ANDROID_ID - MD5 not supported");
            }
            try {
                ids.add(new TypedIdentifier(TypedIdentifier.TYPE_ANDROID_ID_SHA1, DigestUtil.sha1Hash(androidId)));
            } catch (NoSuchAlgorithmException nsae) {
                Log.v(TAG, "Error hashing ANDROID_ID - SHA1 not supported");
            }
        } else {
            Log.v(TAG, "Error retrieving ANDROID_ID.");
        }
        return (ids);
    }
}

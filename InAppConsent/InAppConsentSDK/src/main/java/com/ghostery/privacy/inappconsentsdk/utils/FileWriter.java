package com.ghostery.privacy.inappconsentsdk.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import android.content.Context;
import android.graphics.Bitmap;

/*
 * Writes data to file in internal storage
 * @param context: application context
 * @param file: file name
 * @param data: data to be saved
 */
public class FileWriter {

    public static void writeFile(Context context, String file, String data) {
        try {
            FileOutputStream fos = context.openFileOutput(file, Context.MODE_PRIVATE);
            Writer out = new BufferedWriter(new OutputStreamWriter(fos, "UTF-8"));
            out.write(data);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void writeBitmap(Bitmap bitmap, File file) {
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 80, fos);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void writeObject(Context context, Object object, String file) {
        ObjectOutputStream oos = null;
        try {
            FileOutputStream fos = context.openFileOutput(file, Context.MODE_PRIVATE);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(object);
            oos.flush();
            oos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


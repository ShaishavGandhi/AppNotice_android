package com.ghostery.privacy.inappconsentsdk.utils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.StringReader;

import android.content.Context;

/*
 * Reads data from file in internal storage
 * @param context: application context
 * @param file: file to be read
 */
public class FileReader
{

    public static StringReader readReader(Context context, String file)
    {
        StringReader reader = null;
        try {
            FileInputStream fis = context.openFileInput(file);
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));
            StringBuffer sb = new StringBuffer();
            String str = "";
            while ((str = br.readLine()) != null) {
                sb.append(str);
            }
            br.close();
            reader = new StringReader(sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return reader;
    }

    public static String readString(Context context, String file)
    {
        String reader = null;
        try {
            FileInputStream fis = context.openFileInput(file);
            BufferedReader br = new BufferedReader(new InputStreamReader(fis, "UTF-8"));
            StringBuffer sb = new StringBuffer();
            String str = "";
            while ((str = br.readLine()) != null) {
                sb.append(str);
            }
            br.close();
            reader = sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return reader;
    }

    public static Object readObject(Context context, String file)
    {
        ObjectInputStream ois = null;
        Object object = null;

        try {
            FileInputStream fis = context.openFileInput(file);
            ois = new ObjectInputStream(fis);
            object = ois.readObject();
            ois.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return object;
    }
}


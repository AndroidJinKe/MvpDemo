package com.yipin.basepj.utils;

import android.content.Context;
import android.text.TextUtils;
import android.util.Base64;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;


/**
 * app信息本地缓存
 *
 * @author jkzhang
 *
 */
public class AppPrefs extends BasePrefs {

    private static final String PREFS_NAME = "AppPrefs";

    private AppPrefs(Context context) {
        super(context, PREFS_NAME);
    }

    public static AppPrefs get(Context context) {
        return new AppPrefs(context);
    }



    @Override
    public Object getObject(String arg0) {
        return super.getObject(arg0);
    }

    @Override
    public void putObject(String arg0, Object arg1) {
        super.putObject(arg0, arg1);
    }

    @Override
    public String getString(String key, String defValue) {
        return super.getString(key, defValue);
    }

    @Override
    public void putString(String key, String v) {
        super.putString(key, v);
    }


    @Override
    public int getInt(String key, int defValue) {
        return super.getInt(key, defValue);
    }

    @Override
    public void putInt(String key, int v) {
        super.putInt(key, v);
    }


    @Override
    public long getLong(String key, long defValue) {
        return super.getLong(key, defValue);
    }

    @Override
    public void putLong(String key, long v) {
        super.putLong(key, v);
    }

    @Override
    public boolean getBoolean(String key, boolean defValue) {
        return super.getBoolean(key, defValue);
    }

    @Override
    public void putBoolean(String key, boolean v) {
        super.putBoolean(key, v);
    }

    public static String writeObject(Object obj) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(obj);
            String stringBase64 = new String(Base64.encode(baos.toByteArray(), Base64.DEFAULT));
            return stringBase64;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }




    public static Object getObjectFromString(String stringBase64) {
        try {
            if (TextUtils.isEmpty(stringBase64))
                return null;
            byte[] base64Bytes = Base64.decode(stringBase64.getBytes(), Base64.DEFAULT);
            ByteArrayInputStream bais = new ByteArrayInputStream(base64Bytes);
            ObjectInputStream ois = new ObjectInputStream(bais);
            return ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}

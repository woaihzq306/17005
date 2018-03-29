package cn.yunhu.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SPUtil {

    protected static Context context;


    public static void init(Context context) {
        SPUtil.context = context;
    }


    public static void putString(String key, String s) {
        SharedPreferences sp = context.getSharedPreferences("CloudPhone", Context.MODE_PRIVATE);


        Editor editor = sp.edit();
        editor.putString(key, s);
        editor.apply();
    }

    public static String getString(String key) {
        SharedPreferences sp = context.getSharedPreferences("CloudPhone", Context.MODE_PRIVATE);
        return sp.getString(key, "");
    }

    public static void putboolean(String key, boolean s) {
        SharedPreferences sp = context.getSharedPreferences("CloudPhone", Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.putBoolean(key, s);
        editor.apply();
    }
    public static boolean getboolean(String key) {
        SharedPreferences sp = context.getSharedPreferences("CloudPhone", Context.MODE_PRIVATE);
        return sp.getBoolean(key, false);
    }

    public static void putInt(String key, int s) {
        SharedPreferences sp = context.getSharedPreferences("CloudPhone", Context.MODE_PRIVATE);


        Editor editor = sp.edit();
        editor.putInt(key, s);
        editor.apply();
    }


    public static int getInt(String key) {
        SharedPreferences sp = context.getSharedPreferences("CloudPhone", Context.MODE_PRIVATE);
        return sp.getInt(key, 0);
    }


//    public static void putBoolean(String key, boolean value) {
//        SharedPreferences sharedPreferences = context.getSharedPreferences("CloudPhone", Context.MODE_PRIVATE);
//
//
//        Editor editor = sharedPreferences.edit();
//        editor.putBoolean(key, value);
//        editor.apply();
//    }


    public static boolean getBoolean(String key, boolean value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("CloudPhone", Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(key, value);
    }


    public static String getString(String key, String value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("CloudPhone", Context.MODE_PRIVATE);
        return sharedPreferences.getString(key, value);
    }


    public static void clear() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("CloudPhone", Context.MODE_PRIVATE);


        Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}



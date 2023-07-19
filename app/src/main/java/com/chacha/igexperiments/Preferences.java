package com.chacha.igexperiments;

import android.content.Context;
import android.content.SharedPreferences;

import com.coniy.fileprefs.FileSharedPreferences;

public class Preferences {
    private static SharedPreferences pref;
    private static SharedPreferences.Editor editor;

    public static SharedPreferences loadPreferences(Context context){
        try {
            //noinspection deprecation
            pref = context.getSharedPreferences(Utils.PREFS_NAME, Context.MODE_WORLD_READABLE);
        } catch (SecurityException ignored) {
            pref = context.getSharedPreferences( Utils.PREFS_NAME, Context.MODE_PRIVATE);
        }
        FileSharedPreferences.makeWorldReadable(context.getPackageName(),  Utils.PREFS_NAME);
        editor = pref.edit();
        return pref;
    }

    public static SharedPreferences getPrefs(){
        return pref;
    }

    public static SharedPreferences.Editor getEditor(){
        return editor;
    }
}
package com.chacha.igexperiments;

import android.content.Context;
import android.content.SharedPreferences;
import com.coniy.fileprefs.FileSharedPreferences;

public class Preferences {
    private static SharedPreferences pref;
    private static SharedPreferences.Editor editor;

    /**
     * Init the preferences
     * @param context
     * @return the preferences
     */
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

    /**
     *
     * @return the preferences
     */
    public static SharedPreferences getPrefs(){
        return pref;
    }

    /**
     *
     * @return the editor
     */
    public static SharedPreferences.Editor getEditor(){
        return editor;
    }
}
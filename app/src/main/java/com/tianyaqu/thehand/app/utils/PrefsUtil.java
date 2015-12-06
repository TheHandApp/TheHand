package com.tianyaqu.thehand.app.utils;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.tianyaqu.thehand.app.AppContext;

/**
 * Created by Alex on 2015/11/25.
 */
public class PrefsUtil {
    public enum CONFIG_KEY{
        VERSION,
        LATEST_ISSUE_NUM,
    }
    private static SharedPreferences getDefaultPrefs() {
        return PreferenceManager.getDefaultSharedPreferences(AppContext.getContext());
    }

    public static String getString(CONFIG_KEY key){
        return getDefaultPrefs().getString(key.name(),"");
    }

    public static void setString(CONFIG_KEY key,String value){
        SharedPreferences.Editor editor = getDefaultPrefs().edit();
        editor.putString(key.name(),value);
        editor.apply();
    }

    public static long getLong(CONFIG_KEY key){
        return getDefaultPrefs().getLong(key.name(),0);
    }

    public static void setLong(CONFIG_KEY key,long value){
        SharedPreferences.Editor editor = getDefaultPrefs().edit();
        editor.putLong(key.name(),value);
        editor.apply();
    }

    public static boolean getBool(CONFIG_KEY key){
        return getDefaultPrefs().getBoolean(key.name(),false);
    }

    public static void setBool(CONFIG_KEY key,boolean value){
        SharedPreferences.Editor editor = getDefaultPrefs().edit();
        editor.putBoolean(key.name(),value);
        editor.apply();
    }

}

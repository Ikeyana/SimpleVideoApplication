package com.example.videoapplication.Utilities;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceManager {

    private SharedPreferences sharedPreferences;


    public PreferenceManager(Context context){
        sharedPreferences = context.getSharedPreferences(Constants.Key_Preference_Name, Context.MODE_PRIVATE);
    }

    public void putBoolean(String key, Boolean value){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }


    public Boolean getBoolean(String key, boolean b) {
        return sharedPreferences.getBoolean(key, false);
    }

    public void putString(String key, String value){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key,value);
        editor.apply();
    }

    public  String getString(String key){
        return sharedPreferences.getString(key, null);
    }

    public void  clearPreferences(){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}

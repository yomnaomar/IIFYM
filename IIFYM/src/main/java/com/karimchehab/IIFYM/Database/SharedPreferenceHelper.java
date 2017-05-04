package com.karimchehab.IIFYM.Database;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Set;

/**
 * Created by Kareem on 13-Feb-17.
 */

public class SharedPreferenceHelper {

    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    public static final String PREFERENCES_NAME = "IIFYM Tracker";

    public SharedPreferenceHelper(Context context) {
        preferences = context.getSharedPreferences(PREFERENCES_NAME,
                Context.MODE_PRIVATE);
    }

    public void addPreference(String key, String value) {
        editor = preferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public void addPreference(String key, Set<String> values) {
        editor = preferences.edit();
        editor.putStringSet(key, values);
        editor.commit();
    }

    public void addPreference(String key, int value) {
        editor = preferences.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public void addPreference(String key, float value) {
        editor = preferences.edit();
        editor.putFloat(key, value);
        editor.commit();
    }

    public void addPreference(String key, boolean value) {
        editor = preferences.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public void addPreference(String key, long value) {
        editor = preferences.edit();
        editor.putLong(key, value);
        editor.commit();
    }

    public void editPreference(String key, String value) {
        editor = preferences.edit();
        if (preferences.contains(key)) {
            editor.remove(key);
        }
        addPreference(key, value);
    }

    public void editPreference(String key, Set<String> value) {
        editor = preferences.edit();
        if (preferences.contains(key)) {
            editor.remove(key);
        }
        addPreference(key, value);
    }

    public void editPreference(String key, int value) {
        editor = preferences.edit();
        if (preferences.contains(key)) {
            editor.remove(key);
        }
        addPreference(key, value);
    }

    public void editPreference(String key, float value) {
        editor = preferences.edit();
        if (preferences.contains(key)) {
            editor.remove(key);
        }
        addPreference(key, value);
    }

    public void editPreference(String key, boolean value) {
        editor = preferences.edit();
        if (preferences.contains(key)) {
            editor.remove(key);
        }
        addPreference(key, value);
    }

    public void editPreference(String key, long value) {
        editor = preferences.edit();
        if (preferences.contains(key)) {
            editor.remove(key);
        }
        addPreference(key, value);
    }

    public String getStringValue(String key) {
        String value = preferences.getString(key, "");
        return value;
    }

    public String getStringValue(String key, String defValue) {
        String value = preferences.getString(key, defValue);
        return value;
    }

    public Set<String> getValues(String key) {
        Set<String> values = preferences.getStringSet(key, null);
        return values;
    }

    public int getIntValue(String key) {
        int value = preferences.getInt(key, 0);
        return value;
    }

    public boolean getBooleanValue(String key) {
        boolean value = preferences.getBoolean(key, false);
        return value;
    }

    public float getFloatValue(String key) {
        float value = preferences.getFloat(key, 0);
        return value;
    }

    public Long getLongValue(String key) {
        Long value = preferences.getLong(key, 0);
        return value;
    }

    public void removeKey(String key) {
        preferences.edit().remove(key);
        preferences.edit().commit();
    }

    public void clearAllPreferences() {
        preferences.edit().clear();
        preferences.edit().commit();
    }
}
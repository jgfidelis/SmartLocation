
package com.zhji.location.app.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.zhji.location.app.SmartLocationApp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class SharedPreferencesHelper {
    /**
     * Shared preferences name
     */
    private static final String SHARED_PREFERENCES_NAME = "location";

    private static SharedPreferences mSharedPreferences;

    private static void prepare() {
        if (mSharedPreferences == null) {
            mSharedPreferences = SmartLocationApp.getContext().getSharedPreferences(
                    SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        }
    }

    /**
     * This method save value in the SharedPreferences.
     * 
     * @param key The key to edit.
     * @param value The new value to put.
     */
    public static void savePreferences(final String key, final Object value) {
        prepare();
        new SharedPreferencesHelperTask().execute(key, value);
    }

    /**
     * Mark in the editor that a preference value should be removed, which will
     * be done in the actual preferences once commit() is called.
     * 
     * @param key The name of the preference to remove.
     */
    public static void removeValue(final String key) {
        prepare();
        final SharedPreferences.Editor sharedEditor = mSharedPreferences.edit();
        sharedEditor.remove(key).commit();
    }

    /**
     * Retrieve a boolean value from the preferences.
     * 
     * @param key The name of the preference to retrieve.
     * @param defValue Value to return if this preference does not exist.
     * @return Returns the preference value if it exists, or defValue.
     */
    public static boolean getBoolean(final String key, final boolean defValue) {
        prepare();
        return mSharedPreferences.getBoolean(key, defValue);
    }

    /**
     * Retrieve a float value from the preferences.
     * 
     * @param key The name of the preference to retrieve.
     * @param defValue Value to return if this preference does not exist.
     * @return Returns the preference value if it exists, or defValue.
     */
    public static float getFloat(final String key, final float defValue) {
        prepare();
        return mSharedPreferences.getFloat(key, defValue);
    }

    /**
     * Retrieve a int value from the preferences.
     * 
     * @param key The name of the preference to retrieve.
     * @param defValue Value to return if this preference does not exist.
     * @return Returns the preference value if it exists, or defValue.
     */
    public static int getInt(final String key, final int defValue) {
        prepare();
        return mSharedPreferences.getInt(key, defValue);
    }

    /**
     * Retrieve a long value from the preferences.
     * 
     * @param key The name of the preference to retrieve.
     * @param defValue Value to return if this preference does not exist.
     * @return Returns the preference value if it exists, or defValue.
     */
    public static long getLong(final String key, final long defValue) {
        prepare();
        return mSharedPreferences.getLong(key, defValue);
    }

    /**
     * Retrieve a String value from the preferences.
     * 
     * @param key The name of the preference to retrieve.
     * @param defValue Value to return if this preference does not exist.
     * @return Returns the preference value if it exists, or defValue.
     */
    public static String getString(final String key, final String defValue) {
        prepare();
        return mSharedPreferences.getString(key, defValue);
    }

    /**
     * Retrieve a Set<String> value from the preferences.
     * 
     * @param key The name of the preference to retrieve.
     * @param defValue Value to return if this preference does not exist.
     * @return Returns the preference value if it exists, or defValue.
     */
    public static Set<String> getStringSet(final String key, final Set<String> defValue) {
        prepare();
        return mSharedPreferences.getStringSet(key, defValue);
    }

    /**
     * Checks whether the preferences contains a preference.
     * 
     * @param key The name of the preference to check.
     * @return true if the preference exists in the preferences, otherwise
     *         false.
     */
    public static boolean contains(final String key) {
        prepare();
        return mSharedPreferences.contains(key);
    }

    private static class SharedPreferencesHelperTask extends AsyncTask<Object, Void, Void> {

        @SuppressWarnings("unchecked")
        @Override
        protected Void doInBackground(final Object... params) {
            if (mSharedPreferences != null) {
                final SharedPreferences.Editor sharedEditor = mSharedPreferences.edit();
                if (params[1] instanceof Boolean) {
                    sharedEditor.putBoolean((String) params[0], (Boolean) params[1]);
                } else if (params[1] instanceof Integer) {
                    sharedEditor.putInt((String) params[0], (Integer) params[1]);
                } else if (params[1] instanceof Float) {
                    sharedEditor.putFloat((String) params[0], (Float) params[1]);
                } else if (params[1] instanceof String) {
                    sharedEditor.putString((String) params[0], (String) params[1]);
                } else if (params[1] instanceof Long) {
                    sharedEditor.putLong((String) params[0], (Long) params[1]);
                } else if (params[1] instanceof Set<?>) {
                    if (params[1] != null) {
                        final Set<?> set = (Set<?>) params[1];
                        if (!set.isEmpty() && set.toArray()[0] instanceof String) {
                            sharedEditor.putStringSet((String) params[0], (Set<String>) params[1]);
                        }
                    }
                } else if (params[1] instanceof ArrayList<?>) {
                    final Set<String> set = new HashSet<String>();
                    set.addAll((Collection<String>) params[1]);
                    if (!set.isEmpty() && set.toArray()[0] instanceof String) {
                        sharedEditor.putStringSet((String) params[0], set);
                    }
                }
                sharedEditor.commit();
            }
            return null;
        }
    }
}

package edu.nettester;

import edu.nettester.util.Constant;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.util.Log;

public class SettingsFragment extends PreferenceFragment implements Constant {
    
    public static final String KEY_PREF_USERNAME = "pref_login_username";
    
    public static final String KEY_PREF_LASTSERVER = "pref_server_last";
    
    private SharedPreferences.OnSharedPreferenceChangeListener listener;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (DEBUG)
            Log.d(TAG, "Enter SettingsFragment onCreate");
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
        
        listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                    String key) {
                if (DEBUG)
                    Log.d(TAG, "Enter onSharedPreferenceChanged "+key);
                
                Preference userPref = findPreference(key);

                if (key.equals(KEY_PREF_USERNAME)) {
                    userPref.setSummary(sharedPreferences.getString(key, "Anonymous"));
                }
                else if (key.equals(KEY_PREF_LASTSERVER)) {
                    userPref.setSummary(sharedPreferences.getString(key, "Never"));
                }
            }
            
        };
        
        getPreferenceScreen().getSharedPreferences().
                registerOnSharedPreferenceChangeListener(listener);
    }
    
    @Override
    public void onResume() {
        if (DEBUG)
            Log.d(TAG, "Enter SettingsFragment onResume");
        super.onResume();
        
        // init updates
        SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();
        Preference userPref = findPreference(KEY_PREF_USERNAME);
        userPref.setSummary(sharedPreferences.getString(KEY_PREF_USERNAME, "Anonymous"));
        
        userPref = findPreference(KEY_PREF_LASTSERVER);
        userPref.setSummary(sharedPreferences.getString(KEY_PREF_LASTSERVER, "Never"));
    }

    @Override
    public void onPause() {
        if (DEBUG)
            Log.d(TAG, "Enter SettingsFragment onPause");
        super.onPause();
    }
    
    @Override
    public void onDestroy() {
        if (DEBUG)
            Log.d(TAG, "Enter SettingsFragment onDestroy");
        super.onDestroy();
        
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(listener);
    }
    
}

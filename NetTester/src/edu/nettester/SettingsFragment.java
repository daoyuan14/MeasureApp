package edu.nettester;

import edu.nettester.util.Constant;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.util.Log;

public class SettingsFragment extends PreferenceFragment implements Constant {
    
    public static final String KEY_PREF_LOGIN = "pref_login_view";
    public static final String KEY_PREF_USERNAME = "pref_login_username";
    public static final String KEY_PREF_LOGOUT = "pref_logout_view";
    public static final String KEY_PREF_LASTSERVER = "pref_server_last";
    public static final String KEY_PREF_LASTOFFLINE = "pref_offline_last";
    
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
                
                Preference pref = findPreference(key);

                if (key.equals(KEY_PREF_USERNAME)) {
                    String username = sharedPreferences.getString(KEY_PREF_USERNAME, "Anonymous");
                    pref.setSummary(username);
                    
                    if (username.equals("Anonymous")) {
                        pref = findPreference(KEY_PREF_LOGOUT);
                        pref.setEnabled(false);
                        pref = findPreference(KEY_PREF_LOGIN);
                        pref.setEnabled(true);
                    } else {
                        pref = findPreference(KEY_PREF_LOGIN);
                        pref.setEnabled(false);
                        pref = findPreference(KEY_PREF_LOGOUT);
                        pref.setEnabled(true);
                    }
                }
                else if (key.equals(KEY_PREF_LASTSERVER)) {
                    pref.setSummary(sharedPreferences.getString(key, "Never"));
                }
                else if (key.equals(KEY_PREF_LASTOFFLINE)) {
                    pref.setSummary(sharedPreferences.getString(key, "Never"));
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
        Preference pref = findPreference(KEY_PREF_USERNAME);
        String username = sharedPreferences.getString(KEY_PREF_USERNAME, "Anonymous");
        pref.setSummary(username);
        
        if (username.equals("Anonymous")) {
            pref = findPreference(KEY_PREF_LOGOUT);
            pref.setEnabled(false);
            pref = findPreference(KEY_PREF_LOGIN);
            pref.setEnabled(true);
        } else {
            pref = findPreference(KEY_PREF_LOGIN);
            pref.setEnabled(false);
            pref = findPreference(KEY_PREF_LOGOUT);
            pref.setEnabled(true);
        }
        
        pref = findPreference(KEY_PREF_LASTSERVER);
        pref.setSummary(sharedPreferences.getString(KEY_PREF_LASTSERVER, "Never"));
        
        pref = findPreference(KEY_PREF_LASTOFFLINE);
        pref.setSummary(sharedPreferences.getString(KEY_PREF_LASTOFFLINE, "Never"));
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

package edu.nettester;

import edu.nettester.util.Constant;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.util.Log;

public class SettingsFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener, Constant {
    
    public static final String KEY_PREF_USERNAME = "pref_login_username";
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (DEBUG)
            Log.d(TAG, "Enter onSharedPreferenceChanged");
            
        if (key.equals(KEY_PREF_USERNAME)) {
            Preference userPref = findPreference(key);
            userPref.setSummary(sharedPreferences.getString(key, "Anonymous"));
        }
    }
    
    @Override
    public void onResume() {
        if (DEBUG)
            Log.d(TAG, "Enter SettingsFragment onResume");
        
        super.onResume();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        if (DEBUG)
            Log.d(TAG, "Enter SettingsFragment onPause");
        
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }
    
}

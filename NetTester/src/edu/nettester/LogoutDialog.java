package edu.nettester;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.DialogPreference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;

public class LogoutDialog extends DialogPreference {
    
    private Context mContext;

    public LogoutDialog(Context context, AttributeSet attrs) {
        super(context, attrs);
        
        this.mContext = context;
        
        setDialogTitle("Logout really?");
        setPositiveButtonText("Yes");
        setNegativeButtonText("No");
    }
    
    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
            Editor editor = sharedPref.edit();
            editor.remove(SettingsFragment.KEY_PREF_USERNAME);
            editor.commit();
        }
    }

}

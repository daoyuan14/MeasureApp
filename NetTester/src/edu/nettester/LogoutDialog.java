package edu.nettester;

import edu.nettester.util.CommonMethod;
import edu.nettester.util.Constant;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.DialogPreference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;

public class LogoutDialog extends DialogPreference implements Constant {
    
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
            editor.remove(PREF_MUID);
            editor.remove(PREF_MHASH);
            editor.commit();
            
            CommonMethod.M_UID = "0";
            CommonMethod.M_UNAME = "Anonymous";
            CommonMethod.M_HASH = "";
        }
    }

}

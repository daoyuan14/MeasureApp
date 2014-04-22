package edu.nettester;

import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;

public class SyncDialogPreference extends DialogPreference {

    public SyncDialogPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        
        setDialogLayoutResource(R.layout.dialog_sync);
        setPositiveButtonText(android.R.string.ok);
        setNegativeButtonText(android.R.string.cancel);
        
        setDialogIcon(null);
    }

}

package edu.nettester;

import edu.nettester.task.OfflineTask;

import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;

public class OfflineSyncPreference extends DialogPreference {
	private Context mContext;
	
    public OfflineSyncPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        
        setPositiveButtonText("Sync");
        setNegativeButtonText(android.R.string.cancel);
    }
    
    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            new OfflineTask(mContext).execute();
        }
    }

}

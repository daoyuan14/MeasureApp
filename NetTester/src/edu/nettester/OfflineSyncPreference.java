package edu.nettester;

import edu.nettester.task.OfflineTask;
import android.content.Context;
import android.preference.Preference;
import android.util.AttributeSet;

public class OfflineSyncPreference extends Preference {
	private Context mContext;
	
    public OfflineSyncPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
    }

    @Override
    protected void onClick() {
        super.onClick();
        
        new OfflineTask(mContext).execute();
        // TODO
    }

}

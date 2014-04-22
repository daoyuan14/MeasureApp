package edu.nettester;

import edu.nettester.task.SyncServerListTask;

import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class SyncDialogPreference extends DialogPreference {
    
    private TextView txt_sync_server;
    private Button btn_sync;

    public SyncDialogPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        
        setDialogLayoutResource(R.layout.dialog_sync);
        //setPositiveButtonText(R.string.btn_close);
        setPositiveButtonText(android.R.string.ok);
        setNegativeButtonText(android.R.string.cancel);
        
        //setDialogIcon(null);
    }
    
    @Override
    protected void onBindDialogView(View view) {
        txt_sync_server = (TextView) view.findViewById(R.id.txt_sync_server);
        btn_sync = (Button) view.findViewById(R.id.btn_sync);
        
        btn_sync.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                txt_sync_server.setText(R.string.sync_doing);
                
                new SyncServerListTask(txt_sync_server).execute();
            }
        });
    }
    
    @Override
    protected void onDialogClosed(boolean positiveResult) {
        // TODO
    }

}

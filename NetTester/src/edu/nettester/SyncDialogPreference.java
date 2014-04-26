package edu.nettester;

import edu.nettester.task.SyncServerListTask;

import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;

public class SyncDialogPreference extends DialogPreference {
    
//    private TextView txt_sync_server;
//    private Button btn_sync;
    
    private Context mContext;

    public SyncDialogPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        
//        setDialogLayoutResource(R.layout.dialog_sync);
        
        this.mContext = context;
        
        setPositiveButtonText("Sync");
        setNegativeButtonText(android.R.string.cancel);
    }
    
//    @Override
//    protected void onBindDialogView(View view) {
//        txt_sync_server = (TextView) view.findViewById(R.id.txt_sync_server);
//        btn_sync = (Button) view.findViewById(R.id.btn_sync);
//        
//        btn_sync.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                txt_sync_server.setText(R.string.sync_doing);
//                
//                new SyncServerListTask(txt_sync_server).execute();
//            }
//        });
//    }
    
    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            new SyncServerListTask(mContext).execute();
        }
    }

}

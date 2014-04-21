package edu.nettester;

import edu.nettester.db.MeasureContract.MeasureLog;
import edu.nettester.db.MeasureDBHelper;
import edu.nettester.util.Constant;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

public class DisplayResult extends AsyncTask<Void, Void, String> implements Constant {
    
    private Context mContext;
    private TextView mView;
    
    public DisplayResult(Context context, TextView view) {
        mContext = context;
        mView = view;
    }

    @Override
    protected String doInBackground(Void... params) {
        if (DEBUG)
            Log.d(TAG, "Enter doInBackground in DisplayResult");
        
        MeasureDBHelper mDbHelper = new MeasureDBHelper(mContext);
        
        Cursor cur = mDbHelper.fetchAllLogs();
        
        String result = "";
        
        if (cur.moveToFirst()) {
            do {
                String mid = cur.getString(cur.getColumnIndex(MeasureLog.COLUMN_NAME_MID));
                String timestamp = cur.getString(cur.getColumnIndex(MeasureLog.COLUMN_NAME_TIME));
                String rtt = cur.getString(cur.getColumnIndex(MeasureLog.COLUMN_NAME_RTT));
                
                result += mid + ", ";
                result += timestamp + ", ";
                result += rtt + ", ";
                result += "\n";
            } while (cur.moveToNext());
        }
        cur.close();
        
        if (DEBUG)
            Log.d(TAG, result);
        
        return result;
    }

    @Override
    protected void onPostExecute(String result) {
        // View cannot be accessible in doInBackground()
        mView.setText(result);
    }
}

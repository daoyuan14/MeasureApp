package edu.nettester;

import edu.nettester.db.MeasureContract.MeasureLog;
import edu.nettester.db.MeasureDBHelper;
import edu.nettester.util.Constant;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

/**
 * @deprecated
 * @author Daoyuan
 */
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
                String mid = cur.getString(cur.getColumnIndex(MeasureLog.MID));
                //String timestamp = cur.getString(cur.getColumnIndex(MeasureLog.COLUMN_NAME_TIME));
                String rtt = cur.getString(cur.getColumnIndex(MeasureLog.AVG_RTT));
                String down_tp = cur.getString(cur.getColumnIndex(MeasureLog.DOWN_TP));
                String up_tp = cur.getString(cur.getColumnIndex(MeasureLog.UP_TP));
                
                result += mid + ", RTT: ";
                //result += timestamp + ", ";
                result += rtt + " ms, Download: ";
                result += down_tp + " kbps, Upload:";
                result += up_tp + " kbps";
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

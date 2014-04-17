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
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        
        String[] projection = {
                MeasureLog._ID,
                MeasureLog.COLUMN_NAME_MID,
                MeasureLog.COLUMN_NAME_TIME,
                MeasureLog.COLUMN_NAME_RTT,
                };
        
        Cursor cur = db.query(
                MeasureLog.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                                   // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                 // The sort order
                );
        
        String result = "";
        
        for (cur.moveToFirst(); cur.moveToNext(); ) {
            String mid = cur.getString(cur.getColumnIndex(MeasureLog.COLUMN_NAME_MID));
            String timestamp = cur.getString(cur.getColumnIndex(MeasureLog.COLUMN_NAME_TIME));
            String rtt = cur.getString(cur.getColumnIndex(MeasureLog.COLUMN_NAME_RTT));
            
            result += mid + ", ";
            result += timestamp + ", ";
            result += rtt + ", ";
            result += "\n";
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

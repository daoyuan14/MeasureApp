package edu.nettester.task;

import edu.nettester.db.MeasureContract.MeasureLog;
import edu.nettester.db.MeasureDBHelper;
import edu.nettester.util.Constant;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

/**
 * One measurement task
 * 
 * @author Daoyuan
 */
public class RTTTask extends AsyncTask<Void, Integer, Integer> implements Constant {
    
    private Context mContext;
    
    public RTTTask(Context context) {
        mContext = context;
    }

    /**
     * do measurement
     * TODO
     */
    @Override
    protected Integer doInBackground(Void... params) {
        return null;
    }

    /**
     * handle progress bar
     */
    @Override
    protected void onProgressUpdate(Integer... progress) {
        //TODO
    }
    
    /**
     * insert into DB
     */
    @Override
    protected void onPostExecute(Integer result) {
        MeasureDBHelper mDbHelper = new MeasureDBHelper(mContext);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        
        // TODO insert some fake data
        ContentValues values = new ContentValues();
        values.put(MeasureLog.COLUMN_NAME_MID, "1234567890");
        values.put(MeasureLog.COLUMN_NAME_TIME, "Apr 16");
        values.put(MeasureLog.COLUMN_NAME_RTT, "128");
        
        long newRowId;
        newRowId = db.insert(
                MeasureLog.TABLE_NAME,
                null,
                values);
        if (DEBUG)
            Log.d(TAG, "Insert a db row: "+newRowId);
    }

}

package edu.nettester.task;

import java.util.ArrayList;
import java.util.Collections;
import java.lang.Float;
import java.lang.String;

import edu.nettester.db.MeasureContract.MeasureLog;
import edu.nettester.db.MeasureDBHelper;
import edu.nettester.util.Constant;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
//import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.util.Log;

/**
 * One measurement task
 * 
 * @author Daoyuan and Weichao
 */
public class RTTTask extends AsyncTask<Void, Integer, String> implements Constant {
    
    private Context mContext;
    //private AndroidHttpClient httpClient = null;
    String OUTAG = "NetTester";
    
    public RTTTask(Context context) {
        mContext = context;
    }

    @Override
    protected String doInBackground(Void... params) {
        //for test only
    	String mserver = "http://sp1.szunicom.info/speedtest/";
    	
    	String up_url = mserver+"upload.php";
        
        
        
        //perform ping task
    	/*
        ArrayList<Float> rtt_list = new ArrayList<Float>();
        for(int i=0;i<10;i++) {
        	HTTPPinger pingtask = new HTTPPinger();
        	pingtask.init(mserver);
        	float rtt = pingtask.execute();
        	rtt_list.add(rtt);
        	
        	//update progress
        	
        }
        Collections.sort(rtt_list);
        float min_rtt = rtt_list.get(0);
        float max_rtt = rtt_list.get(9);        
        float avg_rtt = getAverage(rtt_list);
        float median_rtt = getMedian(rtt_list);
        float stdv_rtt = getStdDv(rtt_list);*/
        
        //perform download throughput test
        HTTPDownTP downtask = new HTTPDownTP();
        downtask.init(mserver);
        float tp = downtask.execute();
        Log.d(OUTAG,String.valueOf(tp));
        
        return String.valueOf(tp);
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
    protected void onPostExecute(String result) {
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
    
    
    public float getAverage(ArrayList<Float> inlist){
    	int num = inlist.size();
        int sum = 0;
        for(int i=0;i<num;i++){
            sum += inlist.get(i);
        }
        return (float)(sum/num);
    }
    
    public float getStdDv(ArrayList<Float> inlist){
    	int num = inlist.size();
        double sum = 0;
        float avg_val = getAverage(inlist);
        for(int i=0;i<num;i++){
        	float cur_val = inlist.get(i);
            sum += Math.sqrt((cur_val - avg_val) * (cur_val - avg_val));
        }
        return (float)(sum/(num-1));
    }
    
    public float getMedian(ArrayList<Float> inlist) {
    	int num = inlist.size();
    	Collections.sort(inlist);
    	
    	int rindex = 0;
    	float outval;
    	
    	int a=num%2;
    	if (a==0) {
    		rindex = num/2;
    		outval = (inlist.get(rindex) + inlist.get(rindex+1))/2;
    	} else {
    		rindex = (num+1)/2;
    		outval = inlist.get(rindex);
    	}
    	
    	return outval;
    }

}

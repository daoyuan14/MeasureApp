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
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.util.Log;

/**
 * One measurement task
 * 
 * @author Daoyuan and Weichao
 */
public class RTTTask extends AsyncTask<String, Integer, String[]> implements Constant {
    
    private Context mContext;
    //private AndroidHttpClient httpClient = null;
    
    public RTTTask(Context context) {
        mContext = context;
    }

    @Override
    protected String[] doInBackground(String... params) {
        //for test only
    	String target = params[0];
    	String mserver = servermap.get(target);
    	
    	String mid = String.valueOf(System.currentTimeMillis());
    	String mnetwork = getNetworkType(mContext);
    	String mlocation = getLocation(mContext);
    	String deviceID = getDeviceID(mContext);
    	    	
        //perform ping task
        ArrayList<Float> rtt_list = new ArrayList<Float>();
        for(int i=0;i<10;i++) {
        	HTTPPinger pingtask = new HTTPPinger(mserver);
        	float rtt = pingtask.execute();
        	rtt_list.add(rtt);
        	
        	//update progress
        	
        }
        Collections.sort(rtt_list);
        float min_rtt = rtt_list.get(0);
        float max_rtt = rtt_list.get(9);        
        float avg_rtt = getAverage(rtt_list);
        float median_rtt = getMedian(rtt_list);
        float stdv_rtt = getStdDv(rtt_list);
        
        //perform download throughput test
        HTTPDownTP downtask = new HTTPDownTP(mserver);
        float downtp = downtask.execute();
        //update progress
        
    	//perform upload throughput test
    	HTTPUpTP uptask = new HTTPUpTP(mserver);
    	float uptp = uptask.execute();
    	//update progress
    	
    	if (DEBUG) {
    		Log.d(TAG, "RTT: "+String.valueOf(median_rtt)+"ms");
    		Log.d(TAG, "Down TP: "+String.valueOf(downtp)+"kbps");
    		Log.d(TAG, "Up TP: "+String.valueOf(uptp)+"kbps");
    	}
    		
        
        return new String[] {mid, deviceID, mnetwork, mlocation, mserver, String.valueOf(avg_rtt), 
        		String.valueOf(median_rtt), String.valueOf(min_rtt), String.valueOf(max_rtt), 
        		String.valueOf(stdv_rtt), String.valueOf(downtp), String.valueOf(uptp)};
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
    protected void onPostExecute(String[] result) {
        MeasureDBHelper mDbHelper = new MeasureDBHelper(mContext);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        
        // TODO insert some fake data
        ContentValues values = new ContentValues();
        values.put(MeasureLog.MID, result[0]);
        values.put(MeasureLog.M_NET_INFO, result[1]);
        values.put(MeasureLog.M_LOC_INFO, result[3]);
        values.put(MeasureLog.M_TAR_SERVER, result[4]);
        values.put(MeasureLog.AVG_RTT, result[5]);
        values.put(MeasureLog.MEDIAN_RTT, result[6]);
        values.put(MeasureLog.MIN_RTT, result[7]);
        values.put(MeasureLog.MAX_RTT, result[8]);
        values.put(MeasureLog.STDV_RTT, result[9]);
        values.put(MeasureLog.DOWN_TP, result[10]);
        values.put(MeasureLog.UP_TP, result[11]);
        
        long newRowId;
        newRowId = db.insert(
                MeasureLog.TABLE_NAME,
                null,
                values);
        if (DEBUG)
            Log.d(TAG, "Insert a db row: "+newRowId);
        
        //upload data
        
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
    
    private String getNetworkType(Context mContext){
        ConnectivityManager connManager = (ConnectivityManager)mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkinfo = connManager.getActiveNetworkInfo();  
        String networkType = "";  
        if(networkinfo != null) {
        	networkType = networkinfo.getTypeName();              
        }
        if (DEBUG)
        	Log.d(TAG, "Network type:" + networkType);
        return networkType;  
    }
    
    private String getLocation(Context mContext) {
		String outloc = "";
		
		LocationManager lm = (LocationManager)mContext.getSystemService(Context.LOCATION_SERVICE);
		Criteria criteria = new Criteria(); 
		criteria.setCostAllowed(false);
		criteria.setAccuracy(Criteria.ACCURACY_COARSE);
		String providerName = lm.getBestProvider(criteria, true);
		
		if (providerName != null) {
			Location location = lm.getLastKnownLocation(providerName);
            Log.i(TAG, "-------"+location);
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            outloc = String.valueOf(longitude) + "," + String.valueOf(latitude);
        } else {
        	outloc = "Unknow place";
        }
		if (DEBUG)
        	Log.d(TAG, "Location:" + outloc);
		return outloc;
	}
    
    private String getDeviceID(Context mContext) {
    	String deviceID = "";
    	TelephonyManager telephonyManager = null;
    	
    	TelephonyManager tryTelephonyManager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);    	
    	String deviceId = telephonyManager.getDeviceId();  // This ID is permanent to a physical phone.
        // "generic" means the emulator.
    	
        if (deviceId == null || Build.DEVICE.equals("generic")) {
        	// This ID changes on OS reinstall/factory reset.
        	deviceId = Secure.getString(mContext.getContentResolver(), Secure.ANDROID_ID);
        }
    	
    	return deviceID;
    }
}

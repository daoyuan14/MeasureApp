package edu.nettester.task;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import edu.nettester.LoginActivity;
import edu.nettester.SettingsFragment;
import edu.nettester.db.MeasureContract.OfflineDel;
import edu.nettester.db.MeasureDBHelper;
import edu.nettester.db.MofflineDBHelper;
import edu.nettester.db.MeasureContract.MeasureLog;
import edu.nettester.util.CommonMethod;
import edu.nettester.util.Constant;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

public class OfflineTask extends AsyncTask<Void, Integer, Boolean> implements Constant {
	private Context mContext;
    
    public OfflineTask(Context context) {
        this.mContext = context;
    }
	
    @Override
    protected Boolean doInBackground(Void... params) {
    	OPHTTPClient mclient = new OPHTTPClient();
    	
    	//check whether there are some measurement logs deleted at the server side
    	publishProgress(0);
    	
    	String[] del_ar = {};
    	String check_url = delcheck_url + "?m_uid=" + CommonMethod.M_UID + "&m_hash=" + CommonMethod.M_HASH;
    	String check_output = mclient.getPage(check_url);
    	if(DEBUG) Log.d(TAG, check_output);
    	
    	if(check_output.equals("nouser")) {
    		Toast.makeText(mContext, "No such user", Toast.LENGTH_SHORT).show();
    		if(DEBUG) Log.d(TAG, "No such user");
    	} else if(!check_output.equals("nodata")){
    		del_ar = check_output.split(",");
    	}
    	
    	if(del_ar.length>0) {
    		MeasureDBHelper mDbHelper = new MeasureDBHelper(mContext);
			SQLiteDatabase db = mDbHelper.getReadableDatabase();
    		for(int i=0;i<del_ar.length;i++) {
    			int result = db.delete(MeasureLog.TABLE_NAME, MeasureLog.MUID+"="+CommonMethod.M_UID + " AND " + MeasureLog.MID+"="+del_ar[i], null);
    			if (DEBUG)
    	            Log.d(TAG, "Delete "+result+" row.");
    		}
    		db.close();
    		mDbHelper.close();
    	}
    	//TODO, update the result activity
    	

    	//delete the unsync logs at the server side
    	publishProgress(1);
    	
    	String mid_str = "";
    	ArrayList<String> mid_ar = new ArrayList<String>();
    	MofflineDBHelper mOffline = new MofflineDBHelper(mContext);
    	Cursor cur = mOffline.fetchAllLogs();
    	boolean have_rec = cur.moveToFirst();
    	while(have_rec) {
    		int cindex = cur.getColumnIndex(OfflineDel.MID);
    		String cmid = cur.getString(cindex);
    		int cindex2 = cur.getColumnIndex(OfflineDel.MUID);
    		String cmuid = cur.getString(cindex2);
    		Log.d(TAG, cmid+","+cmuid);
    		
    		if(cmuid.equals(CommonMethod.M_UID)) {
	    		if(mid_str.length()>0) {
	    			mid_str += ",";
	    		}
	    		mid_str += cmid;
	    		mid_ar.add(cmid);
    		}
    		if(cur.isLast()) {
    			break;
    		} else {
    			cur.moveToNext();
    		}
    	}
    	
    	if(DEBUG) Log.d(TAG, mid_str);
    	if(mid_str.length()>0) {
    		List<NameValuePair> DataList = new ArrayList<NameValuePair>();
            DataList.add(new BasicNameValuePair(MeasureLog.MUID, CommonMethod.M_UID));
            DataList.add(new BasicNameValuePair(MeasureLog.MHASH, CommonMethod.M_HASH));
            DataList.add(new BasicNameValuePair("m_mids", mid_str));
	        
	        String del_output = mclient.postPage(deldata_url, DataList);
	        if(DEBUG) Log.d(TAG, del_output);
	        
	        if(del_output.equals("success")) {
	        	//delete the offlinedb
	        	for(int i=0;i<mid_ar.size();i++) {
	        		String cc_mid = mid_ar.get(i);
	        		int drow = mOffline.getReadableDatabase().delete(
	        				OfflineDel.TABLE_NAME, 
	        				OfflineDel.MID + "=? AND " + OfflineDel.MUID + "=?", 
	        				new String[]{cc_mid, CommonMethod.M_UID});
	        		if(drow>0) {
	        			if(DEBUG) Log.d(TAG, "row deleted");
	        		}
	        	}
	        }
    	}
    	
    	mOffline.close();
    	
    	
    	//upload the logs that haven't been successfully uploaded
    	publishProgress(2);
    	
    	MeasureDBHelper mDbHelper = new MeasureDBHelper(mContext);
		SQLiteDatabase db = mDbHelper.getReadableDatabase();
		
		Cursor mcur = db.query(MeasureLog.TABLE_NAME, null, MeasureLog.UPFLG + "=?", new String[] {"0"}, null, null, null);
		boolean mh_rec = mcur.moveToFirst();
		while(mh_rec) {
			long q_id = mcur.getInt(0);
			String q_muid = mcur.getString(2);
			if(q_muid.equals(CommonMethod.M_UID) || q_muid.equals("0")) {
				List<NameValuePair> DataList = new ArrayList<NameValuePair>();
				DataList.add(new BasicNameValuePair(MeasureLog.MUID, CommonMethod.M_UID));
		        DataList.add(new BasicNameValuePair(MeasureLog.MHASH, CommonMethod.M_HASH));
		        DataList.add(new BasicNameValuePair(MeasureLog.MID, mcur.getString(3)));
		        DataList.add(new BasicNameValuePair(MeasureLog.M_NET_INFO, mcur.getString(5)));
		        DataList.add(new BasicNameValuePair(MeasureLog.M_LOC_INFO, mcur.getString(6)));
		        DataList.add(new BasicNameValuePair(MeasureLog.M_TAR_SERVER, mcur.getString(7)));
		        DataList.add(new BasicNameValuePair(MeasureLog.M_DEVID, getDeviceID(mContext)));
		        DataList.add(new BasicNameValuePair(MeasureLog.AVG_RTT, mcur.getString(8)));
		        DataList.add(new BasicNameValuePair(MeasureLog.MEDIAN_RTT, mcur.getString(9)));
		        DataList.add(new BasicNameValuePair(MeasureLog.MAX_RTT, mcur.getString(11)));
		        DataList.add(new BasicNameValuePair(MeasureLog.MIN_RTT, mcur.getString(10)));
		        DataList.add(new BasicNameValuePair(MeasureLog.STDV_RTT, mcur.getString(12)));
		        DataList.add(new BasicNameValuePair(MeasureLog.UP_TP, mcur.getString(13)));
		        DataList.add(new BasicNameValuePair(MeasureLog.DOWN_TP, mcur.getString(14)));
		        
		        String q_output = mclient.postPage(updata_url, DataList);
		        
		        //update database
		        if(q_output.equals("success") || q_output.equals("exist")) {
		        	ContentValues upvalues = new ContentValues();
		        	upvalues.put(MeasureLog.UPFLG, "1");
		        	db.update(MeasureLog.TABLE_NAME, upvalues, MeasureLog._ID+"=?", new String[]{String.valueOf(q_id)});
		        }
			}
			
			if(mcur.isLast()) {
    			break;
    		} else {
    			mcur.moveToNext();
    		}
		}
		
		db.close();
		mDbHelper.close();
    	mclient.destroy();
    	
    	//update the last sync time
    	publishProgress(3);
    	
    	return true;
    }
    
    @Override
    protected void onProgressUpdate(Integer... progress) {
        switch (progress[0]) {
            case 0:
                Toast.makeText(mContext, "Checking remote database...", Toast.LENGTH_SHORT)
                .show();
                break;
            case 1:
                Toast.makeText(mContext, "Deleting data at the server side...", Toast.LENGTH_SHORT)
                .show();
                break;
            case 2:
                Toast.makeText(mContext, "Uploading data...", Toast.LENGTH_SHORT)
                .show();
                break;
            case 3:
                Toast.makeText(mContext, "Measurement log sync: done!", Toast.LENGTH_SHORT)
                .show();
                break;
            default:
                break;
        }
    }
    
    @Override
    protected void onPostExecute(Boolean result) {
        if (result) {
            // update preferences
            long ts = System.currentTimeMillis();
            SimpleDateFormat df = new SimpleDateFormat("MMM d HH:mm, yyyy");
            String curtime = df.format(ts);
            
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
            Editor editor = sharedPref.edit();
            editor.putString(SettingsFragment.KEY_PREF_LASTOFFLINE, curtime);
            editor.commit();
        }
    }
    
    private String getDeviceID(Context mContext) {
    	String deviceID = "NA";
    	TelephonyManager telephonyManager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
    	
    	String deviceId = telephonyManager.getDeviceId();  // This ID is permanent to a physical phone.
        // "generic" means the emulator.
    	
        if (deviceId == null || Build.DEVICE.equals("generic")) {
        	// This ID changes on OS reinstall/factory reset.
        	deviceId = Secure.getString(mContext.getContentResolver(), Secure.ANDROID_ID);
        }
    	
        if (DEBUG)
        	Log.d(TAG, "Device ID:" + deviceID);
        
    	return deviceID;
    }
}

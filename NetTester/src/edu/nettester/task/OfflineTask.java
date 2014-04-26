package edu.nettester.task;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import edu.nettester.db.MeasureContract.OfflineDel;
import edu.nettester.db.MeasureDBHelper;
import edu.nettester.db.MofflineDBHelper;
import edu.nettester.db.MeasureContract.MeasureLog;
import edu.nettester.util.CommonMethod;
import edu.nettester.util.Constant;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class OfflineTask extends AsyncTask<Void, Void, Boolean> implements Constant {
	private Context mContext;
    
    public OfflineTask(Context context) {
        this.mContext = context;
    }
	
    @Override
    protected Boolean doInBackground(Void... params) {
    	OPHTTPClient mclient = new OPHTTPClient();
    	
    	//check whether there are some measurement logs deleted at the server side
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
    	
    	
    	
    	mclient.destroy();
    	return true;
    }
    
    @Override
    protected void onPostExecute(Boolean result) {
    	
    }

}

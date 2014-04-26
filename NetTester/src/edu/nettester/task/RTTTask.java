package edu.nettester.task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
//import java.lang.Float;
import java.io.BufferedInputStream;
import java.lang.String;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.message.BasicNameValuePair;

import edu.nettester.R;
import edu.nettester.db.MeasureContract.MeasureLog;
import edu.nettester.db.MeasureDBHelper;
import edu.nettester.util.CommonMethod;
import edu.nettester.util.Constant;

import android.content.ContentValues;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.database.sqlite.SQLiteDatabase;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * One measurement task
 * 
 * @author Daoyuan and Weichao
 */
public class RTTTask extends AsyncTask<String, Integer, String[]> implements Constant {
    
    private Context mContext;
    private TextView txt_task;
    private ProgressBar mProgress;
    private Button btn_look;
    
    public RTTTask(Context context, TextView text, ProgressBar progress, Button look) {
        mContext = context;
        txt_task = text;
        mProgress = progress;
        btn_look = look;
    }

    @Override
    protected String[] doInBackground(String... params) {
        //for test only
    	String target = params[0];
    	String mserver = servermap.get(target);
    	
    	String mid = String.valueOf(System.currentTimeMillis());
    	String mtime = String.valueOf(System.currentTimeMillis()); //TODO should be high-level time
    	
    	String mnetwork = getNetworkType(mContext);
    	String mlocation = getLocation(mContext);
    	String deviceID = getDeviceID(mContext);
    	    	
        /*
         * perform ping task
         */
    	publishProgress(new Integer[] {Cat_RTT, 0});
        ArrayList<Double> rtt_list = new ArrayList<Double>();
        for(int i=0;i<10;i++) {
        	HTTPPinger pingtask = new HTTPPinger(mserver);
        	double rtt = pingtask.execute();
        	rtt_list.add(rtt);
        	
        	//update progress
        	publishProgress(new Integer[] {Cat_RTT, i*10});        	
        }
        publishProgress(new Integer[] {Cat_RTT, 100});
        
        Collections.sort(rtt_list);
        double min_rtt = rtt_list.get(0);
        double max_rtt = rtt_list.get(9);        
        double avg_rtt = getAverage(rtt_list);
        double median_rtt = getMedian(rtt_list);
        double stdv_rtt = getStdDv(rtt_list);
        
        /*
         * perform download throughput test
         */
        publishProgress(new Integer[] {Cat_DOWNLOAD, 0});
        HTTPDownTP downtask = new HTTPDownTP(mserver);
        float downtp = downtask.execute();
        publishProgress(new Integer[] {Cat_DOWNLOAD, 100});
        
    	/*
    	 * perform upload throughput test
    	 */
        publishProgress(new Integer[] {Cat_UPLOAD, 0});
    	HTTPUpTP uptask = new HTTPUpTP(mserver);
    	float uptp = uptask.execute();
    	publishProgress(new Integer[] {Cat_UPLOAD, 100});
    	
    	if (DEBUG) {
    		Log.d(TAG, "RTT: "+String.valueOf(median_rtt)+"ms");
    		Log.d(TAG, "Down TP: "+String.valueOf(downtp)+"kbps");
    		Log.d(TAG, "Up TP: "+String.valueOf(uptp)+"kbps");
    	}
        
        return new String[] {mid, deviceID, mnetwork, mlocation, mserver, String.valueOf(avg_rtt), 
        		String.valueOf(median_rtt), String.valueOf(min_rtt), String.valueOf(max_rtt), 
        		String.valueOf(stdv_rtt), String.valueOf(downtp), String.valueOf(uptp), mtime};
    }

    /**
     * handle progress bar
     */
    @Override
    protected void onProgressUpdate(Integer... progress) {
        Integer category = progress[0];
        Integer speed = progress[1];
        
        txt_task.setVisibility(View.VISIBLE);
        mProgress.setVisibility(View.VISIBLE);
        
        switch(category) {
            case Cat_RTT:
                txt_task.setText("Measuring RTT (Round Trip Time)...");
                break;
            case Cat_DOWNLOAD:
                txt_task.setText("Measuring download throughput...");
                break;
            case Cat_UPLOAD:
                txt_task.setText("Measuring upload throughput...");
                break;
            default:
                break;
        }
        
        mProgress.setProgress(speed);
    }
    
    /**
     * insert into DB
     */
    @Override
    protected void onPostExecute(String[] result) {
        txt_task.setText("Finished measurement!");
        
        MeasureDBHelper mDbHelper = new MeasureDBHelper(mContext);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        
        String m_uid, m_hash;
        if(CommonMethod.M_UID != null) {
        	m_uid = CommonMethod.M_UID;
        	m_hash = CommonMethod.M_HASH;
        } else {
        	m_uid = "0";
        	m_hash = "";
        }
        
        // TODO insert some fake data
        ContentValues values = new ContentValues();
        values.put(MeasureLog.MUID, m_uid);
        values.put(MeasureLog.MID, result[0]);
        values.put(MeasureLog.M_NET_INFO, result[2]);
        values.put(MeasureLog.M_LOC_INFO, result[3]);
        values.put(MeasureLog.M_TAR_SERVER, result[4]);
        values.put(MeasureLog.AVG_RTT, result[5]);
        values.put(MeasureLog.MEDIAN_RTT, result[6]);
        values.put(MeasureLog.MIN_RTT, result[7]);
        values.put(MeasureLog.MAX_RTT, result[8]);
        values.put(MeasureLog.STDV_RTT, result[9]);
        values.put(MeasureLog.DOWN_TP, result[10]);
        values.put(MeasureLog.UP_TP, result[11]);
        values.put(MeasureLog.MTIME, result[12]);
        
        long newRowId;
        newRowId = db.insert(
                MeasureLog.TABLE_NAME,
                null,
                values);
        if (DEBUG)
            Log.d(TAG, "Insert a db row: "+newRowId);
        
        // display btn_look
        btn_look.setVisibility(View.VISIBLE);
        
        /*
         * upload data
         */
        if(DEBUG) {
        	Log.d(TAG, m_uid + ":" + m_hash);
        }
        
        List<NameValuePair> DataList = new ArrayList<NameValuePair>();
        DataList.add(new BasicNameValuePair(MeasureLog.MUID, m_uid));
        DataList.add(new BasicNameValuePair(MeasureLog.MHASH, m_hash));
        DataList.add(new BasicNameValuePair(MeasureLog.MID, result[0]));
        DataList.add(new BasicNameValuePair(MeasureLog.M_NET_INFO, result[2]));
        DataList.add(new BasicNameValuePair(MeasureLog.M_LOC_INFO, result[3]));
        DataList.add(new BasicNameValuePair(MeasureLog.M_TAR_SERVER, result[4]));
        DataList.add(new BasicNameValuePair(MeasureLog.M_DEVID, result[1]));
        DataList.add(new BasicNameValuePair(MeasureLog.AVG_RTT, result[5]));
        DataList.add(new BasicNameValuePair(MeasureLog.MEDIAN_RTT, result[6]));
        DataList.add(new BasicNameValuePair(MeasureLog.MAX_RTT, result[8]));
        DataList.add(new BasicNameValuePair(MeasureLog.MIN_RTT, result[7]));
        DataList.add(new BasicNameValuePair(MeasureLog.STDV_RTT, result[9]));
        DataList.add(new BasicNameValuePair(MeasureLog.UP_TP, result[11]));
        DataList.add(new BasicNameValuePair(MeasureLog.DOWN_TP, result[10]));
        
        //OPHTTPClient mclient = new OPHTTPClient();
        //String upload_output = mclient.postPage(CommonMethod.updata_url, DataList);
        try {
	        UploadProc mupload = new UploadProc();
	        String upload_output = mupload.execute(DataList).get();
	        
	        if(upload_output.equals("success") || upload_output.equals("exist")) {
	        	//upload database
	        	ContentValues upvalues = new ContentValues();
	        	upvalues.put(MeasureLog.UPFLG, "1");
	        	db.update(MeasureLog.TABLE_NAME, upvalues, MeasureLog._ID+"=?", new String[]{String.valueOf(newRowId)});
	        }
        } catch (Exception e) {  
            Log.e(CommonMethod.TAG, e.getMessage());
        }
        
        db.close();
        mDbHelper.close();
    }
    
    
    public double getAverage(ArrayList<Double> inlist){
    	int num = inlist.size();
        int sum = 0;
        for(int i=0;i<num;i++){
            sum += inlist.get(i);
        }
        return (double)(sum/num);
    }
    
    public double getStdDv(ArrayList<Double> inlist){
    	int num = inlist.size();
        double sum = 0;
        double avg_val = getAverage(inlist);
        for(int i=0;i<num;i++){
        	double cur_val = inlist.get(i);
            sum += Math.sqrt((cur_val - avg_val) * (cur_val - avg_val));
        }
        return (double)(sum/(num-1));
    }
    
    public double getMedian(ArrayList<Double> inlist) {
    	int num = inlist.size();
    	Collections.sort(inlist);
    	
    	int rindex = 0;
    	double outval;
    	
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
    
    /**
     * 
     * @author Weichao
     * @since 14-04-20
     */
    public class HTTPDownTP implements Constant {
        private String mserver = "";
        private int do_size[] = {350,500,750,1000,1500,2000,2500,3000,3500,4000};
        
        public HTTPDownTP(String in_server) {
            this.mserver = in_server;
        }
        
        public float execute() {
            float tp = 0;
            int num_test = do_size.length;
            boolean success = false;
            
            try {
                AndroidHttpClient client = AndroidHttpClient.newInstance("Dalvik/1.6.0 NetTester of OneProbe Group");
                for(int i=0;i<num_test;i++) {
                    int statusCode = 0;
                    long ts = System.nanoTime();
                    String base_down_url = mserver+"random"; 
                    String down_url = base_down_url + do_size[i] + "x" + do_size[i] + ".jpg?x=" + ts;
                    
                    HttpGet httpGet = new HttpGet(down_url);
                    
                    long startTime = System.nanoTime();
                    HttpResponse response = client.execute(httpGet);
                    
                    StatusLine statusLine = response.getStatusLine();
                    if (statusLine != null) {
                        statusCode = statusLine.getStatusCode();
                        success = (statusCode == 200);
                    }
                                    
                    if(success) {
                        try{
                            HttpEntity responseEntity = response.getEntity();
                            
                            BufferedInputStream bufferedinStrm;
                            bufferedinStrm = new BufferedInputStream(responseEntity.getContent());                      
                            
                            // TO-DO  getContentLength() returns negative number if body length is unknown
                            long downsize = responseEntity.getContentLength();
                            
                            byte abyte[];
                            abyte = new byte[32768];                        
                            long have_read = 0;
                            long readLen;
                            
                            while ((readLen = bufferedinStrm.read(abyte)) > 0) {
                                have_read += readLen;
                            }
                            
                            long endTime = System.nanoTime();
                            
                            float duration = (endTime-startTime)/1000000;
                            if (DEBUG)
                                Log.d(TAG, String.valueOf(downsize)+":"+String.valueOf(have_read)+":"+String.valueOf(duration));
                            tp = (have_read*8)/duration;
                            
                            if(duration > 10000) {
                                break;
                            }
                            TimeUnit.MILLISECONDS.sleep(200);
                        } catch (InterruptedException e){
                            Log.e(TAG, e.getMessage());
                        }
                    } else {
                        Log.w(TAG, "download failed");
                    }
                    httpGet.abort();
                    
                    publishProgress(new Integer[] {Cat_DOWNLOAD, i*(100/num_test)});
                }
                client.close();
            } catch (Exception e) {  
                Log.e(TAG, e.getMessage());
            }
            
            return tp;
        }
    }
    
    private class UploadProc extends AsyncTask<List<NameValuePair>, Void, String> {
    	@Override
		protected String doInBackground(List<NameValuePair>...params) {
    		String output = "";
    		        	
        	OPHTTPClient mclient = new OPHTTPClient();
        	output = mclient.postPage(CommonMethod.updata_url, params[0]);
        	
        	if (CommonMethod.DEBUG)
        		Log.d(CommonMethod.TAG, output);
        	
        	mclient.destroy();
        	
    		return output;
    	}
    	
    	@Override
        protected void onPostExecute(String result) {
    		
    	}
    }
}

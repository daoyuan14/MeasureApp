package edu.nettester.task;

import edu.nettester.util.Constant;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.TimeUnit;
//import java.lang.Float;
import java.lang.String;

import android.util.Log;

/**
 * 
 * @author Weichao
 * @since 14-04-20
 */
public class HTTPPinger implements Constant {
	private String mserver = "";
	
	public HTTPPinger(String mserver) {
        this.mserver = mserver;
    }
	
	public double execute() {
		double rtt = 0;
		try {
			int resp_code = 0;
			
			long ts = System.nanoTime();
			String rtt_url = this.mserver+"latency.txt?x=" + ts;
			
			URL url1 = new URL(rtt_url);
			HttpURLConnection httpconn = (HttpURLConnection) url1.openConnection();
			httpconn.setConnectTimeout(3000);
			httpconn.setUseCaches(false);
			httpconn.setRequestProperty("Accept-Encoding", "");
			httpconn.connect();
			
			long t1 = System.nanoTime();
			resp_code = httpconn.getResponseCode();
			long t2 = System.nanoTime();
			rtt = (t2-t1)/1000000;
			
			//String rtt_out = String.valueOf(rtt);
			//Log.d(OUTAG, rtt_out);
			
			if(resp_code > 0) {
				try{
					TimeUnit.MILLISECONDS.sleep(200);
					resp_code = 0;
				} catch (InterruptedException e){
					Log.e(TAG, e.getMessage());
				}
			}
			httpconn.disconnect();
		} catch (Exception e) {  
            Log.e(TAG, e.getMessage());
        }
		return rtt;
	}
}

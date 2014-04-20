package edu.nettester.task;

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
public class HTTPPinger {
	private String mserver = "";
	private String OUTAG = "NetTester";
	
	public void init(String in_server) {
		this.mserver = in_server;
	}
	
	public float execute() {
		float rtt = 0;
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
					Log.e(OUTAG, e.getMessage());
				}
			}
			httpconn.disconnect();
		} catch (Exception e) {  
            Log.e(OUTAG, e.getMessage());
        }
		return rtt;
	}
}

package edu.nettester.task;

import java.io.BufferedInputStream;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpGet;

import android.net.http.AndroidHttpClient;
import android.util.Log;

/**
 * 
 * @author Weichao
 * @since 14-04-20
 */
public class HTTPDownTP {
	private String mserver = "";
	private String OUTAG = "NetTester"; 
    private int do_size[] = {350,500,750,1000,1500,2000,2500,3000,3500,4000};
	
	public void init(String in_server) {
		this.mserver = in_server;
	}
	
	public float execute() {
		float tp = 0;
		int num_test = do_size.length;
		boolean success = false;
		
		try {
			for(int i=0;i<num_test;i++) {
				AndroidHttpClient client = AndroidHttpClient.newInstance("Dalvik/1.6.0 NetTester of OneProbe Group");
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
						Log.d(OUTAG, String.valueOf(downsize)+":"+String.valueOf(have_read)+":"+String.valueOf(duration));
						tp = (have_read*8)/duration;
						
						if(duration > 10000) {
							break;
						}
						TimeUnit.MILLISECONDS.sleep(200);
					} catch (InterruptedException e){
						Log.e(OUTAG, e.getMessage());
					}
				} else {
					Log.d(OUTAG, "download failed");
				}
				client.close();
			}
			
		} catch (Exception e) {  
            Log.e(OUTAG, e.getMessage());
        }
		
		return tp;
	}
}

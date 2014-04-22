package edu.nettester.task;

import edu.nettester.util.Constant;

import java.io.BufferedInputStream;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;

import android.net.http.AndroidHttpClient;
import android.util.Log;

/**
 * 
 * @author Weichao
 * @since 14-04-20
 */

public class HTTPUpTP implements Constant {
	private String mserver = "";
	private String OUTAG = "NetTester";
	
	public HTTPUpTP(String in_server) {
		this.mserver = in_server;
	}
	
	public float execute() {
		float tp = 0;
		boolean success = false;
		
		try {
			String up_url = this.mserver+"upload.php";
			AndroidHttpClient client = AndroidHttpClient.newInstance("Dalvik/1.6.0 NetTester of OneProbe Group");
			
			String p = prepare_str(250000);
			
			for(int i=0;i<6;i++) {
				int statusCode = 0;
				//long ts = System.nanoTime();
				long upsize = p.length();
				
				ArrayList<NameValuePair> DataList = new ArrayList<NameValuePair>();
				
	            DataList.add(new BasicNameValuePair("data", p));
	            
	            HttpPost httppost = new HttpPost(up_url);
	            httppost.setEntity(new UrlEncodedFormEntity(DataList));
	            
	            long startTime = System.nanoTime();
	            
	            HttpResponse response = client.execute(httppost);
	            
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
						//long downsize = responseEntity.getContentLength();
						
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
						    Log.d(TAG, String.valueOf(upsize)+":"+String.valueOf(have_read)+":"+String.valueOf(duration));
						tp = (upsize*8)/duration;
						
						if(duration > 8000) {
							break;
						}
						TimeUnit.MILLISECONDS.sleep(200);
					} catch (InterruptedException e){
						Log.e(TAG, e.getMessage());
					}
				} else {
					Log.w(TAG, "download failed");
				}
				httppost.abort();
				
				//p = generateNewStr(p, 2^(i+1));
				p = generateNewStr(p, 2);
			}
			
            client.close();
		} catch (Exception e) {  
            Log.e(OUTAG, e.getMessage());
        }
		return tp;
	}
	
	private String prepare_str(int strLen)
    {
        StringBuilder stringbuilder;
        for (stringbuilder = new StringBuilder(strLen);stringbuilder.length() < strLen;stringbuilder.append((long)(1.0D + Math.floor(100000000D * Math.random())))) {
        	
        }
        return stringbuilder.toString();
    }
	
	/**
	 * 
	 * @param basestr
	 * @param num Multiple number
	 * @return
	 */
	private String generateNewStr(String basestr, int num) {
	    int strlen = num * basestr.length();
	    StringBuilder sb = new StringBuilder(strlen);
	    
	    for (int i = 0; i < num; i++)
	        sb.append(basestr);
	    
	    return sb.toString();
	}
}

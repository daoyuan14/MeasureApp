package edu.nettester.task;

//import java.net.CookieStore;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.CookieStore;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import edu.nettester.util.CommonMethod;
import edu.nettester.util.Constant;
import android.util.Log;

/**
 * 
 * @author Weichao
 * @since 14-04-20
 */

public class OPHTTPClient implements Constant {
	private String url = "";
	//private String method = "";
	//private String PHPSESSID = null;
	private DefaultHttpClient client;
	private HttpEntity httpEntity;
	
	/*
	public OPHTTPClient(String in_url) {
		this.url = in_url;
	}*/
	
	public String getPage(String in_url) {
		this.url = in_url;
		String outstr = "";
		
		HttpGet request = new HttpGet(this.url);
		
		if(CommonMethod.PHPSESSID != null) {
			request.setHeader("Cookie", "PHPSESSID="+CommonMethod.PHPSESSID);
		}
		
		try {
			this.client = new DefaultHttpClient();
			HttpResponse response = this.client.execute(request);
			
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				HttpEntity entity = response.getEntity();
				outstr = EntityUtils.toString(entity);
				
				CookieStore mCookieStore = this.client.getCookieStore();
				List<Cookie> cookies = mCookieStore.getCookies();
				for (int i = 0; i < cookies.size(); i++) {
					if ("PHPSESSID".equals(cookies.get(i).getName())) {
						CommonMethod.PHPSESSID = cookies.get(i).getValue();
						if(DEBUG)
							Log.d(TAG, CommonMethod.PHPSESSID);
                        break;
                    }
				}
			}
			
		} catch (Exception e) {  
            Log.e(TAG, e.getMessage());
        }
		request.abort();		
		return outstr;
	}
	
	public String postPage(String in_url, List<NameValuePair> DataList) {
		this.url = in_url;
		String outstr = "";
		
		HttpPost request = new HttpPost(this.url);
		
		if(CommonMethod.PHPSESSID != null) {
			request.setHeader("Cookie", "PHPSESSID="+CommonMethod.PHPSESSID);
		}
		
		try {
			this.httpEntity = new UrlEncodedFormEntity(DataList, HTTP.UTF_8);
			request.setEntity(this.httpEntity);
			this.client = new DefaultHttpClient();
			HttpResponse response = this.client.execute(request);
			
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				HttpEntity entity = response.getEntity();
				outstr = EntityUtils.toString(entity);
				
				CookieStore mCookieStore = this.client.getCookieStore();
				List<Cookie> cookies = mCookieStore.getCookies();
				for (int i = 0; i < cookies.size(); i++) {
					if ("PHPSESSID".equals(cookies.get(i).getName())) {
						CommonMethod.PHPSESSID = cookies.get(i).getValue();
						if(DEBUG)
							Log.d(TAG, CommonMethod.PHPSESSID);
                        break;
                    }
				}
			}
			
		} catch (Exception e) {  
            Log.e(TAG, e.getMessage());
        }
		
		return outstr;
	}
	
	public void destroy() {
		this.client.getConnectionManager().shutdown();
	}
}

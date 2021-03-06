package edu.nettester.task;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;

import edu.nettester.SettingsFragment;
import edu.nettester.util.CommonMethod;
import edu.nettester.util.Constant;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;

public class SyncServerListTask extends AsyncTask<Void, Void, Boolean> implements Constant {
    
    private Context mContext;
    
    public SyncServerListTask(Context context) {
        this.mContext = context;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        Boolean result = false;
        
        AndroidHttpClient client = AndroidHttpClient.newInstance("Dalvik/1.6.0 NetTester of OneProbe Group");
        HttpGet httpGet = new HttpGet(ServerListURL);
        try {
            HttpResponse response = client.execute(httpGet);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                // save file
                HttpEntity entity = response.getEntity();
                InputStream is = entity.getContent();
                CommonMethod.writeFile(ServerListPath, is);
                is.close();
                
                // update server list
                CommonMethod.readServerList(ServerListPath);
                
                result = true;
                
            } else {
                result = false;
            }
        } catch (IOException e) {
            Log.e(TAG, e.toString());
        }
        httpGet.abort();
        client.close();
        return result;
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
            editor.putString(SettingsFragment.KEY_PREF_LASTSERVER, curtime);
            editor.commit();
        }
    }

}

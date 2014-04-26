package edu.nettester;

import edu.nettester.db.MeasureDBHelper;
import edu.nettester.db.MeasureContract.MeasureLog;
import edu.nettester.util.CommonMethod;
import edu.nettester.util.Constant;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

public class ResultActivity extends ActionBarActivity implements Constant {
    
    private Intent myIntent;
    
    private ImageView alog_wifi;
    private TextView alog_time;
    private TextView alog_muid;
    private TextView alog_mserver;
    private TextView alog_DOWN_TP;
    private TextView alog_UP_TP;
    private TextView alog_AVG_RTT;
    private TextView alog_mloc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alog_result);
        
        //enable the app icon as an Up button
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        
        initViews();
        handleIntent();
    }
    
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);      
        setIntent(intent);
        handleIntent();
    }
    
    private void initViews() {
        alog_wifi = (ImageView) findViewById(R.id.alog_wifi);
        alog_time = (TextView) findViewById(R.id.alog_time);
        alog_muid = (TextView) findViewById(R.id.alog_muid);
        alog_mserver = (TextView) findViewById(R.id.alog_mserver);
        alog_DOWN_TP = (TextView) findViewById(R.id.alog_DOWN_TP);
        alog_UP_TP = (TextView) findViewById(R.id.alog_UP_TP);
        alog_AVG_RTT = (TextView) findViewById(R.id.alog_AVG_RTT);
        alog_mloc = (TextView) findViewById(R.id.alog_mloc);
    }
    
    /**
     * handle external Intent and display the result
     */
    private void handleIntent() {
        myIntent = getIntent();
        
        long keyid = myIntent.getLongExtra("keyid", -1);
        if (DEBUG)
            Log.d(TAG, "got keyid: "+keyid);
        if (keyid == -1)
            return;
        
        MeasureDBHelper mDbHelper = new MeasureDBHelper(this);
        Cursor cursor = mDbHelper.fetchOneRow(keyid);
        cursor.moveToFirst();
        String value;
        int columnIndex;
        
        // wifi
        columnIndex = cursor.getColumnIndex(MeasureLog.M_NET_INFO);
        value = cursor.getString(columnIndex);
        if (value.equals("WIFI"))
            alog_wifi.setImageResource(R.drawable.wifi_2);
        else
            alog_wifi.setImageResource(R.drawable.progress);
        
        // time
        columnIndex = cursor.getColumnIndex(MeasureLog.MTIME);
        value = cursor.getString(columnIndex);
        alog_time.setText(CommonMethod.transferTime(value));
        
        // user name
        columnIndex = cursor.getColumnIndex(MeasureLog.MUID);
        value = cursor.getString(columnIndex);
        if (value.equals("0"))
            alog_muid.setText(R.string.pref_login_username_default);
        else
            alog_muid.setText(value);
        
        // target server
        columnIndex = cursor.getColumnIndex(MeasureLog.M_TAR_SERVER);
        value = cursor.getString(columnIndex);
        alog_mserver.setText(value);
        
        // download
        columnIndex = cursor.getColumnIndex(MeasureLog.DOWN_TP);
        value = cursor.getString(columnIndex);
        alog_DOWN_TP.setText(CommonMethod.transferTP(value)+" Mbps");
        
        // upload
        columnIndex = cursor.getColumnIndex(MeasureLog.UP_TP);
        value = cursor.getString(columnIndex);
        alog_UP_TP.setText(CommonMethod.transferTP(value)+" Mbps");
        
        // RTT
        columnIndex = cursor.getColumnIndex(MeasureLog.AVG_RTT);
        value = cursor.getString(columnIndex);
        alog_AVG_RTT.setText(CommonMethod.transferAVG_RTT(value)+" ms");
        
        // client location
        columnIndex = cursor.getColumnIndex(MeasureLog.M_LOC_INFO);
        value = cursor.getString(columnIndex);
        alog_mloc.setText(value);
    }
        
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                Intent upIntent = NavUtils.getParentActivityIntent(this);
                upIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP); //need to still have the same state on the main activity
                
                if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
                    // This activity is NOT part of this app's task, so create a new task
                    // when navigating up, with a synthesized back stack.
                    TaskStackBuilder.create(this)
                            // Add all of this activity's parents to the back stack
                            .addNextIntentWithParentStack(upIntent)
                            // Navigate up to the closest parent
                            .startActivities();
                } else {
                    // This activity is part of this app's task, so simply
                    // navigate up to the logical parent activity.
                    NavUtils.navigateUpTo(this, upIntent);
                }
                return true;
                
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

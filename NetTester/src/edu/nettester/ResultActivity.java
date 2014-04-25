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
import android.widget.TextView;

public class ResultActivity extends ActionBarActivity implements Constant {
    
    private Intent myIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alog_result);
        
        //enable the app icon as an Up button
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        
        handleIntent();
    }
    
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);      
        setIntent(intent);
        handleIntent();
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
        
        TextView alog_time = (TextView) findViewById(R.id.alog_time);
        value = cursor.getString(cursor.getColumnIndex(MeasureLog.MTIME));
        alog_time.setText(CommonMethod.transferTime(value));
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

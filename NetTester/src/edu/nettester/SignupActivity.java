package edu.nettester;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import edu.nettester.task.OPHTTPClient;
import edu.nettester.util.CommonMethod;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SignupActivity extends ActionBarActivity {
    
    private Button btn_signup;
    private EditText edit_email;
    private EditText edit_pwd1;
    private EditText edit_pwd2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        
        //enable the app icon as an Up button
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        
        findViews();
        initButtons();
    }
    
    private void findViews() {
        btn_signup = (Button) findViewById(R.id.btn_signup_signup);
        edit_email = (EditText) findViewById(R.id.edit_email_signup);
        edit_pwd1 = (EditText) findViewById(R.id.edit_pwd1_signup);
        edit_pwd2 = (EditText) findViewById(R.id.edit_pwd2_signup);
    }
    
    private void initButtons() {
        btn_signup.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            	String str_email = edit_email.getText().toString();
                String str_pwd1 = edit_pwd1.getText().toString();
                String str_pwd2 = edit_pwd2.getText().toString();
                
                if (str_email.isEmpty() || str_pwd1.isEmpty() || str_pwd2.isEmpty()) {
                    Toast.makeText(SignupActivity.this, "Email or password should be not null", Toast.LENGTH_SHORT)
                         .show();
                } else if(!str_pwd1.equals(str_pwd2)) {
                	Toast.makeText(SignupActivity.this, "The two passwords should be the same", Toast.LENGTH_SHORT)
                    .show();
                } else {
                    if (sendSignupData(str_email, str_pwd1)) {
                    	
                        // TODO login success
                    }
                }
            }
        });
    }
    
    public boolean sendSignupData(String str_email, String str_pwd) {
    	try {
    		RegProc msignup = new RegProc();
        	String fout = msignup.execute(str_email, str_pwd).get();
        	
        	if(fout.equals("exists")) {
        		Toast.makeText(SignupActivity.this, "The user has already existed", Toast.LENGTH_SHORT)
                .show();
        	} else if(fout.equals("error")) {
        		Toast.makeText(SignupActivity.this, "An error occurs", Toast.LENGTH_SHORT)
                .show();
        	} else {
        		String[] outs = fout.split("\t");
        		CommonMethod.M_UID = outs[0];
        		CommonMethod.M_UNAME = outs[1];
        		CommonMethod.M_HASH = outs[2];
        		
        		//redirect to the main page?
        		
        		
        	}
        	
    	} catch (Exception e) {  
            Log.e(CommonMethod.TAG, e.getMessage());
        }
    	
        return false;
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
    
    private class RegProc extends AsyncTask<String, Void, String> {
    	@Override
		protected String doInBackground(String... params) {
    		String output = "";
    		List<NameValuePair> DataList = new ArrayList<NameValuePair>();
        	
        	DataList.add(new BasicNameValuePair("UserName", params[0]));
        	DataList.add(new BasicNameValuePair("Password", params[1]));
        	
        	OPHTTPClient mclient = new OPHTTPClient();
        	output = mclient.postPage(CommonMethod.reg_url, DataList);
        	
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

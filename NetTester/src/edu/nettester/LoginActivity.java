package edu.nettester;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import edu.nettester.task.OPHTTPClient;
import edu.nettester.util.CommonMethod;
import edu.nettester.util.Constant;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
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

public class LoginActivity extends ActionBarActivity implements Constant {
    
    private Button btn_login;
    private Button btn_signup;
    private EditText edit_email;
    private EditText edit_pwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        
        //enable the app icon as an Up button
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        
        findViews();
        initButtons();
    }
    
    private void findViews() {
        btn_login = (Button) findViewById(R.id.btn_login);
        btn_signup = (Button) findViewById(R.id.btn_signup);
        edit_email = (EditText) findViewById(R.id.edit_email);
        edit_pwd = (EditText) findViewById(R.id.edit_pwd);
    }
    
    private void initButtons() {
        btn_login.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                String str_email = edit_email.getText().toString();
                String str_pwd = edit_pwd.getText().toString();
                
                if (str_email.isEmpty() || str_pwd.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Email or password should be not null", Toast.LENGTH_SHORT)
                         .show();
                } else {
                    if (sendLoginData(str_email, str_pwd)) {
                        Toast.makeText(LoginActivity.this, "Log in is success: "+CommonMethod.M_UNAME, Toast.LENGTH_SHORT)
                             .show();
                        
                        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(view.getContext());
                        Editor editor = sharedPref.edit();
                        editor.putString(SettingsFragment.KEY_PREF_USERNAME, CommonMethod.M_UNAME);
                        editor.putString(PREF_MUID, CommonMethod.M_UID);
                        editor.putString(PREF_MHASH, CommonMethod.M_HASH);
                        editor.commit();
                        
                        openSettingActivity();
                    }
                }
            }
        });
        
        btn_signup.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                openSignUpActivity();
            }
        });
    }
    
    public boolean sendLoginData(String str_email, String str_pwd) {
        boolean result = false;
        
    	try {
    		LoginProc mlogin = new LoginProc();
        	String fout = mlogin.execute(str_email, str_pwd).get();
        	
        	if(fout.equals("nouser") || fout.equals("fail")) {
        		Toast.makeText(LoginActivity.this, "Login fail, please check your account or password", Toast.LENGTH_SHORT)
                .show();
        	} else {
        		String[] outs = fout.split("\t");
        		CommonMethod.M_UID = outs[0];
        		CommonMethod.M_UNAME = outs[1];
        		CommonMethod.M_HASH = outs[2];
        		result = true;
        	}
        	
    	} catch (Exception e) {  
            Log.e(CommonMethod.TAG, e.getMessage());
        }
    	
        return result;
    }
    
    private void openSignUpActivity() {
        Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
        startActivity(intent);
    }
    
    /**
     * TODO
     */
    private void openSettingActivity() {
        Intent intent = new Intent(LoginActivity.this, SettingsActivity.class);
        startActivity(intent);
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
    
    private class LoginProc extends AsyncTask<String, Void, String> {
    	@Override
		protected String doInBackground(String... params) {
    		String output = "";
    		List<NameValuePair> DataList = new ArrayList<NameValuePair>();
        	
        	DataList.add(new BasicNameValuePair("UserName", params[0]));
        	DataList.add(new BasicNameValuePair("Password", params[1]));
        	
        	OPHTTPClient mclient = new OPHTTPClient();
        	output = mclient.postPage(CommonMethod.login_url, DataList);
        	
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

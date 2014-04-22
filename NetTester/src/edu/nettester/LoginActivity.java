package edu.nettester;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends ActionBarActivity {
    
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
            public void onClick(View v) {
                String str_email = edit_email.getText().toString();
                String str_pwd = edit_pwd.getText().toString();
                
                if (str_email.isEmpty() || str_pwd.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Email or password should be not null", Toast.LENGTH_SHORT)
                         .show();
                } else {
                    if (sendLoginData(str_email, str_pwd)) {
                        // TODO login success
                    }
                }
            }
        });
        
        btn_signup.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO
                openSignUpActivity();
            }
        });
    }
    
    public boolean sendLoginData(String str_email, String str_pwd) {
        // TODO
        
        return false;
    }
    
    /**
     * TODO
     */
    private void openSignUpActivity() {
        
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

package edu.nettester;

import edu.nettester.db.MeasureContract.MeasureLog;
import edu.nettester.db.MeasureDBHelper;
import edu.nettester.task.RTTTask;
import edu.nettester.util.CommonMethod;
import edu.nettester.util.Constant;

import android.support.v7.app.ActionBar.Tab;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter.ViewBinder;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Main UI Activity
 * 
 * @author Daoyuan
 */
public class MainActivity extends ActionBarActivity implements Constant {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (DEBUG)
            Log.d(TAG, "enter MainActivity: onCreate");
        super.onCreate(savedInstanceState);
        
        // Notice that setContentView() is not used, because we use the root
        // android.R.id.content as the container for each fragment
        // see ft.add(android.R.id.content, mFragment, mTag);
        //setContentView(R.layout.activity_main);
        
        /*
         * init server list
         */
        try {
            if (!CommonMethod.isFileExists(ServerListPath)) {
                // copy the default server list
                AssetManager am = getAssets();
                InputStream is = am.open(ServerListName);
                CommonMethod.writeFile(ServerListPath, is);
                is.close();
            }
            CommonMethod.readServerList(ServerListPath);
            
        } catch (IOException e) {
            Log.e(TAG, e.toString());
        }
        
        /*
         * init several global variables
         */
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        CommonMethod.M_UNAME = sharedPref.getString(SettingsFragment.KEY_PREF_USERNAME, "Anonymous");
        CommonMethod.M_UID = sharedPref.getString(PREF_MUID, "0");
        CommonMethod.M_HASH = sharedPref.getString(PREF_MHASH, "");
        
        // setup action bar for tabs
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setDisplayShowTitleEnabled(true);

        Tab tab1, tab2;
        tab1 = actionBar.newTab()
                       .setText(R.string.tab_measure)
                       .setTabListener(new TabListener<MeasureFragment>(
                               this, "Measure", MeasureFragment.class));
        tab2 = actionBar.newTab()
                       .setText(R.string.tab_result)
                       .setTabListener(new TabListener<ResultFragment>(
                               this, "Result", ResultFragment.class));
        
        if (savedInstanceState == null) {
            actionBar.addTab(tab1);
            actionBar.addTab(tab2);
        } else {
            actionBar.addTab(tab1, false);
            actionBar.addTab(tab2, false);
        }
        
        if (DEBUG)
            Log.d(TAG, "exit MainActivity: onCreate");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings:
                openSettingsActivity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    /**
     * jump to SettingsActivity
     */
    private void openSettingsActivity() {
        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(intent);
    }
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (DEBUG)
            Log.d(TAG, "enter MainActivity: onSaveInstanceState");
        super.onSaveInstanceState(outState);
        
        int i = getSupportActionBar().getSelectedNavigationIndex();
        outState.putInt(selectedTab, i);
        if (DEBUG)
            Log.d(TAG, "Save tab index: "+i);
        
        if (DEBUG)
            Log.d(TAG, "exit MainActivity: onSaveInstanceState");
    }
    
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if (DEBUG)
            Log.d(TAG, "enter MainActivity: onRestoreInstanceState");
        super.onRestoreInstanceState(savedInstanceState);
        
        if (savedInstanceState != null) {
            int index = savedInstanceState.getInt(selectedTab);
            getSupportActionBar().setSelectedNavigationItem(index);
        }
        
        if (DEBUG)
            Log.d(TAG, "exit MainActivity: onRestoreInstanceState");
    }
    
    /**
     * A fragment for measure tab
     * @author Daoyuan
     */
    public static class MeasureFragment extends Fragment {
        
        private Spinner spinner;
        private Button btn_test;
        private TextView txt_task;
        private ProgressBar mProgress;
        private Button btn_look;
        
        private String target = null;
        
        boolean isVisible = false;
        
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            super.onCreateView(inflater, container, savedInstanceState);
            
            View rootView = inflater.inflate(R.layout.fragment_measure, container, false);
            
            btn_test = (Button) rootView.findViewById(R.id.btn_test);
            spinner = (Spinner) rootView.findViewById(R.id.spinner);
            txt_task = (TextView) rootView.findViewById(R.id.txt_task);
            mProgress = (ProgressBar) rootView.findViewById(R.id.progressBar1);
            btn_look = (Button) rootView.findViewById(R.id.btn_look);
            
            initButtons();
            initSpinner();
            
            return rootView;
        }
        
        @Override
        public void onResume() {
            super.onResume();
            
            displaySpinner();            
            displayViews();
        }
        
        private void displayViews() {
            if (isVisible) {
                txt_task.setVisibility(View.VISIBLE);
                mProgress.setVisibility(View.VISIBLE);
                btn_look.setVisibility(View.VISIBLE);
                
                btn_test.setEnabled(false);
                
            } else {
                txt_task.setVisibility(View.INVISIBLE);
                mProgress.setVisibility(View.INVISIBLE);
                btn_look.setVisibility(View.INVISIBLE);
                
                btn_test.setEnabled(true);
            }
        }
        
        @Override
        public void onSaveInstanceState(Bundle outState) {
            super.onSaveInstanceState(outState);
        }
        
        private void initSpinner() {
            spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
                public void onItemSelected(AdapterView<?> parent, View view, 
                        int position, long id) {
                    target = parent.getItemAtPosition(position).toString();
                    if (false)
                        Toast.makeText(getActivity(), servermap.get(target), Toast.LENGTH_SHORT).show();
                }

                public void onNothingSelected(AdapterView<?> parent) {
                    // Another interface callback
                }
            });
        }
        
        private void displaySpinner() {
            ArrayList<String> arrayList1 = new ArrayList<String>(servermap.keySet());
            
            // Create an ArrayAdapter using the string array and a default spinner layout
            ArrayAdapter<String> adapter = new ArrayAdapter<String> (
                    getActivity(), android.R.layout.simple_spinner_dropdown_item, arrayList1);
            
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            
            // Apply the adapter to the spinner
            spinner.setAdapter(adapter);
        }
        
        private void initButtons() {
            btn_test.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (DEBUG)
                        Toast.makeText(getActivity(), "Prepare to test", Toast.LENGTH_SHORT).show();
                    
                    isVisible = true;
                    CommonMethod.isMeasure = true;
                    getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
                    
                    btn_test.setEnabled(false);
                    new RTTTask(getActivity(), txt_task, mProgress, btn_look).execute(target);
                }
            });
            
            btn_look.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((ActionBarActivity)getActivity()).getSupportActionBar().setSelectedNavigationItem(0);
                    
                    isVisible = false;
                    CommonMethod.isMeasure = false;
                    getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                    
                    ((ActionBarActivity)getActivity()).getSupportActionBar().setSelectedNavigationItem(1);
                }
            });
        }
    }
    
    /**
     * A fragment for result tab
     * @author Daoyuan
     */
    public static class ResultFragment extends Fragment {
        
        private ListView list_result;
        
        private MeasureDBHelper mDbHelper;
        
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            super.onCreateView(inflater, container, savedInstanceState);
            
            View rootView = inflater.inflate(R.layout.fragment_result, container, false);
            
            list_result = (ListView) rootView.findViewById(R.id.list_result);
            
            initListView();
            
            return rootView;
        }
        
        private void initListView() {
            mDbHelper = new MeasureDBHelper(getActivity());
            Cursor cursor = mDbHelper.fetchAllLogs();
            String[] fromColumns = {MeasureLog.M_NET_INFO, MeasureLog.MTIME, MeasureLog.DOWN_TP, MeasureLog.UP_TP, MeasureLog.AVG_RTT};
            //int[] toViews = {R.id.mlog_mid, R.id.mlog_time, R.id.mlog_rtt};
            int[] toViews = {R.id.item1, R.id.item2, R.id.item3, R.id.item4, R.id.item5};
            
            if (DEBUG)
                Log.d(TAG, "Count: " + cursor.getCount());
            
            SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                    getActivity(),
                    //android.R.layout.simple_list_item_1,
                    //R.layout.list_item,
                    R.layout.mylist_item_single_choice,
                    cursor, fromColumns, toViews, 0);
            // set auto transmission
            adapter.setViewBinder(new ViewBinder() {
                public boolean setViewValue(View aView, Cursor aCursor, int aColumnIndex) {
                    if (aColumnIndex == aCursor.getColumnIndex(MeasureLog.M_NET_INFO)) {
                        if (DEBUG)
                            Log.d(TAG, "Enter into M_NET_INFO");
                        
                        String value = aCursor.getString(aColumnIndex);
                        ImageView imageView = (ImageView) aView;
                        
                        if (value.equals("WIFI"))
                            imageView.setImageResource(R.drawable.result_ic_wifi_highlighted);
                        else
                            imageView.setImageResource(R.drawable.result_ic_cell_highlighted);
                        
                        return true;
                    }
                    else if (aColumnIndex == aCursor.getColumnIndex(MeasureLog.MTIME)) {
                        String value = aCursor.getString(aColumnIndex);
                        TextView textView = (TextView) aView;
                        textView.setText(CommonMethod.transferTime(value));
                        return true;
                    }
                    else if (aColumnIndex == aCursor.getColumnIndex(MeasureLog.DOWN_TP)) {
                        String value = aCursor.getString(aColumnIndex);
                        TextView textView = (TextView) aView;
                        textView.setText(CommonMethod.transferTP(value));
                        return true;
                    }
                    else if (aColumnIndex == aCursor.getColumnIndex(MeasureLog.UP_TP)) {
                        String value = aCursor.getString(aColumnIndex);
                        TextView textView = (TextView) aView;
                        textView.setText(CommonMethod.transferTP(value));
                        return true;
                    }
                    else if (aColumnIndex == aCursor.getColumnIndex(MeasureLog.AVG_RTT)) {
                        String value = aCursor.getString(aColumnIndex);
                        TextView textView = (TextView) aView;
                        textView.setText(CommonMethod.transferAVG_RTT(value));
                        return true;
                    }
                    return false;
                }
            });
            
            list_result.setAdapter(adapter);
            
            list_result.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // Get the cursor, positioned to the corresponding row in the result set
                    Cursor cursor = (Cursor) list_result.getItemAtPosition(position);
                    
                    new ResultDialogFragment(getActivity(), cursor, list_result).show(getActivity().getSupportFragmentManager(), "ResultDialog");
                }
            });
        }
        
        @Override
        public void onSaveInstanceState(Bundle outState) {
            super.onSaveInstanceState(outState);
        }

        @Override
        public void onResume() {
            super.onResume();
            
            //new DisplayResult(getActivity(), text_result).execute();
        }
        
    }
    
    /**
     * @author Daoyuan
     * @param <T>
     * @see http://developer.android.com/guide/topics/ui/actionbar.html#Tabs
     */
    public static class TabListener<T extends Fragment> implements ActionBar.TabListener {
        private Fragment mFragment;
        private final Activity mActivity;
        private final String mTag;
        private final Class<T> mClass;
        
        /** Constructor used each time a new tab is created.
         * @param activity  The host Activity, used to instantiate the fragment
         * @param tag  The identifier tag for the fragment
         * @param clz  The fragment's Class, used to instantiate the fragment
         */
       public TabListener(Activity activity, String tag, Class<T> clz) {
           mActivity = activity;
           mTag = tag;
           mClass = clz;
       }

        @Override
        public void onTabSelected(Tab tab, FragmentTransaction ft) {
            if (CommonMethod.isMeasure)
                return;
            
            // Check if the fragment is already initialized
            if (mFragment == null) {
                // If not, instantiate and add it to the activity
                mFragment = Fragment.instantiate(mActivity, mClass.getName());
                ft.replace(android.R.id.content, mFragment, mTag); //http://code.google.com/p/android/issues/detail?id=58602#c30
            } else {
                // If it exists, simply attach it in order to show it
                ft.attach(mFragment);
            }
        }

        @Override
        public void onTabUnselected(Tab tab, FragmentTransaction ft) {
            if (CommonMethod.isMeasure)
                return;
            
            if (mFragment != null) {
                // Detach the fragment, because another one is being attached
                ft.detach(mFragment);
            }
        }
        
        @Override
        public void onTabReselected(Tab tab, FragmentTransaction ft) {
            // User selected the already selected tab. Usually do nothing.
        }
        
    }

}

package edu.nettester;

import edu.nettester.task.RTTTask;

import android.support.v7.app.ActionBar.Tab;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Main UI Activity
 * 
 * @author Daoyuan
 */
public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Notice that setContentView() is not used, because we use the root
        // android.R.id.content as the container for each fragment
        //setContentView(R.layout.activity_main);
        
        // setup action bar for tabs
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setDisplayShowTitleEnabled(true);

        Tab tab;
        tab = actionBar.newTab()
                       .setText(R.string.tab_measure)
                       .setTabListener(new TabListener<MeasureFragment>(
                               this, "Measure", MeasureFragment.class));
        actionBar.addTab(tab);

        tab = actionBar.newTab()
                       .setText(R.string.tab_result)
                       .setTabListener(new TabListener<ResultFragment>(
                               this, "Result", ResultFragment.class));
        actionBar.addTab(tab);
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
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    /**
     * A fragment for measure tab
     * @author Daoyuan
     */
    public static class MeasureFragment extends Fragment {
        private Button btn_test;
        
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_measure, container, false);
            
            btn_test = (Button) rootView.findViewById(R.id.btn_test);
            initButtons();
            
            return rootView;
        }
        
        private void initButtons() {
            btn_test.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getActivity(), "Prepare to test", Toast.LENGTH_SHORT)
                         .show();
                    
                    new RTTTask(getActivity()).execute();
                }
            });
        }
    }
    
    /**
     * A fragment for result tab
     * @author Daoyuan
     */
    public static class ResultFragment extends Fragment {
        private TextView text_result;
        private Button btn_display;
        
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_result, container, false);
            
            text_result = (TextView) rootView.findViewById(R.id.text_result);
            btn_display = (Button) rootView.findViewById(R.id.btn_display);
            initButtons();
            
            return rootView;
        }
        
        private void initButtons() {
            btn_display.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getActivity(), "Prepare to output", Toast.LENGTH_SHORT)
                         .show();
                    
                    new DisplayResult(getActivity()).execute(text_result);
                }
            });
        }

//        @Override
//        public void onResume() {
//            super.onResume();
//            
//            new DisplayResult(getActivity()).execute(text_result);
//        }
        
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
            // Check if the fragment is already initialized
            if (mFragment == null) {
                // If not, instantiate and add it to the activity
                mFragment = Fragment.instantiate(mActivity, mClass.getName());
                ft.add(android.R.id.content, mFragment, mTag);
            } else {
                // If it exists, simply attach it in order to show it
                ft.attach(mFragment);
            }
        }

        @Override
        public void onTabUnselected(Tab tab, FragmentTransaction ft) {
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

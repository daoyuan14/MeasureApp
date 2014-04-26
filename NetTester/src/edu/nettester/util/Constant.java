package edu.nettester.util;

import java.util.HashMap;
import java.util.LinkedHashMap;

public interface Constant {
    
    public static final boolean DEBUG = true;
    
    public static final String TAG = "NetTester";
    
    public static final String PREF_MUID = "muid";
    
    public static final String PREF_MHASH = "mhash";
    
    public static final String APP_DIR = "/data/data/edu.nettester/";
    
    public static final String ServerListName = "serverlist.txt";
    
    public static final String ServerListPath = APP_DIR + ServerListName;
    
    public static final String ServerListURL = "http://158.132.255.76:25001/mserver/serverlist.txt";
    
    public static final String selectedTab = "selectedTabIndex";
    
    public static HashMap<String, String> servermap = new LinkedHashMap<String, String>();
    
    public static final String login_url = "http://158.132.255.76:25001/mserver/login_process.php";
    
    public static final String logout_url = "http://158.132.255.76:25001/mserver/logout.php";
    
    public static final String reg_url = "http://158.132.255.76:25001/mserver/reg_insert.php";
    
    public static final String updata_url = "http://158.132.255.76:25001/mserver/updata.php";
    
    public static final String deldata_url = "http://158.132.255.76:25001/mserver/deldata.php";
    
    public static final String delcheck_url = "http://158.132.255.76:25001/mserver/delcheck.php";
    
    public static final int Cat_RTT = 1;
    public static final int Cat_DOWNLOAD = 2;
    public static final int Cat_UPLOAD = 3;
    
}

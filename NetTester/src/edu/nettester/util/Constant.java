package edu.nettester.util;

import java.util.HashMap;
import java.util.LinkedHashMap;

public interface Constant {
    
    public static final boolean DEBUG = true;
    
    public static final String TAG = "NetTester";
    
    public static final String ServerListName = "serverlist.txt";
    
    public static final String selectedTab = "selectedTabIndex";
    
    public static HashMap<String, String> servermap = new LinkedHashMap<String, String>();
    
    public static final String login_url = "http://158.132.255.76:25001/mserver/login_process.php";
    
    public static final String logout_url = "http://158.132.255.76:25001/mserver/logout.php";
    
    public static final String reg_url = "http://158.132.255.76:25001/mserver/reg_insert.php";
    
    public static final String updata_url = "http://158.132.255.76:25001/mserver/updata.php";
}

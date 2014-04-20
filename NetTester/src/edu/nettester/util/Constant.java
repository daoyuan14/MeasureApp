package edu.nettester.util;

import java.util.HashMap;
import java.util.LinkedHashMap;

public interface Constant {
    
    public static final boolean DEBUG = true;
    
    public static final String TAG = "NetTester";
    
    public static final String ServerListName = "serverlist.txt";
    
    public static final String selectedTab = "selectedTabIndex";
    
    public static HashMap<String, String> servermap = new LinkedHashMap<String, String>();

}

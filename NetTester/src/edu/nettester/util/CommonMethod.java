package edu.nettester.util;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Formatter;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Common static methods
 * 
 * @author Daoyuan
 * @since 14-04-20
 */
public class CommonMethod implements Constant {
	
    public static String PHPSESSID;
    public static String M_UID;
    public static String M_UNAME;
    public static String M_HASH;
    
    private static void readServerList(BufferedReader br) throws IOException {
        //BufferedReader br = new BufferedReader(new InputStreamReader(is));
        
        HashMap<String, String> localmap = new LinkedHashMap<String, String>();
        
        while (true) {
            String line = br.readLine();
            if (line == null)
                break;
            
            String[] twos = line.split("\t");
            localmap.put(twos[0], twos[1]);
        }
        
        if (!localmap.isEmpty()) {
            servermap.clear();
            servermap.putAll(localmap);
        }
    }
    
    public static void readServerList(String filepath) throws FileNotFoundException, IOException {
        FileReader fr = new FileReader(filepath);
        BufferedReader br = new BufferedReader(fr);
        readServerList(br);
        br.close();
        fr.close();
    }
    
    public static boolean isFileExists(String filepath) {
        File file = new File(filepath);
        return file.exists();
    }
    
    public static void writeFile(String filepath, InputStream is) throws IOException {
        File file = new File(filepath);
        BufferedWriter bw = new BufferedWriter(new FileWriter(file));
        
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        
        String line;
        while ((line = br.readLine()) != null) {
            bw.write(line + "\n");
        }
        
        bw.close();
        br.close();
    }
    
    public static String transferTP(String oldValue) {
        String newValue = oldValue;
        float ov = Float.valueOf(oldValue)/1000;
        Formatter fmt = new Formatter();
        
        if(ov>=100) {
        	newValue = String.valueOf(Math.floor(ov+0.5));
        } else if(ov>10) {
        	newValue = fmt.format("%.1f", ov).toString();
        } else {
        	newValue = fmt.format("%.2f", ov).toString();
        }
        
        return newValue;
    }
        
    public static String transferAVG_RTT(String oldValue) {
        String newValue = oldValue;
        float ov = Float.valueOf(oldValue);
        newValue = String.valueOf(Math.floor(ov+0.5));
        return newValue;
    }
    
    public static String transferTime(String timestamp) {
    	String newValue = "";
    	long ts = Long.valueOf(timestamp);
    	SimpleDateFormat df = new SimpleDateFormat("MMM d\nHH:mm");
    	newValue = df.format(ts);    	
    	return newValue;
    }

}

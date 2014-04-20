package edu.nettester.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Common static methods
 * 
 * @author Daoyuan
 * @since 14-04-20
 */
public class CommonMethod implements Constant {
    
    public static void readServerList(InputStream is) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        
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
        
        br.close();
    }

}

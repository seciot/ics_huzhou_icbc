package com.jadic.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

public class KKConfig {
	private Properties properties;
    private FileInputStream inputStream;
    private String configFileName;

    public KKConfig(String configFilePath) {
        this.properties = new Properties();
        this.configFileName = System.getProperty("user.dir") + System.getProperty("file.separator") + configFilePath;
        try {
            this.inputStream = new FileInputStream(configFileName);
            this.properties.load(this.inputStream);
        } catch (FileNotFoundException ex) {
            KKLog.error("Can't find config file[" + configFilePath + "]");
        } catch (IOException ex) {
        	KKLog.error("Read config file IOException");
        } finally {
        	try {
        		if (this.inputStream != null)
        			this.inputStream.close();
			} catch (IOException e) {
			}
        }
    }

    public String getStrValue(String key) {
        if (this.properties.containsKey(key)) {
            return this.properties.getProperty(key, "").trim();
        }
        return "";
    }

    public int getIntValue(String key) {
        if (this.properties.containsKey(key)) {
            try {
                return Integer.parseInt(this.properties.getProperty(key, "0"));
            } catch (Exception e) {
                return 0;
            }
        }
        return 0;
    }
    
    public boolean writeValue(Map<String, String> params) {
    	FileOutputStream fos;
    	try {
			fos = new FileOutputStream(this.configFileName);
			Set<Map.Entry<String, String>> paramSet = params.entrySet();
			Iterator<Entry<String, String>> ite = paramSet.iterator();
			while (ite.hasNext()) {
				Entry<String, String> param = ite.next();
				this.properties.setProperty(param.getKey(), param.getValue());
			}
			this.properties.store(fos, "set");
			return true;
		} catch (FileNotFoundException e1) {
			KKLog.error("write to config file err: " + this.configFileName + " not found");
    	} catch (IOException e) {
    		KKLog.error("Write to config file err: IOException");
		}
    	return false;
    }
}

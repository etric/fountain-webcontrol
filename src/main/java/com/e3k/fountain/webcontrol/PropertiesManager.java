package com.e3k.fountain.webcontrol;

import java.io.*;
import java.util.Properties;

/**
 *
 * @author Alexander 'etric' Khamylov
 */
public enum PropertiesManager {

    ONE;
    
    public static final String PATH_TO_DATA = "path_to_data";
    
    public static final String BITS_INVERSE = "bits_inverse";
    public static final String BYTES_IN_PACKET = "bytes_in_packet";
    public static final String DELAY_BETWEEN_PACKETS_MILLIS = "delay_between_packets_millis";
    public static final String DELAY_BETWEEN_SONGS_SECS = "delay_between_songs_secs";
    public static final String DELAY_BETWEEN_BITS_US = "delay_between_bits_us";
    public static final String TIME_START = "time_start";
    public static final String TIME_END = "time_end";
    
    private final Properties props;
    private final File propsFile;

    private PropertiesManager() {
        props = new Properties();
        propsFile = new File("config.properties");        
        loadProperties();
    }
    
    public PropertiesManager setProp(String property, String value) {
        props.setProperty(property, value);
        Static.debug("SET PROPERTY: "+property+" = "+value);
        return this;
    }

    public String getProp(String property, String defaultValue) {
        String value = props.getProperty(property);
        Static.debug("GET PROPERTY: "+property+" = "+value);
        if (value == null) return defaultValue;
        return value;
    }
    
    public String getProp(String property) {
        return getProp(property, null);
    }
    
    public boolean storeProperties() {
        boolean result = false;
        OutputStream propsOut = null;
        try {
            propsOut = new FileOutputStream(propsFile);
            props.store(propsOut, "ScheduledComWriter configuration file");
            result = true;
            Static.debug("Properties successfully stored");
        } catch (IOException ex) {
            Static.debug(ex);
        } finally {
            if (propsOut != null) {
                try {
                    propsOut.close();
                } catch (IOException ex) {
                    Static.debug(ex);
                }
            }
        }
        return result;
    }
    
    private void loadProperties() {
        if (propsFile.exists()) {
            InputStream propsIn = null;
            try {
                propsIn = new FileInputStream(propsFile);
                props.load(propsIn);
                propsIn.close();
                Static.debug("Properties successfully loaded");
            } catch (IOException ex) {
                Static.debug(ex);
            } finally {
                if (propsIn != null) {
                    try {
                        propsIn.close();
                    } catch (IOException ex) {
                        Static.debug(ex);
                    }
                }
            }
        } else {
            try {
                propsFile.createNewFile();
            } catch (IOException ex) {
                Static.debug(ex);
            }
        }
    }
}
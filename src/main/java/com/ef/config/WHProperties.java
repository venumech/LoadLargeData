package com.ef.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Venu
 */
public class WHProperties {

    public static final String MYSQL_URL;
    public static final String MYSQL_DRIVER;
    public static final String MYSQL_USER;
    public static final String MYSQL_PASSWORD;
    public static final String SERVER_LOG_FILE_NAME;
    private static Map<String,String> propsMap = new HashMap<>();

    static {
        Properties properties = new Properties();
        InputStream in;
        try {
            in = WHProperties.class.getClassLoader().getResourceAsStream("application.properties");
            properties.load(in);
            in.close();
            
        } catch ( IOException ex) {
            Logger.getLogger(WHProperties.class.getName()).log(Level.SEVERE, null, ex);

        } 
        String config_path = properties.getProperty("config.file.path");

                Properties properties1 = new Properties();
        InputStream in1;
        try {
            in1 = new FileInputStream(config_path);
            properties1.load(in1);
            in1.close();
            
        } catch ( IOException ex) {
            Logger.getLogger(WHProperties.class.getName()).log(Level.SEVERE, null, ex);

        } 
        
        MYSQL_DRIVER = properties1.getProperty("mysql.jdbc.driver");
        MYSQL_URL = properties1.getProperty("mysql.jdbc.url");
        MYSQL_USER = properties1.getProperty("mysql.jdbc.username");
        MYSQL_PASSWORD = properties1.getProperty("mysql.jdbc.password");
        SERVER_LOG_FILE_NAME = properties1.getProperty("server.data.log.file.name");

        
    }
    
    public WHProperties(){}
    
    public static Map<String,String> props(String config_path){
                Properties properties1 = new Properties();
        InputStream in1;
        try {
            in1 = new FileInputStream(config_path);
            properties1.load(in1);
            in1.close();
            
        } catch ( IOException ex) {
            Logger.getLogger(WHProperties.class.getName()).log(Level.SEVERE, null, ex);

        } 
        //local vars hiding the fields
        String MYSQL_DRIVER = properties1.getProperty("mysql.jdbc.driver");
        String MYSQL_URL = properties1.getProperty("mysql.jdbc.url");
        String MYSQL_USER = properties1.getProperty("mysql.jdbc.username");
        String MYSQL_PASSWORD = properties1.getProperty("mysql.jdbc.password");
        String SERVER_LOG_FILE_NAME = properties1.getProperty("server.data.log.file.name");
        
        System.out.println("SERVER_LOG_FILE_NAME = " + SERVER_LOG_FILE_NAME);
        propsMap.put("MYSQL_DRIVER", MYSQL_DRIVER);
        propsMap.put("MYSQL_URL", MYSQL_URL);
        propsMap.put("MYSQL_USER", MYSQL_USER);
        propsMap.put("MYSQL_PASSWORD", MYSQL_PASSWORD);
        propsMap.put("SERVER_LOG_FILE_NAME", SERVER_LOG_FILE_NAME);
        
        return propsMap;

    }

    
}

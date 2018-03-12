/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ef.config;

/**
 *
 * @author Venu
 */
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.DataSource;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
 
@Configuration
@ComponentScan(basePackages = "com.ef")
@PropertySource(value = { "classpath:application.properties" })
public class ApplicationConfig {
 
    private Map<String,String> props;
    
    @Autowired
    private Environment env;
 
    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        String config_path = env.getRequiredProperty("config.file.path");
        props = WHProperties.props(config_path);
        dataSource.setDriverClassName(props.get("MYSQL_DRIVER"));
        dataSource.setUrl(props.get("MYSQL_URL"));
        dataSource.setUsername(props.get("MYSQL_USER"));
        dataSource.setPassword(props.get("MYSQL_PASSWORD"));
        return dataSource;
    }
 
    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.setResultsMapCaseInsensitive(true);
        return jdbcTemplate;
    }
 
    @Bean
    public File ServerLogFile(){
       String fileName =  props.get("SERVER_LOG_FILE_NAME");
        File file = new File(fileName);
        if ( file.exists()) {
            System.out.println("found File: " + fileName);
        } else {
            System.out.println("Not found File: " + fileName);
            System.out.println("Exiting. The tool did not find the server log file, 'access.log'");
            System.exit(-1);
        }
        
        return file;
    }
   
}
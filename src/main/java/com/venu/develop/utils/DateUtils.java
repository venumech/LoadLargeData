/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.venu.develop.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author Venu
 */
public class DateUtils {
    
   public static String getTimeStampString(LocalDateTime localDateTime){
       String datepattern1 = "yyyy-MM-dd HH:mm:ss.SSS";
                     DateTimeFormatter formatter = DateTimeFormatter.ofPattern(datepattern1);

       return localDateTime.format(formatter);
       
   }
    
    /*
    expected value: 2017-01-01 00:00:11.763 or 2017-01-01.00:00:11
     */
    public static LocalDateTime formatDateValue(String value) throws java.time.format.DateTimeParseException {

        LocalDateTime localDateTime = null;
        String datepattern1 = "yyyy-MM-dd HH:mm:ss.SSS"; // for date values - text file validations
        String datepattern2 = "yyyy-MM-dd.HH:mm:ss"; // for user input validations
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(datepattern1);
        try {
            localDateTime = LocalDateTime.parse(value, formatter);
        } catch (java.time.format.DateTimeParseException e1) {

            //try {
                formatter = DateTimeFormatter.ofPattern(datepattern2);
                localDateTime = LocalDateTime.parse(value, formatter);
            //} catch (java.time.format.DateTimeParseException e2) {

            //}
        }
        return localDateTime;
    }
}

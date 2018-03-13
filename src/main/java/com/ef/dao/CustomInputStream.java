/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ef.dao;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CustomInputStream extends InputStream {
	BufferedReader bufferedReader;
    private byte[] currentRecord;
    private int currentBufferIndex;

    //public CustomInputStream(ByteArrayInputStream bufferedReader) throws IOException {
    public CustomInputStream(BufferedReader bufferedReader) throws IOException {
        this.bufferedReader = bufferedReader;
        //skip headers
        //bufferedReader.readLine();
        getNextRecord();
    }

    private void getNextRecord() throws IOException {
        String csvLineString = bufferedReader.readLine();
        StringBuilder builder = new StringBuilder();
	//LocalDateTime timestamp;
	String timestamp;
	String ipAddress;
	String method;
	Integer status;
	String comments;
        
        String[] csvLine;
        if(csvLineString!=null){
            csvLine = csvLineString.split("\\|");
        }else{
            currentRecord=null;
            return;
        }
        currentBufferIndex=0;
         //timestamp = formatDateValue(csvLine[0]);
         timestamp = csvLine[0];
        ipAddress = csvLine[1];
        status = Integer.valueOf(csvLine[3]);
        method = csvLine[2];
        comments = csvLine[4];
        
        builder.append(String.format("%s,%s,%s,%d,%s", timestamp, ipAddress,method,status,comments));
        //builder.append(csvLineString);
        builder.append("\n");
        currentRecord = builder.toString().getBytes();
        //System.out.printf("%s,%s,%s,%d,%s\n", timestamp, ipAddress,method,status,comments);
    }

    @Override 
    public int read() throws IOException {
        if (currentRecord == null) {
            return -1;
        }
        if (currentBufferIndex == currentRecord.length) {
            getNextRecord();
            currentBufferIndex = 0;
            if (currentRecord == null) {
                return -1;
            }
        }
        int retValue = currentRecord[currentBufferIndex];
        currentBufferIndex++;
        return retValue;
    }
    
        /*
    expected value: 2017-01-01 00:00:11.763 or 2017-01-01.00:00:11
    */
    private LocalDateTime formatDateValue(String value) {

        LocalDateTime localDateTime = null;
        String datepattern1 = "yyyy-MM-dd HH:mm:ss.SSS"; // for date values - text file validations
        String datepattern2 = "yyyy-MM-dd.HH:mm:ss"; // for user input validations
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(datepattern1);
        try {
            localDateTime = LocalDateTime.parse(value, formatter);
        } catch (java.time.format.DateTimeParseException e1) {

            try {
                formatter = DateTimeFormatter.ofPattern(datepattern2);
                localDateTime = LocalDateTime.parse(value, formatter);
            } catch (java.time.format.DateTimeParseException e2) {

            }
        }
        return localDateTime;
    }

}

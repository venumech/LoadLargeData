/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ef.service;

import com.ef.Parser;
import com.ef.Record;
import com.ef.dao.ServerLogDao;
import static com.venu.develop.utils.DateUtils.formatDateValue;
import static com.venu.develop.utils.DateUtils.getTimeStampString;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Venu
 */
@Service("ParserService")
public class ParserServiceImpl implements ParserService{
private Map<String, List<Record>> recordsMap = new HashMap<>();


//private LocalDateTime startDate;
//private LocalDateTime endDate;
//private Integer threshold;


@Autowired
File serverLogFile;

    @Autowired
    ServerLogDao serverLogDao;

    private static final String LINE_SEPARATOR = System.getProperty("line.separator");
    private final Logger logger = LoggerFactory.getLogger(ParserServiceImpl.class);

    @Override
    /**
     * save the data into Mysql Table
     *
     */
    public Integer saveDB(LocalDateTime startDate,LocalDateTime endDate,Integer threshold) throws SQLException, IOException {
        int successfulRecordCount=0;
        
        BufferedReader bR = checkQualifiesforDBStore(startDate, endDate, threshold);
        int recordCount = serverLogDao.saveDB(bR);
        
        logger.info("Actual Total rows inserted into the Table, SERVER_LOG=" + recordCount);
        
        return successfulRecordCount;
    }
    
    
    /*
    check if  there are enough  hits received by the server for any of the IPAddresses
     */
    private BufferedReader checkQualifiesforDBStore(LocalDateTime startDate,LocalDateTime endDate,Integer threshold) {
        Map<String, List<Record>> recordsMap = getRecordsMap();
        StringBuilder builder = new StringBuilder();
        Set<String> ips = new TreeSet<String>();
        String ipAddress = "";
        String method = "";
        Integer status = 0;
        String comments = "";

        parse( startDate, endDate, threshold);
        
        for (String ip : recordsMap.keySet()) {
            // search  for ip value satisfying the threshold count
            List<Record> records = recordsMap.get(ip);
            // System.out.println("IP ADDRESS = " + ip + ";\t Filtered Data Count= " + recordsMap.get(ip).size());

            if (records.size() >= threshold) {
                for (Record r : records) {
                    LocalDateTime timestamp = r.getTimestamp();
                    ipAddress = r.getIpAddress();
                    method = r.getMethod();
                    status = r.getStatus();
                    comments = r.getComments();
                    String record = String.format("%s|%s|%s|%d|%s", getTimeStampString(timestamp), ipAddress, method, status, comments);
                    builder.append(record);
                    builder.append(LINE_SEPARATOR);
                    ips.add(ip);
                }
            }
        }

        logger.info(String.format("Below Ip addresses qualifies for this Minimum hit count:%d\n", threshold));
        ips.forEach(ip -> {
            System.out.println("\t" + ip);
        });

        //convert StringBuilder to BufferedReader
        Reader inputString = new StringReader(builder.toString());
        BufferedReader bufferedReader = new BufferedReader(inputString);
        return bufferedReader;
    }

    
    /* 
    *Read the server log file and save the data to the collections.
    this collections data can be queried against the user's query filters provided in the 
    command line args
     */
    private void parse(LocalDateTime startDate,LocalDateTime endDate,Integer threshold) {
        //File file = new File("C:\\assignment\\WalletHub\\Java_MySQL_Test\\access.log");
        //File file = new File(SERVER_LOG_FILE_NAME);
        logger.info("Now parsing the log file located at \""+serverLogFile.getAbsolutePath() +"\".");
        try (Scanner scanner = new Scanner(serverLogFile);) {
            while (scanner.hasNextLine()) {
                //System.out.println(scanner.nextLine());
                fetchRecords(scanner.nextLine(), startDate, endDate, threshold);
            }
            scanner.close();

        } catch (FileNotFoundException e) {
            //e.printStackTrace();
            logger.error("Server log File not found", e.getMessage());
        }
    }

    private Map<String, List<Record>> fetchRecords(String line, LocalDateTime startDate,LocalDateTime endDate,Integer threshold) {

        String[] tokens = line.split("\\|");
        Map<String, List<Record>> recordsMap = getRecordsMap();

        LocalDateTime date = formatDateValue(tokens[0]);
        String ipAddress = tokens[1];
        String method = tokens[2];
        Integer status = Integer.valueOf(tokens[3]);
        String comments = tokens[4];

        //consider the data if the date is equal to either of startdate or end endDate.
        //Or if the date is in between the startDate and EndDate        
        if ((date.isEqual(startDate) || date.isEqual(endDate))
                || (date.isAfter(startDate)
                && date.isBefore(endDate))) {
            Record item = new Record();
            item.setTimestamp(date);
            item.setIpAddress(ipAddress);
            item.setMethod(method.replaceAll("\"", ""));
            item.setComments(comments.replaceAll("\"", ""));
            item.setStatus(status);

            boolean addFl = false;
            for (String ip : recordsMap.keySet()) {
                // search  for value
                if (ip.equalsIgnoreCase(ipAddress)) {
                    addFl = recordsMap.get(ip).add(item);
                }

                //System.out.println("Key = " + ip + ", data size= " + recordsMap.get(ip).size());
            }

            if (!addFl) {
                List<Record> l = new ArrayList<>();
                l.add(item);
                recordsMap.put(ipAddress, l);
            }
        }

        return recordsMap;
    }

 
    /**
     * @return the recordMap
     */
    public Map<String, List<Record>> getRecordsMap() {
        return recordsMap;
    }

    /**
     * @param recordMap the recordMap to set
     */
    public void setRecordsMap(Map<String, List<Record>> recordMap) {
        this.recordsMap = recordMap;
    }

}

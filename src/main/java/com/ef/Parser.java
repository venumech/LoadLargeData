/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ef;

/**
 *
 * @author Venu
 */
import static com.venu.develop.utils.DateUtils.formatDateValue;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.ef.config.ApplicationConfig;
import com.ef.service.ParserService;
import java.util.logging.Level;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

public class Parser {

    private static final String JDBC_CONN_STRING = "jdbc:mysql://localhost:3306/venu?user=root&password=venu";
    private static final String USAGE_HINT
            = "  Usage: java -cp \"parser.jar\" com.ef.Parser --startDate=2017-01-01.13:00:00 --duration=hourly --threshold=100 \n"
            + "  The tool takes \"startDate\", \"duration\" and \"threshold\" as command line arguments. \n"
            + "  \"startDate\" is of \"yyyy-MM-dd.HH:mm:ss\" format, \n"
            + "  \"duration\" can take only \"hourly\", \"daily\" as inputs and\n"
            + "  \"threshold\" can be an integer.\n";
    private static LocalDateTime startDate;
    private static LocalDateTime endDate;
    private static Integer threshold;

    private static final String START_DATE_STR = "--startDate";
    private static final String THRESHOLD_STR = "--threshold";
    private static final String DURATION_STR = "--duration";
    private static final String LINE_SEPARATOR = System.getProperty("line.separator");
    private Map<String, List<Record>> recordsMap = new HashMap<>();

    private final Logger logger = LoggerFactory.getLogger(Parser.class);

    public static void main(String[] args) throws IOException {
        AbstractApplicationContext context = new AnnotationConfigApplicationContext(ApplicationConfig.class);
        ParserService parserService = (ParserService) context.getBean("ParserService");
        
        Parser loadMysql = new Parser();
        //1.validate if the correct command line args are passed
        loadMysql.validateUserQuery(args);

        //2. check if the file exists. default location is at the same dir where the jar file exists.
        // Now start parsing the Data from the input text file
        try {
            parserService.saveDB(getStartDate(), getEndDate(), getThreshold());
        } catch (SQLException ex) {
            java.util.logging.Logger.getLogger(Parser.class.getName()).log(Level.SEVERE, null, ex);
        }

    }



    /*
     * validate the correct command line args are passed
     */
    private void validateUserQuery(String[] args) {

        //"Usage:" User expected to provide the required data query parmeters with valid values.
        System.out.println("args.length =" + args.length);
        if (args.length != 3) {
            showConsoleError();
            System.exit(-1);
        } else {
            logger.info("User provided all the required params. Now validating them.");
        }

        String arg1 = args[0];
        String arg2 = args[1];
        String arg3 = args[2];
        String[] a1 = arg1.split("=");
        String[] a2 = arg2.split("=");
        String[] a3 = arg3.split("=");

        if (!(a1.length == 2
                && a2.length == 2
                && a3.length == 2)) {
            showConsoleError();
            System.exit(-1);
        }
        if (!(a1[0].equals(START_DATE_STR)
                && a2[0].equals(DURATION_STR)
                && a3[0].equals(THRESHOLD_STR))) {
            showConsoleError();
            System.exit(-1);
        }

        logger.info("1st param =" + a1[0] + "; value=" + a1[1]); //--startDate=2017-01-01.13:00:00
        logger.info("2nd param=" + a2[0] + ";  value=" + a2[1]); // --duration=hourly
        logger.info("3nd param=" + a3[0] + ";  value=" + a3[1]); //--threshold=100

        try {
            setStartDate(formatDateValue(a1[1]));
            setEndDate(a2[1]);
            setThreshold(Integer.valueOf(a3[1]));
        } catch (java.time.format.DateTimeParseException | UnsupportedOperationException e) {
            showConsoleError();
            System.err.print(e.getMessage());
            System.exit(-1);
        }

        logger.info("User provided all the required params with valide data.");

        logger.info(String.format("startDate: rawDate value='%s'; formatted Data value=%s\n", a1[1], getStartDate()));
        logger.info(String.format("endDate: rawDate value='%s'; formatted Data value=%s\n", a2[1], getEndDate()));
        logger.info(String.format("threshold: raw threshold value='%s'; threshold value=%d\n", a3[1], getThreshold()));
    }
    
   private static void createFileUsingFileClass() throws IOException {
        File file = new File("..\\testFile11.txt");

        //Create the file
        if (file.createNewFile()) {
            System.out.println("File is created!");
        } else {
            System.out.println("File already exists.");
        }

        //Write Content
        FileWriter writer = new FileWriter(file);
        writer.write("Test data");
        writer.close();
    }

    /**
     * @return the startDate
     */
    public static LocalDateTime getStartDate() {
        return startDate;
    }

    /**
     * @param aStartDate the startDate to set
     */
    public static void setStartDate(LocalDateTime aStartDate) {
        startDate = aStartDate;
    }

    /**
     * @return the endDate
     */
    public static LocalDateTime getEndDate() {
        return endDate;
    }

    /**
     * @param aEndDate the endDate to set
     */
    public static void setEndDate(String value) {
        LocalDateTime aEndDate = getStartDate();
        //Threshold t = Threshold.DAILY;
        int hours = 0;
        switch (value) {
            case "daily":
                hours = 24;
                break;
            case "hourly":
                hours = 1;
                break;
            case "weekly":
                hours = 24 * 7;
                break;
            default:
                throw new UnsupportedOperationException("--duration value "+ value +" Not supported yet.");
        }

        endDate = aEndDate.plusHours(hours);
    }

    /**
     * @return the threshold
     */
    public static Integer getThreshold() {
        return threshold;
    }

    /**
     * @param aThreshold the threshold to set
     */
    public static void setThreshold(Integer aThreshold) {
        if (aThreshold < 1) throw new UnsupportedOperationException("--duration: Negative or zero values not supported");

        threshold = aThreshold;
    }

    /**
     * @return the recordsMap
     */
    public Map<String, List<Record>> getRecordsMap() {
        return recordsMap;
    }

    /**
     * @param recordsMap the recordsMap to set
     */
    public void setRecordsMap(Map<String, List<Record>> recordsMap) {
        this.recordsMap = recordsMap;
    }


    /* this error is displayed ot the user if validation fails
    
     */
    private void showConsoleError() {
        System.err.println(USAGE_HINT);
        logger.error("User did not pass the required number of params.\nExiting now.");
        //System.exit(1);
    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ef.dao;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Venu
 */
@Repository
@Qualifier("serverLogDao")
public class ServerLogDao implements LoadDaoInfc {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public Integer saveDB(BufferedReader bufferedReader) throws SQLException, IOException {
        //BufferedReader bufferedReader = checkQualifiesforDBStore();
        Integer recordCount =0;
        String query
                = "LOAD DATA LOCAL INFILE 'input.csv' INTO TABLE SERVER_LOG  FIELDS TERMINATED BY ',' ESCAPED BY '\"' ENCLOSED BY '\"' LINES TERMINATED BY '\\n'";

        //jdbcTemplate.update(query);
        Connection conn = jdbcTemplate.getDataSource().getConnection();
        PreparedStatement statement;
        statement = conn.prepareStatement(query);
        com.mysql.jdbc.PreparedStatement mysqlStatement;
        mysqlStatement = statement.unwrap(com.mysql.jdbc.PreparedStatement.class);
        mysqlStatement.setLocalInfileInputStream(new CustomInputStream(bufferedReader));
        mysqlStatement.execute();
       recordCount =  mysqlStatement.getUpdateCount();
        //System.out.println("Total records loaded in the mysql table, SERVER_LOG= " + recordCount);
        conn.close();
        return recordCount;
    }

}

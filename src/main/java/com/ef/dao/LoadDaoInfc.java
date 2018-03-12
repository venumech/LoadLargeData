/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ef.dao;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.SQLException;

/**
 *
 * @author Venu
 */
public interface LoadDaoInfc {
        public Integer saveDB(BufferedReader bufferedReader) throws SQLException, IOException;
}

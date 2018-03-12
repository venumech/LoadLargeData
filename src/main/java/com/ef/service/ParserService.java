/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ef.service;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;

/**
 *
 * @author Venu
 */
public interface ParserService {
 public Integer saveDB(LocalDateTime startDate,LocalDateTime endDate,Integer threshold) throws SQLException, IOException;   
}

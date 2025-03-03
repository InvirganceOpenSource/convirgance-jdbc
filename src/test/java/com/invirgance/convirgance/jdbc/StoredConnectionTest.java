/*
 * The MIT License
 *
 * Copyright 2025 jbanes.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.invirgance.convirgance.jdbc;

import com.invirgance.convirgance.dbms.DBMS;
import com.invirgance.convirgance.dbms.Query;
import com.invirgance.convirgance.dbms.QueryOperation;
import com.invirgance.convirgance.jdbc.datasource.DriverDataSource;
import java.io.File;
import javax.sql.DataSource;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeAll;

/**
 *
 * @author jbanes
 */
public class StoredConnectionTest
{
    private static String url = "jdbc:hsqldb:file:target/unit-test-work/dbms/testdb/;hsqldb.lock_file=false";
    
    private static void delete(File file)
    {
        if(!file.isDirectory())
        {
            file.delete();
            return;
        }
        
        for(File child : file.listFiles())
        {
            delete(child);
        }
        
        file.delete();
    }
    
    @BeforeAll
    public static void setup()
    {
        DataSource source = DriverDataSource.getDataSource(url, "SA", "");
        DBMS dbms = new DBMS(source);
        
        delete(new File("target/unit-test-work/connections"));
        delete(new File("target/unit-test-work/dbms/testdb"));
        
        new File("target/unit-test-work/dbms/testdb").mkdirs();
        
        System.setProperty("convirgance.jdbc.connections", "target/unit-test-work/connections");
        
        dbms.update(new QueryOperation(new Query("create table CUSTOMER (\n" +
                    "                                 CUSTOMER_ID INTEGER,\n" +
                    "                                 DISCOUNT_CODE CHAR(1),\n" +
                    "                                 ZIP VARCHAR(10),\n" +
                    "                                 NAME VARCHAR(30),\n" +
                    "                                 ADDRESSLINE1 VARCHAR(30),\n" +
                    "                                 ADDRESSLINE2 VARCHAR(30),\n" +
                    "                                 CITY VARCHAR(25),\n" +
                    "                                 STATE CHAR(2),\n" +
                    "                                 PHONE CHAR(12),\n" +
                    "                                 FAX CHAR(12),\n" +
                    "                                 EMAIL VARCHAR(40),\n" +
                    "                                 CREDIT_LIMIT INTEGER\n" +
                    "                             );")));
    }
    
    @Test
    public void testCreateConnection() throws Exception
    {
        AutomaticDriver driver = AutomaticDrivers.getDriverByName("HSQLDB");
        StoredConnection connection = driver
                                            .createConnection("test")
                                            .driver()
                                            .url(url)
                                            .username("SA")
                                            .password("")
                                            .build();
        
        assertNotNull(connection.getConnection());
        
        driver.save();
        
        for(StoredConnection stored : new StoredConnections())
        {
            assertEquals("test", stored.getName());
            assertEquals("HSQLDB", stored.getDriver().getName());
            assertEquals("SA", stored.getDriverConfig().getUsername());
            assertEquals("", stored.getDriverConfig().getPassword());
            assertEquals(url, stored.getDriverConfig().getURL());
        }
        
        driver.delete();
        
        assertFalse(new StoredConnections().iterator().hasNext());
    }
    
}

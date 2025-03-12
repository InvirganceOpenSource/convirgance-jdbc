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
package com.invirgance.convirgance.jdbc.schema;

import com.invirgance.convirgance.dbms.DBMS;
import com.invirgance.convirgance.dbms.Query;
import com.invirgance.convirgance.input.JSONInput;
import com.invirgance.convirgance.jdbc.AutomaticDriver;
import com.invirgance.convirgance.jdbc.AutomaticDrivers;
import com.invirgance.convirgance.jdbc.StoredConnection;
import com.invirgance.convirgance.jdbc.datasource.DriverDataSource;
import com.invirgance.convirgance.json.JSONObject;
import com.invirgance.convirgance.source.FileSource;
import java.io.File;
import java.sql.SQLException;
import javax.sql.DataSource;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 *
 * @author jbanes
 */
public class TableTest
{
    private static String url = "jdbc:hsqldb:file:" + new File("target/unit-test-work/dbms/tabledb").getAbsolutePath() + ";hsqldb.lock_file=false;shutdown=true";    
    private static DataSource source;
    
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
        File directory = new File("target/unit-test-work/dbms/tabledb");
        DataSource source = DriverDataSource.getDataSource(url, "SA", "");
        DBMS dbms = new DBMS(source);
        
        delete(directory);
        directory.mkdirs();
        
        dbms.update(new Query("create table CUSTOMER (\n" +
                              "    CUSTOMER_ID INTEGER PRIMARY KEY,\n" +
                              "    DISCOUNT_CODE CHAR(1),\n" +
                              "    ZIP VARCHAR(10),\n" +
                              "    NAME VARCHAR(30) NOT NULL,\n" +
                              "    ADDRESSLINE1 VARCHAR(30),\n" +
                              "    ADDRESSLINE2 VARCHAR(30),\n" +
                              "    CITY VARCHAR(25),\n" +
                              "    STATE CHAR(2),\n" +
                              "    PHONE CHAR(12),\n" +
                              "    FAX CHAR(12),\n" +
                              "    EMAIL VARCHAR(40),\n" +
                              "    CREDIT_LIMIT INTEGER\n" +
                              ");"));
        
        dbms.update(new Query("create view ALL_CUSTOMERS as select * from CUSTOMER"));
        
        for(JSONObject record : new JSONInput().read(new FileSource(new File("src/test/resources/table/customer.json"))))
        {
            dbms.update(new Query("insert into CUSTOMER values (:CUSTOMER_ID, :DISCOUNT_CODE, :ZIP, :NAME, :ADDRESSLINE1, :ADDRESSLINE2, :CITY, :STATE, :PHONE, :FAX, :EMAIL, :CREDIT_LIMIT)", record));
        }
    }
    
    private static DataSource getDataSource() throws SQLException
    {
        AutomaticDriver driver = AutomaticDrivers.getDriverByName("HSQLDB");
        StoredConnection connection = driver
                                        .createConnection("test")
                                        .datasource()
                                            .property("url", url)
                                            .property("user", "SA")
                                            .property("password", "")
                                        .build();
        
        if(source == null) source = connection.getDataSource();
        
        return source;
    }
    
    private DatabaseSchema getSchema() throws SQLException
    {
        AutomaticDriver driver = AutomaticDrivers.getDriverByName("HSQLDB");

        return new DatabaseSchema(driver, getDataSource());
    }

    @Test
    public void testIterator() throws SQLException
    {
        DatabaseSchema schema = getSchema();

        Table table = schema.getTables()[0];
        int count = 0;
        
        for(JSONObject record : table)
        {
            System.out.println(record);
            
            assertEquals(++count, record.getInt("CUSTOMER_ID"));
        }
        
        assertEquals(13, count);
    }
    
    
}

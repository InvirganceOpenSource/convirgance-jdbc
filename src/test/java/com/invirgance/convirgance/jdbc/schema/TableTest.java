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
import java.sql.ResultSet;
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
    private static String url = "jdbc:hsqldb:file:target/unit-test-work/dbms/tabledb/;hsqldb.lock_file=false;shutdown=true";    
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
    
//    @BeforeAll
    public static void setup()
    {
        File directory = new File("target/unit-test-work/dbms/tabledb");
        DBMS dbms;
        
        delete(directory);
        directory.mkdirs();
        
        dbms = new DBMS(source);
        
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
        
        dbms.update(new Query("create schema TESTING AUTHORIZATION DBA"));
        dbms.update(new Query("create table TESTING.TEST_TABLE ( ID INTEGER PRIMARY KEY, TEST_CUSTOMER_ID INTEGER REFERENCES PUBLIC.CUSTOMER )"));
        
        for(JSONObject record : new JSONInput().read(new FileSource(new File("src/test/resources/table/customer.json"))))
        {
            dbms.update(new Query("insert into CUSTOMER values (:CUSTOMER_ID, :DISCOUNT_CODE, :ZIP, :NAME, :ADDRESSLINE1, :ADDRESSLINE2, :CITY, :STATE, :PHONE, :FAX, :EMAIL, :CREDIT_LIMIT)", record));
        }
    }
    
    public static DataSource getDataSource()
    {
        AutomaticDriver driver = AutomaticDrivers.getDriverByName("HSQLDB");
        StoredConnection connection = driver
                                        .createConnection("test")
                                        .datasource()
                                            .property("url", url)
                                            .property("user", "SA")
                                            .property("password", "")
                                        .build();
        
        if(source == null) 
        {
            source = connection.getDataSource();
            
            setup();
        }
        
        return source;
    }
    
    public static DatabaseSchemaLayout getLayout()
    {
        AutomaticDriver driver = AutomaticDrivers.getDriverByName("HSQLDB");

        return new DatabaseSchemaLayout(driver, getDataSource());
    }
    
    @Test
    public void testSchema()
    {
        DatabaseSchemaLayout layout = getLayout();
        Table table = layout.getCurrentSchema().getTables()[0];
        
        assertEquals("CUSTOMER", table.getName());
        assertEquals("PUBLIC", table.getSchema().getName());
        
        table = layout.getAllTables()[0];
        
        assertEquals("CUSTOMER", table.getName());
        assertEquals("PUBLIC", table.getSchema().getName());
    }

    @Test
    public void testIterator()
    {
        DatabaseSchemaLayout layout = getLayout();

        Table table = layout.getAllTables()[0];
        int count = 0;
        
        for(JSONObject record : table)
        {
            assertEquals(++count, record.getInt("CUSTOMER_ID"));
        }
        
        assertEquals(13, count);
        assertEquals("CUSTOMER_ID", table.getColumns()[0].getName());
        assertEquals(table.getColumns()[0], table.getPrimaryKey().getColumn());
        assertTrue(table.getPrimaryKey().getName().startsWith("SYS_PK_"));
    }
    
    @Test
    public void testForeignKeys()
    {
        DatabaseSchemaLayout layout = getLayout();
        Table table = layout.getCurrentCatalog().getSchema("TESTING").getTable("TEST_TABLE");
        
        for(Table.ForeignKey key : table.getForeignKeys())
        {
            assertEquals("PUBLIC", key.getTable().getSchema().getCatalog().getName());
            assertEquals("TESTING", key.getTable().getSchema().getName());
            assertEquals("TEST_CUSTOMER_ID", key.getColumn().getName());
            assertEquals("PUBLIC", key.getTarget().getSchema().getName());
            assertEquals("CUSTOMER", key.getTarget().getName());
            assertEquals("CUSTOMER_ID", key.getTargetKey().getName());
            assertTrue(key.getName().startsWith("SYS_FK_"));
        }
        
        assertEquals(1, table.getForeignKeys().length);
    }
}

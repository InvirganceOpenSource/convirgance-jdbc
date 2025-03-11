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
import com.invirgance.convirgance.dbms.QueryOperation;
import com.invirgance.convirgance.jdbc.AutomaticDriver;
import com.invirgance.convirgance.jdbc.AutomaticDrivers;
import com.invirgance.convirgance.jdbc.StoredConnection;
import com.invirgance.convirgance.jdbc.datasource.DataSourceManager;
import com.invirgance.convirgance.jdbc.datasource.DriverDataSource;
import com.invirgance.convirgance.json.JSONObject;
import java.io.File;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeAll;

/**
 *
 * @author jbanes
 */
public class DatabaseSchemaTest
{
    private static String url = "jdbc:hsqldb:file:target/unit-test-work/dbms/schemadb/;hsqldb.lock_file=false";
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
    public static void setup() throws SQLException
    {
        File directory = new File("target/unit-test-work/dbms/schemadb");
        DBMS dbms;
        
        delete(directory);
        directory.mkdirs();
        
        dbms = new DBMS(getHSQLDataSource());
        
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
    }
    
    private static DataSource getHSQLDataSource() throws SQLException
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
    
    private DatabaseSchema getHSQLSchema() throws SQLException
    {
        AutomaticDriver driver = AutomaticDrivers.getDriverByName("HSQLDB");
        
        return new DatabaseSchema(driver, getHSQLDataSource());
    }

    @Test
    public void testTables() throws SQLException
    {
        String[] names = {
            "CUSTOMER_ID", 
            "DISCOUNT_CODE", 
            "ZIP", 
            "NAME", 
            "ADDRESSLINE1", 
            "ADDRESSLINE2", 
            "CITY", 
            "STATE", 
            "PHONE", 
            "FAX", 
            "EMAIL", 
            "CREDIT_LIMIT"
        };
        
        DatabaseSchema schema = getHSQLSchema();
        Table[] tables = schema.getTables();
        View[] views = schema.getViews();
        
        int count;
        
        
        assertEquals(1, tables.length);
        assertEquals("CUSTOMER", tables[0].getName());
        assertEquals("TABLE", tables[0].getType());
        
        count = 0;
        
        for(Column column : tables[0].getColumns())
        {
//            System.out.println("    " + column.getName() + ":" + column.isNullable() + ":" + column.getType() + ":" + column.getJDBCType() + ":" + column.getTypeClass());
            assertEquals(names[count++], column.getName());
        }
        
        assertEquals(names.length, count);
        
        assertEquals(1, views.length);
        assertEquals("ALL_CUSTOMERS", views[0].getName());
        assertEquals("VIEW", views[0].getType());
        
        count = 0;

        for(Column column : views[0].getColumns())
        {
//            System.out.println("    " + column.getName() + ":" + column.isNullable() + ":" + column.getType() + ":" + column.getJDBCType() + ":" + column.getTypeClass());
            assertEquals(names[count++], column.getName());
        }
        
        assertEquals(names.length, count);
    }

    @Test
    public void testTableTypes() throws SQLException
    {
        String[] expected = {"GLOBAL TEMPORARY", "SYSTEM TABLE", "TABLE", "VIEW"};
        
        DatabaseSchema schema = getHSQLSchema();
        int count = 0;
        
        for(String type : schema.getTypes())
        {
            assertEquals(expected[count++], type);
        }
        
        assertEquals(expected.length, count);
    }
}
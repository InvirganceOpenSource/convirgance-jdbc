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
import com.invirgance.convirgance.dbms.TransactionOperation;
import com.invirgance.convirgance.jdbc.AutomaticDriver;
import com.invirgance.convirgance.jdbc.AutomaticDrivers;
import com.invirgance.convirgance.jdbc.StoredConnection;
import java.io.File;
import javax.sql.DataSource;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeAll;

/**
 *
 * @author jbanes
 */
public class DatabaseSchemaLayoutTest
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
    
    private static synchronized void setup()
    {
        File directory = new File("target/unit-test-work/dbms/schemadb");
        DBMS dbms;
        
        delete(directory);
        delete(new File("target/unit-test-work/dbms/h2db"));
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
    
    public static DataSource getHSQLDataSource()
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
    
    public static DatabaseSchemaLayout getHSQLLayout()
    {
        AutomaticDriver driver = AutomaticDrivers.getDriverByName("HSQLDB");
        
        return new DatabaseSchemaLayout(driver, getHSQLDataSource());
    }
    
    private DatabaseSchemaLayout getH2Layout()
    {
        AutomaticDriver driver = AutomaticDrivers.getDriverByName("H2");
        StoredConnection connection = driver
                                        .createConnection("test")
                                        .datasource()
                                            .property("url", "jdbc:h2:./target/unit-test-work/dbms/h2db/h2")
                                            .property("user", "SA")
                                            .property("password", "")
                                        .build();
        
        return new DatabaseSchemaLayout(driver, connection.getDataSource());
    }
    
    @Test
    public void testCatalogs()
    {
        DatabaseSchemaLayout schema = getHSQLLayout();
        
        for(Catalog catalog : schema.getCatalogs())
        {
            assertEquals("PUBLIC", catalog.getName());
        }
        
        assertEquals(1, schema.getCatalogs().length);
        assertEquals("PUBLIC", schema.getCurrentCatalog().getName());
        assertEquals("PUBLIC", schema.getCatalog("PUBLIC").getName());
    }

    @Test
    public void testTables()
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
        
        DatabaseSchemaLayout schema = getHSQLLayout();
        Table[] tables = schema.getAllTables();
        View[] views = schema.getAllViews();
        
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
    public void testTableTypes()
    {
        String[] expected = {"GLOBAL TEMPORARY", "SYSTEM TABLE", "TABLE", "VIEW"};
        
        DatabaseSchemaLayout schema = getHSQLLayout();
        int count = 0;
        
        for(String type : schema.getTypes())
        {
            assertEquals(expected[count++], type);
        }
        
        assertEquals(expected.length, count);
    }
    
    @Test
    public void testH2Tables()
    {
        DatabaseSchemaLayout layout = getH2Layout();
        Query createTable = new Query("create table test ( test_column VARCHAR(64) );");
        Query createView = new Query("create view test_view as select * from test;");
        int count = 0;
        
        new DBMS(layout.getDataSource()).update(new TransactionOperation(createTable, createView));
        
        for(Table table : layout.getAllTables())
        {
            // H2 returns a bunch of built-in tables as normal tables, so we're
            // looking for the one we created to confirm that it's there.
            if(table.getName().equals("TEST")) count++;
        }
        
        assertEquals(1, count);
        assertEquals("H2", layout.getCurrentCatalog().getName());
        assertEquals("PUBLIC", layout.getCurrentSchema().getName());
        assertEquals(1, layout.getCurrentSchema().getTables().length);
        assertEquals("TEST", layout.getCurrentSchema().getTables()[0].getName());
        assertEquals(1, layout.getCurrentSchema().getViews().length);
        assertEquals("TEST_VIEW", layout.getCurrentSchema().getViews()[0].getName());
    }
}
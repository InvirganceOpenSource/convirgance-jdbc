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
import com.invirgance.convirgance.jdbc.datasource.DriverDataSource;
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
        
        delete(new File("target/unit-test-work/schema"));
        delete(new File("target/unit-test-work/dbms/schemadb"));
        
        new File("target/unit-test-work/dbms/schemadb").mkdirs();
        
        System.setProperty("convirgance.jdbc.connections", "target/unit-test-work/schema");
        
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

    @Test
    public void testTables() throws SQLException
    {
        AutomaticDriver driver = AutomaticDrivers.getDriverByName("HSQLDB");
        StoredConnection connection = driver
                                        .createConnection("test")
                                        .datasource()
                                            .property("url", url)
                                            .property("user", "SA")
                                            .property("password", "")
                                        .build();
        
        DatabaseSchema schema = new DatabaseSchema(driver, connection.getDataSource());
        Table[] tables = schema.getTables();
        View[] views = schema.getViews();
        
        assertEquals(1, tables.length);
        assertEquals("CUSTOMER", tables[0].getName());
        assertEquals("TABLE", tables[0].getType());
        
        assertEquals(1, views.length);
        assertEquals("ALL_CUSTOMERS", views[0].getName());
        assertEquals("VIEW", views[0].getType());
        
        for(Table table : schema.getTables())
        {
            System.out.println(table.getName());
            
            for(Column column : table.getColumns())
            {
                System.out.println("    " + column.getName() + ":" + column.isNullable() + ":" + column.getType() + ":" + column.getJDBCType() + ":" + column.getTypeClass());
            }
        }
        
        for(View view : schema.getViews())
        {
            System.out.println(view.getName());
            
            for(Column column : view.getColumns())
            {
                System.out.println("    " + column.getName() + ":" + column.isNullable() + ":" + column.getType() + ":" + column.getJDBCType() + ":" + column.getTypeClass());
            }
        }
    }

    @Test
public void testTablesTemp() throws SQLException
    {
        AutomaticDriver driver = AutomaticDrivers.getDriverByName("PostgreSQL");
        StoredConnection connection = driver
                                        .createConnection("test")
                                        .driver()
                                            .url("jdbc:postgresql://localhost:5432/warehouse_management")
                                            .username("postgres")
                                            .password("")
                                        .build();
        
        for(Table table : new DatabaseSchema(driver, connection.getDataSource()).getTables())
        {
            System.out.println(table.getName());
            
            for(Column column : table.getColumns())
            {
                System.out.println("    " + column.getName() + ":" + column.isNullable() + ":" + column.getType() + ":" + column.getJDBCType() + ":" + column.getTypeClass());
//                System.out.println(column);
            }
        }
        
        for(View view : new DatabaseSchema(driver, connection.getDataSource()).getViews())
        {
            System.out.println(view.getName());
        }
    }
    
}

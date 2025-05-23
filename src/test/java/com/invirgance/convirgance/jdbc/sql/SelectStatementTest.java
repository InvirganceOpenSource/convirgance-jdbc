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
package com.invirgance.convirgance.jdbc.sql;

import com.invirgance.convirgance.dbms.DBMS;
import com.invirgance.convirgance.dbms.Query;
import com.invirgance.convirgance.input.JSONInput;
import com.invirgance.convirgance.jdbc.AutomaticDriver;
import com.invirgance.convirgance.jdbc.AutomaticDrivers;
import com.invirgance.convirgance.jdbc.StoredConnection;
import com.invirgance.convirgance.jdbc.schema.DatabaseSchemaLayout;
import com.invirgance.convirgance.jdbc.schema.Table;
import com.invirgance.convirgance.json.JSONObject;
import com.invirgance.convirgance.source.FileSource;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import javax.sql.DataSource;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author jbanes
 */
public class SelectStatementTest
{
    private static String url = "jdbc:hsqldb:file:target/unit-test-work/dbms/querydb/;hsqldb.lock_file=false";
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
        File directory = new File("target/unit-test-work/dbms/querydb");
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
        
        for(JSONObject record : new JSONInput().read(new FileSource(new File("src/test/resources/table/customer.json"))))
        {
            dbms.update(new Query("insert into CUSTOMER values (:CUSTOMER_ID, :DISCOUNT_CODE, :ZIP, :NAME, :ADDRESSLINE1, :ADDRESSLINE2, :CITY, :STATE, :PHONE, :FAX, :EMAIL, :CREDIT_LIMIT)", record));
        }
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
    
    @Test
    public void testBasicSelect()
    {
        List<String> zips = Arrays.asList(new String[]{"10095", "10096", "12347", "48124", "48128", "85638", "94401", "95035", "95117"});
        
        DatabaseSchemaLayout layout = getHSQLLayout();
        Table table = layout.getCurrentSchema().getTable("customer");
        DBMS dbms = new DBMS(getHSQLDataSource());
        
        SelectStatement select = new SelectStatement(layout).column(table.getColumn("zip"));
        int count = 0;
        
        assertEquals("select \"ZIP\" from \"PUBLIC\".\"CUSTOMER\";", select.toString());
        
        for(JSONObject record : dbms.query(select.query()))
        {
            assertTrue(zips.contains(record.getString("ZIP")));
            count++;
        }

        assertEquals(13, count);
    }
    
    @Test
    public void testPrettyPrint()
    {
        List<String> zips = Arrays.asList(new String[]{"10095", "10096", "12347", "48124", "48128", "85638", "94401", "95035", "95117"});
        
        DatabaseSchemaLayout layout = getHSQLLayout();
        Table table = layout.getCurrentSchema().getTable("customer");
        DBMS dbms = new DBMS(getHSQLDataSource());
        
        SelectStatement select = new SelectStatement(layout).column(table.getColumn("name")).column(table.getColumn("zip"));
        int count = 0;

        assertEquals("SELECT\n    \"NAME\",\n    \"ZIP\"\nFROM \"PUBLIC\".\"CUSTOMER\";", new SQLRenderer().capitialize(true).pretty(true).statement(select).toString());

        for(JSONObject record : dbms.query(select.query()))
        {
            assertTrue(zips.contains(record.getString("ZIP")));
            count++;
        }

        assertEquals(13, count);
        
        select
            .where()
                .equals(table.getColumn("zip"), "90210")
                .notEquals(table.getColumn("city"), "LA")
                .done();
        
        assertEquals("select\n    \"NAME\",\n    \"ZIP\"\nfrom \"PUBLIC\".\"CUSTOMER\" \nwhere \"ZIP\" = '90210'\nand \"CITY\" <> 'LA';", new SQLRenderer().pretty(true).statement(select).toString());
    }
    
    @Test
    public void testNamedColumns()
    {
        List<String> zips = Arrays.asList(new String[]{"10095", "10096", "12347", "48124", "48128", "85638", "94401", "95035", "95117"});
        
        DatabaseSchemaLayout layout = getHSQLLayout();
        Table table = layout.getCurrentSchema().getTable("customer");
        DBMS dbms = new DBMS(getHSQLDataSource());
        
        SelectStatement select = new SelectStatement(layout).column(table.getColumn("zip"), "zipcode");
        int count = 0;
        
        assertEquals("select \"ZIP\" as \"zipcode\" from \"PUBLIC\".\"CUSTOMER\";", select.toString());
        
        for(JSONObject record : dbms.query(select.query()))
        {
            assertTrue(zips.contains(record.getString("zipcode")));
            count++;
        }

        assertEquals(13, count);
    }
    
    @Test
    public void testOrderBy()
    {
        DatabaseSchemaLayout layout = getHSQLLayout();
        Table table = layout.getCurrentSchema().getTable("customer");
        
        SelectStatement select = new SelectStatement(layout)
                .column(table.getColumn("name"))
                .column(table.getColumn("zip"))
                .order(table.getColumn("name"))
                .order(table.getColumn("zip"), OrderBy.DESCENDING);
        
        System.out.println(select.render(new SQLRenderer().pretty(true)).toString());
        
        assertEquals("select \"NAME\", \"ZIP\" from \"PUBLIC\".\"CUSTOMER\" order by \"NAME\" asc, \"ZIP\" desc;", select.toString());
        assertEquals("select\n    \"NAME\",\n    \"ZIP\"\nfrom \"PUBLIC\".\"CUSTOMER\" \norder by\n    \"NAME\" asc,\n    \"ZIP\" desc;", select.render(new SQLRenderer().pretty(true)).toString());
    }
}

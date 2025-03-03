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

import com.invirgance.convirgance.ConvirganceException;
import com.invirgance.convirgance.json.JSONArray;
import com.invirgance.convirgance.json.JSONObject;
import java.io.File;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeAll;

/**
 *
 * @author jbanes
 */
public class JDBCAutomaticDriversTest
{
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
        delete(new File("target/unit-test-work/drivers"));
        
        System.setProperty("convirgance.jdbc.drivers", "target/unit-test-work/drivers");
    }
    
    @Test
    public void testCreateDriver()
    {
        JSONObject expected = new JSONObject("{\n" +
                                             "    \"name\": \"Bob\",\n" +
                                             "    \"artifact\": [\n" +
                                             "        \"org.hsqldb:hsqldb:2.7.4\"\n" +
                                             "    ],\n" +
                                             "    \"driver\": \"org.hsqldb.jdbc.JDBCDriver\",\n" +
                                             "    \"datasource\": \"org.hsqldb.jdbc.JDBCDataSource\",\n" +
                                             "    \"prefixes\": [\n" +
                                             "        \"jdbc:hsqldb:\"\n" +
                                             "    ],\n" +
                                             "    \"examples\": [\n" +
                                             "        \"jdbc:hsqldb:hsql://<serverName>[:port][/databaseName]\"\n" +
                                             "    ]\n" +
                                             "}");
        
        JDBCAutomaticDriver driver = JDBCAutomaticDrivers
                                        .createDriver("Bob")
                                        .artifact("org.hsqldb:hsqldb:2.7.4")
                                        .driver("org.hsqldb.jdbc.JDBCDriver")
                                        .datasource("org.hsqldb.jdbc.JDBCDataSource")
                                        .prefix("jdbc:hsqldb:")
                                        .example("jdbc:hsqldb:hsql://<serverName>[:port][/databaseName]")
                                        .build();
        
        assertEquals(expected, new JSONObject(driver.toString()));
        
        driver.save(); // Attempt to persist the driver
        
        assertEquals(expected, new JSONObject(JDBCAutomaticDrivers.getDriverByName("Bob").toString()));
        
        // Validate that save() replaces the record
        expected.put("examples", new JSONArray<String>("dud", "dud2"));
        driver.setExamples("dud", "dud2");
        driver.save();
        
        assertEquals(expected, new JSONObject(JDBCAutomaticDrivers.getDriverByName("Bob").toString()));
    }
    
    @Test
    public void testCreateDuplicateDriver()
    {
        try
        {
            JDBCAutomaticDrivers.createDriver("HSQLDB").build();
            
            fail("Expected a duplicate driver to cause a failure");
        }
        catch(ConvirganceException e)
        {
            assertEquals("HSQLDB already exists in the driver list", e.getMessage());
        }
    }
    
    @Test
    public void testNullDriver()
    {
        JDBCAutomaticDriver driver = JDBCAutomaticDrivers
                                        .createDriver("NoDriver")
                                        .build();
        
        assertNull(driver.getDriver());
        assertNull(driver.getDataSource());
        assertEquals(0, driver.getArtifacts().length);
        assertEquals(0, driver.getPrefixes().length);
        assertEquals(0, driver.getExamples().length);
    }
}

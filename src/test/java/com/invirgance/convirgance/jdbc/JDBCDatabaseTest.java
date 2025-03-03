/*
 * Copyright 2024 INVIRGANCE LLC

Permission is hereby granted, free of charge, to any person obtaining a copy 
of this software and associated documentation files (the “Software”), to deal 
in the Software without restriction, including without limitation the rights to 
use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies 
of the Software, and to permit persons to whom the Software is furnished to do 
so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all 
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR 
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, 
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE 
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER 
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, 
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE 
SOFTWARE.
 */
package com.invirgance.convirgance.jdbc;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author jbanes
 */
public class JDBCDatabaseTest
{

    @Test
    public void testDatabases()
    {
        int count = 0;
        
        for(JDBCAutomaticDriver database : new JDBCAutomaticDrivers())
        {
            System.out.println(database);
            
            assertNotNull(database.getName());
            assertNotNull(database.getDriver());
            assertNotNull(database.getDataSource());
            
            assertTrue(database.getDriver().getClass().getName().contains("Driver"));
            assertTrue(database.getDataSource().getClass().getName().contains("DataSource"));
            assertTrue(database.getPrefixes()[0].startsWith("jdbc:"));
            assertTrue(database.getExamples()[0].startsWith("jdbc:"));
            
            assertTrue(database.getArtifacts().length > 0);
            assertTrue(database.getArtifacts()[0].length() > 0);
            assertTrue(database.getPrefixes().length > 0);
            assertTrue(database.getPrefixes()[0].length() > 0);
            assertTrue(database.getExamples().length > 0);
            assertTrue(database.getExamples()[0].length() > 0);
            
            count++;
        }
        
        assertTrue(count > 0);
    }
    
}

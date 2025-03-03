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

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author jbanes
 */
public class JDBCAutomaticDriverTest
{
    @Test
    public void testSymmetry()
    {
        JDBCAutomaticDriver driver = JDBCAutomaticDrivers.getDriverByName("HSQLDB");
        
        String driverClass = driver.getDriver().getClass().getName();
        String datasourceClass = driver.getDataSource().getClass().getName();
        String[] artifacts = driver.getArtifacts();
        String[] prefixes = driver.getPrefixes();
        String[] examples = driver.getExamples();
        
        driver.setDriver(null);
        driver.setDataSource(null);
        driver.setArtifacts();
        driver.setPrefixes();
        driver.setExamples();
        
        assertNull(driver.getDriver());
        assertNull(driver.getDataSource());
        assertEquals(0, driver.getArtifacts().length);
        assertEquals(0, driver.getPrefixes().length);
        assertEquals(0, driver.getExamples().length);
        
        driver.setDriver(driverClass);
        driver.setDataSource(datasourceClass);
        driver.setArtifacts(artifacts);
        driver.setPrefixes(prefixes);
        driver.setExamples(examples);
        
        assertNotNull(driver.getDriver());
        assertNotNull(driver.getDataSource());
        assertEquals(artifacts.length, driver.getArtifacts().length);
        assertEquals(prefixes.length, driver.getPrefixes().length);
        assertEquals(examples.length, driver.getExamples().length);
    }
    
}

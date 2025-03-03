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
package com.invirgance.convirgance.jdbc.datasource;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;
import javax.sql.DataSource;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author jbanes
 */
public class DataSourceManagerTest
{
    @Test
    public void testPropertyList()
    {
        DriverDataSource source = DriverDataSource.getDataSource("jdbc:test", "testuser", "testpass");
        DataSourceManager manager = new DataSourceManager(source);
        String[] properties = manager.getProperties();
        
        assertEquals("password", properties[0]);
        assertEquals("url", properties[1]);
        assertEquals("username", properties[2]);
    }
    
    @Test
    public void testPropertyValues()
    {
        DriverDataSource source = DriverDataSource.getDataSource("jdbc:test", "testuser", "testpass");
        DataSourceManager manager = new DataSourceManager(source);
        
        assertEquals("testpass", manager.getProperty("password"));
        assertEquals("jdbc:test", manager.getProperty("url"));
        assertEquals("testuser", manager.getProperty("username"));
    }
    
    @Test
    public void testPropertyAssignments()
    {
        DriverDataSource source = new DriverDataSource();
        DataSourceManager manager = new DataSourceManager(source);
        
        assertNull(manager.getProperty("password"));
        assertNull(manager.getProperty("url"));
        assertNull(manager.getProperty("username"));
        
        manager.setProperty("password", "testpass");
        manager.setProperty("url", "jdbc:test");
        manager.setProperty("username", "testuser");
        
        assertEquals("testpass", manager.getProperty("password"));
        assertEquals("jdbc:test", manager.getProperty("url"));
        assertEquals("testuser", manager.getProperty("username"));
    }
    
    @Test
    public void testCoersion()
    {
        DataSourceManager manager = new DataSourceManager(new AssertionDataSource());
        
        for(String property : manager.getProperties()) System.out.println(property);
        
        manager.setProperty("int", 42);
        manager.setProperty("long", 1337L);
        manager.setProperty("string", "string");
        manager.setProperty("boolean", true);
        manager.setProperty("integerObject", 42);
        manager.setProperty("longObject", 1337L);
        manager.setProperty("booleanObject", true);
        
        manager.setProperty("int", "42");
        manager.setProperty("long", "1337");
        manager.setProperty("string", "string");
        manager.setProperty("boolean", "true");
        manager.setProperty("integerObject", "42");
        manager.setProperty("longObject", "1337");
        manager.setProperty("booleanObject", "true");
        
        manager.setProperty("stringArray", new String[]{"One", "Two", "Three"});
    }
    
    private class AssertionDataSource implements DataSource
    {
        @Override
        public Connection getConnection() throws SQLException
        {
            throw new UnsupportedOperationException("Not supported."); 
        }

        @Override
        public Connection getConnection(String username, String password) throws SQLException
        {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public PrintWriter getLogWriter() throws SQLException
        {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public void setLogWriter(PrintWriter out) throws SQLException
        {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public void setLoginTimeout(int seconds) throws SQLException
        {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public int getLoginTimeout() throws SQLException
        {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public Logger getParentLogger() throws SQLFeatureNotSupportedException
        {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public <T> T unwrap(Class<T> iface) throws SQLException
        {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public boolean isWrapperFor(Class<?> iface) throws SQLException
        {
            throw new UnsupportedOperationException("Not supported.");
        }
        
        public String getString()
        {
            return "string";
        }
        
        public void setString(String value)
        {
            assertEquals("string", value);
        }
        
        public int getInt()
        {
            return 42;
        }
        
        public void setInt(int value)
        {
            assertEquals(42, value);
        }
        
        public long getLong()
        {
            return 1337L;
        }
        
        public void setLong(long value)
        {
            assertEquals(1337L, value);
        }
        
        public boolean getBoolean()
        {
            return true;
        }
        
        public void setBoolean(boolean value)
        {
            assertTrue(value);
        }
        
        public Integer getIntegerObject()
        {
            return 42;
        }
        
        public void setIntegerObject(Integer value)
        {
            assertEquals(42, value);
        }
        
        public Long getLongObject()
        {
            return 1337L;
        }
        
        public void setLongObject(Long value)
        {
            assertEquals(1337L, value);
        }
        
        public Boolean getBooleanObject()
        {
            return true;
        }
        
        public void setBooleanObject(Boolean value)
        {
            assertTrue(value);
        }
        
        public String[] getStringArray()
        {
            return new String[]{"One", "Two", "Three"};
        }
        
        public void setStringArray(String[] array)
        {
            assertEquals(3, array.length);
            assertEquals("One", array[0]);
            assertEquals("Two", array[1]);
            assertEquals("Three", array[2]);
        }
    }
    
}

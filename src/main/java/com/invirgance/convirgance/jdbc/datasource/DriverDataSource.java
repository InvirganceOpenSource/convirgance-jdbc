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

import com.invirgance.convirgance.jdbc.AutomaticDrivers;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Logger;
import javax.sql.DataSource;

/**
 *
 * @author jbanes
 */
public class DriverDataSource implements DataSource
{
    private String url;
    private String username;
    private String password;

    public DriverDataSource()
    {
    }

    DriverDataSource(String url, String username, String password)
    {
        this.url = url;
        this.username = username;
        this.password = password;
    }
    
    public static DriverDataSource getDataSource(String url, String username, String password)
    {
        return new DriverDataSource(url, username, password);
    }
    
    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    @Override
    public Connection getConnection() throws SQLException
    {
        Properties properties = new Properties();
        
        properties.put("user", this.username);
        if(this.password != null) properties.put("password", this.password);
        
        return AutomaticDrivers.getDriverByURL(url).getDriver().connect(url, properties);
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException
    {
        Properties properties = new Properties();
        
        properties.put("user", username);
        properties.put("password", password);
        
        return AutomaticDrivers.getDriverByURL(url).getDriver().connect(url, properties);
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException
    {
        return new PrintWriter(System.out);
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException
    {
        // No op
    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException
    {
        // No op
    }

    @Override
    public int getLoginTimeout() throws SQLException
    {
        return 30;
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException
    {
        return null;
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException
    {
        throw new UnsupportedOperationException("Not supported."); 
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException
    {
        return false;
    }
    
}

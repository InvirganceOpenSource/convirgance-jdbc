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
import java.sql.Driver;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Logger;
import javax.sql.DataSource;

/**
 * A JDBC DataSource implementation that wraps a database driver to provide connection services.
 * 
 * This class automates the process of establishing database connections using driver-based
 * connectivity. It determines the appropriate JDBC driver based on the connection URL
 * and manages connection credentials. This is particularly useful for scenarios where
 * you need to create {@link DataSource}s dynamically from existing login information.
 * 
 * @author jbanes
 */
public class DriverDataSource implements DataSource
{
    private String url;
    private String username;
    private String password;
    
    private Driver driver;

    public DriverDataSource()
    {
    }

    DriverDataSource(String url, String username, String password)
    {
        this.url = url;
        this.username = username;
        this.password = password;
    }
    
    /**
     * Returns a DriverDataSource created with the provided info.
     * 
     * @param url The connection URL.
     * @param username The username.
     * @param password The password.
     * @return A DriverDataSource.
     */
    public static DriverDataSource getDataSource(String url, String username, String password)
    {
        return new DriverDataSource(url, username, password);
    }
    
    private Driver getDriver()
    {
        if(driver == null) 
        {
            driver = AutomaticDrivers.getDriverByURL(url).getDriver();
        }
        
        return driver;
    }
    
    /**
     * Get the connection URL.
     * 
     * @return The connection string URL.
     */
    public String getUrl()
    {
        return url;
    }

    /**
     * Sets the connection URL.
     * Remember to include the connection prefix.
     * Ex: "jdbc:oracle:thin:"
     * 
     * @param url The new connection URL.
     */
    public void setUrl(String url)
    {
        this.url = url;
    }

    /**
     * Returns the username used when connecting to the database.
     * 
     * @return The username.
     */
    public String getUsername()
    {
        return username;
    }

    /**
     * Sets the username to use for connecting to the database.
     * 
     * @param username The username.
     */
    public void setUsername(String username)
    {
        this.username = username;
    }

    /**
     * Returns the password.
     * 
     * @return The password.
     */
    public String getPassword()
    {
        return password;
    }

    /**
     * Sets the password that will be used with the username when connecting.
     * 
     * @param password The password.
     */
    public void setPassword(String password)
    {
        this.password = password;
    }

    /**
     * Returns a connection to the database using the current username and password.
     * The password is optional when connecting with this method.
     * 
     * @return A connection.
     * @throws SQLException When the username and password are incorrect or the something else.
     */
    @Override
    public Connection getConnection() throws SQLException
    {
        Properties properties = new Properties();
        
        properties.put("user", this.username);
        if(this.password != null) properties.put("password", this.password);
        
        return getDriver().connect(url, properties);
    }

    /**
     * Returns a connection to the database using the provided username and password.
     * 
     * @param username The username.
     * @param password The password.
     * @return A connection.
     * @throws SQLException When the username and password are incorrect or the something else.
     */
    @Override
    public Connection getConnection(String username, String password) throws SQLException
    {
        Properties properties = new Properties();
        
        properties.put("user", username);
        properties.put("password", password);
        
        return getDriver().connect(url, properties);
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

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

import com.invirgance.convirgance.ConvirganceException;
import com.invirgance.convirgance.jdbc.datasource.DriverDataSource;
import com.invirgance.convirgance.json.JSONObject;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;

/**
 *
 * @author jbanes
 */
public class JDBCStoredConnection
{
    private JSONObject record;
    private JDBCConnectionDatabase database;

    JDBCStoredConnection(JSONObject record, JDBCConnectionDatabase database)
    {
        this.record = record;
        this.database = database;
    }
    
    public String getName()
    {
        return record.getString("name");
    }
    
    public JDBCAutomaticDriver getDriver()
    {
        return JDBCAutomaticDrivers.getDriverByName(record.getString("driver"));
    }
    
    public DriverConfig getDriverConfig()
    {
        if(record.isNull("driverConfig")) record.put("driverConfig", new JSONObject());
        
        return new DriverConfig(record.getJSONObject("driverConfig"));
    }
    
    public Connection getConnection() throws SQLException
    {
        return getDataSource().getConnection();
    }
    
    public DataSource getDataSource() throws SQLException
    {
        DriverConfig config = getDriverConfig();
        DataSource source;
        
        if(config != null) 
        {
            return DriverDataSource.getDataSource(config.getURL(), config.getUsername(), config.getPassword());
        }
        
        throw new SQLException("Connection not configured!");
    }
    
    public void save()
    {
        if(getDriverConfig() == null) throw new ConvirganceException("Connection not configured!");
        
        database.saveDescriptor(record);
    }
    
    public void delete()
    {
        database.deleteDescriptor(record);
    }

    @Override
    public String toString()
    {
        return record.toString(4);
    }
    
    public class DriverConfig
    {
        private JSONObject config;

        private DriverConfig(JSONObject config)
        {
            this.config = config;
        }
        
        public String getUsername()
        {
            return config.getString("username");
        }

        public void setUsername(String username)
        {
            config.put("username", username);
        }

        public String getPassword()
        {
            return config.getString("password");
        }

        public void setPassword(String password)
        {
            config.put("password", password);
        }

        public String getURL()
        {
            return config.getString("url");
        }

        public void setURL(String url)
        {
            config.put("url", url);
        }
    }
}

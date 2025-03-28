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
import com.invirgance.convirgance.dbms.DBMS;
import com.invirgance.convirgance.jdbc.callback.ConnectionCallback;
import com.invirgance.convirgance.jdbc.datasource.DataSourceManager;
import com.invirgance.convirgance.jdbc.datasource.DriverDataSource;
import com.invirgance.convirgance.jdbc.schema.DatabaseSchemaLayout;
import com.invirgance.convirgance.json.JSONObject;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;

/**
 * Represents a customized stored connection for interacting with databases.
 * To create a StoredConnection use the {@link StoredConnections.StoredConnectionBuilder} 
 * returned by {@link AutomaticDriver#createConnection(String)}.
 * 
 * @author jbanes
 */
public class StoredConnection
{
    private JSONObject record;
    private ConnectionDatabase database;
    private DataSource source;

    StoredConnection(JSONObject record, ConnectionDatabase database)
    {
        this.record = record;
        this.database = database;
    }
    
    /**
     * Returns the name that this StoredConnection is recognized by.
     * 
     * @return The name.
     */
    public String getName()
    {
        return record.getString("name");
    }
    
    /**
     * Returns the driver (AutomaticDriver) in use.
     * Use the returned object to update the behavior of this driver along with its DataSource, Artifacts etc...
     * 
     * @return The driver.
     */
    public AutomaticDriver getDriver()
    {
        return AutomaticDrivers.getDriverByName(record.getString("driver"));
    }
    
    /**
     * Returns the current driver configuration.
     * Use this to update/retrieve driver information such as the username, password or URl.
     * 
     * @return The drivers configuration.
     */
    public DriverConfig getDriverConfig()
    {
        if(record.isNull("driverConfig")) record.put("driverConfig", new JSONObject());
        
        return new DriverConfig(record.getJSONObject("driverConfig"));
    }
    
    /**
     * Returns the current driver's data source configuration.
     * If no configuration exists the DataSourceConfig will be created using the 
     * defaults for ALL properties.
     * 
     * @return The data source configuration.
     */
    public DataSourceConfig getDataSourceConfig()
    {
        DataSource source = getDriver().getDataSource();
        
        if(source == null) throw new ConvirganceException("Automatic driver " + record.getString("driver") + " does not have a DataSource.");
        if(record.isNull("datasourceConfig")) record.put("datasourceConfig", new JSONObject());
        
        // Reset the cache
        this.source = null;
        
        return new DataSourceConfig(record.getJSONObject("datasourceConfig"), new DataSourceManager(source).getConfig());
    }
    
    /**
     * Attempts to get a connection to the database using the data source configuration.
     * Will throw an exception if the connection fails.
     * 
     * @return A connection to the database.
     */
    public Connection getConnection()
    {
        try
        {
            return getDataSource().getConnection();
        }
        catch(SQLException e)
        {
            throw new ConvirganceException(e);
        }
    }
    
    /**
     * Obtains a connection to the database, executes the callback, and then
     * closes the connection.
     * 
     * @param callback a callback containing the logic to execute against the database
     * @throws ConvirganceException if an SQL error occurs
     */
    public void execute(ConnectionCallback callback)
    {
        ConnectionCallback.execute(getDataSource(), callback);
    }
    
    /**
     * Returns the data source for the driver used by this StoredConnection.
     * If the drivers configuration lacks a data source a new one is assigned 
     * based on the URL's prefix.
     * 
     * @return The data source.
     */
    public DataSource getDataSource()
    {
        DriverConfig config = getDriverConfig();
        DataSourceManager manager;
        DataSource source;
        
        if(this.source != null) return this.source;
        
        if(!record.isNull("datasourceConfig"))
        {
            manager = new DataSourceManager(getDriver().getDataSource());
            
            manager.setConfig(record.getJSONObject("datasourceConfig"));
            
            this.source = manager.getDataSource();
            
            return this.source;
        }
        
        if(config != null) 
        {
            this.source = DriverDataSource.getDataSource(config.getURL(), config.getUsername(), config.getPassword());
            
            return this.source;
        }
        
        throw new ConvirganceException("Connection not configured!");
    }
    
    /**
     * Returns a new DBMS setup specifically for this StoredConnection.
     * Used to act on the database.
     * 
     * @return A DBMS for executing queries etc...
     */
    public DBMS getDBMS()
    {
        return new DBMS(getDataSource());
    }
    
    /**
     * Returns the DatabaseSchemaLayout from this connection's driver and data source.
     * This can be used to interact with tables, views, and metadata of the schema.
     * 
     * @return DatabaseSchemaLayout.
     */
    public DatabaseSchemaLayout getSchemaLayout()
    {
        return new DatabaseSchemaLayout(getDriver(), getDataSource());
    }
    
    /**
     * Persist any changes to the connection configuration. If this is a new
     * connection, the connection will not be saved until this is called.
     */
    public void save()
    {
        JSONObject driverConfig = record.getJSONObject("driverConfig");
        JSONObject datasourceConfig = record.getJSONObject("datasourceConfig");
        
        if(driverConfig == null && datasourceConfig == null) throw new ConvirganceException("Connection not configured!");
        
        database.saveDescriptor(record);
    }
    
    /**
     * Permanently deletes this connection from the repository of stored connections
     */
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

        @Override
        public String toString()
        {
            return config.toString(4);
        }
    }
    
    /**
     * Represents the data source configuration for a driver.
     */
    public class DataSourceConfig
    {
        private JSONObject config;
        private JSONObject defaults;
        
        private DataSourceConfig(JSONObject config, JSONObject defaults)
        {
            this.config = config;
            this.defaults = defaults;
        }
        
        /**
         * Returns the property names available to the data source.
         * 
         * @return A string array of the property names.
         */
        public String[] getProperties()
        {
            if(!defaults.isEmpty()) defaults.keySet().toArray(String[]::new);
            
            return config.keySet().toArray(String[]::new);
        }
        
        /**
         * Gets the current value of a property.
         * If the property is missing from the current configuration the value of the default is returned.
         * The default value returned will most likely be null, this is the same if the property doesn't exist at all.
         *  
         * @param property Property name.
         * @return The current value.
         */
        public Object getProperty(String property)
        {
            if(!config.containsKey(property)) return defaults.get(property);
            
            return config.get(property);
        }
        
        /**
         * Updates the configuration, updating the property's value.
         * 
         * @param property Property name.
         * @param value Value.
         */
        public void setProperty(String property, String value)
        {
            this.setProperty(property, (Object)value);
        }
        
        /**
         * Updates the configuration, updating the property's value.
         * This preserves the values type, but JSONObject must be able to serialize the object.
         * 
         * @param property Property name.
         * @param value Value.
         */
        public void setProperty(String property, Object value)
        {
            config.put(property, value);
        }

        @Override
        public String toString()
        {
            return config.toString(4);
        }
    }
}

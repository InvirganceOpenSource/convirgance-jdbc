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
import com.invirgance.convirgance.jdbc.datasource.DataSourceManager;
import com.invirgance.convirgance.json.JSONObject;
import java.util.Iterator;
import javax.sql.DataSource;

/**
 * Used for creating customized reusable database connections.
 * Allows configuring the driver, the drivers data source, and the StoredConnection itself.
 * This class utilizes the builder pattern.
 * 
 * @author jbanes
 */
public class StoredConnections implements Iterable<StoredConnection>
{
    private static ConnectionDatabase database = new ConnectionDatabase();
    
    static StoredConnectionBuilder createConnection(AutomaticDriver driver, String name)
    {
        JSONObject record = new JSONObject();
        
        record.put("driver", driver.getName());
        record.put("name", name);
        
        if(getConnection(name) != null)
        {
            throw new ConvirganceException("Connection " + name + " already exists");
        }
        
        return new StoredConnectionBuilder(record, driver);
    }
    
    /**
     * Gets the StoredConnection that has the provided name.
     * This would be used to retrieve a saved StoredConnection.
     * Example: The user saved a StoredConnection and named it "CoolDatabase".
     * 
     * @param name The name assigned to the StoredConnection.
     * @return The StoredConnection.
     */
    public static StoredConnection getConnection(String name)
    {
        JSONObject descriptor = database.findDescriptorByName(name);
        
        if(descriptor == null) return null;
        
        return new StoredConnection(descriptor, database);
    }
    
    public static Iterable<StoredConnection> list()
    {
        return new StoredConnections();
    }
    
    @Override
    public Iterator<StoredConnection> iterator()
    {
        Iterator<JSONObject> connections = new ConnectionDatabase().iterator();
        
        return new Iterator<StoredConnection>() {
            
            @Override
            public boolean hasNext()
            {
                return connections.hasNext();
            }

            @Override
            public StoredConnection next()
            {
                return new StoredConnection(connections.next(), database);
            }
        };
    }
    
    /**
     * A Builder for creating and StoredConnections.
     * Example: Creating a new StoredConnection for a Driver that was retrieved 
     * with AutomaticDrivers.getDriverByName().
     */
    public static class StoredConnectionBuilder
    {
        private JSONObject record;
        private AutomaticDriver driver;

        StoredConnectionBuilder(JSONObject record, AutomaticDriver driver)
        {
            this.record = record;
            this.driver = driver;
        }
        
        /**
         * Builds and finalizes the StoredConnection.
         * At this point you could call 'save()' to preserve it or simply use the StoredConnection within the current run-time.
         * 
         * @return The StoredConnection.
         */
        public StoredConnection build()
        {
            return new StoredConnection(record, database);
        }
        
        /**
         * Returns a DriverConfigBuilder for configuring a Driver.
         * This builder can be used to setup the Driver configuration of a StoredConnection.
         * Example: Used to set the username, URL and password for the StoredConnection's driver.
         * 
         * @return DriverConfigBuilder.
         */
        public DriverConfigBuilder driver()
        {
            record.put("driverConfig", new JSONObject());
            
            return new DriverConfigBuilder(this, record.getJSONObject("driverConfig"));
        }
        
        /**
         * Returns a builder to setup Driver specific DataSource properties.
         * Example: setting the 'explicitCachingEnabled' property for the Oracle Thin Driver.
         * 
         * @return The DataSourceConfigBuilder.
         */
        public DataSourceConfigBuilder datasource()
        {
            DataSource source = driver.getDataSource();

            if(source == null) throw new ConvirganceException("DataSource is not configured on automatic driver " + record.getString("driver"));
            if(record.isNull("datasourceConfig")) record.put("datasourceConfig", new DataSourceManager(source).getConfig());
            
            return new DataSourceConfigBuilder(this, record.getJSONObject("datasourceConfig"));
        }
    }
    
    /**
     * Builds a DriverConfig for a StoredConnectionBuilder.
     */
    public static class DriverConfigBuilder
    {
        private StoredConnectionBuilder parent;
        private JSONObject config;
        
        DriverConfigBuilder(StoredConnectionBuilder parent, JSONObject config)
        {
            this.parent = parent;
            this.config = config;
        }
        
        /**
         * Builds the StoredConnection with the DriverConfig.
         * 
         * @return The StoredConnection.
         */
        public StoredConnection build()
        {
            if(config.isNull("url") || config.isNull("password"))
            {
                throw new ConvirganceException("Both Username and URL are required when configuring a JDBC Driver connection");
            }
            
            return parent.build();
        }
        
        /**
         * Returns the StoredConnectionBuilder for further building.
         * The connection is not built with this, allowing for further 
         * configuration such as building a DataSourceConfig.
         * 
         * @return The builder.
         */
        public StoredConnectionBuilder done()
        {
            if(config.isNull("url") || config.isNull("password"))
            {
                throw new ConvirganceException("Both Username and URL are required when configuring a JDBC Driver connection");
            }
            
            return parent;
        }
        
        /**
         * Sets the password for the DriverConfig used by the StoredConnection.
         * 
         * @param password The password.
         * @return This builder.
         */
        public DriverConfigBuilder password(String password)
        {
            config.put("password", password);
            
            return this;
        }
        
        /**
         * Sets the username to use for the DriverConfig of the StoredConnection.
         * 
         * @param username The username.
         * @return This builder.
         */
        public DriverConfigBuilder username(String username)
        {
            config.put("username", username);
            
            return this;
        }
        
        /**
         * Sets the database URL the DriverConfig will use for the StoredConnection.
         * 
         * @param url The database URL.
         * @return This builder.
         */
        public DriverConfigBuilder url(String url)
        {
            config.put("url", url);
            
            return this;
        }
    }
    
    /**
     * Builds the DataSourceConfig for the driver of a StoredConnection.
     */
    public static class DataSourceConfigBuilder
    {
        private StoredConnectionBuilder parent;
        private JSONObject config;
        
        DataSourceConfigBuilder(StoredConnectionBuilder parent, JSONObject config)
        {
            this.parent = parent;
            this.config = config;
        }
        
        /**
         * Builds the parent with the options setup by this builder.
         * 
         * @return The built StoredConnection.
         */
        public StoredConnection build()
        {
            return parent.build();
        }
        
        /**
         * Returns the parent builder, for further configuration.
         * Example: After calling this you could now 
         * use the DriverConfigBuilder if needed. 
         * 
         * @return The parent builder.
         */
        public StoredConnectionBuilder done()
        {
            return parent;
        }
        
        /**
         * Sets the property of the DataSource for a StoredConnection's Driver.
         * Example: assigning the Oracle Thin Driver's 'explicitCachingEnabled' property to 'true'.
         * 
         * @param name The property name.
         * @param value The value to set.
         * @return This builder.
         */
        public DataSourceConfigBuilder property(String name, Object value)
        {
            config.put(name, value);
            
            return this;
        }
    }
            
}

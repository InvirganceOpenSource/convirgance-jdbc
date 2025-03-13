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
    
    public static class StoredConnectionBuilder
    {
        private JSONObject record;
        private AutomaticDriver driver;

        StoredConnectionBuilder(JSONObject record, AutomaticDriver driver)
        {
            this.record = record;
            this.driver = driver;
        }
        
        public StoredConnection build()
        {
            return new StoredConnection(record, database);
        }
        
        public DriverConfigBuilder driver()
        {
            record.put("driverConfig", new JSONObject());
            
            return new DriverConfigBuilder(this, record.getJSONObject("driverConfig"));
        }
        
        public DataSourceConfigBuilder datasource()
        {
            DataSource source = driver.getDataSource();

            if(source == null) throw new ConvirganceException("DataSource is not configured on automatic driver " + record.getString("driver"));
            if(record.isNull("datasourceConfig")) record.put("datasourceConfig", new JSONObject());
            
            return new DataSourceConfigBuilder(this, record.getJSONObject("datasourceConfig"));
        }
    }
    
    public static class DriverConfigBuilder
    {
        private StoredConnectionBuilder parent;
        private JSONObject config;
        
        DriverConfigBuilder(StoredConnectionBuilder parent, JSONObject config)
        {
            this.parent = parent;
            this.config = config;
        }
        
        public StoredConnection build()
        {
            if(config.isNull("url") || config.isNull("password"))
            {
                throw new ConvirganceException("Both Username and URL are required when configuring a JDBC Driver connection");
            }
            
            return parent.build();
        }
        
        public StoredConnectionBuilder done()
        {
            if(config.isNull("url") || config.isNull("password"))
            {
                throw new ConvirganceException("Both Username and URL are required when configuring a JDBC Driver connection");
            }
            
            return parent;
        }
        
        public DriverConfigBuilder password(String password)
        {
            config.put("password", password);
            
            return this;
        }
        
        public DriverConfigBuilder username(String username)
        {
            config.put("username", username);
            
            return this;
        }
        
        public DriverConfigBuilder url(String url)
        {
            config.put("url", url);
            
            return this;
        }
    }
    
    public static class DataSourceConfigBuilder
    {
        private StoredConnectionBuilder parent;
        private JSONObject config;
        
        DataSourceConfigBuilder(StoredConnectionBuilder parent, JSONObject config)
        {
            this.parent = parent;
            this.config = config;
        }
        
        public StoredConnection build()
        {
            return parent.build();
        }
        
        public StoredConnectionBuilder done()
        {
            return parent;
        }
        
        public DataSourceConfigBuilder property(String name, Object value)
        {
            config.put(name, value);
            
            return this;
        }
    }
            
}

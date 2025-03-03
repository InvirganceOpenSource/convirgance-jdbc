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
import com.invirgance.convirgance.json.JSONObject;
import java.util.Iterator;

/**
 *
 * @author jbanes
 */
public class JDBCStoredConnections implements Iterable<JDBCStoredConnection>
{
    private static JDBCConnectionDatabase database = new JDBCConnectionDatabase();
    
    static StoredConnectionBuilder createConnection(JDBCAutomaticDriver driver, String name)
    {
        JSONObject record = new JSONObject();
        
        record.put("driver", driver.getName());
        record.put("name", name);
        
        //TODO: Verify the name isn't already taken
        
        return new StoredConnectionBuilder(record);
    }
    
    @Override
    public Iterator<JDBCStoredConnection> iterator()
    {
        Iterator<JSONObject> connections = new JDBCConnectionDatabase().iterator();
        
        return new Iterator<JDBCStoredConnection>() {
            
            @Override
            public boolean hasNext()
            {
                return connections.hasNext();
            }

            @Override
            public JDBCStoredConnection next()
            {
                return new JDBCStoredConnection(connections.next(), database);
            }
        };
    }
    
    public static class StoredConnectionBuilder
    {
        private JSONObject record;

        StoredConnectionBuilder(JSONObject record)
        {
            this.record = record;
        }
        
        public JDBCStoredConnection build()
        {
            return new JDBCStoredConnection(record, database);
        }
        
        public DriverConfigBuilder driver()
        {
            record.put("driverConfig", new JSONObject());
            
            return new DriverConfigBuilder(this, record.getJSONObject("driverConfig"));
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
        
        public JDBCStoredConnection build()
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
            
}

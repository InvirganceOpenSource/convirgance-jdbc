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
import com.invirgance.convirgance.jdbc.StoredConnections.StoredConnectionBuilder;
import com.invirgance.convirgance.jdbc.datasource.DataSourceManager;
import com.invirgance.convirgance.json.JSONArray;
import com.invirgance.convirgance.json.JSONObject;
import java.sql.Driver;
import java.util.Arrays;
import javax.sql.DataSource;

/**
 * Provides access to a vendor-specific database driver. Supports both 
 * <code>java.sql.Driver</code> and <code>javax.sql.DataSource</code>. 
 * 
 * @author jbanes
 */
public class AutomaticDriver
{
    private JSONObject record;
    private DriverDatabase database;
    
    AutomaticDriver(JSONObject record, DriverDatabase database)
    {
        this.record = record;
        this.database = database;
    }
    
    public String getName()
    {
        return record.getString("name");
    }
    
    public Driver getDriver()
    {
        return database.getDriver(record);
    }
    
    public void setDriver(String driverClass)
    {
        record.put("driver", driverClass);
    }
    
    /**
     * Return the class name of the Driver without initializing the object
     * 
     * @return the name of the Driver class
     */
    public String getDriverClassName()
    {
        return record.getString("driver");
    }
    
    /**
     * Returns an unconfigured <code>DataSource</code> instance for this 
     * database. Consult the documentation for the database to find what properties
     * are available to setup the connection.
     * 
     * @return an unconfigured <code>DataSource</code>
     * @see DataSourceManager
     */
    public DataSource getDataSource()
    {
        return database.getDataSource(record);
    }
    
    /**
     * Sets the DataSource the Driver will use when creating connections.
     * 
     * @param dataSourceClass The full class name of the data source
     */
    public void setDataSource(String dataSourceClass)
    {
        record.put("datasource", dataSourceClass);
    }
    
    /**
     * Return the class name of the DataSource without initializing the object
     * 
     * @return the name of the DataSource class
     */
    public String getDataSourceClassName()
    {
        return record.getString("datasource");
    }

    /**
     * Returns the artifacts that will be loaded from maven.
     * Note: Some Drivers do not include the DataSource.
     * 
     * @return The artifacts coordinates.
     */
    public String[] getArtifacts()
    {
        return ((JSONArray<String>)record.getJSONArray("artifact")).toArray(String[]::new);
    }
    
    /**
     * Set the maven artifacts for the driver, these will be used to load the driver.
     * Include the full artifact coordinates.
     * @param artifacts Artifacts
     */
    public void setArtifacts(String... artifacts)
    {
        record.put("artifact", new JSONArray(Arrays.asList(artifacts)));
    }
    
    /**
     * Returns the prefixes that can be used in connection URLs.
     * 
     * @return URL prefix strings
     */
    public String[] getPrefixes()
    {
        return ((JSONArray<String>)record.getJSONArray("prefixes")).toArray(String[]::new);
    }
    
    /**
     * Update the prefixes used by the Driver to connect to the Database.
     * 
     * @param prefixes Strings that represent connection URL prefixes.
     */
    public void setPrefixes(String... prefixes)
    {
        record.put("prefixes", new JSONArray(Arrays.asList(prefixes)));
    }
    
    public String[] getExamples()
    {
        return ((JSONArray<String>)record.getJSONArray("examples")).toArray(String[]::new);
    }
    
    /**
     * Set the connection URL examples for the driver.
     * 
     * @param examples URL Strings showcasing the connection URL for the driver.
     */
    public void setExamples(String... examples)
    {
        record.put("examples", new JSONArray(Arrays.asList(examples)));
    }
    
    private JSONObject getConfiguration()
    {
        if(!this.record.containsKey("config")) return new JSONObject();
        
        return this.record.getJSONObject("config");
    }
    
    /**
     * Return the requested driver configuration value, or the provided default 
     * value if the configuration is not found.
     *  
     * @param key the configuration value to retrieve
     * @param defaultValue the value to return if the key is not configured
     * @return the configuration value if available, or the default value otherwise
     */
    public String getConfiguration(String key, String defaultValue)
    {
        return getConfiguration().getString(key, defaultValue);
    }
    
    /**
     * Natively quotes an identifier for use in a SQL statement. If the identifier
     * contains the quote character, the quote character will be escaped by 
     * doubling the instance of the character. e.g. <code>This "Thing"</code>
     * is quoted as <code>"This ""Thing"""</code>.
     * 
     * @param name value to quote
     * @return the quoted value
     */
    public String quoteIdentifier(String name)
    {
        String quote = getConfiguration("identifierChar", "\"");
        
        name = name.replace(quote, quote + quote);
        
        return quote + name + quote;
    }
    
    /**
     * Create a new StoredConnection for permanently recording a connection to an 
     * external database. Returns a builder object to allow for rapid configuration
     * of the connection. Connection will not be saved until {@link StoredConnection#save()}
     * is called.
     * 
     * @param name a unique name that can be used to later retrieve the connection
     * @return a builder for configuring the new connection
     */
    public StoredConnectionBuilder createConnection(String name)
    {
        return StoredConnections.createConnection(this, name);
    }
    
    /**
     * Persist changes of the driver configuration to the driver list
     * 
     * @throws ConvirganceException if configured drivers cannot be loaded or 
     *         there is a database conflict
     */
    public void save() throws ConvirganceException
    {
        if(getDriver() == null) throw new ConvirganceException("Unable to validate driver " + record.get("driver") + " using artifacts " + record.getJSONArray("artifact"));
        
        database.saveDescriptor(record);
    }
    
    /**
     * Permanently remove this driver configuration from the driver list
     * 
     * @throws ConvirganceException if configured drivers cannot be loaded or 
     *         there is a database conflict
     */
    public void delete() throws ConvirganceException
    {
        database.deleteDescriptor(record);
    }

    @Override
    public String toString()
    {
        return this.record.toString(4);
    }
}

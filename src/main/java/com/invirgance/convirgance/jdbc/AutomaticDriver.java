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
import com.invirgance.convirgance.json.JSONArray;
import com.invirgance.convirgance.json.JSONObject;
import java.sql.Driver;
import java.util.Arrays;
import javax.sql.DataSource;

/**
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
    
    public DataSource getDataSource()
    {
        return database.getDataSource(record);
    }
    
    public void setDataSource(String dataSourceClass)
    {
        record.put("datasource", dataSourceClass);
    }
    
    public String[] getArtifacts()
    {
        return ((JSONArray<String>)record.getJSONArray("artifact")).toArray(String[]::new);
    }
    
    public void setArtifacts(String... artifacts)
    {
        record.put("artifact", new JSONArray(Arrays.asList(artifacts)));
    }
    
    public String[] getPrefixes()
    {
        return ((JSONArray<String>)record.getJSONArray("prefixes")).toArray(String[]::new);
    }
    
    public void setPrefixes(String... prefixes)
    {
        record.put("prefixes", new JSONArray(Arrays.asList(prefixes)));
    }
    
    public String[] getExamples()
    {
        return ((JSONArray<String>)record.getJSONArray("examples")).toArray(String[]::new);
    }
    
    public void setExamples(String... examples)
    {
        record.put("examples", new JSONArray(Arrays.asList(examples)));
    }
    
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

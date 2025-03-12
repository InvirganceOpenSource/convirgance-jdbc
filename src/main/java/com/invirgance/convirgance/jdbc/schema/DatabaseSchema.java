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
package com.invirgance.convirgance.jdbc.schema;

import com.invirgance.convirgance.ConvirganceException;
import com.invirgance.convirgance.jdbc.AutomaticDriver;
import com.invirgance.convirgance.json.JSONArray;
import com.invirgance.convirgance.json.JSONObject;
import java.sql.*;
import java.util.Arrays;
import javax.sql.DataSource;

/**
 *
 * @author jbanes
 */
public class DatabaseSchema
{
    private AutomaticDriver driver;
    private DataSource source;
    
    private String tableType;
    private String viewType;

    public DatabaseSchema(AutomaticDriver driver, DataSource source)
    {
        this.driver = driver;
        this.source = source;
        
        this.tableType = driver.getConfiguration().getString("tableType", "TABLE");
        this.viewType = driver.getConfiguration().getString("viewType", "VIEW");
    }
    
    DatabaseMetaData getMetaData()
    {
        try
        {
            return this.source.getConnection().getMetaData();
        }
        catch(SQLException e)
        {
            throw new ConvirganceException(e);
        }
    }
    
    AutomaticDriver getDriver()
    {
        return driver;
    }
    
    DataSource getDataSource()
    {
        return source;
    }
    
    JSONArray<JSONObject> getObjects(ResultSet set) throws SQLException
    {
        ResultSetMetaData metadata = set.getMetaData();
        int count = metadata.getColumnCount();
        
        JSONArray<JSONObject> array = new JSONArray<>();
        JSONObject record;
        
        while(set.next())
        {
            record = new JSONObject(true);

            for(int i=0; i<count; i++)
            {
                record.put(metadata.getColumnName(i+1), set.getObject(i+1));
            }

            if(!array.contains(record)) array.add(record);
        }
        
        return array;
    }
    
    private JSONArray<JSONObject> getDatabaseObjects()
    {   
        try(ResultSet set = getMetaData().getTables(null, null, null, null))
        {
            return getObjects(set);
        }
        catch(SQLException e)
        {
            throw new ConvirganceException(e);
        }
    }
    
    Column[] getColumns(JSONObject table)
    {
        String catalog = table.getString("TABLE_CAT");
        String schema = table.getString("TABLE_SCHEM");
        String name = table.getString("TABLE_NAME");
        
        JSONArray<Column> array = new JSONArray<>();
        
        try(ResultSet set = getMetaData().getColumns(catalog, schema, name, null))
        {
            for(JSONObject record : getObjects(set))
            {
                array.add(new Column(record));
            }
            
            return array.toArray(Column[]::new);
        }
        catch(SQLException e)
        {
            throw new ConvirganceException(e);
        }
    }
    
    String quoteIdentifier(String name)
    {
        String quote = driver.getConfiguration().getString("identifierChar", "\"");
        
        name = name.replace(quote, quote + quote);
        
        return quote + name + quote;
    }
    
    public TabularStructure[] getStructures(String type)
    {
        JSONArray<TabularStructure> structures = new JSONArray<>();
        TabularStructure structure;
        String tableType;
        
        for(JSONObject record : getDatabaseObjects())
        {
            if(type != null && !record.getString("TABLE_TYPE", "UNKNOWN").equals(type)) continue;
            
            tableType = record.getString("TABLE_TYPE");
            
            if(tableType.equals(this.tableType)) structure = new Table(record, this); 
            else if(tableType.equals(this.viewType)) structure = new View(record, this); 
            else structure = new TabularStructure(record, this);
            
            structures.add(structure);
        }
        
        return structures.toArray(TabularStructure[]::new);
    }
    
    public Catalog getCurrentCatalog()
    {
        JSONObject record = new JSONObject();
        
        try(Connection connection = source.getConnection())
        {
            record.put("TABLE_CAT", connection.getCatalog());
            
            return new Catalog(record, this);
        }
        catch(SQLException e)
        {
            throw new ConvirganceException(e);
        }
    }
    
    public Schema getCurrentSchema()
    {
        Catalog catalog = getCurrentCatalog();
        String name;
        
        try(Connection connection = source.getConnection())
        {
            name = connection.getSchema();
            
            for(Schema schema : catalog.getSchemas())
            {
                if(schema.getName().equals(name)) return schema;
            }
            
            return null;
        }
        catch(SQLException e)
        {
            throw new ConvirganceException(e);
        }
    }
    
    public Catalog[] getCatalogs()
    {
        JSONArray<Catalog> catalogs = new JSONArray<>();
        
        try(ResultSet set = getMetaData().getCatalogs())
        {
            for(JSONObject record : getObjects(set))
            {
                catalogs.add(new Catalog(record, this));
            }
            
            return catalogs.toArray(Catalog[]::new);
        }
        catch(SQLException e)
        {
            throw new ConvirganceException(e);
        }
    }
    
    public Catalog getCatalog(String name)
    {
        for(Catalog catalog : getCatalogs())
        {
            if(catalog.getName().equalsIgnoreCase(name)) return catalog;
        }
        
        return null;
    }
    
    public Table[] getTables()
    {
        String type = driver.getConfiguration().getString("tableType", "TABLE");
        
        return Arrays.asList(getStructures(type)).toArray(Table[]::new);
    }
    
    public View[] getViews()
    {
        String type = driver.getConfiguration().getString("viewType", "TABLE");
        
        return Arrays.asList(getStructures("VIEW")).toArray(View[]::new);
    }
    
    public String[] getTypes()
    {
        JSONArray<String> types = new JSONArray<>();
        
        try(ResultSet set = getMetaData().getTableTypes())
        {
            for(JSONObject record : getObjects(set))
            {
                types.add(record.getString("TABLE_TYPE"));
            }
            
            types.sort(null);
            
            return types.toArray(String[]::new);
        }
        catch(SQLException e)
        {
            throw new ConvirganceException(e);
        }
    }
}

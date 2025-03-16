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
import com.invirgance.convirgance.jdbc.callback.ConnectionCallback;
import com.invirgance.convirgance.json.JSONArray;
import com.invirgance.convirgance.json.JSONObject;
import java.sql.*;
import java.util.Arrays;
import javax.sql.DataSource;
import com.invirgance.convirgance.jdbc.callback.DatabaseMetaDataCallback;

/**
 *
 * @author jbanes
 */
public class DatabaseSchemaLayout
{
    private AutomaticDriver driver;
    private DataSource source;
    
    String tableType;
    String viewType;

    public DatabaseSchemaLayout(AutomaticDriver driver, DataSource source)
    {
        this.driver = driver;
        this.source = source;
        
        this.tableType = driver.getConfiguration("tableType", "TABLE");
        this.viewType = driver.getConfiguration("viewType", "VIEW");
    }
    
    void useMetaData(DatabaseMetaDataCallback callback)
    {
        ConnectionCallback.execute(source, callback);
    }
    
    public AutomaticDriver getDriver()
    {
        return driver;
    }
    
    public DataSource getDataSource()
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
    
    private JSONArray<JSONObject> getDatabaseObjects(String catalog, String schema, String type)
    {
        JSONArray<JSONObject> objects = new JSONArray<>();
        String[] types = type == null ? null : new String[]{ type };
        
        useMetaData(metadata -> {
            try(ResultSet set = metadata.getTables(catalog, schema, null, types))
            {
                objects.addAll(getObjects(set));
            }
        });
        
        return objects;
    }
    
    Column[] getColumns(TabularStructure table)
    {
        String catalog = table.getRecord().getString("TABLE_CAT");
        String schema = table.getRecord().getString("TABLE_SCHEM");
        String name = table.getRecord().getString("TABLE_NAME");
        
        JSONArray<Column> array = new JSONArray<>();
        
        useMetaData(metadata -> {
            try(ResultSet set = metadata.getColumns(catalog, schema, name, null))
            {
                for(JSONObject record : getObjects(set))
                {
                    array.add(new Column(record, table));
                }
            }
        });
        
        return array.toArray(Column[]::new);
    }
    
    String quoteIdentifier(String name)
    {
        return driver.quoteIdentifier(name);
    }
    
    TabularStructure[] getStructures(String catalog, String schema, String type)
    {
        JSONArray<TabularStructure> structures = new JSONArray<>();
        TabularStructure structure;
        String tableType;
        
        for(JSONObject record : getDatabaseObjects(catalog, schema, type))
        {
            if(type != null && !record.getString("TABLE_TYPE", "UNKNOWN").equals(type)) continue;
            
            tableType = record.getString("TABLE_TYPE");
            
            if(tableType.equals(this.tableType)) structure = new Table(record, this, null); 
            else if(tableType.equals(this.viewType)) structure = new View(record, this, null); 
            else structure = new TabularStructure(record, this, null);
            
            structures.add(structure);
        }
        
        return structures.toArray(TabularStructure[]::new);
    }
    
    public TabularStructure[] getAllStructures()
    {
        return getAllStructures(null);
    }
    
    public TabularStructure[] getAllStructures(String type)
    {
        return getStructures(null, null, type);
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
        
        useMetaData(metadata -> {
            try(ResultSet set = metadata.getCatalogs())
            {
                for(JSONObject record : getObjects(set))
                {
                    catalogs.add(new Catalog(record, this));
                }
            }
        });
        
        return catalogs.toArray(Catalog[]::new);
    }
    
    public Catalog getCatalog(String name)
    {
        for(Catalog catalog : getCatalogs())
        {
            if(catalog.getName().equalsIgnoreCase(name)) return catalog;
        }
        
        return null;
    }
    
    public Table[] getAllTables()
    {
        return Arrays.asList(getAllStructures(tableType)).toArray(Table[]::new);
    }
    
    public View[] getAllViews()
    {
        return Arrays.asList(getAllStructures(viewType)).toArray(View[]::new);
    }
    
    public String[] getTypes()
    {
        JSONArray<String> types = new JSONArray<>();
        
        useMetaData(metadata -> {
            try(ResultSet set = metadata.getTableTypes())
            {
                for(JSONObject record : getObjects(set))
                {
                    types.add(record.getString("TABLE_TYPE"));
                }
            }
        });
        
        types.sort(null);

        return types.toArray(String[]::new);
    }
}

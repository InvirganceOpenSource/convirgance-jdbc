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
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
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

    public DatabaseSchema(AutomaticDriver driver, DataSource source)
    {
        this.driver = driver;
        this.source = source;
    }
    
    private DatabaseMetaData getMetaData()
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
    
    private JSONArray<JSONObject> getObjects(ResultSet set) throws SQLException
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
    
    public TabularStructure[] getStructures(String type)
    {
        JSONArray<TabularStructure> structures = new JSONArray<>();
        TabularStructure structure;
        
        for(JSONObject record : getDatabaseObjects())
        {
            if(type != null && !record.getString("TABLE_TYPE", "UNKNOWN").equals(type)) continue;
            
            switch(record.getString("TABLE_TYPE"))
            {
                case "TABLE":
                    structure = new Table(record, this); 
                    break;
                    
                case "VIEW":
                    structure = new View(record, this); 
                    break;
                    
                default:
                    structure = new TabularStructure(record, this);
            }
            
            structures.add(structure);
        }
        
        return structures.toArray(TabularStructure[]::new);
    }
    
    public Table[] getTables()
    {
        return Arrays.asList(getStructures("TABLE")).toArray(Table[]::new);
    }
    
    public View[] getViews()
    {
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

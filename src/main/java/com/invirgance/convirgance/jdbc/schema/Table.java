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
package com.invirgance.convirgance.jdbc.schema;

import com.invirgance.convirgance.ConvirganceException;
import com.invirgance.convirgance.dbms.DBMS;
import com.invirgance.convirgance.dbms.Query;
import com.invirgance.convirgance.json.JSONArray;
import com.invirgance.convirgance.json.JSONObject;
import java.sql.ResultSet;
import java.util.Iterator;

/**
 *
 * @author jbanes
 */
public class Table extends TabularStructure implements Iterable<JSONObject>
{
    Table(JSONObject record, DatabaseSchemaLayout layout, Schema schema)
    {
        super(record, layout, schema);
    }
    
    /**
     * Obtain the primary key {@link Column}. Throws an exception if the primary key is 
     * multi-column.
     * 
     * @return the primary key {@link Column}
     * @throws ConvirganceException if primary key is multi-column
     */
    public Column getPrimaryKey()
    {
        Column[] primary = getPrimaryKeys();
        
        if(primary.length > 1) throw new ConvirganceException("Requested single primary key when table \"" + getName() + "\" primary key consists of " + primary.length + " columns");
        
        return primary[0];
    }
    
    /**
     * Obtain the primary key columns. Columns are guaranteed to be returned in
     * primary key order.
     * 
     * @return an array of {@link Column} objects representing the primary key
     */
    public Column[] getPrimaryKeys()
    {
        Column[] columns = getColumns();
        Column[] primary;
        
        DatabaseSchemaLayout layout = getLayout();
        JSONArray<JSONObject> keys = new JSONArray<>();
        
        layout.useMetaData(metadata -> {
            try(ResultSet set = metadata.getPrimaryKeys(getSchema().getCatalog().getName(), getSchema().getName(), getName()))
            {
                keys.addAll(layout.getObjects(set));
            }
        });
        
        primary = new Column[keys.size()];
        
        for(JSONObject key : keys)
        {
            for(Column column : columns)
            {
                if(!column.getName().equals(key.getString("COLUMN_NAME"))) continue;
                
                primary[key.getInt("KEY_SEQ")-1] = column;
                break;
            }
        }
        
        return primary;
    }
    
    public Query generateSelect()
    {
        StringBuffer buffer = new StringBuffer("select \n");
        
        for(Column column : getColumns())
        {
            if(buffer.length() > 9) buffer.append(",\n");
            
            buffer.append("    ");
            buffer.append(getLayout().quoteIdentifier(column.getName()));
        }
        
        buffer.append("\n");
        buffer.append("from ");
        buffer.append(getLayout().quoteIdentifier(getName()));
        
        return new Query(buffer.toString());
    }

    @Override
    public Iterator<JSONObject> iterator()
    {
        return new DBMS(getLayout().getDataSource()).query(generateSelect()).iterator();
    }
}

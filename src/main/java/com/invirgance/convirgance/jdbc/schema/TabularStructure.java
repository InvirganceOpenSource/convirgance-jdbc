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

import com.invirgance.convirgance.json.JSONObject;

/**
 * A way to represent different database schema objects like Tables and Views.
 * 
 * @author jbanes
 */
public class TabularStructure implements NamedSchema
{    
    private JSONObject record;
    private DatabaseSchemaLayout layout;
    private Schema schema;

    TabularStructure(JSONObject record, DatabaseSchemaLayout layout, Schema schema)
    {
        this.record = record;
        this.layout = layout;
        this.schema = schema;
    }
    
    JSONObject getRecord()
    {
        return record;
    }
    
    DatabaseSchemaLayout getLayout()
    {
        return layout;
    }
    
    void setSchema(Schema schema)
    {
        this.schema = schema;
    }
    
    @Override
    public String getName()
    {
        return record.getString("TABLE_NAME");
    }
    
    @Override
    public String getQuotedName()
    {
        return layout.quoteIdentifier(getName());
    }
    
    /**
     * Returns the schema this object is apart of.
     * 
     * @return The Schema
     */
    public Schema getSchema()
    {
        if(this.schema != null) return schema;
        
        this.schema = layout.getCatalog(record.getString("TABLE_CAT")).getSchema(record.getString("TABLE_SCHEM"));
        
        return this.schema;
    }
    
    /**
     * Returns the TABLE_TYPE this object is recognized as.
     * 
     * @return A String of the objects table type.
     */
    public String getType()
    {
        return record.getString("TABLE_TYPE", "UNKNOWN");
    }
    
    /**
     * Returns the column with the provided name.
     * For example getting the column for first_names.
     * 
     * @param name The name of a column.
     * @return The column.
     */
    public Column getColumn(String name)
    {
        for(Column column : getColumns())
        {
            if(column.getName().equalsIgnoreCase(name)) return column;
        }
        
        return null;
    }
    
    /**
     * Returns all columns of this object.
     * 
     * @return Array of Columns.
     */
    public Column[] getColumns()
    {
        return layout.getColumns(this);
    }

    /**
     * Returns the hash code.
     * Hash-code is retrieved from the record used to create this object.
     * 
     * @return A integer.
     */
    @Override
    public int hashCode()
    {
        return record.hashCode();
    }

    @Override
    public boolean equals(Object obj)
    {
        if(!(obj instanceof TabularStructure)) return false;
        
        return record.equals(((TabularStructure)obj).record);
    }

    @Override
    public String toString()
    {
        return this.record.toString(4);
    }
}

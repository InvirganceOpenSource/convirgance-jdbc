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
 *
 * @author jbanes
 */
public class TabularStructure
{    
    private JSONObject record;
    private DatabaseSchema schema;

    TabularStructure(JSONObject record, DatabaseSchema schema)
    {
        this.record = record;
        this.schema = schema;
    }
    
    JSONObject getRecord()
    {
        return record;
    }
    
    public String getName()
    {
        return record.getString("TABLE_NAME");
    }
    
    public String getType()
    {
        return record.getString("TABLE_TYPE", "UNKNOWN");
    }
    
    public Column[] getColumns()
    {
        return schema.getColumns(this.record);
    }

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

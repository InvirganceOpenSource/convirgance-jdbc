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

import com.invirgance.convirgance.dbms.DBMS;
import com.invirgance.convirgance.jdbc.sql.SelectStatement;
import com.invirgance.convirgance.json.JSONObject;
import java.util.Iterator;

/**
 * @author jbanes
 */
public class View extends TabularStructure implements Iterable<JSONObject>
{
    public View(JSONObject record, DatabaseSchemaLayout layout, Schema schema)
    {
        super(record, layout, schema);
    }
    
    /**
     * Creates a select statement for this view.
     * @return SelectStatement to query against this view.
     */
    public SelectStatement select()
    {
        SelectStatement select = new SelectStatement(getLayout());
        
        for(Column column : getColumns())
        {
            select.column(column);
        }
        
        return select.from(this);
    }

    @Override
    public Iterator<JSONObject> iterator()
    {
        return new DBMS(getLayout().getDataSource()).query(select().query()).iterator();
    }
}

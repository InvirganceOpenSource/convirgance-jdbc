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

import com.invirgance.convirgance.json.JSONArray;
import com.invirgance.convirgance.json.JSONObject;
import java.sql.ResultSet;

/**
 *
 * @author jbanes
 */
public class Catalog implements NamedSchema
{
    private JSONObject record;
    private DatabaseSchemaLayout layout;

    Catalog(JSONObject record, DatabaseSchemaLayout layout)
    {
        this.record = record;
        this.layout = layout;
    }
    
    @Override
    public String getName()
    {
        // Specs say "TABLE_CAT", but some databases appear to be returning CATALOG_NAME
        return record.getString("CATALOG_NAME", record.getString("TABLE_CAT"));
    }
    
    @Override
    public String getQuotedName()
    {
        return layout.quoteIdentifier(getName());
    }
    
    public Schema[] getSchemas()
    {
        JSONArray<Schema> schemas = new JSONArray<>();
        
        layout.useMetaData(metadata -> {
            try(ResultSet set = metadata.getSchemas(getName(), null))
            {
                for(JSONObject record : layout.getObjects(set))
                {
                    schemas.add(new Schema(record, layout, this));
                }
            }
        });
            
        return schemas.toArray(Schema[]::new);
    }
    
    public Schema getSchema(String name)
    {
        for(Schema schema : getSchemas())
        {
            if(schema.getName().equalsIgnoreCase(name)) return schema;
        }
        
        return null;
    }

    @Override
    public String toString()
    {
        return this.record.toString();
    }
}

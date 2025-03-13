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
import java.util.Arrays;

/**
 *
 * @author jbanes
 */
public class Schema
{
    private JSONObject record;
    private DatabaseSchemaLayout schema;
    private Catalog catalog;

    Schema(JSONObject record, DatabaseSchemaLayout schema, Catalog catalog)
    {
        this.record = record;
        this.schema = schema;
    }
    
    public Catalog getCatalog()
    {
        if(catalog != null) return catalog;
        
        return schema.getCatalog(record.getString("TABLE_CATALOG"));
    }
    
    public String getName()
    {
        return record.getString("TABLE_SCHEM");
    }
    
    public boolean isDefault()
    {
        return record.getBoolean("IS_DEFAULT", false);
    }
    
    public Table[] getTables()
    {
        TabularStructure[] structures = schema.getStructures(record.getString("TABLE_CATALOG"), getName(), schema.tableType);
        
        // Optimization that prevents excessive database lookups for schema
        for(TabularStructure structure : structures) structure.setSchema(this);
        
        return Arrays.asList(structures).toArray(Table[]::new);
    }
    
    public View[] getViews()
    {
        TabularStructure[] structures = schema.getStructures(record.getString("TABLE_CATALOG"), getName(), schema.viewType);
        
        return Arrays.asList(structures).toArray(View[]::new);
    }

    @Override
    public String toString()
    {
        return this.record.toString();
    }
}

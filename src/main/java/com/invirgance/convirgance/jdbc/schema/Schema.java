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
public class Schema implements NamedSchema
{
    private JSONObject record;
    private DatabaseSchemaLayout layout;
    private Catalog catalog;

    Schema(JSONObject record, DatabaseSchemaLayout schema, Catalog catalog)
    {
        this.record = record;
        this.layout = schema;
    }
    
    public Catalog getCatalog()
    {
        if(catalog != null) return catalog;
        
        this.catalog = layout.getCatalog(record.getString("TABLE_CATALOG"));
        
        return this.catalog;
    }
    
    @Override
    public String getName()
    {
        return record.getString("TABLE_SCHEM");
    }
    
    @Override
    public String getQuotedName()
    {
        return layout.quoteIdentifier(getName());
    }
    
    public boolean isDefault()
    {
        return record.getBoolean("IS_DEFAULT", false);
    }
    
    public Table getTable(String name)
    {
        for(Table table : getTables())
        {
            if(table.getName().equalsIgnoreCase(name)) return table;
        }
        
        return null;
    }
    
    public Table[] getTables()
    {
        TabularStructure[] structures = layout.getStructures(record.getString("TABLE_CATALOG"), getName(), layout.tableType);
        
        // Optimization that prevents excessive database lookups for schema
        for(TabularStructure structure : structures) structure.setSchema(this);
        
        return Arrays.asList(structures).toArray(Table[]::new);
    }
    
    public View getView(String name)
    {
        for(View view : getViews())
        {
            if(view.getName().equalsIgnoreCase(name)) return view;
        }
        
        return null;
    }
    
    public View[] getViews()
    {
        TabularStructure[] structures = layout.getStructures(record.getString("TABLE_CATALOG"), getName(), layout.viewType);
        
        return Arrays.asList(structures).toArray(View[]::new);
    }

    @Override
    public String toString()
    {
        return this.record.toString();
    }
}

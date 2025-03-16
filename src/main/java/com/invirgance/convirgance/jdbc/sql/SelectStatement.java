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
package com.invirgance.convirgance.jdbc.sql;

import com.invirgance.convirgance.jdbc.schema.Column;
import com.invirgance.convirgance.jdbc.schema.DatabaseSchemaLayout;
import com.invirgance.convirgance.jdbc.schema.Table;
import com.invirgance.convirgance.json.JSONArray;

/**
 *
 * @author jbanes
 */
public class SelectStatement implements SQLStatement
{
    private JSONArray<ExpressionStatement> columns = new JSONArray<>();
    private JSONArray<Table> tables = new JSONArray<>();

    private DatabaseSchemaLayout layout;
    private Table from;
    
    private SQLRenderer renderer = new SQLRenderer();
    
    public SelectStatement(DatabaseSchemaLayout layout)
    {
        this.layout = layout;
    }
    
    @Override
    public SQLRenderer getRenderer()
    {
        return renderer;
    }

    public ExpressionStatement[] getColumns()
    {
        return columns.toArray(ExpressionStatement[]::new);
    }
    
    /**
     * Adds a column to the select clause
     * 
     * @param column Column to select
     * @return this object for chaining
     */
    public SelectStatement column(Column column)
    {
        columns.add(new ColumnExpressionStatement(layout, column, null, this));
        
        if(!tables.contains((Table)column.getParent())) tables.add((Table)column.getParent());
        
        return this;
    }
    
    /**
     * Adds a column to the select clause with the specified result set name
     * 
     * @param column Column to select
     * @param name to generate "as" clause
     * @return this object for chaining
     */
    public SelectStatement column(Column column, String name)
    {
        columns.add(new ColumnExpressionStatement(layout, column, name, this));
        
        if(!tables.contains((Table)column.getParent())) tables.add((Table)column.getParent());
        
        return this;
    }
    
    public SelectStatement from(Table table)
    {
        this.from = table;
        
        if(!tables.contains(table)) tables.add(table);
        
        return this;
    }

    @Override
    public SQLRenderer render(SQLRenderer renderer)
    {
        Table from = this.from != null ? this.from : !this.tables.isEmpty() ? this.tables.get(0) : null;
 
        renderer
            .keyword("select");
        
        for(ExpressionStatement column : this.columns)
        {
            renderer.statement(column);
        }
        
        renderer
            .keyword("from")
            .schema(from)
            .endStatement();
        
        return renderer;
    }

    @Override
    public String toString()
    {
        return render(renderer.reset()).toString();
    }
}

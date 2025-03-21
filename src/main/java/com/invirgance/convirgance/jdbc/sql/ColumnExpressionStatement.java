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

/**
 *
 * @author jbanes
 */
public class ColumnExpressionStatement implements ExpressionStatement
{
    private DatabaseSchemaLayout layout;
    private SQLStatement parent;
    
    private Column column;
    private String name;
    
    
    public ColumnExpressionStatement(DatabaseSchemaLayout layout, Column column)
    {
        this(layout, column, null);
    }
    
    public ColumnExpressionStatement(DatabaseSchemaLayout layout, Column column, String name)
    {
        this(layout, column, name, null);
    }
    
    ColumnExpressionStatement(DatabaseSchemaLayout layout, Column column, String name, SQLStatement parent)
    {
        this.layout = layout;
        this.column = column;
        this.name = name;
        this.parent = parent;
    }
    
    @Override
    public SQLStatement getParent()
    {
        return this.parent;
    }
    
    public void setParent(SQLStatement parent)
    {
        this.parent = parent;
    }
    
    public Column getColumn()
    {
        return this.column;
    }

    @Override
    public String getName()
    {
        return (name == null) ? column.getName() : name;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }

    @Override
    public String getQuotedName()
    {
        return layout.getDriver().quoteIdentifier(getName());
    }

    @Override
    public SQLRenderer render(SQLRenderer renderer)
    {
        //TODO: Need to generate prefix with table reference for multiple tables
        renderer.column(column);
        
        if(this.name != null)
        {
            renderer
                .keyword(Keyword.AS)
                .schema(this);
        }
        
        return renderer;
    }
    
    @Override
    public String toString()
    {
        return render(new SQLRenderer()).toString();
    }
}

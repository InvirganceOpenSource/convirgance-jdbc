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
import com.invirgance.convirgance.json.JSONArray;

/**
 *
 * @author jbanes
 */
public class WhereStatement implements SQLStatement
{
    private DatabaseSchemaLayout layout;
    private SQLStatement parent;
    
    private JSONArray<ComparisonStatement> clauses = new JSONArray<>();

    
    public WhereStatement(DatabaseSchemaLayout layout)
    {
        this(layout, null);
    }
    
    public WhereStatement(DatabaseSchemaLayout layout, SQLStatement parent)
    {
        this.layout = layout;
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
    
    public WhereStatement equals(ExpressionStatement column, ExpressionStatement value)
    {
        EqualsComparisonStatement comparison = new EqualsComparisonStatement(layout, column, value, this);
        
        column.setParent(comparison);
        value.setParent(comparison);
        
        clauses.add(comparison);
        
        return this;
    }
    
    public WhereStatement equals(Object column, Object value)
    {
        ExpressionStatement left = new LiteralExpressionStatement(layout, column, null);
        ExpressionStatement right = new LiteralExpressionStatement(layout, value, null);
        
        return equals(left, right);
    }
    
    public WhereStatement equals(Column column, Object value)
    {
        ExpressionStatement left = new ColumnExpressionStatement(layout, column, null);
        ExpressionStatement right = new LiteralExpressionStatement(layout, value, null);
        
        return equals(left, right);
    }
    
    public WhereStatement equals(Column column, Column value)
    {
        ExpressionStatement left = new ColumnExpressionStatement(layout, column, null);
        ExpressionStatement right = new ColumnExpressionStatement(layout, value, null);
        
        return equals(left, right);
    }
    
    public WhereStatement equals(Column column, BindVariable value)
    {
        ExpressionStatement left = new ColumnExpressionStatement(layout, column, null);
        ExpressionStatement right = new BindExpressionStatement(layout, value, null);
        
        return equals(left, right);
    }
    
    @Override
    public SQLRenderer render(SQLRenderer renderer)
    {
        boolean written = false;
        
        for(ComparisonStatement statement : this.clauses)
        {
            if(!written)
            {
                renderer.keyword(Keyword.WHERE);
                written = true;
            }
            else
            {
                renderer.keyword(Keyword.AND);
            }
            
            renderer.statement(statement);
        }
        
        return renderer;
    }

    @Override
    public String toString()
    {
        return render(new SQLRenderer()).toString();
    }
}

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
 * @param <P> the parent type
 */
public class WhereStatement<P extends SQLStatement> implements SQLStatement
{
    private DatabaseSchemaLayout layout;
    private P parent;
    
    private JSONArray<ComparisonStatement> clauses = new JSONArray<>();

    
    public WhereStatement(DatabaseSchemaLayout layout)
    {
        this(layout, null);
    }
    
    public WhereStatement(DatabaseSchemaLayout layout, P parent)
    {
        this.layout = layout;
        this.parent = parent;
    }

    @Override
    public SQLStatement getParent()
    {
        return this.parent;
    }

    @Override
    public void setParent(SQLStatement parent)
    {
        this.parent = (P)parent;
    }
    
    public WhereStatement<P> filter(ExpressionStatement column, ComparisonOperator operator, ExpressionStatement value)
    {
        ComparisonOperatorStatement comparison = new ComparisonOperatorStatement(layout, column, operator, value, this);
        
        column.setParent(comparison);
        value.setParent(comparison);
        
        clauses.add(comparison);
        
        return this;
    }
    
    public WhereStatement<P> filter(Object column, ComparisonOperator operator, Object value)
    {
        ExpressionStatement left = new LiteralExpressionStatement(layout, column, null);
        ExpressionStatement right = new LiteralExpressionStatement(layout, value, null);
        
        return filter(left, operator, right);
    }
    
    public WhereStatement<P> filter(Column column, ComparisonOperator operator, Object value)
    {
        ExpressionStatement left = new ColumnExpressionStatement(layout, column, null);
        ExpressionStatement right = new LiteralExpressionStatement(layout, value, null);
        
        return filter(left, operator, right);
    }
    
    public WhereStatement<P> filter(Column column, ComparisonOperator operator, Column value)
    {
        ExpressionStatement left = new ColumnExpressionStatement(layout, column, null);
        ExpressionStatement right = new ColumnExpressionStatement(layout, value, null);
        
        return filter(left, operator, right);
    }
    
    public WhereStatement<P> filter(Column column, ComparisonOperator operator, BindVariable value)
    {
        ExpressionStatement left = new ColumnExpressionStatement(layout, column, null);
        ExpressionStatement right = new BindExpressionStatement(layout, value, null);
        
        return filter(left, operator, right);
    }
    
    public WhereStatement<P> equals(ExpressionStatement column, ExpressionStatement value)
    {
        return filter(column, ComparisonOperator.EQUAL, value);
    }
    
    public WhereStatement<P> equals(Object column, Object value)
    {
        return filter(column, ComparisonOperator.EQUAL, value);
    }
    
    public WhereStatement<P> equals(Column column, Object value)
    {
        return filter(column, ComparisonOperator.EQUAL, value);
    }
    
    public WhereStatement<P> equals(Column column, Column value)
    {
        return filter(column, ComparisonOperator.EQUAL, value);
    }
    
    public WhereStatement<P> equals(Column column, BindVariable value)
    {
        return filter(column, ComparisonOperator.EQUAL, value);
    }
    
    public WhereStatement<P> notEquals(ExpressionStatement column, ExpressionStatement value)
    {
        return filter(column, ComparisonOperator.NOT_EQUAL, value);
    }
    
    public WhereStatement<P> notEquals(Object column, Object value)
    {
        return filter(column, ComparisonOperator.NOT_EQUAL, value);
    }
    
    public WhereStatement<P> notEquals(Column column, Object value)
    {
        return filter(column, ComparisonOperator.NOT_EQUAL, value);
    }
    
    public WhereStatement<P> notEquals(Column column, Column value)
    {
        return filter(column, ComparisonOperator.NOT_EQUAL, value);
    }
    
    public WhereStatement<P> notEquals(Column column, BindVariable value)
    {
        return filter(column, ComparisonOperator.NOT_EQUAL, value);
    }
    
    public WhereStatement<P> greaterThan(ExpressionStatement column, ExpressionStatement value)
    {
        return filter(column, ComparisonOperator.GREATER_THAN, value);
    }
    
    public WhereStatement<P> greaterThan(Object column, Object value)
    {
        return filter(column, ComparisonOperator.GREATER_THAN, value);
    }
    
    public WhereStatement<P> greaterThan(Column column, Object value)
    {
        return filter(column, ComparisonOperator.GREATER_THAN, value);
    }
    
    public WhereStatement<P> greaterThan(Column column, Column value)
    {
        return filter(column, ComparisonOperator.GREATER_THAN, value);
    }
    
    public WhereStatement<P> greaterThan(Column column, BindVariable value)
    {
        return filter(column, ComparisonOperator.GREATER_THAN, value);
    }
    
    public WhereStatement<P> greaterThanOrEquals(ExpressionStatement column, ExpressionStatement value)
    {
        return filter(column, ComparisonOperator.GREATER_THAN_OR_EQUAL, value);
    }
    
    public WhereStatement<P> greaterThanOrEquals(Object column, Object value)
    {
        return filter(column, ComparisonOperator.GREATER_THAN_OR_EQUAL, value);
    }
    
    public WhereStatement<P> greaterThanOrEquals(Column column, Object value)
    {
        return filter(column, ComparisonOperator.GREATER_THAN_OR_EQUAL, value);
    }
    
    public WhereStatement<P> greaterThanOrEquals(Column column, Column value)
    {
        return filter(column, ComparisonOperator.GREATER_THAN_OR_EQUAL, value);
    }
    
    public WhereStatement<P> greaterThanOrEquals(Column column, BindVariable value)
    {
        return filter(column, ComparisonOperator.GREATER_THAN_OR_EQUAL, value);
    }
    
    public WhereStatement<P> lessThan(ExpressionStatement column, ExpressionStatement value)
    {
        return filter(column, ComparisonOperator.LESS_THAN, value);
    }
    
    public WhereStatement<P> lessThan(Object column, Object value)
    {
        return filter(column, ComparisonOperator.LESS_THAN, value);
    }
    
    public WhereStatement<P> lessThan(Column column, Object value)
    {
        return filter(column, ComparisonOperator.LESS_THAN, value);
    }
    
    public WhereStatement<P> lessThan(Column column, Column value)
    {
        return filter(column, ComparisonOperator.LESS_THAN, value);
    }
    
    public WhereStatement<P> lessThan(Column column, BindVariable value)
    {
        return filter(column, ComparisonOperator.LESS_THAN, value);
    }
    
    public WhereStatement<P> lessThanOrEquals(ExpressionStatement column, ExpressionStatement value)
    {
        return filter(column, ComparisonOperator.LESS_THAN_OR_EQUAL, value);
    }
    
    public WhereStatement<P> lessThanOrEquals(Object column, Object value)
    {
        return filter(column, ComparisonOperator.LESS_THAN_OR_EQUAL, value);
    }
    
    public WhereStatement<P> lessThanOrEquals(Column column, Object value)
    {
        return filter(column, ComparisonOperator.LESS_THAN_OR_EQUAL, value);
    }
    
    public WhereStatement<P> lessThanOrEquals(Column column, Column value)
    {
        return filter(column, ComparisonOperator.LESS_THAN_OR_EQUAL, value);
    }
    
    public WhereStatement<P> lessThanOrEquals(Column column, BindVariable value)
    {
        return filter(column, ComparisonOperator.LESS_THAN_OR_EQUAL, value);
    }
    
    public WhereStatement<P> isNull(ExpressionStatement column)
    {
        IsNullComparisonStatement comparison = new IsNullComparisonStatement(layout, column, this);
        
        column.setParent(comparison);
        clauses.add(comparison);
        
        return this;
    }
    
    public WhereStatement<P> isNull(Object column)
    {
        return isNull(new LiteralExpressionStatement(layout, column));
    }
    
    public WhereStatement<P> isNull(Column column)
    {
        return isNull(new ColumnExpressionStatement(layout, column));
    }
    
    public WhereStatement<P> isNull(BindVariable column)
    {
        return isNull(new BindExpressionStatement(layout, column));
    }
    
    public WhereStatement<P> isNotNull(ExpressionStatement column)
    {
        IsNotNullComparisonStatement comparison = new IsNotNullComparisonStatement(layout, column, this);
        
        column.setParent(comparison);
        clauses.add(comparison);
        
        return this;
    }
    
    public WhereStatement<P> isNotNull(Object column)
    {
        return isNotNull(new LiteralExpressionStatement(layout, column));
    }
    
    public WhereStatement<P> isNotNull(Column column)
    {
        return isNotNull(new ColumnExpressionStatement(layout, column));
    }
    
    public WhereStatement<P> isNotNull(BindVariable column)
    {
        return isNotNull(new BindExpressionStatement(layout, column));
    }
    
    public P done()
    {
        return parent;
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

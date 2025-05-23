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

import com.invirgance.convirgance.jdbc.schema.DatabaseSchemaLayout;

/**
 * Represents the "IS NOT NULL" expression, for creating comparisons on expressions.
 * 
 * <pre><code>
 * DatabaseSchemaLayout layout = new DatabaseSchemaLayout(driver, getHSQLDataSource());
 * 
 * Table table = layout.getCurrentSchema().getTable("customer");
 * 
 * ExpressionStatement children = new ColumnExpressionStatement(layout, table.getColumn("children"));
 * 
 * IsNotNullComparisonStatement statement = 
 * new IsNotNullComparisonStatement(layout, children);
 * </code></pre>
 * 
 * @author jbanes
 */
public class IsNotNullComparisonStatement extends IsNullComparisonStatement
{
    /**
     * Creates the statement with only the layout...
     * 
     * @param layout The database layout.
     */
    public IsNotNullComparisonStatement(DatabaseSchemaLayout layout)
    {
        super(layout);
    }
    
    /**
     * Creates the statement with the provided database layout and expression.
     * 
     * @param layout Database layout.
     * @param expression Expression statement.
     */
    public IsNotNullComparisonStatement(DatabaseSchemaLayout layout, ExpressionStatement expression)
    {
        super(layout, expression);
    }
    
    IsNotNullComparisonStatement(DatabaseSchemaLayout layout, ExpressionStatement expression, SQLStatement parent)
    {
        super(layout, expression, parent);
    }

    @Override
    public SQLRenderer render(SQLRenderer renderer)
    {
        renderer.statement(getExpression());
        renderer.operator(ComparisonOperator.IS_NOT_NULL);
        
        return renderer;
    }
    
    
}

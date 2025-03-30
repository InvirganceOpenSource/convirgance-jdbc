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
 * Represents the SQL "IS NULL" comparison operator in conditions.
 * This class creates SQL expressions that check whether a column or expression
 * is NULL (contains no value) in database queries.
 * 
 * @author jbanes
 */
public class IsNullComparisonStatement implements ComparisonStatement
{
    private DatabaseSchemaLayout layout;
    private SQLStatement parent;
    
    private ExpressionStatement expression;
    
    /**
     * Creates the statement using the provided database layout.
     * 
     * @param layout The layout.
     */
    public IsNullComparisonStatement(DatabaseSchemaLayout layout)
    {
        this(layout, null);
    }
    
    /**
     * Creates the statement using the provided layout and expression.
     * 
     * @param layout The layout.
     * @param expression The expression.
     */
    public IsNullComparisonStatement(DatabaseSchemaLayout layout, ExpressionStatement expression)
    {
        this(layout, expression, null);
    }
    
    IsNullComparisonStatement(DatabaseSchemaLayout layout, ExpressionStatement expression, SQLStatement parent)
    {
        this.layout = layout;
        this.parent = parent;
        this.expression = expression;
    }
    
    @Override
    public SQLStatement getParent()
    {
        return this.parent;
    }

    @Override
    public void setParent(SQLStatement parent)
    {
        this.parent = parent;
    }

    /**
     * Returns the current expression that will be evaluated in the query.
     * 
     * @return ExpressionStatement
     */
    public ExpressionStatement getExpression()
    {
        return expression;
    }

    /**
     * Sets the expression to evaluate in the comparison.
     * 
     * @param expression ExpressionStatement
     */
    public void setExpression(ExpressionStatement expression)
    {
        this.expression = expression;
    }

    @Override
    public SQLRenderer render(SQLRenderer renderer)
    {
        renderer.statement(expression);
        renderer.operator(ComparisonOperator.IS_NULL);
        
        return renderer;
    }
}

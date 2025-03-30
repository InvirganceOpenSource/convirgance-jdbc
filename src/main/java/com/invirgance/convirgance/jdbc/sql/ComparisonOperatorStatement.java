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
 * Represents a binary comparison operation in SQL with a left expression, 
 * comparison operator, and right expression. Forms the foundation of 
 * conditional logic in WHERE clauses and other filter expressions.
 * 
 * @author jbanes
 */
public class ComparisonOperatorStatement implements ComparisonStatement
{
    private DatabaseSchemaLayout layout;
    private SQLStatement parent;
    
    private ExpressionStatement left;
    private ExpressionStatement right;
    private ComparisonOperator operator;

    /**
     * Creates an empty comparison statement with just the database layout.
     * 
     * @param layout The database layout.
     */
    public ComparisonOperatorStatement(DatabaseSchemaLayout layout)
    {
        this(layout, null, null, null);
    }
    
    /**
    * Creates a complete comparison statement ready for use in WHERE clauses.
    * This builds expressions like "column = value" or "price > :minPrice".
    * 
    * @param layout The database layout.
    * @param left The left side of the comparison.
    * @param operator The comparison type {@link ComparisonOperator}
    * @param right The right side of the comparison (value, variable, or another column)
    */
    public ComparisonOperatorStatement(DatabaseSchemaLayout layout, ExpressionStatement left, ComparisonOperator operator, ExpressionStatement right)
    {
        this(layout, left, operator, right, null);
    }
    
    ComparisonOperatorStatement(DatabaseSchemaLayout layout, ExpressionStatement left, ComparisonOperator operator, ExpressionStatement right, SQLStatement parent)
    {
        this.layout = layout;
        this.parent = parent;
        this.left = left;
        this.right = right;
        this.operator = operator;
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
     * Returns the left expression.
     * 
     * @return ExpressionStatement.
     */
    public ExpressionStatement getLeft()
    {
        return left;
    }

    /**
     * Sets the left expression to use in the comparison.
     * 
     * @param left ExpressionStatement.
     */
    public void setLeft(ExpressionStatement left)
    {
        this.left = left;
    }

    /**
     * Returns the right expression.
     * 
     * @return ExpressionStatement.
     */
    public ExpressionStatement getRight()
    {
        return right;
    }

    /**
     * Sets the right expression that will be used in the comparison.
     * 
     * @param right ExpressionStatement.
     */
    public void setRight(ExpressionStatement right)
    {
        this.right = right;
    }

    /**
     * Returns the operator that will be used in for the comparison.
     * 
     * @return ComparisonOperator.
     */
    public ComparisonOperator getOperator()
    {
        return operator;
    }

    /**
     * Sets the comparison operator that will be used in the query when comparing the 
     * expressions.
     * 
     * @param operator ComparisonOperator.
     */
    public void setOperator(ComparisonOperator operator)
    {
        this.operator = operator;
    }

    @Override
    public SQLRenderer render(SQLRenderer renderer)
    {
        renderer.statement(left);
        renderer.operator(operator);
        renderer.statement(right);
        
        return renderer;
    }
    
}

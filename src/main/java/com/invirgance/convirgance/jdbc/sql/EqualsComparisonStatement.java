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
 *
 * @author jbanes
 */
public class EqualsComparisonStatement implements ComparisonStatement
{
    private DatabaseSchemaLayout layout;
    private SQLStatement parent;
    
    private ExpressionStatement left;
    private ExpressionStatement right;

    public EqualsComparisonStatement(DatabaseSchemaLayout layout)
    {
        this(layout, null, null, null);
    }
    
    public EqualsComparisonStatement(DatabaseSchemaLayout layout, ExpressionStatement left, ExpressionStatement right)
    {
        this(layout, left, right, null);
    }
    
    EqualsComparisonStatement(DatabaseSchemaLayout layout, ExpressionStatement left, ExpressionStatement right, SQLStatement parent)
    {
        this.layout = layout;
        this.parent = parent;
        this.left = left;
        this.right = right;
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

    public ExpressionStatement getLeft()
    {
        return left;
    }

    public void setLeft(ExpressionStatement left)
    {
        this.left = left;
    }

    public ExpressionStatement getRight()
    {
        return right;
    }

    public void setRight(ExpressionStatement right)
    {
        this.right = right;
    }

    @Override
    public SQLRenderer render(SQLRenderer renderer)
    {
        renderer.statement(left);
        renderer.operator("=");
        renderer.statement(right);
        
        return renderer;
    }
    
}

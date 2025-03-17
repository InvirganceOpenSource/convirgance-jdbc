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
public class BindExpressionStatement implements ExpressionStatement
{
    private DatabaseSchemaLayout layout;
    private SQLStatement parent;
    private BindVariable bind;
    private String name;

    public BindExpressionStatement(DatabaseSchemaLayout layout, BindVariable bind)
    {
        this(layout, bind, null);
    }
    
    public BindExpressionStatement(DatabaseSchemaLayout layout, BindVariable bind, String name)
    {
        this(layout, bind, name, null);
    }
    
    BindExpressionStatement(DatabaseSchemaLayout layout, BindVariable bind, String name, SQLStatement parent)
    {
        this.layout = layout;
        this.parent = parent;
        this.bind = bind;
        this.name = name;
    }

    public BindVariable getBind()
    {
        return bind;
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

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public String getQuotedName()
    {
        return layout.getDriver().quoteIdentifier(getName());
    }
    
    @Override
    public SQLRenderer render(SQLRenderer renderer)
    {
        renderer.schema(bind);
        
        return renderer;
    }
}

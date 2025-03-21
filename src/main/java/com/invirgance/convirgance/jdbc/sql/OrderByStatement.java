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
import com.invirgance.convirgance.json.JSONArray;

/**
 *
 * @author jbanes
 */
public class OrderByStatement implements SQLStatement
{
    private DatabaseSchemaLayout layout;
    private SQLStatement parent;
    
    private JSONArray<ExpressionStatement> clauses = new JSONArray<>();
    private JSONArray<OrderBy> orders = new JSONArray<>();

    
    public OrderByStatement(DatabaseSchemaLayout layout)
    {
        this(layout, null);
    }
    
    OrderByStatement(DatabaseSchemaLayout layout, SQLStatement parent)
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
        this.parent = parent;
    }
    
    public OrderByStatement order(ExpressionStatement clause, OrderBy order)
    {
        clause.setParent(this);
        this.clauses.add(clause);
        this.orders.add(order);
        
        return this;
    }

    @Override
    public SQLRenderer render(SQLRenderer renderer)
    {
        boolean written = false;
        ExpressionStatement clause;
        
        for(int i=0; i<clauses.size(); i++)
        {
            clause = clauses.get(i);
            
            if(!written)
            {
                renderer.keyword(Keyword.ORDER);
                renderer.keyword(Keyword.BY);
                
                written = true;
            }
            
            renderer.order(clause, orders.get(i));
        }
        
        return renderer;
    }

    @Override
    public String toString()
    {
        return render(new SQLRenderer()).toString();
    }
}

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
 * Creates a logical NOT operator in SQL, negating the conditions it contains.
 * This prepends NOT to a condition or group of conditions joined with AND.
 * 
 * Example: 
 * SQLStatement query = table
 *     .select()
 *     .column(table.getColumn("name"))
 *     .where()
 *         .not() // BooleanNotStatement
 *             .equals(column, value)
 *             .greaterThan(column, value)
 *         .end()
 *     .done();
 * 
 * Produces SQL like: WHERE NOT (column = value AND column > value)
 * 
 * @author jbanes
 * @param <P> The parent WhereStatement type this returns to when done.
 */
public class BooleanNotStatement<P extends WhereStatement> extends WhereStatement implements ComparisonStatement
{
    public BooleanNotStatement(DatabaseSchemaLayout layout)
    {
        super(layout);
    }
    
    BooleanNotStatement(DatabaseSchemaLayout layout, P parent)
    {
        super(layout, parent);
    }

    @Override
    public SQLStatement done()
    {
        return ((WhereStatement)getParent()).done();
    }
    
    @Override
    public WhereStatement end()
    {
        return (WhereStatement)getParent();
    }
    
    @Override
    public WhereStatement where()
    {
        if(getParent() == null) return this;
        
        return ((WhereStatement)getParent()).where();
    }
    
    @Override
    public SQLRenderer render(SQLRenderer renderer)
    {
        boolean written = false;
        ComparisonStatement[] clauses = getClauses();
        
        for(ComparisonStatement statement : clauses)
        {
            if(!written)
            {
                renderer.keyword(Keyword.NOT);
                
                if(clauses.length > 1) renderer.openParenthesis();
                
                written = true;
            }
            else
            {
                renderer.keyword(Keyword.AND);
            }
            
            renderer.statement(statement);
        }
        
        if(written && clauses.length > 1) renderer.closeParenthesis();
        
        return renderer;
    }
}

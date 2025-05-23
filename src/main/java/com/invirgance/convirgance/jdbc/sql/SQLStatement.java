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

import com.invirgance.convirgance.dbms.Query;

/**
 * Core interface for all SQL statement components.
 * 
 * This interface defines the contract for objects that represent SQL statements
 * or parts of statements within the compositional SQL building hierarchy. 
 * 
 * Implementing classes represent specific SQL constructs (SELECT, WHERE, 
 * FROM, JOIN, etc.).
 * <pre><code>
 * DatabaseSchemaLayout layout = new DatabaseSchemaLayout(driver, getHSQLDataSource())
 * Table table = layout.getCurrentSchema().getTable("customer");
 * 
 * SQLStatement select = new SelectStatement(layout)
 *              .column(table.getColumn("name"))
 *              .column(table.getColumn("zip"))
 *              .from(table,"c")
 *              .where()
 *                  .equals(table.getColumn("bridge"), bridgeId)
 *                  .and()
 *                      .greaterThan(table.getColumn("bridgeHeight"), 6)
 *                  .end()
 *                  .done();
 * </code></pre>
 * @author jbanes
 */
public interface SQLStatement
{
    /**
     * If this statement is part of a larger statement, this returns the parent
     * statement.
     * 
     * @return the parent statement
     */
    public default SQLStatement getParent()
    {
        return null;
    }
    
    public default void setParent(SQLStatement parent)
    {
        throw new UnsupportedOperationException("Not implemented yet");
    }
    
    /**
     * Obtain a {@link Query} object for execution
     * 
     * @return a Query object
     */
    public default Query query()
    {
        return query(new SQLRenderer());
    }
    
    /**
     * Obtain a {@link Query} object for execution
     * 
     * @param renderer the SQL renderer to use when creating the query
     * @return a Query object
     */
    public default Query query(SQLRenderer renderer)
    {
        return new Query(render(renderer).toString());
    }
    
    public SQLRenderer render(SQLRenderer renderer);
}

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

import com.invirgance.convirgance.jdbc.schema.*;
import com.invirgance.convirgance.json.JSONArray;

/**
 * Represents a SQL SELECT statement builder.
 * 
 * This class implements the builder pattern to construct SQL SELECT queries
 * through method chaining. It manages columns, tables, filtering conditions,
 * and ordering specifications, automatically tracking table dependencies
 * when columns are added.
 * 
 * <pre><code>
 * SelectStatement query = database
 *     .select()
 *     .column(customerTable.getColumn("name"))
 *     .column(customerTable.getColumn("email"), "contact_email")
 *     .from(customerTable, "c")
 *     .where()
 *         .equals(customerTable.getColumn("status"), "active")
 *         .and()
 *             .greaterThan(customerTable.getColumn("last_order"), dateVariable)
 *         .end()
 *     .done()
 *     .order(customerTable.getColumn("name"));
 * </code></pre>
 * 
 * @author jbanes
 */
public class SelectStatement implements SQLStatement
{
    private JSONArray<ExpressionStatement> columns = new JSONArray<>();
    private JSONArray<TabularStructure> tables = new JSONArray<>();

    private DatabaseSchemaLayout layout;
    private FromStatement from;
    private WhereStatement where;
    private OrderByStatement order;
    
    /**
     * Creates a new Statement using the provided database layout.
     * 
     * @param layout The database layout.
     */
    public SelectStatement(DatabaseSchemaLayout layout)
    {
        this.layout = layout;
    }

    /**
     * Gets the columns of the current statement.
     * 
     * @return An array.
     */
    public ExpressionStatement[] getColumns()
    {
        return columns.toArray(ExpressionStatement[]::new);
    }
    
    /**
     * Adds a column to the select query.
     * 
     * @param column Column to include.
     * @return this
     */
    public SelectStatement column(Column column)
    {
        columns.add(new ColumnExpressionStatement(layout, column, null, this));
        
        if(!tables.contains(column.getParent())) tables.add(column.getParent());
        
        return this;
    }
    
    /**
     * Adds a column to the select clause with the specified result set name
     * 
     * @param column Column to include
     * @param name to generate "as" clause
     * @return this
     */
    public SelectStatement column(Column column, String name)
    {
        columns.add(new ColumnExpressionStatement(layout, column, name, this));
        
        if(!tables.contains((Table)column.getParent())) tables.add((Table)column.getParent());
        
        return this;
    }
    
    /**
     * Creates a SelectStatement from the provided {@link Table}.
     * 
     * @param table The table.
     * @return this.
     */
    public SelectStatement from(TabularStructure table)
    {
        return from(table, null);
    }
    
    /**
     * Creates a SelectStatement from the provided table, with optional aliasing.
     * 
     * @param table The table/view to use.
     * @param name The name.
     * @return this.
     */
    public SelectStatement from(TabularStructure table, String name)
    {
        this.from = new FromStatement(layout, table, name, this);
        
        if(!tables.contains(table)) tables.add(table);
        
        return this;
    }
    
    /**
     * Creates a WHERE clause for this SELECT statement to filter results.
     * 
     * This method begins a new context for adding filter conditions. After adding
     * all desired conditions, you must call .done() on the returned WhereStatement
     * to return to the SelectStatement context.
     * 
     * <pre><code>
     * selectStatement
     *     .where()  // Enter WHERE context
     *         .equals(customerTable.getColumn("id"), 1)
     *     .done(); // Return to SELECT context
     * </code></pre>
     * 
     * @return A WhereStatement builder attached to this SelectStatement
     */
    public WhereStatement<SelectStatement> where()
    {
        this.where = new WhereStatement<>(layout, this);
        
        return where;
    }
    
    /**
     * Sets a column's ordering to ascending.
     * 
     * @param column The column to order.
     * @return this.
     */
    public SelectStatement order(ExpressionStatement column)
    {
        return order(column, OrderBy.ASCENDING);
    }
    
    /**
     * Sets a columns ordering.
     * 
     * @param column The column.
     * @param order The ordering to use.
     * @return this.
     */
    public SelectStatement order(ExpressionStatement column, OrderBy order)
    {
        if(this.order == null) this.order = new OrderByStatement(layout, this);
        
        this.order.order(column, order);
        
        return this;
    }
    
    /**
     * Sets a columns ordering to ascending.
     * 
     * @param column The column.
     * @return this.
     */   
    public SelectStatement order(Column column)
    {
        return order(new ColumnExpressionStatement(layout, column), OrderBy.ASCENDING);
    }
    
    /**
     * Sets a columns ordering.
     * 
     * @param column The column.
     * @param order The ordering to use.
     * @return this.
     */    
    public SelectStatement order(Column column, OrderBy order)
    {
        return order(new ColumnExpressionStatement(layout, column), order);
    }

    @Override
    public SQLRenderer render(SQLRenderer renderer)
    {
        FromStatement from = this.from;
 
        if(this.from == null && !this.tables.isEmpty())
        {
            from = new FromStatement(layout, this.tables.get(0), null, this);
        }
        
        renderer.keyword(Keyword.SELECT);
        
        for(ExpressionStatement column : this.columns)
        {
            renderer.statement(column);
        }
        
        renderer
            .statement(from)
            .statement(where)
            .statement(order)
            .endStatement();
        
        return renderer;
    }

    @Override
    public String toString()
    {
        return render(new SQLRenderer()).toString();
    }
}

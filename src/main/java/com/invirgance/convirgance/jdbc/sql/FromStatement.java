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
import com.invirgance.convirgance.jdbc.schema.NamedSchema;
import com.invirgance.convirgance.jdbc.schema.TabularStructure;

/**
 * Creates the FROM clause for a SQL query with the provided {@link TabularStructure}. 
 * Additionally supports table aliasing with the "AS" syntax when a name is provided.
 * 
 * <pre><code>
 * DatabaseSchemaLayout layout = new DatabaseSchemaLayout(driver, getHSQLDataSource());
 * Table table = layout.getCurrentSchema().getTable("customer");
 * 
 * FromStatement from = new FromStatement(layout, table);
 * </code></pre>
 * 
 * @author jbanes
 */
public class FromStatement implements SQLStatement, NamedSchema
{
    private DatabaseSchemaLayout layout;
    private TabularStructure table;
    private SQLStatement parent;
    private String name;

    /**
     * Creates the from statement using the provided layout and {@link TabularStructure}.
     * 
     * @param layout Database layout.
     * @param table The table.
     */
    public FromStatement(DatabaseSchemaLayout layout, TabularStructure table)
    {
        this(layout, table, null);
    }
    
    /**
     * Creates the from statement using the provided layout and {@link TabularStructure}.
     * 
     * @param layout Database layout.
     * @param table The table.
     * @param name The alias to use.
     */    
    public FromStatement(DatabaseSchemaLayout layout, TabularStructure table, String name)
    {
        this(layout, table, name, null);
    }
    
    FromStatement(DatabaseSchemaLayout layout, TabularStructure table, String name, SQLStatement parent)
    {
        this.layout = layout;
        this.table = table;
        this.name = name;
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

    @Override
    public String getName()
    {
        return (name == null) ? table.getName() : name;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }

    @Override
    public String getQuotedName()
    {
        return layout.getDriver().quoteIdentifier(getName());
    }

    @Override
    public SQLRenderer render(SQLRenderer renderer)
    {
        renderer.keyword(Keyword.FROM);
        renderer.schema(table);
        
        if(this.name != null) renderer.schema(this);
        
        return renderer;
    }

    @Override
    public String toString()
    {
        return render(new SQLRenderer()).toString();
    }
}

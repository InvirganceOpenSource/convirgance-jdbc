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
import com.invirgance.convirgance.jdbc.schema.Table;
import com.invirgance.convirgance.jdbc.schema.TableTest;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author jbanes
 */
public class WhereStatementTest
{
    @Test
    public void testEquals()
    {
        DatabaseSchemaLayout layout = TableTest.getLayout();
        WhereStatement statement = new WhereStatement(layout);
        Table table = layout.getCurrentSchema().getTable("customer");
        
        assertEquals("", statement.toString());
        assertEquals("where \"ZIP\" = '90210'", statement.equals(table.getColumn("zip"), "90210").toString());
        assertEquals("where \"ZIP\" = '90210' and '12' = 12", statement.equals("12", 12).toString());
        assertEquals("where \"ZIP\" = '90210' and '12' = 12 and \"ZIP\" = \"ZIP\"", statement.equals(table.getColumn("zip"), table.getColumn("zip")).toString());
        assertEquals("where \"ZIP\" = '90210' and '12' = 12 and \"ZIP\" = \"ZIP\" and \"NAME\" = :name", statement.equals(table.getColumn("name"), new BindVariable("name")).toString());
    }
    
    @Test
    public void testNull()
    {
        DatabaseSchemaLayout layout = TableTest.getLayout();
        Table table = layout.getCurrentSchema().getTable("customer");
        
        assertEquals("where \"ZIP\" is null", new WhereStatement(layout).isNull(table.getColumn("zip")).toString());
        assertEquals("where null is null", new WhereStatement(layout).isNull((Object)null).toString());
        assertEquals("where :test is null", new WhereStatement(layout).isNull(new BindVariable("test")).toString());
        assertEquals("where \"ZIP\" is not null", new WhereStatement(layout).isNotNull(table.getColumn("zip")).toString());
        assertEquals("where '90210' is not null", new WhereStatement(layout).isNotNull("90210").toString());
        assertEquals("where :test is not null", new WhereStatement(layout).isNotNull(new BindVariable("test")).toString());
    }
    
    @Test
    public void testOr()
    {
        DatabaseSchemaLayout layout = TableTest.getLayout();
        Table table = layout.getCurrentSchema().getTable("customer");
        SQLStatement statement;
        
        statement = new WhereStatement(layout)
            .or()
                .equals(table.getColumn("zip"), "90210")
                .isNull(table.getColumn("zip"))
                .where()
            .or()
                .equals(table.getColumn("name"), "Mike")
                .equals(table.getColumn("name"), "Bob")
                .where();

        assertEquals("where ( \"ZIP\" = '90210' or \"ZIP\" is null ) and ( \"NAME\" = 'Mike' or \"NAME\" = 'Bob' )", statement.toString());
        assertEquals("\nwhere (\n    \"ZIP\" = '90210'\n    or \"ZIP\" is null\n)\nand (\n    \"NAME\" = 'Mike'\n    or \"NAME\" = 'Bob'\n)", statement.render(new SQLRenderer().pretty(true)).toString());
    }
}

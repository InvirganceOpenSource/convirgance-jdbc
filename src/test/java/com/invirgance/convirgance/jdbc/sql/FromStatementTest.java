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
import com.invirgance.convirgance.jdbc.schema.View;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author jbanes
 */
public class FromStatementTest
{
    @Test
    public void testStatement()
    {
        DatabaseSchemaLayout layout = SelectStatementTest.getHSQLLayout();
        Table table = layout.getCurrentSchema().getTable("customer");
        View view = layout.getCurrentSchema().getView("all_customers");
        
        assertEquals("from \"PUBLIC\".\"CUSTOMER\"", new FromStatement(layout, table).toString());
        assertEquals("from \"PUBLIC\".\"CUSTOMER\" \"c\"", new FromStatement(layout, table, "c").toString());
        assertEquals("from \"PUBLIC\".\"ALL_CUSTOMERS\" \"ac\"", new FromStatement(layout, view, "ac").toString());
    }
    
}

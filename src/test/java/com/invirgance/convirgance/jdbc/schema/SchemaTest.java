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
package com.invirgance.convirgance.jdbc.schema;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author jbanes
 */
public class SchemaTest
{
    @Test
    public void testTables()
    {
        DatabaseSchemaLayout dbschema = DatabaseSchemaLayoutTest.getHSQLLayout();
        int count = 0;
        
        for(Table table : dbschema.getCurrentSchema().getTables())
        {
            assertEquals("CUSTOMER", table.getName());
            
            count++;
        }
        
        assertEquals(1, count);
    }
    
    @Test
    public void testViews()
    {
        DatabaseSchemaLayout dbschema = DatabaseSchemaLayoutTest.getHSQLLayout();
        int count = 0;
        
        for(View view : dbschema.getCurrentSchema().getViews())
        {
            assertEquals("ALL_CUSTOMERS", view.getName());
            
            count++;
        }
        
        assertEquals(1, count);
    }
    
}

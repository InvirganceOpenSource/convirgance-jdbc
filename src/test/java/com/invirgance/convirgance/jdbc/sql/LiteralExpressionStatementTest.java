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
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author jbanes
 */
public class LiteralExpressionStatementTest
{
    @Test
    public void testExpression()
    {
        DatabaseSchemaLayout layout = SelectStatementTest.getHSQLLayout();
        
        assertEquals("'Test'", new LiteralExpressionStatement(layout, "Test").toString());
        assertEquals("'Test ''Quoted'''", new LiteralExpressionStatement(layout, "Test 'Quoted'").toString());
        assertEquals("12", new LiteralExpressionStatement(layout, 12).toString());
        assertEquals("null", new LiteralExpressionStatement(layout, null).toString());
        
        assertEquals("'Test' as \"value\"", new LiteralExpressionStatement(layout, "Test", "value").toString());
        assertEquals("'Test ''Quoted''' as \"value\"", new LiteralExpressionStatement(layout, "Test 'Quoted'", "value").toString());
        assertEquals("12 as \"value\"", new LiteralExpressionStatement(layout, 12, "value").toString());
        assertEquals("null as \"value\"", new LiteralExpressionStatement(layout, null, "value").toString());
    }
    
}

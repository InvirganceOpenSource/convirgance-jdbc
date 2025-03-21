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
public class SQLRendererTest
{
    @Test
    public void testLineNumbers()
    {
        DatabaseSchemaLayout layout = SelectStatementTest.getHSQLLayout();
        SQLRenderer renderer = new SQLRenderer();
        
        renderer.setPrettyPrint(true);
        
        assertEquals(1, renderer.getLine());
        assertEquals(1, renderer.getCharacter());
        
        renderer.keyword(Keyword.SELECT);
        
        assertEquals(1, renderer.getLine());
        assertEquals(7, renderer.getCharacter());
        
        renderer.statement(new LiteralExpressionStatement(layout, 12));
        
        assertEquals(2, renderer.getLine());
        assertEquals(7, renderer.getCharacter());
        
        renderer.statement(new LiteralExpressionStatement(layout, "Test", "test"));
        
        assertEquals(3, renderer.getLine());
        assertEquals(21, renderer.getCharacter());
        
        renderer.keyword(Keyword.FROM);
        
        assertEquals(4, renderer.getLine());
        assertEquals(5, renderer.getCharacter());
        
        renderer.schema(layout.getCurrentSchema().getTable("customer"));
        
        assertEquals(4, renderer.getLine());
        assertEquals(25, renderer.getCharacter());
    }
    
}

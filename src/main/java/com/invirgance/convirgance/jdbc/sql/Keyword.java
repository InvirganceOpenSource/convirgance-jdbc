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

/**
 * Enumerations for SQL keywords. These are used for rendering 
 * different {@link ExpressionStatement}s
 * 
 * @author jbanes
 */
public enum Keyword
{
    SELECT("select"),
    FROM("from"),
    AS("as"),
    WHERE("where"),
    INSERT("insert"),
    INTO("into"),
    VALUES("values"),
    ORDER("order"),
    BY("by"),
    ASC("asc"),
    DESC("desc"),
    GROUP("group"),
    HAVING("having"),
    AND("and"),
    OR("or"),
    NOT("not");

    private final String keyword;
    private final String upper;
        
    private Keyword(String keyword)
    {
        this.keyword = keyword;
        this.upper = keyword.toUpperCase();
    }
    
    public String getLowerCase()
    {
        return this.keyword;
    }
    
    public String getUpperCase()
    {
        return this.upper;
    }

    @Override
    public String toString()
    {
        return getLowerCase();
    }
}

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
 * Represents the order key words for the SQL ORDER BY clauses.
 * Provides constants for the two possible ordering directions:
 * ascending and descending.
 * 
 * @author jbanes
 */
public enum OrderBy
{
    ASCENDING("asc"),
    DESCENDING("desc");
    
    private final String order;
    private final String upper;
        
    private OrderBy(String order)
    {
        this.order = order;
        this.upper = order.toUpperCase();
    }
    
    public String getLowerCase()
    {
        return this.order;
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

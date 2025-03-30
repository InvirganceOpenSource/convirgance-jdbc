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
 * Different comparison operators. These are used by SQL statements to properly 
 * generate the correct SQL when different filters are used 
 * such as with {@link WhereStatement}.
 * 
 * @author jbanes
 */
public enum ComparisonOperator
{
    EQUAL("="),
    GREATER_THAN(">"),
    GREATER_THAN_OR_EQUAL(">="),
    LESS_THAN("<"),
    LESS_THAN_OR_EQUAL("<="),
    NOT_EQUAL("<>"),
    LIKE("like"),
    IS_NULL("is null"),
    IS_NOT_NULL("is not null");
    
    private final String operator;
    private final String upper;

    private ComparisonOperator(String operator)
    {
        this.operator = operator;
        this.upper = operator.toUpperCase();
    }
    
    public String getLowerCase()
    {
        return this.operator;
    }
    
    public String getUpperCase()
    {
        return this.upper;
    }

    @Override
    public String toString()
    {
        return operator;
    }
}

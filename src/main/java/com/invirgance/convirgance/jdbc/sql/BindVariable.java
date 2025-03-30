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

import com.invirgance.convirgance.jdbc.schema.NamedSchema;

/**
 * A named parameter that can be used when creating SQL queries with {@link SQLStatement}.
 * 
 * Example:
 *   SQLStatement statement = table
 *        .select()
 *        .where()
 *            .equals(table.getColumn("zip"), new BindVariable("zipcode"))
 *           .done();
 * 
 * // The query would result in something like this. "... from "PUBLIC"."CUSTOMER" where "ZIP" = :zipcode"
 * @author jbanes
 */
public class BindVariable implements NamedSchema
{
    private String name;

    /**
     * The name to use for the binding variable.
     * Example: "zipcode"
     * 
     * @param name A name.
     */
    public BindVariable(String name)
    {
        char c;
        
        this.name = name;
        
        for(int i=0; i<name.length(); i++)
        {
            c = name.charAt(i);
            
            if(c == ':') 
            {
                throw new IllegalArgumentException("Bind variables cannot contain colons. Bind name: [" + name + "]");
            }
            
            if(Character.isWhitespace(c)) 
            {
                throw new IllegalArgumentException("Bind variables cannot contain whitespace. Bind name: [" + name + "]");
            }
            
            if(!Character.isLetterOrDigit(c) && c != '_' && c != '-') 
            {
                throw new IllegalArgumentException("Bind variables cannot special characters other than _ or -. Bind name: [" + name + "]");
            }
        }
    }
    
    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public String getQuotedName()
    {
        return ":" + name;
    }

    @Override
    public int hashCode()
    {
        return 7 + this.name.hashCode();
    }

    @Override
    public boolean equals(Object obj)
    {
        if(!(obj instanceof BindVariable)) return false;
        
        return ((BindVariable)obj).getName().equals(name);
    }

    @Override
    public String toString()
    {
        return getQuotedName();
    }
}

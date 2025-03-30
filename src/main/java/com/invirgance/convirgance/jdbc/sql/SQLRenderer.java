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

import com.invirgance.convirgance.jdbc.schema.Column;
import com.invirgance.convirgance.jdbc.schema.NamedSchema;
import com.invirgance.convirgance.jdbc.schema.TabularStructure;
import static com.invirgance.convirgance.jdbc.sql.Keyword.AND;
import static com.invirgance.convirgance.jdbc.sql.Keyword.OR;
import static com.invirgance.convirgance.jdbc.sql.Keyword.WHERE;
import java.util.Stack;

/**
 * Core SQL generation engine that renders SQL statements into properly 
 * formatted SQL strings. This class handles all SQL syntax details including 
 * keyword formatting, proper spacing, parentheses balancing, and identifier 
 * quoting based on database type.
 * 
 * @author jbanes
 */
public class SQLRenderer
{
    private StringBuffer buffer = new StringBuffer();
    private Stack stack = new Stack();
    
    private boolean capitalizeKeywords = false;
    private boolean prettyPrint = false;
    
    private boolean requireNewline = false;
    private int depth = 0;
    private int parenthenses = 0;
    private int line = 1;
    private int lineOffset = 0;
    private Object last;
    
    /**
     * If SQL keywords will be capitalized.
     * 
     * @return If the keywords will be capitalized.
     */
    public boolean isCapitalizeKeywords()
    {
        return capitalizeKeywords;
    }

    /**
     * Will change the query to use capitalized keywords.
     * 
     * @param capitalizeKeywords True to enable.
     */
    public void setCapitalizeKeywords(boolean capitalizeKeywords)
    {
        this.capitalizeKeywords = capitalizeKeywords;
    }

    /**
     * If the query will use pretty printing.
     * 
     * @return True is enabled.
     */
    public boolean isPrettyPrint()
    {
        return prettyPrint;
    }

    /**
     * This will make it easier for other people to read the query. If
     * you don't intend on other people reading the query leave this disabled
     * to save a few bytes.
     * 
     * @param pretty True to enable.
     */
    public void setPrettyPrint(boolean pretty)
    {
        this.prettyPrint = pretty;
    }
    
    /**
     * Returns the current line count of the query.
     * 
     * @return An int.
     */
    public int getLine()
    {
        return line;
    }
    
    /**
     * Gets the current character index. 
     * Note: This will change depending on the state of pretty print.
     * 
     * @return The character index.
     */
    public int getCharacter()
    {
        return (buffer.length() - lineOffset) + 1;
    }
    
    private void newline()
    {
        buffer.append("\n");
        
        line++;
        lineOffset = buffer.length();
        
        for(int i=0; i<depth; i++)
        {
            buffer.append("    ");
        }
    }
    
    private boolean whitespace()
    {
        return Character.isWhitespace(buffer.charAt(buffer.length()-1));
    }
    
    private void prefix(Object value)
    {
        boolean column = (value instanceof ExpressionStatement);
        
        if(requireNewline) 
        {
            newline();
        }
        else if(last instanceof ExpressionStatement && !(value instanceof ComparisonOperator)) 
        {
            if(column) buffer.append(",");
            
            if(prettyPrint) 
            {
                if(!column) depth--;
                
                newline();
            }
        }
        
        if(buffer.length() > lineOffset && !whitespace()) 
        {
            buffer.append(" ");
        }
        
        requireNewline = false;
        last = value;
    }
    
    /**
     * Keywords will be capitalized if this is enabled.
     * 
     * @param capitalize True to enable.
     * @return this
     */
    public SQLRenderer capitialize(boolean capitalize)
    {
        this.capitalizeKeywords = capitalize;
        
        return this;
    }
    
    /**
     * Enables pretty printing for this renderer.
     * 
     * @param pretty True to enable.
     * @return this
     */
    public SQLRenderer pretty(boolean pretty)
    {
        this.prettyPrint = pretty;
        
        return this;
    }
    
    /**
     * Resets this object.
     * @return this
     */
    public SQLRenderer reset()
    {
        buffer.setLength(0);
        stack.clear();
        
        requireNewline = false;
        depth = 0;
        parenthenses = 0;
        line = 1;
        lineOffset = 0;
        last = null;
        
        return this;
    }
    
    private void push()
    {
        stack.push(last);
        
        last = null;
    }
    
    private void pop()
    {
        last = stack.pop();
    }
    
    /**
     * Adds a provided SQL keyword to the query.
     * 
     * @param keyword A SQL {@link Keyword}
     * @return this
     */
    public SQLRenderer keyword(Keyword keyword)
    {
        if(prettyPrint)
        {
            switch(keyword)
            {
                case WHERE:
                case AND:
                case OR:
                case ORDER:
                    newline();
            }
        }
        
        prefix(keyword);
        
        buffer.append(capitalizeKeywords ? keyword.getUpperCase() : keyword.getLowerCase());
        
        if(prettyPrint) 
        {
            switch(keyword)
            {
                case SELECT:
                case BY:
                    requireNewline = true;
                    depth++;
            }
        }
        
        return this;
    }
    
    /**
     * Adds a comparison operator to the SQL query.
     * 
     * @param operator A {@link ComparisonOperator}
     * @return this
     */
    public SQLRenderer operator(ComparisonOperator operator)
    {
        prefix(operator);
        
        buffer.append(this.capitalizeKeywords ? operator.getUpperCase() : operator.getLowerCase());
        
        last = operator;
        
        return this;
    }
    
    /**
     * Adds a column to the SQL query.
     * 
     * @param column A {@link Column}
     * @return this
     */
    public SQLRenderer column(Column column)
    {
        prefix(column);
        
        buffer.append(column.getQuotedName());

        return this;
    }
    
    /**
     * Sets the ordering of a Column/NamedSchema.
     * 
     * @param column A {@link NamedSchema}
     * @param order The ordering to use {@link OrderBy}
     * @return this
     */
    public SQLRenderer order(NamedSchema column, OrderBy order)
    {
        prefix(column);
        
        buffer.append(column.getQuotedName());
        buffer.append(" ");
        buffer.append(capitalizeKeywords ? order.getUpperCase() : order.getLowerCase());

        return this;
    }
    
    private void quoteString(String value)
    {
        buffer.append("'");
        buffer.append(value.replaceAll("'", "''"));
        buffer.append("'");
    }
    
    /**
     * Adds a literal to the query.
     * Specific handling for Null, Number and String, otherwise the object is
     * toString()-ed
     * 
     * @param literal An object.
     * @return this
     */
    public SQLRenderer literal(Object literal)
    {
        prefix(literal);
        
        if(literal == null) buffer.append("null");
        else if(literal instanceof String) quoteString((String)literal);
        else if(literal instanceof Number) buffer.append(literal.toString());
        else quoteString(literal.toString());
        
        return this;
    }
    
    /**
     * Appends a named schema element into the SQL query.
     * This method handles proper formatting of schema identifiers, including
     * automatically adding schema qualification (e.g., "schema.table") for
     * TabularStructure objects.
     * 
     * @param named The NamedSchema object to render (table, column, etc.)
     * @return this
     */
    public SQLRenderer schema(NamedSchema named)
    {
        prefix(named);
        
        if(named instanceof TabularStructure)
        {
            buffer.append(((TabularStructure)named).getSchema().getQuotedName());
            buffer.append(".");
        }
        
        buffer.append(named.getQuotedName());
        
        return this;
    }
    
    /**
     * Adds a SQLStatement to the query.
     * 
     * @param statement A SQLStatement.
     * @return this
     */
    public SQLRenderer statement(SQLStatement statement)
    {
        if(statement == null) return this;
        
        prefix(statement);
        
        push();
        statement.render(this);
        pop();
        
        return this;
    }
    
    /**
     * Adds a left parenthesis to the query.
     * 
     * @return this
     */
    public SQLRenderer openParenthesis()
    {
        prefix("(");
        
        buffer.append("(");
        
        if(prettyPrint) requireNewline = true;
        
        depth++;
        parenthenses++;
        
        return this;
    }
    
    /**
     * Adds a right parenthesis to the query.
     * 
     * @return this
     */    
    public SQLRenderer closeParenthesis()
    {
        depth--;
        parenthenses--;
        
        if(!prettyPrint) buffer.append(" ");
        else newline();
        
        buffer.append(")");
        last = ")";
        
        return this;
    }
    
    /**
     * Ends the current query statement.
     * 
     * @return this
     */
    public SQLRenderer endStatement()
    {
        // Right trim
        while(Character.isWhitespace(buffer.charAt(buffer.length()-1)))
        {
            buffer.setLength(buffer.length()-1);
        }
        
        buffer.append(";");
        
        requireNewline = true;
        last = ";";
        
        return this;
    }

    @Override
    public String toString()
    {
        return buffer.toString();
    }
}

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
package com.invirgance.convirgance.jdbc.callback;

import com.invirgance.convirgance.ConvirganceException;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;

/**
 * A callback interface for executing database operations.
 * This provides lifecycle management, by using this you don't need to 
 * worry about closing the database connection.
 * 
 * @author jbanes
 */
public interface ConnectionCallback
{
    /**
     * Executes database operations with the provided connection.
     * 
     * @param connection The JDBC connection to use
     * @throws SQLException If a database access error occurs
     */    
    public void execute(Connection connection) throws SQLException;
    
    /**
    * Utility method that handles connection lifecycle (open/close)
    * and exception handling. 
    * This uses try-with-resources, which implements auto-closable
    * ensuring the connection is closed.
    * 
    * @param source The data source to obtain a connection from.
    * @param callback The callback defining operations to perform.
    * @throws ConvirganceException Wrapping any SQLExceptions that occur
    */
    public static void execute(DataSource source, ConnectionCallback callback)
    {
        try(Connection connection = source.getConnection())
        {
            callback.execute(connection);
        }
        catch(SQLException e)
        {
            throw new ConvirganceException(e);
        }
    }
}

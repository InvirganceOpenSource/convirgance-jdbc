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

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

/**
 * Provides a connection callback specifically for database metadata.
 * This interface safely retrieves the database metadata from a connection 
 * and closes the connection after execution.
 * 
 * @author jbanes
 */
public interface DatabaseMetaDataCallback extends ConnectionCallback
{    
    /**
     * Default implementation that obtains metadata from the connection and passes it
     * to the {@link #execute(DatabaseMetaData)} method.
     *
     * @param connection the JDBC connection to use.
     * @throws SQLException if a database access error occurs.
     */
    @Override
    public default void execute(Connection connection) throws SQLException
    {
        execute(connection.getMetaData());
    }
    
    /**
     * Executes operations using the database metadata.
     * Implementations should perform their metadata operations within this method/lambda.
     *
     * @param metadata the database metadata object to use.
     * @throws SQLException if a database access error occurs.
     */    
    public void execute(DatabaseMetaData metadata) throws SQLException;
}

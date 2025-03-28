/*
 * The MIT License
 *
 * Copyright 2025 tadghh.
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

/**
 * Provides callback interfaces for safe and efficient JDBC connection management.
 * 
 * <p>This package implements the callback pattern to ensure proper lifecycle 
 * management of database connections. It handles resource acquisition, operation
 * execution, and automatic resource release, even when exceptions occur. The
 * connection uses try-with-resources constructs to guarantee connections
 * are properly closed.</p>
 * 
 * <p>Example usage:</p>
 * <pre>
 * // Create a transaction with a query operation
 * TransactionOperation transaction = new TransactionOperation(
 *     new QueryOperation(new Query("truncate table " + tableName)));
 * 
 * // Execute using a StoredConnection that manages lifecycle
 * // The connection is provided through the StoredConnection's data source
 * storedConnection.execute(connection -> {
 *     transaction.execute(connection);
 * });
 * </pre>
 * 
 */
package com.invirgance.convirgance.jdbc.callback;

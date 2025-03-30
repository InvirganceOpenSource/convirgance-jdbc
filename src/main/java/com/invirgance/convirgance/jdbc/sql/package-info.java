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
 * SQL statement builder framework for constructing SQL queries.
 * 
 * This package provides a fluent API for building type-safe SQL statements without writing raw SQL.
 * It follows a compositional builder pattern where SQL components are represented as Java objects
 * that can be combined into complete SQL statements.
 * <pre>
 * Key components:
 * - Operator Classes: And, Not and Or Boolean statements.
 * - Statement Classes: SelectStatement and other complete SQL statements
 * - Clause Classes: FromStatement, WhereStatement for SQL clauses
 * - Expression Classes: ColumnExpressionStatement, BindExpressionStatement
 * - Comparison Classes: ComparisonOperatorStatement, IsNullComparisonStatement
 * - SQLRenderer: Core engine for generating formatted SQL text
 *
 * Basic example:
 * <code>
 * SelectStatement query = database
 *     .select()
 *     .column(customerTable.getColumn("name"))
 *     .from(customerTable)
 *     .where()
 *         .equals(customerTable.getColumn("status"), "active")
 *     .done();
 *     
 * String sql = query.toString();
 * </code></pre>
 *
 */
package com.invirgance.convirgance.jdbc.sql;

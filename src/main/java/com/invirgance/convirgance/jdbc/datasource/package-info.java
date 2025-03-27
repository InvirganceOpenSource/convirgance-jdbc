/*
 * Copyright 2025 INVIRGANCE LLC

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the “Software”), to deal
in the Software without restriction, including without limitation the rights to
use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
of the Software, and to permit persons to whom the Software is furnished to do
so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */

/**
 * Provides classes for managing JDBC data sources.
 * 
 * This package contains implementations for database connectivity
 * through the JDBC API, including a DataSourceManager for property configuration
 * and a DriverDataSource for database connections.
 * 
 * A DriverDataSource(DataSource) can be created with 
 * DriverDataSource.getDataSource(url, name, pass) and retrieved 
 * off existing AutomaticDriver(s) or StoredConnection(s);
 * this can then be passed into DataSourceManager if needed.
 * 
 * @author jbanes
 * @since 1.0
 */
package com.invirgance.convirgance.jdbc.datasource;

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
package com.invirgance.convirgance.jdbc.schema;

import com.invirgance.convirgance.json.JSONObject;
import java.math.BigDecimal;
import java.sql.*;

/**
 *
 * @author jbanes
 */
public class Column
{
    private JSONObject record;

    Column(JSONObject record)
    {
        this.record = record;
    }
    
    public String getName()
    {
        return this.record.getString("COLUMN_NAME");
    }
    
    public boolean isNullable()
    {
        return (this.record.getInt("NULLABLE") > 0);
    }
    
    public String getType()
    {
        return this.record.getString("TYPE_NAME");
    }
    
    public JDBCType getJDBCType()
    {
        int type = this.record.getInt("DATA_TYPE");
        String name;

        if(type < 0)
        {
            switch(getType())
            {
                case "BIGINT":
                case "int8": return JDBCType.BIGINT;
                case "bool": return JDBCType.BOOLEAN;
                case "float4": return JDBCType.FLOAT;
                default: return JDBCType.OTHER;
            }
        }

        return JDBCType.valueOf(type);
    }
    
    public Class getTypeClass()
    {
        switch(getJDBCType())
        {
            case JDBCType.ARRAY: return Array.class;
            case JDBCType.BIGINT: return Long.class;
            case JDBCType.BINARY: return byte[].class;
            case JDBCType.BLOB: return Blob.class;
            case JDBCType.BOOLEAN: return Boolean.class;
            case JDBCType.CHAR: return String.class;
            case JDBCType.CLOB: return Clob.class;
            case JDBCType.DECIMAL: return BigDecimal.class;
            case JDBCType.INTEGER: return Integer.class;
            case JDBCType.DATE: return java.sql.Date.class;
            case JDBCType.DOUBLE: return Double.class;
            case JDBCType.FLOAT: return Float.class;
            case JDBCType.LONGVARBINARY: return byte[].class;
            case JDBCType.LONGNVARCHAR: return String.class;
            case JDBCType.LONGVARCHAR: return String.class;
            case JDBCType.NCHAR: return String.class;
            case JDBCType.NVARCHAR: return String.class;
            case JDBCType.NUMERIC: return BigDecimal.class;
            case JDBCType.REAL: return Float.class;
            case JDBCType.SMALLINT: return Short.class;
            case JDBCType.STRUCT: return Struct.class;
            case JDBCType.TIME: return Time.class;
            case JDBCType.TIMESTAMP: return Timestamp.class;
            case JDBCType.TIMESTAMP_WITH_TIMEZONE: return Timestamp.class;
            case JDBCType.VARBINARY: return byte[].class;
            case JDBCType.VARCHAR: return String.class;
            
            default: return Object.class;
        }
    }
    
    public Object getDefault()
    {
        return record.get("COLUMN_DEF");
    }
    
    @Override
    public int hashCode()
    {
        return record.hashCode();
    }

    @Override
    public boolean equals(Object obj)
    {
        if(!(obj instanceof Column)) return false;
        
        return record.equals(((Column)obj).record);
    }

    @Override
    public String toString()
    {
        return this.record.toString(4);
    }
}

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
 * Represents a column in a table. Can be used to create logic that depends 
 * on column metadata like data-type, null-ability or parent.
 * You can get this object through {@link DatabaseSchemaLayout#getAllStructures()} 
 * and use the returned array then calling {@link TabularStructure#getColumn(name)}.
 *
 * @author jbanes
 */
public class Column implements NamedSchema
{
    private JSONObject record;
    private TabularStructure parent;

    Column(JSONObject record, TabularStructure parent)
    {
        this.record = record;
        this.parent = parent;
    }
    
    @Override
    public String getName()
    {
        return this.record.getString("COLUMN_NAME");
    }
    
    @Override
    public String getQuotedName()
    {
        return getParent().getLayout().quoteIdentifier(getName());
    }

    /**
     * Returns the parent object of this, like a view or table.
     * 
     * @return TabularStructure
     */
    public TabularStructure getParent()
    {
        return parent;
    }
    
    /**
     * Returns if this column can store null values.
     * 
     * @return True if null values are allowed.
     */
    public boolean isNullable()
    {
        return (this.record.getInt("NULLABLE") > 0);
    }
    
    /**
     * Returns the column's data type.
     * 
     * @return A String representing the data type.
     */
    public String getType()
    {
        return this.record.getString("TYPE_NAME");
    }
    
    /**
     * Returns the JDBCType (data-type) for this column.
     * 
     * @return The JDBCType.
     */
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
                case "LONG VARCHAR": return JDBCType.LONGNVARCHAR;
                default: return JDBCType.OTHER;
            }
        }

        return JDBCType.valueOf(type);
    }
    
    /**
     * Returns the Object class that this column stores.
     * 
     * @return A Class.
     */
    public Class getTypeClass()
    {
        switch(getJDBCType())
        {
            case ARRAY: return Array.class;
            case BIGINT: return Long.class;
            case BINARY: return byte[].class;
            case BLOB: return Blob.class;
            case BOOLEAN: return Boolean.class;
            case CHAR: return String.class;
            case CLOB: return Clob.class;
            case DECIMAL: return BigDecimal.class;
            case INTEGER: return Integer.class;
            case DATE: return java.sql.Date.class;
            case DOUBLE: return Double.class;
            case FLOAT: return Float.class;
            case LONGVARBINARY: return byte[].class;
            case LONGNVARCHAR: return String.class;
            case LONGVARCHAR: return String.class;
            case NCHAR: return String.class;
            case NVARCHAR: return String.class;
            case NUMERIC: return BigDecimal.class;
            case REAL: return Float.class;
            case SMALLINT: return Short.class;
            case STRUCT: return Struct.class;
            case TIME: return Time.class;
            case TIMESTAMP: return Timestamp.class;
            case TIMESTAMP_WITH_TIMEZONE: return Timestamp.class;
            case VARBINARY: return byte[].class;
            case VARCHAR: return String.class;
            
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

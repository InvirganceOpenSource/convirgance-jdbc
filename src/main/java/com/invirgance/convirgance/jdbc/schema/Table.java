/*
 * Copyright 2024 INVIRGANCE LLC

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
package com.invirgance.convirgance.jdbc.schema;

import com.invirgance.convirgance.ConvirganceException;
import com.invirgance.convirgance.dbms.DBMS;
import com.invirgance.convirgance.dbms.Query;
import com.invirgance.convirgance.json.JSONArray;
import com.invirgance.convirgance.json.JSONObject;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.Iterator;

/**
 *
 * @author jbanes
 */
public class Table extends TabularStructure implements Iterable<JSONObject>
{
    Table(JSONObject record, DatabaseSchemaLayout layout, Schema schema)
    {
        super(record, layout, schema);
    }
    
    public PrimaryKey getPrimaryKey()
    {
        DatabaseSchemaLayout layout = getLayout();
        JSONArray<JSONObject> records = new JSONArray<>();
        
        layout.useMetaData(metadata -> {
            try(ResultSet set = metadata.getPrimaryKeys(getSchema().getCatalog().getName(), getSchema().getName(), getName()))
            {
                records.addAll(layout.getObjects(set));
            }
        });

        return new PrimaryKey(getColumns(), records.toArray(JSONObject[]::new));
    }
    
    private boolean matchTable(JSONObject record1, JSONObject record2)
    {
        if(!record1.getString("PKTABLE_CAT", "").equals(record2.getString("PKTABLE_CAT", ""))) return false;
        if(!record1.getString("PKTABLE_SCHEM", "").equals(record2.getString("PKTABLE_SCHEM", ""))) return false;
        
        return record1.getString("PKTABLE_SCHEM", "").equals(record2.getString("PKTABLE_SCHEM", ""));
    }
    
    public ForeignKey[] getForeignKeys()
    {
        JSONArray<ForeignKey> keys = new JSONArray<>();
        JSONArray<JSONObject> key = new JSONArray<>();
        JSONArray<JSONObject> records = new JSONArray<>();
        
        DatabaseSchemaLayout layout = getLayout();
        Column[] columns = getColumns();
        JSONObject last = null;
        
        layout.useMetaData(metadata -> {
            try(ResultSet set = metadata.getImportedKeys(getSchema().getCatalog().getName(), getSchema().getName(), getName()))
            {
                records.addAll(layout.getObjects(set));
            }
        });
        
        for(JSONObject record : records)
        {
            if(last == null || matchTable(record, last))
            {
                key.add(record);
                
                last = record;
            }
            else
            {
                keys.add(new ForeignKey(columns, key.toArray(JSONObject[]::new)));
                
                key.clear();
                key.add(record);
                
                last = record;
            }
        }
        
        if(!key.isEmpty()) keys.add(new ForeignKey(columns, key.toArray(JSONObject[]::new)));

        return keys.toArray(ForeignKey[]::new);
    }
    
    public Query generateSelect()
    {
        StringBuffer buffer = new StringBuffer("select \n");
        
        for(Column column : getColumns())
        {
            if(buffer.length() > 9) buffer.append(",\n");
            
            buffer.append("    ");
            buffer.append(getLayout().quoteIdentifier(column.getName()));
        }
        
        buffer.append("\n");
        buffer.append("from ");
        buffer.append(getLayout().quoteIdentifier(getName()));
        
        return new Query(buffer.toString());
    }

    @Override
    public Iterator<JSONObject> iterator()
    {
        return new DBMS(getLayout().getDataSource()).query(generateSelect()).iterator();
    }
    
    public class PrimaryKey
    {
        private Column[] columns;
        private JSONObject[] records;

        public PrimaryKey(Column[] columns, JSONObject[] records)
        {
            this.columns = columns;
            this.records = records;
        }
        
        public String getName()
        {
            return records[0].getString("PK_NAME");
        }
        
        /**
         * The table the primary key belongs to
         * 
         * @return the Table containing the primary key column(s)
         */
        public Table getTable()
        {
            return Table.this;
        }
        
        /**
         * Obtain the primary key {@link Column}. Throws an exception if the primary key is 
         * multi-column.
         * 
         * @return the primary key {@link Column}
         * @throws ConvirganceException if primary key is multi-column
         */
        public Column getColumn()
        {
            Column[] columns = getColumns();
            
            if(columns.length > 1) throw new ConvirganceException("Single column requested for primary key on table \"" + getTable().getName() + "\" when key consists of " + columns.length + " columns");
            
            return columns[0];
        }
        
        /**
         * Obtain the primary key columns. Columns are guaranteed to be returned in
         * primary key order.
         * 
         * @return an array of {@link Column} objects representing the primary key
         */
        public Column[] getColumns()
        {
            Column[] columns = new Column[this.records.length];
            
            for(JSONObject record : records)
            {
                for(Column column : this.columns)
                {
                    if(record.getString("COLUMN_NAME").equals(column.getName()))
                    {
                        columns[record.getInt("KEY_SEQ")-1] = column;
                    }
                }
            }
            
            return columns;
        }

        @Override
        public boolean equals(Object obj)
        {
            PrimaryKey key;
            
            if(!(obj instanceof PrimaryKey)) return false;
            
            key = (PrimaryKey)obj;
            
            if(key.records.length != records.length) return false;
            if(!key.getTable().equals(getTable())) return false;
            
            for(int i=0; i<records.length; i++)
            {
                if(!records[i].equals(key.records[i])) return false;
            }
            
            return true;
        }

        @Override
        public int hashCode()
        {
            return 79 * 13 + Arrays.deepHashCode(this.records);
        }

        @Override
        public String toString()
        {
            return new JSONArray(Arrays.asList(records)).toString(4);
        }
    }
    
    public class ForeignKey
    {
        private Column[] columns;
        private JSONObject[] records;

        private ForeignKey(Column[] columns, JSONObject[] records)
        {
            this.columns = columns;
            this.records = records;
        }
        
        /**
         * Returns the object name of the foreign key relationship, if available. 
         * In some database the relationship may not be named. In which case this
         * will return null.
         * 
         * @return the name of the foreign key relationship if available, null otherwise
         */
        public String getName()
        {
            return records[0].getString("FK_NAME");
        }
        
        /**
         * The table the foreign key belongs to
         * 
         * @return the Table containing the foreign key columns
         */
        public Table getTable()
        {
            return Table.this;
        }
        
        /**
         * Returns the foreign key column. An exception is thrown if the foreign
         * key consists of more than one column.
         * 
         * @return the foreign key column
         * @throws ConvirganceException if the foreign key consists of more than one column
         */
        public Column getColumn()
        {
            Column[] columns = getColumns();
            
            if(columns.length > 1) throw new ConvirganceException("Single column requested for foreign key on table \"" + getTable().getName() + "\" when key consists of " + columns.length + " columns");
            
            return columns[0];
        }
        
        /**
         * Returns the list of columns in the foreign key
         * 
         * @return the columns in the foreign key
         */
        public Column[] getColumns()
        {
            Column[] columns = new Column[this.records.length];
            
            for(JSONObject record : records)
            {
                for(Column column : this.columns)
                {
                    if(record.getString("FKCOLUMN_NAME").equals(column.getName()))
                    {
                        columns[record.getInt("KEY_SEQ")-1] = column;
                    }
                }
            }
            
            return columns;
        }
        
        /**
         * The table being targeted by the foreign key. Typically, the foreign
         * key references this table's primary keys.
         * 
         * @return the Table exposing its primary keys to the foreign key reference
         */
        public Table getTarget()
        {
            Catalog catalog = getLayout().getCatalog(records[0].getString("PKTABLE_CAT"));
            Schema schema = catalog.getSchema(records[0].getString("PKTABLE_SCHEM"));
            
            return schema.getTable(records[0].getString("PKTABLE_NAME"));
        }
        
        /**
         * 
         * 
         * @return 
         */
        public Column getTargetKey()
        {
            Column[] columns = getTargetKeys();
            
            if(columns.length > 1) throw new ConvirganceException("Single column requested for key on table \"" + columns[0].getParent().getName() + "\" when key consists of " + columns.length + " columns");
            
            return columns[0];
        }
        
        /**
         * The keys referenced by the foreign key, typically the primary keys
         * of the target table.
         * 
         * @return key columns referenced by the foreign key
         */
        public Column[] getTargetKeys()
        {
            Column[] columns = new Column[this.records.length];
            
            for(JSONObject record : records)
            {
                for(Column column : getTarget().getColumns())
                {
                    if(record.getString("PKCOLUMN_NAME").equals(column.getName()))
                    {
                        columns[record.getInt("KEY_SEQ")-1] = column;
                    }
                }
            }
            
            return columns;
        }

        @Override
        public boolean equals(Object obj)
        {
            ForeignKey key;
            
            if(!(obj instanceof ForeignKey)) return false;
            
            key = (ForeignKey)obj;
            
            if(key.records.length != records.length) return false;
            if(!key.getTable().equals(getTable())) return false;
            
            for(int i=0; i<records.length; i++)
            {
                if(!records[i].equals(key.records[i])) return false;
            }
            
            return true;
        }

        @Override
        public int hashCode()
        {
            return 79 * 7 + Arrays.deepHashCode(this.records);
        }

        @Override
        public String toString()
        {
            return new JSONArray(Arrays.asList(records)).toString(4);
        }
    }
}

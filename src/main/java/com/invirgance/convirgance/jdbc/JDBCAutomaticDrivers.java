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
package com.invirgance.convirgance.jdbc;

import com.invirgance.convirgance.ConvirganceException;
import com.invirgance.convirgance.json.JSONArray;
import com.invirgance.convirgance.json.JSONObject;
import java.util.Arrays;
import java.util.Iterator;

/**
 *
 * @author jbanes
 */
public class JDBCAutomaticDrivers implements Iterable<JDBCAutomaticDriver>
{
    private static final JDBCDriverDatabase database = new JDBCDriverDatabase();
    
    public static AutomaticDriverBuilder createDriver(String name) throws ConvirganceException
    {
        if(database.findDescriptorByName(name) != null) throw new ConvirganceException(name + " already exists in the driver list");
        
        return new AutomaticDriverBuilder(name);
    }
    
    public static JDBCAutomaticDriver getDriverByName(String name)
    {
        return new JDBCAutomaticDriver(database.findDescriptorByName(name), database);
    }
    
    public static JDBCAutomaticDriver getDriverByURL(String url)
    {
        return new JDBCAutomaticDriver(database.findDescriptorByURL(url), database);
    }
    
    @Override
    public Iterator<JDBCAutomaticDriver> iterator()
    {
        Iterator<JSONObject> drivers = new JDBCDriverDatabase().iterator();
        
        return new Iterator<JDBCAutomaticDriver>() {
            
            @Override
            public boolean hasNext()
            {
                return drivers.hasNext();
            }

            @Override
            public JDBCAutomaticDriver next()
            {
                return new JDBCAutomaticDriver(drivers.next(), database);
            }
        };
    }
    
    public static class AutomaticDriverBuilder
    {
        private JSONObject record;

        AutomaticDriverBuilder(String name)
        {
            this.record = new JSONObject();

            this.record.put("name", name);
            this.record.put("artifact", new JSONArray());
            this.record.put("prefixes", new JSONArray());
            this.record.put("examples", new JSONArray());
        }

        public JDBCAutomaticDriver build()
        {
            return new JDBCAutomaticDriver(record, database);
        }

        public AutomaticDriverBuilder artifact(String... artifacts)
        {
            JSONArray array = this.record.getJSONArray("artifact");

            array.addAll(Arrays.asList(artifacts));

            return this;
        }
        
        public AutomaticDriverBuilder driver(String driverClass)
        {
            this.record.put("driver", driverClass);
            
            return this;
        }
        
        public AutomaticDriverBuilder datasource(String dataSourceClass)
        {
            this.record.put("datasource", dataSourceClass);
            
            return this;
        }

        public AutomaticDriverBuilder prefix(String... prefixes)
        {
            JSONArray array = this.record.getJSONArray("prefixes");

            array.addAll(Arrays.asList(prefixes));

            return this;
        }

        public AutomaticDriverBuilder example(String... examples)
        {
            JSONArray array = this.record.getJSONArray("examples");

            array.addAll(Arrays.asList(examples));

            return this;
        }
    }
}

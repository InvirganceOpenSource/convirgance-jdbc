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
package com.invirgance.convirgance.jdbc;

import com.invirgance.convirgance.json.JSONObject;
import com.invirgance.convirgance.storage.Config;
import java.io.File;
import java.io.PrintStream;
import java.util.Iterator;

/**
 *
 * @author jbanes
 */
class ConnectionDatabase implements Iterable<JSONObject>
{
    private Config config;

    public ConnectionDatabase()
    {
        PrintStream err = System.err;
        String property = System.getProperty("convirgance.jdbc.connections");
        
        File home = new File(System.getProperty("user.home"));
        File location = new File(new File(new File(home, ".convirgance"), "database"), "connections");
        
        if(property != null) location = new File(property);
        
        this.config = new Config(location, "name");
    }
    
    /**
     * Finds the connection descriptor that has the provided name.
     * @param name The name.
     * @return A JSONObject representing the connection descriptor.
     */
    public JSONObject findDescriptorByName(String name)
    {
        for(JSONObject descriptor : this)
        {
            if(descriptor.getString("name").equalsIgnoreCase(name)) return descriptor;
        }
        
        return null;
    }
    
    /**
     * Saves or updates the connections descriptor in the users configuration file.
     * 
     * @param descriptor 
     */
    public void saveDescriptor(JSONObject descriptor)
    {
        config.insert(descriptor);
    }
    
    /**
     * Removes the connections descriptor from the users configuration. 
     * 
     * @param descriptor 
     */
    public void deleteDescriptor(JSONObject descriptor)
    {
        config.delete(descriptor);
    }
    
    @Override
    public Iterator<JSONObject> iterator()
    {
        return config.iterator();
    }
}

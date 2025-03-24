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

import com.invirgance.convirgance.ConvirganceException;
import com.invirgance.convirgance.json.JSONArray;
import com.invirgance.convirgance.json.JSONObject;
import com.invirgance.convirgance.source.ClasspathSource;
import com.invirgance.convirgance.storage.Config;
import java.io.File;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Driver;
import java.util.Iterator;
import javax.sql.DataSource;
import org.jboss.shrinkwrap.resolver.api.maven.ConfigurableMavenResolverSystem;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;

/**
 * Manages the Driver descriptors used throughout the program.
 * Providing ways to load the classes with Maven, and make descriptor configuration changes.
 * 
 * @author jbanes
 */
class DriverDatabase implements Iterable<JSONObject>
{
    private Config config;
    
    /**
     * Setup for the DriverDatabase.
     * When instantiated, writes to the users home directory, under '.convirgance/database/drivers'
     */
    public DriverDatabase()
    {
        PrintStream err = System.err;
        String property = System.getProperty("convirgance.jdbc.drivers");
        
        File home = new File(System.getProperty("user.home"));
        File location = new File(new File(new File(home, ".convirgance"), "database"), "drivers");
        
        if(property != null) location = new File(property);
        
        // Disable unnecessary maven logging
        if(System.getProperty("org.slf4j.simpleLogger.defaultLogLevel") == null)
        {
            System.getProperty("org.slf4j.simpleLogger.defaultLogLevel", "error");
            
            System.setErr(new PrintStream(err) {
                private int counter;
                
                @Override
                public void println(String str)
                {
                    if(str.startsWith("SLF4J: ") && counter < 3)
                    {
                        counter++;
                        
                        return;
                    }
                    
                    if(System.err == this)
                    {
                        System.setErr(err); // We're done filtering. Time to return the stream
                    }
                    
                    super.println(str);
                }
                
            });
        }
        
        this.config = new Config(new ClasspathSource("/database/drivers.json"), location, "name");
    }
    
    /**
     * Returns information about the database with the provided name.
     * Ex "PostgreSQL" would return, the driver it uses, its data source 
     * and other information such as connection examples, prefixes
     * 
     * Used in AutomaticDriver to determine the correct driver.
     * 
     * @param name Database name
     * @return The descriptor
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
     * Returns the database descriptor based on the URL prefix.
     * Example: "jdbc:derby:classpath:SomeDatabaseName"
     * Used in AutomaticDriver to determine the correct driver.
     * 
     * @param url The URL or just the prefix itself.
     * @return The descriptor.
     */
    public JSONObject findDescriptorByURL(String url)
    {
        for(JSONObject descriptor : this)
        {
            for(String prefix : (JSONArray<String>)descriptor.getJSONArray("prefixes"))
            {
                if(url.startsWith(prefix)) return descriptor;
            }
        }
        
        return null;
    }
    
    /**
     * Adds or updates the database's descriptor in the users configuration file.
     * 
     * @param descriptor A JSONObject.
     */
    public void saveDescriptor(JSONObject descriptor)
    {
        config.insert(descriptor);
    }
    
    /**
     * Removes a database descriptor.
     * 
     * @param descriptor A JSONObject.
     */   
    public void deleteDescriptor(JSONObject descriptor)
    {
        config.delete(descriptor);
    }
    
    private URLClassLoader getClassLoader(JSONArray<String> artifacts)
    {
        ConfigurableMavenResolverSystem maven = Maven.configureResolver();
        URL[] files =  maven.withMavenCentralRepo(true).resolve(artifacts).withTransitivity().as(URL.class);
        
        return new URLClassLoader(files);
    }
    
    private Object loadClass(JSONArray<String> artifacts, String className)
    {
        URLClassLoader loader = getClassLoader(artifacts);
        Class clazz;
        
        try
        {
            clazz = loader.loadClass(className);
            
            return clazz.getDeclaredConstructor().newInstance();
        }
        catch(ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e)
        {
            throw new ConvirganceException(e);
        }
    }
    
    /**
     * Returns and loads the driver in the descriptor.
     * The driver itself is loaded in from maven based on the artifact id.
     * 
     * @param descriptor Driver descriptor.
     * @return The driver.
     */
    public Driver getDriver(JSONObject descriptor)
    {
        if(descriptor == null || descriptor.isNull("driver")) return null;
        
        return (Driver)loadClass(descriptor.getJSONArray("artifact"), descriptor.getString("driver"));
    }
    
    /**
     * Returns and loads the data source in the descriptor.
     * The class object is returned.
     * 
     * @param descriptor The descriptor.
     * @return The DataSource.
     */
    public DataSource getDataSource(JSONObject descriptor)
    {
        if(descriptor == null || descriptor.isNull("datasource")) return null;
                
        return (DataSource)loadClass(descriptor.getJSONArray("artifact"), descriptor.getString("datasource"));
    }
    
    // TODO Null pointer if user config missing at this point
    @Override
    public Iterator<JSONObject> iterator()
    {
        return config.iterator();
    }
    
}

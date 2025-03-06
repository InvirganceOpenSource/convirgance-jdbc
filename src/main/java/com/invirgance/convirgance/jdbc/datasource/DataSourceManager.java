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
package com.invirgance.convirgance.jdbc.datasource;

import com.invirgance.convirgance.ConvirganceException;
import com.invirgance.convirgance.json.JSONArray;
import com.invirgance.convirgance.json.JSONObject;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import javax.sql.DataSource;

/**
 *
 * @author jbanes
 */
public class DataSourceManager
{
    private DataSource source;
    private JSONArray<String> excluded;

    public DataSourceManager(DataSource source)
    {
        this.source = source;
        this.excluded = new JSONArray<>("class", "logWriter", "parentLogger", "connection", "loginTimeout");
    }

    public DataSource getDataSource()
    {
        return source;
    }
    
    private BeanInfo getBeanInfo()
    {
        try
        {
            return Introspector.getBeanInfo(source.getClass());
        }
        catch(IntrospectionException e) { throw new ConvirganceException(e); }
    }
    
    public String[] getProperties()
    {
        BeanInfo info = getBeanInfo();
        JSONArray<String> array = new JSONArray<>();
        
        for(PropertyDescriptor descriptor : info.getPropertyDescriptors())
        {
            if(excluded.contains(descriptor.getName())) continue;
            if(descriptor.getWriteMethod() == null) continue;
            
            array.add(descriptor.getName());
        }
        
        array.sort(null);
        
        return array.toArray(String[]::new);
    }
    
    public Object getProperty(String name)
    {
        BeanInfo info = getBeanInfo();
        Object result;
        
        for(PropertyDescriptor descriptor : info.getPropertyDescriptors())
        {
            if(!descriptor.getName().equals(name)) continue;
            
            try
            {
                if(descriptor.getReadMethod() == null) return null;
                
                return descriptor.getReadMethod().invoke(source);
            }
            catch(IllegalAccessException | InvocationTargetException e) { throw new ConvirganceException(e); }
        }
        
        throw new ConvirganceException("Property " + name + " not found");
    }
    
    public void setProperty(String name, String value)
    {
        setProperty(name, (Object)value);
    }
    
    private static boolean isWrapperType(Class primitiveType, Class argumentType) 
    {
        if(primitiveType == int.class) return argumentType == Integer.class;
        else if (primitiveType == double.class) return argumentType == Double.class;
        else if (primitiveType == boolean.class) return argumentType == Boolean.class;
        else if (primitiveType == long.class) return argumentType == Long.class;
        else if (primitiveType == float.class) return argumentType == Float.class;
        else if (primitiveType == short.class) return argumentType == Short.class;
        else if (primitiveType == byte.class) return argumentType == Byte.class;
        else if (primitiveType == char.class) return argumentType == Character.class;
        
        return false;
    }
    
    private Object coerceStringToPrimitive(Class primitiveType, String value)
    {
        if(primitiveType == int.class) return Integer.valueOf(value);
        else if(primitiveType == double.class) return Double.valueOf(value);
        else if(primitiveType == boolean.class) return Boolean.valueOf(value);
        else if(primitiveType == long.class) return Long.valueOf(value);
        else if(primitiveType == float.class) return Float.valueOf(value);
        else if(primitiveType == short.class) return Short.valueOf(value);
        else if(primitiveType == byte.class) return Byte.valueOf(value);
        else if(primitiveType == char.class) return value.charAt(0);
        
        throw new ConvirganceException("Unable to transform " + String.class + " to " + primitiveType + " for [" + value + "]");
    }
    
    private Object coerceStringToNumber(Class type, String value)
    {
        if(type == Integer.class) return Integer.valueOf(value);
        else if(type == Double.class) return Double.valueOf(value);
        else if(type == Boolean.class) return Boolean.valueOf(value);
        else if(type == Long.class) return Long.valueOf(value);
        else if(type == Float.class) return Float.valueOf(value);
        else if(type == Short.class) return Short.valueOf(value);
        else if(type == Byte.class) return Byte.valueOf(value);
        else if(type == Character.class) return value.charAt(0);
        
        throw new ConvirganceException("Unable to transform " + String.class + " to " + type + " for [" + value + "]");
    }
    
    private Object coerceNumber(Class type, Number value)
    {
        if(type == Integer.class || type == int.class) return value.intValue();
        else if(type == Double.class || type == double.class) return value.doubleValue();
        else if(type == Long.class || type == long.class) return value.longValue();
        else if(type == Float.class || type == float.class) return value.floatValue();
        else if(type == Short.class || type == short.class) return value.shortValue();
        else if(type == Byte.class || type == byte.class) return value.byteValue();
        
        throw new ConvirganceException("Unable to transform " + String.class + " to " + type + " for [" + value + "]");
    }
    
    private Object coerceValue(Class source, Class target, Object value)
    {
        if(source.equals(target)) return value;
        if(target.isAssignableFrom(source)) return value;

        // Check for auto-boxing
        if(target.isPrimitive() && isWrapperType(target, source)) return value;
        if(source.isPrimitive() && isWrapperType(source, target)) return value;
        
        // Handle strings that need to be parsed
        if(source.equals(String.class) && target.isPrimitive()) return coerceStringToPrimitive(target, (String)value);
        if(source.equals(String.class) && Number.class.isAssignableFrom(target)) return coerceStringToNumber(target, (String)value);
        if(source.equals(String.class) && Boolean.class.isAssignableFrom(target)) return coerceStringToNumber(target, (String)value);

        // Handle number casts
        if(Number.class.isAssignableFrom(source) && target.isPrimitive()) return coerceNumber(target, (Number)value);
        if(Number.class.isAssignableFrom(source) && Number.class.isAssignableFrom(target)) return coerceNumber(target, (Number)value);
        
        throw new ConvirganceException("Unable to transform " + source + " to " + target + " for [" + value + "]");
    }
    
    public void setProperty(String name, Object value)
    {
        BeanInfo info = getBeanInfo();
        
        for(PropertyDescriptor descriptor : info.getPropertyDescriptors())
        {
            if(!descriptor.getName().equals(name)) continue;
            
            try
            {
                if(value != null) 
                {
                    value = coerceValue(value.getClass(), descriptor.getPropertyType(), value);
                }
                
                descriptor.getWriteMethod().invoke(source, value);
                
                return;
            }
            catch(IllegalAccessException | InvocationTargetException e) { throw new ConvirganceException(e); }
        }
        
        throw new ConvirganceException("Property " + name + " not found");
    }
    
    public JSONObject getConfig()
    {
        JSONObject config = new JSONObject();
        
        for(String key : getProperties()) config.put(key, getProperty(key));
        
        return config;
    }
    
    public void setConfig(JSONObject config)
    {
        for(String key : config.keySet()) setProperty(key, config.get(key));
    }
}

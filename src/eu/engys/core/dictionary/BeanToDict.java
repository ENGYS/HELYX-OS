/*--------------------------------*- Java -*---------------------------------*\
 |		 o                                                                   |                                                                                     
 |    o     o       | HelyxOS: The Open Source GUI for OpenFOAM              |
 |   o   O   o      | Copyright (C) 2012-2016 ENGYS                          |
 |    o     o       | http://www.engys.com                                   |
 |       o          |                                                        |
 |---------------------------------------------------------------------------|
 |	 License                                                                 |
 |   This file is part of HelyxOS.                                           |
 |                                                                           |
 |   HelyxOS is free software; you can redistribute it and/or modify it      |
 |   under the terms of the GNU General Public License as published by the   |
 |   Free Software Foundation; either version 2 of the License, or (at your  |
 |   option) any later version.                                              |
 |                                                                           |
 |   HelyxOS is distributed in the hope that it will be useful, but WITHOUT  |
 |   ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or   |
 |   FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License   |
 |   for more details.                                                       |
 |                                                                           |
 |   You should have received a copy of the GNU General Public License       |
 |   along with HelyxOS; if not, write to the Free Software Foundation,      |
 |   Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA            |
\*---------------------------------------------------------------------------*/

package eu.engys.core.dictionary;

import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BeanToDict {

    private static final Logger logger = LoggerFactory.getLogger(BeanToDict.class);

    public static <B> B dictToBean(Dictionary dictionary, Class<B> klass) {
        try {
            B bean = klass.newInstance();

            dictToBean(dictionary, bean);

            return bean;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public static <B> void dictToBean(Dictionary dictionary, B bean) {
        Method[] methods = bean.getClass().getMethods();
        for (Method m : methods) {
            if (m.getName().startsWith("set")) {
                String fieldName = getFieldNameFromSetter(m);
                if (dictionary.found(fieldName)) {
                    if (dictionary.isField(fieldName)) {
                        String value = dictionary.lookup(fieldName);
                        Class<?> param = m.getParameterTypes()[0];
                        if (param.equals(double.class)) {
                            setValue(bean, m, Double.valueOf(value));
                        } else if (param.equals(long.class)) {
                            setValue(bean, m, Long.valueOf(value));
                        } else if (param.equals(int.class)) {
                            setValue(bean, m, Integer.valueOf(value));
                        } else if (param.equals(boolean.class)) {
                            setValue(bean, m, Boolean.valueOf(value));
                        } else if (param.equals(String.class)) {
                            setValue(bean, m, value);
                        } else if (param.isEnum()) {
                            try {
                                setValue(bean, m, Enum.valueOf((Class<? extends Enum>) param, value));
                            } catch (Exception e) {
                                setValue(bean, m, ((Class<? extends Enum>) param).getEnumConstants()[0]);
                            }
                        } else {
                            System.err.println("ERROR: " + param);
                            logger.error("");
                        }
                    } else if (dictionary.isDictionary(fieldName)) {
                        Class<?> class1 = m.getParameterTypes()[0];
                        Object bean1 = dictToBean(dictionary.subDict(fieldName), class1);
                        setValue(bean, m, bean1);
                    }

                }
            }
        }
    }

    private static <B> void setValue(B bean, Method m, Object value) {
        try {
            m.invoke(bean, value);
        } catch (Exception e) {
            logger.error("ERROR: double parameter", e);
        }
    }

    // public static void dictToBean(Dictionary dictionary, Object bean) {
    // Method[] methods = bean.getClass().getMethods();
    // try {
    // for (Method m : methods) {
    // if (m.getName().startsWith("set")) {
    // String fieldName = getFieldNameFromSetter(m);
    // if (dictionary.isField(fieldName)) {
    // String value = dictionary.lookup(fieldName);
    // m.invoke(bean, Double.valueOf(value));
    // }
    // }
    // }
    // } catch(Exception ex) {
    // ex.printStackTrace();
    // }
    // }

    public static Dictionary beanToDict(Object obj) {
        return beanToDict(null, obj);
    }

    public static Dictionary beanToDict(String name, Object obj) {
        Class<?> klass = obj.getClass();
        Method[] methods = klass.getMethods();
        Dictionary dictionary = new Dictionary(name != null ? name : getFieldNameFromClass(klass));
        // dictionary.add("class", obj.getClass().getName());

        for (Method m : methods) {
            String fieldName = "";
            if (m.getName().startsWith("get") && !m.getName().startsWith("getClass")) {
                fieldName = getFieldNameFromGetter(m);
            } else if (m.getName().startsWith("is")) {
                fieldName = getFieldNameFromBooleanGetter(m);
            } else {
                continue;
            }
            try {
                Object value = m.invoke(obj);
                if (value != null) {
                    addToDictionary(dictionary, fieldName, value);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return dictionary;
    }

    private static void addToDictionary(Dictionary dictionary, String fieldName, Object value) throws Exception {
        if (isSimple(value.getClass())) {
            dictionary.add(fieldName, value.toString());
        } else if (isList(value)) {
            dictionary.add(toList(fieldName, value));
        } else if (isArray(value)) {
            dictionary.add(fieldName, toArray(value));
        } else if (isMap(value)) {
            dictionary.add(mapToDict(fieldName, value));
        } else if (isFile(value)) {
            dictionary.add(fieldName, ((File) value).getAbsolutePath());
        } else if (isDictionary(value)) {
            Dictionary d = new Dictionary((Dictionary) value);
            d.setName(fieldName);
            dictionary.add(d);
        } else {
            dictionary.add(beanToDict(fieldName, value));
        }
    }

    private static boolean isSimple(Class<?> klass) {
        boolean isString = String.class.isAssignableFrom(klass);
        boolean isNumber = Number.class.isAssignableFrom(klass);
        boolean isBoolean = Boolean.class.isAssignableFrom(klass);
        boolean isEnum = Enum.class.isAssignableFrom(klass);
        boolean isPrimitive = klass.isPrimitive();
        return isString || isNumber || isBoolean || isEnum || isPrimitive;
    }

    private static boolean isList(Object value) {
        return List.class.isAssignableFrom(value.getClass()) || (value.getClass().isArray() && !isSimple(value.getClass().getComponentType()));
    }

    private static boolean isMap(Object value) {
        return Map.class.isAssignableFrom(value.getClass());
    }

    private static boolean isFile(Object value) {
        return File.class.isAssignableFrom(value.getClass());
    }

    private static boolean isDictionary(Object value) {
        return Dictionary.class.isAssignableFrom(value.getClass());
    }

    private static boolean isArray(Object value) {
        return value.getClass().isArray();
    }

    private static ListField toList(String name, Object value) throws Exception {
        ListField listField = new ListField(name);
        List<?> list = null;
        if (value.getClass().isArray()) {
            list = Arrays.asList((Object[]) value);
        } else {
            list = (List<?>) value;
        }
        for (Object obj : list) {
            listField.add(beanToDict(obj));
        }
        return listField;
    }

    public static Dictionary mapToDict(String name, Object value) {
        Map<?, ?> map = (Map<?, ?>) value;
        Dictionary dict = new Dictionary(name);
        try {
            for (Object key : map.keySet()) {
                addToDictionary(dict, key.toString(), map.get(key));
            }
        } catch (Exception ex) {

        }
        return dict;
    }

    private static String toArray(Object array) throws Exception {
        return toBracketedArrayOfStrings(array);
    }

    private static String toBracketedArrayOfStrings(Object array) {
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        for (int i = 0; i < Array.getLength(array); i++) {
            sb.append(" ");
            sb.append(Array.get(array, i).toString());
        }
        sb.append(")");

        return sb.toString();
    }

    private static String getFieldNameFromGetter(Method m) {
        String name = m.getName().substring(3);
        return name.substring(0, 1).toLowerCase() + name.substring(1);
    }

    private static String getFieldNameFromBooleanGetter(Method m) {
        String name = m.getName().substring(2);
        return name.substring(0, 1).toLowerCase() + name.substring(1);
    }

    private static String getFieldNameFromSetter(Method m) {
        String name = m.getName().substring(3);
        return name.substring(0, 1).toLowerCase() + name.substring(1);
    }

    static private String getFieldNameFromClass(Class<?> klass) {
        String name = klass.getSimpleName();
        return name.substring(0, 1).toLowerCase() + name.substring(1);
    }

    // private Method getSetterFromFieldName(String name, Class<?> klass) throws Exception {
    // String methodName = name.substring(0,1).toUpperCase()+name.substring(1);
    // methodName = "set"+methodName;
    // return klass.getMethod(methodName);
    // }
    //
    // private static Class<?> getClassFromFieldName(String name) throws ClassNotFoundException{
    // String className = name.substring(0,1).toUpperCase()+name.substring(1);
    // return Class.forName(className);
    // }
}

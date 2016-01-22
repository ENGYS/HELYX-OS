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

package eu.engys.util.bean;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Method;

import eu.engys.util.ui.textfields.DoubleField;

public class Bindings {

    public static void bind(final DoubleField field, final Object bean, final String key) {
        field.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals("value")) {
                    set(bean, key, field.getDoubleValue());
                }
            }
        });
    }
    
    private static void set(Object bean, String key, Object value) {
        try {
            Method method = bean.getClass().getMethod(toSetter(key), value.getClass());
            method.invoke(bean, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String toSetter(String key) {
        return "set" + key.substring(0, 1).toUpperCase() + key.substring(1);
    }

    private static String toGetter(String key) {
        return "get" + key.substring(0, 1).toUpperCase() + key.substring(1);
    }
    
}

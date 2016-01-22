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

package eu.engys.core.parameters;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import eu.engys.core.parameters.Parameter.ParameterKey;
import eu.engys.util.ui.builder.PanelBuilder;

public class Parameters implements Serializable {

    private Map<ParameterKey, Parameter> delegate = new HashMap<>();
    
    public Parameters(Parameters parameters) {
        for (ParameterKey key : parameters.delegate.keySet()) {
            Parameter parameter = parameters.get(key).cloneParameter();
            add(parameter);
        }
    }

    public Parameters() {
    }

    public void add(Parameter p) {
        delegate.put(p.getKey(), p);
    }

    public Parameter get(ParameterKey key) {
        return delegate.get(key);
    }

    public int getSize() {
        return delegate.size();
    }

    public void clear() {
        delegate.clear();
    }

    public void populate(PanelBuilder builder) {
        for (ParameterKey key : delegate.keySet()) {
            Parameter p = delegate.get(key);
            p.populate(builder);
        }        
    }

    public Collection<Parameter> values() {
        return delegate.values();
    }

    public Parameter get(String keyString) {
        for (ParameterKey key : delegate.keySet()) { 
            if (key.toString().equals(keyString)) {
                return delegate.get(key);
            }
        }
        return null;
    }

    public void print() {
        for (ParameterKey key : delegate.keySet()) { 
            System.out.println("[print] " + key.getClass().getSimpleName());
        }
    }

    public Map<ParameterKey, Parameter> toMap() {
        return delegate;
    }

}

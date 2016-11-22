/*******************************************************************************
 *  |       o                                                                   |
 *  |    o     o       | HELYX-OS: The Open Source GUI for OpenFOAM             |
 *  |   o   O   o      | Copyright (C) 2012-2016 ENGYS                          |
 *  |    o     o       | http://www.engys.com                                   |
 *  |       o          |                                                        |
 *  |---------------------------------------------------------------------------|
 *  |   License                                                                 |
 *  |   This file is part of HELYX-OS.                                          |
 *  |                                                                           |
 *  |   HELYX-OS is free software; you can redistribute it and/or modify it     |
 *  |   under the terms of the GNU General Public License as published by the   |
 *  |   Free Software Foundation; either version 2 of the License, or (at your  |
 *  |   option) any later version.                                              |
 *  |                                                                           |
 *  |   HELYX-OS is distributed in the hope that it will be useful, but WITHOUT |
 *  |   ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or   |
 *  |   FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License   |
 *  |   for more details.                                                       |
 *  |                                                                           |
 *  |   You should have received a copy of the GNU General Public License       |
 *  |   along with HELYX-OS; if not, write to the Free Software Foundation,     |
 *  |   Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA            |
 *******************************************************************************/
package eu.engys.core.parameters;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.dictionary.DictionaryUtils;
import eu.engys.core.parameters.Parameter.ParameterKey;
import eu.engys.util.progress.ProgressMonitor;

public class Parameters implements Serializable, Iterable<Parameter> {


    public static final String PARAMETERS = "parameters";
    public static final String BIN = ".bin";
    public static final String DICT = "Dict";
    
    private static final Logger logger = LoggerFactory.getLogger(Parameters.class);
    
    private static final long serialVersionUID = 2397988172238381687L;

    private Map<ParameterKey, Parameter> delegate = new LinkedHashMap<>();
    
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

    public void remove(Parameter p) {
        delegate.remove(p.getKey());
    }

    public int getSize() {
        return delegate.size();
    }

    public void clear() {
        delegate.clear();
    }

    @Override
    public Iterator<Parameter> iterator() {
        return delegate.values().iterator();
    }

    public Parameter[] toArray() {
        return delegate.values().toArray(new Parameter[0]);
    }
    
    public boolean contains(ParameterKey key) {
        return delegate.containsKey(key);
    }
    
    public Parameter get(ParameterKey key) {
        return delegate.get(key);
    }

    public Parameter get(String keyString) {
        for (ParameterKey key : delegate.keySet()) { 
            if (key.getKeyString().equals(keyString)) {
                return delegate.get(key);
            }
        }
        return null;
    }

    public void print() {
        for (ParameterKey key : delegate.keySet()) { 
            System.out.println("[print] " + key.getClass().getSimpleName() + ", " + (key.isGenerated()?"GEN":"NOTGEN") + ", " + (key.isGenerated()? key.getKeyString() : "nokey"));
        }
    }

    public Map<ParameterKey, Parameter> toMap() {
        return delegate;
    }

    public static Parameters read(File parent) {
        return readBinary(parent);
    }
    
    static Parameters readBinary(File parent) {
        // using java.io.Serializable
        File binFile = new File(parent, PARAMETERS + BIN);
        if (binFile.exists()) {
            try (InputStream is = new FileInputStream(binFile); InputStream buffer = new BufferedInputStream(is); ObjectInput input = new ObjectInputStream(buffer);) {
                return (Parameters) input.readObject();
            } catch (ClassNotFoundException ex) {
                logger.error("Cannot perform input. Class not found.", ex);
            } catch (IOException ex) {
                logger.error("Cannot perform input.", ex);
            }
        }
        
        return new Parameters();
    }

    static Parameters readDictionary(File parent) {
        Parameters parameters = new Parameters();
        File dictFile = new File(parent, PARAMETERS + DICT);
        if (dictFile.exists()) {
            Dictionary dictionary = DictionaryUtils.readDictionary(dictFile, (ProgressMonitor)null);
            List<Dictionary> dictionaries = dictionary.subDict(PARAMETERS).getDictionaries();
            for (Dictionary d : dictionaries) {
                System.out.println("Parameters.readDictionary() " + d.getName());
                String klass = d.lookup("class");
                try {
                    Parameter parameter = (Parameter) Class.forName(klass).newInstance();
                    parameter.fromDictionary(d);
                    parameters.add(parameter);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return parameters;
    }

    public static void write(File parent, Parameters parameters) {
        writeBinary(parent, parameters);
        writeDictionary(parent, parameters);
    }

    static void writeDictionary(File parent, Parameters parameters) {
        //dictionary
        File dictFile = new File(parent, PARAMETERS + DICT);
        Dictionary dictionary = new Dictionary(PARAMETERS);
        Map<ParameterKey, Parameter> map = parameters.toMap();
        for (ParameterKey key : map.keySet()) {
            Parameter p = map.get(key);
            dictionary.add(p.toDictionary());
        }
        
//        System.out.println("ParametersManager.writeDictionary() " + dictionary);
        DictionaryUtils.writeDictionaryFile(dictFile, dictionary);
    }

    public static void writeBinary(File parent, Parameters parameters) {
        // using java.io.Serializable
        File binFile = new File(parent, PARAMETERS + BIN);
        try (OutputStream os = new FileOutputStream(binFile); OutputStream buffer = new BufferedOutputStream(os); ObjectOutput output = new ObjectOutputStream(buffer);) {
            output.writeObject(parameters);
        } catch (IOException ex) {
            logger.error("Cannot perform output.", ex);
        }
    }
}

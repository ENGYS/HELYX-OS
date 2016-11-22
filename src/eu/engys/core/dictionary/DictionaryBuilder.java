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

package eu.engys.core.dictionary;

import eu.engys.core.dictionary.parser.ListField2;

public class DictionaryBuilder {

	
	private Dictionary dictionary;

	private DictionaryBuilder(String name) {
		this.dictionary = new Dictionary(name);
	}
	
	public static DictionaryBuilder newDictionary(String name) {
		return new DictionaryBuilder(name);
	}
	
	public DictionaryBuilder field(String k, String v) {
		dictionary.add(k, v);
		return this;
	}
	
	public DictionaryBuilder field(String k, Double v) {
	    dictionary.add(k, v);
	    return this;
	}

	public DictionaryBuilder field(String k, Integer v) {
	    dictionary.add(k, v);
	    return this;
	}

	public DictionaryBuilder fieldUniform(String k, Double v) {
	    dictionary.addUniform(k, v);
	    return this;
	}

	public DictionaryBuilder field(String k, boolean v) {
	    dictionary.add(k, v);
	    return this;
	}

	public DictionaryBuilder field(String k, double[] d) {
	    dictionary.add(k, d);
	    return this;
	}

	public DictionaryBuilder fieldUniform(String k, double[] d) {
	    dictionary.addUniform(k, d);
	    return this;
	}

	public DictionaryBuilder dimensionedScalar(String k, String v, String d) {
	    dictionary.add(new DimensionedScalar(k, v, d));
	    return this;
	}

	public DictionaryBuilder array(String k, String... v) {
		dictionary.add(k, v);
		return this;
	}

	public DictionaryBuilder dict(Dictionary d) {
		dictionary.add(d);
		return this;
	}
	
	public Dictionary done() {
		return dictionary;
	}

	public DictionaryBuilder list(String string, Dictionary... dicts) {
		ListField list = new ListField(string);
		for (Dictionary dictionary : dicts) {
			list.add(dictionary);
		}
		dictionary.add(list);
		return this;
	}

	public DictionaryBuilder list2(String string, DefaultElement... elements) {
		ListField2 list = new ListField2(string);
		for (DefaultElement fieldElement : elements) {
			list.add(fieldElement);
		}
		dictionary.add(list);
		return this;
	}

	public DictionaryBuilder scalar(String k, String dimensions, String v) {
		dictionary.add(new DimensionedScalar(k, v, dimensions));
		return this;
	}

	public DictionaryBuilder foamFile(String parent, String name) {
		dictionary.setFoamFile(FoamFile.getDictionaryFoamFile(parent,name));
		return this;
	}

}

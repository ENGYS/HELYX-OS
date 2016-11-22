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

public class DictionaryLinkResolver {

//	private Dictionary dictionary;
	private Dictionary linksDestination;

	public DictionaryLinkResolver(Dictionary linksDestination) {
		this.linksDestination = linksDestination;
	}
	
	public void resolve(Dictionary dictionary) {
		resolveLinks(dictionary, linksDestination);
	}
	
	private void resolveLinks(Dictionary dict, Dictionary linksDestination) {
		for (Dictionary d : dict.getDictionaries()) {
			resolveLinks(d, linksDestination);
		}
		if (dict.found(Dictionary.DICTIONARY_LINK)) {
			String link = dict.lookupString(Dictionary.DICTIONARY_LINK);
			//System.out.println("------------> link: "+link);
			String name = link.replace("$", "");
			//System.out.println("------------> name: "+name);
			dict.remove(Dictionary.DICTIONARY_LINK);

			Dictionary dest = findDestination(name, linksDestination);
			if (dest != null) {
				dict.merge(dest);
				//System.out.println("------------> dest: "+dest);
			}
		} 

		for(ListField lf : dict.getListFields()) {
			for(DefaultElement el : lf.getListElements()) {
				if (el instanceof Dictionary) {
					resolveLinks((Dictionary) el, linksDestination);
				}
			}
		}
		
		for(FieldElement f : dict.getFields()) {
			if (f.getName().startsWith(Dictionary.VALUE_LINK)) {
				String key = f.getName().replace(Dictionary.VALUE_LINK, "");
				//System.out.println("------------> key: "+key);
				String link = dict.lookupString(f.getName());
				//System.out.println("------------> link: "+link);
				String name = link.replace("$", "");
				//System.out.println("------------> name: "+name);
				dict.remove(f.getName());
				if (linksDestination.found(name)) {
					String dest = linksDestination.lookupString(name);
					//System.out.println("------------> dest: "+dest);
					dict.add(key, dest);
				} else {
					//System.err.println("Warning: "+name+" not found in dictionary "+linksDestination.getName());
				}
			} else if (f.getName().startsWith(Dictionary.VALUE_UNIFORM_LINK)) {
				String key = f.getName().replace(Dictionary.VALUE_UNIFORM_LINK, "");
				//System.out.println("------------> key: "+key);
				String link = dict.lookupString(f.getName());
				//System.out.println("------------> link: "+link);
				String name = link.replace("$", "");
				//System.out.println("------------> name: "+name);
				dict.remove(f.getName());
				if (linksDestination.found(name)) {
					String dest = linksDestination.lookupString(name);
					//System.out.println("------------> dest: "+dest);
					dict.add(key, "uniform "+dest);
				} else {
					//System.err.println("Warning: "+name+" not found in dictionary "+linksDestination.getName());
				}
			}
		}
	}

	private Dictionary findDestination(String name, Dictionary linksDestination) {
		if (linksDestination.isDictionary(name)) {
			return linksDestination.subDict(name);
		} else {
			for (Dictionary d : linksDestination.getDictionaries()) {
				Dictionary dest = findDestination(name, d);
				if (dest != null) {
					return dest;
				}
			}
		}
		return null;
	}
}

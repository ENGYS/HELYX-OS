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

import eu.engys.util.ApplicationInfo;

public class FoamFile extends Dictionary {

	private static final String HELYX = ApplicationInfo.getName();
	private static final String VERSION = ApplicationInfo.getVersion();
	public static final String HEADER = 
	"/*--------------------------------*- C++ -*----------------------------------*\\"+"\n"+
	"|       o          |                                                          |" +"\n"+
	"|    o     o       | "+HELYX+"                                                  |" +"\n"+
	"|   o   O   o      | Version: "+VERSION+"                                           |" +"\n"+
	"|    o     o       | Web:     http://www.engys.com                            |" +"\n"+
	"|       o          |                                                          |" +"\n"+
	"\\*---------------------------------------------------------------------------*/";
    
	private FoamFile(String version, String format, String classe, String location, String object) {
        super("FoamFile");
        add("version", version);
        add("format", format);
        add("class", classe);
        add("location", location);
        add("object", object);
    }
	
	public static FoamFile getFieldFoamFile(String name) {
        return new FoamFile("2.0", "ascii", name.startsWith("U") ? "volVectorField" : (name.startsWith("point") ? "pointVectorField" : "volScalarField"), "\"0\"", name);
    }

	public static FoamFile getDictionaryFoamFile(String parent, String name) {
	    return new FoamFile("2.0", "ascii", "dictionary", parent, name);
	}

	public static FoamFile getDictionaryFoamFile(String classe, String parent, String name) {
	    return new FoamFile("2.0", "ascii", classe, parent, name);
	}

}

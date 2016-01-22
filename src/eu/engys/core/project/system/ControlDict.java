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


package eu.engys.core.project.system;

import java.io.File;

import eu.engys.core.dictionary.DefaultElement;
import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.dictionary.DictionaryException;
import eu.engys.core.dictionary.FoamFile;
import eu.engys.core.dictionary.ListField;

public class ControlDict extends Dictionary {

	public static final String CONTROL_DICT = "controlDict";
	
//	public static final String JPLOT_VALUE = "jplot";
//	public static final String XMGR_VALUE = "xmgr";
//	public static final String GNUPLOT_VALUE = "gnuplot";
	public static final String RAW_VALUE = "raw";
//	public static final String SCIENTIFIC_VALUE = "scientific";
//	public static final String FIXED_VALUE = "fixed";
	public static final String GENERAL_VALUE = "general";
	public static final String COMPRESSED_VALUE = "compressed";
	public static final String UNCOMPRESSED_VALUE = "uncompressed";
//	public static final String BINARY_VALUE = "binary";
	public static final String ASCII_VALUE = "ascii";
	public static final String CLOCK_TIME_VALUE = "clockTime";
	public static final String CPU_TIME_VALUE = "cpuTime";
	public static final String TIME_STEP_VALUE = "timeStep";
	public static final String FIRST_TIME_VALUE = "firstTime";
	public static final String LATEST_TIME_VALUE = "latestTime";
	public static final String FUNCTIONS_KEY = "functions";
	public static final String ADJUSTABLE_RUN_TIME_KEY = "adjustableRunTime";
	public static final String RUN_TIME_VALUE = "runTime";
	public static final String WRITE_INTERVAL_KEY = "writeInterval";
	public static final String WRITE_CONTROL_KEY = "writeControl";
	public static final String PURGE_WRITE_KEY = "purgeWrite";
	public static final String WRITE_FORMAT_KEY = "writeFormat";
	public static final String WRITE_PRECISION_KEY = "writePrecision";
	public static final String WRITE_COMPRESSION_KEY = "writeCompression";
	public static final String TIME_FORMAT_KEY = "timeFormat";
	public static final String TIME_PRECISION_KEY = "timePrecision";
	public static final String GRAPH_FORMAT_KEY = "graphFormat";
	public static final String MAX_DELTA_T_KEY = "maxDeltaT";
	public static final String MAX_ALPHA_CO_KEY = "maxAlphaCo";
	public static final String MAX_CO_KEY = "maxCo";
	public static final String ADJUST_TIME_STEP_KEY = "adjustTimeStep";
	public static final String DELTA_T_KEY = "deltaT";
	public static final String END_TIME_KEY = "endTime";
	public static final String WRITE_NOW_KEY = "writeNow";
	public static final String START_TIME_KEY = "startTime";
	public static final String START_TIME_VALUE = "startTime";
	public static final String START_FROM_KEY = "startFrom";
	public static final String STOP_AT_KEY = "stopAt";
	public static final String RUNTIME_MODIFIABLE_KEY = "runTimeModifiable";
	public static final String INCLUDE_KEY = "include";
	public static final String[] START_FROM_VALUES = { FIRST_TIME_VALUE, LATEST_TIME_VALUE, START_TIME_VALUE };
	public static final String[] WRITE_CONTROL_VALUES = { TIME_STEP_VALUE, RUN_TIME_VALUE, CPU_TIME_VALUE, CLOCK_TIME_VALUE };
//	public static final String[] WRITE_FORMAT_VALUES = { ASCII_VALUE, BINARY_VALUE };
	public static final String[] WRITE_FORMAT_VALUES = { ASCII_VALUE };
	public static final String[] WRITE_COMPRESSION_VALUES = { UNCOMPRESSED_VALUE, COMPRESSED_VALUE };
	public static final String[] TIME_FORMAT_VALUES = { GENERAL_VALUE };
//	public static final String[] TIME_FORMAT_VALUES = { GENERAL_VALUE, FIXED_VALUE, SCIENTIFIC_VALUE };
	public static final String[] GRAPH_FORMAT_VALUE = { RAW_VALUE };
//	public static final String[] GRAPH_FORMAT_VALUE = { RAW_VALUE, GNUPLOT_VALUE, XMGR_VALUE, JPLOT_VALUE };
	
	/*
	 * Radiation
	 */
	public static final String RADIATION = "radiation";
	public static final String REGION_KEY = "region";
	public static final String FUNCTION_OBJECTS_LIBS_KEY = "functionObjectLibs";
	public static final String SOLVER_OBJECTS_SO_KEY = "( \"libsolverFunctionObjects.so\" )";
	public static final String NON_PARTICIPATING_RADIATION_KEY = "nonParticipatingRadiation";


	public static final String SOLAR = "solar";
	public static final String SOLAR_RADIATION_KEY = "solarRadiation";
	public static final String SOURCES_KEY = "sources";
	public static final String TRANSMISSIVITY_KEY = "transmissivity";
	public static final String OUTPUT_CONTROL_KEY = "outputControl";
	public static final String OUTPUT_INTERVAL_KEY = "outputInterval";
	public static final String SOLAR_INTENSITY_KEY = "solarIntensity";
	public static final String SOLAR_DIRECTION_KEY = "solarDirection";
	

	/*
	 *  ELEMENTS
	 */
	public static final String FA1 = "FA1";
	public static final String LDXZ = "LDxz";
	public static final String AVERAGING_START_TIME = "averagingStartTime";
	public static final String RHO_INF = "rhoInf";
	public static final String U_INF = "Uinf";

	public ControlDict() {
		super(CONTROL_DICT);
		setFoamFile(FoamFile.getDictionaryFoamFile(SystemFolder.SYSTEM, CONTROL_DICT));
	}

	public ControlDict(ControlDict controlDict) {
		super(controlDict);
	}

	public ControlDict(File controlDictFile) {
		this();
		readDictionary(controlDictFile);
	}

	public void check() throws DictionaryException {
	}

	public boolean isBinary() {
		return found("writeFormat") && lookup("writeFormat").equals("binary");
	}

	@Override
	public void merge(Dictionary dict) {
	    if (dict instanceof ControlDict) { 
	        ((ControlDict) dict).functionObjectsToDict();
	    }
		functionObjectsToDict();
		super.merge(dict);
		functionObjectsToList();
		if (dict instanceof ControlDict) { 
		    ((ControlDict) dict).functionObjectsToList();
		}
	}

	public void functionObjectsToDict() {
		if (found(FUNCTIONS_KEY) && isList(FUNCTIONS_KEY)) {
			ListField functionsList = getList(FUNCTIONS_KEY);
			Dictionary functionsDict = new Dictionary(FUNCTIONS_KEY);
			for (DefaultElement el : functionsList.getListElements()) {
				functionsDict.add(el);
			}
			remove(FUNCTIONS_KEY);
			add(functionsDict);
		}
	}

	public void functionObjectsToList() {
		if (found(FUNCTIONS_KEY) && isDictionary(FUNCTIONS_KEY)) {
			Dictionary functionsDict = subDict(FUNCTIONS_KEY);
			remove(FUNCTIONS_KEY);
			for (Dictionary dict : functionsDict.getDictionaries()) {
				addToList(FUNCTIONS_KEY, dict);
			}
		}
	}

	public String getValueOnFunctionObject(String foName, String key) {
		if (isList(FUNCTIONS_KEY)) {
			ListField functions = getList(FUNCTIONS_KEY);
			Dictionary foDict = functions.getDictionary(foName);
			if (foDict != null) {
				if (foDict.found(key)) {
					return foDict.lookup(key);
				}
			}
		} else if (isDictionary(FUNCTIONS_KEY)) {
			Dictionary functions = subDict(FUNCTIONS_KEY);
			Dictionary foDict = functions.subDict(foName);
			if (foDict != null) {
				if (foDict.found(key)) {
					return foDict.lookup(key);
				}
			}
		}
		return null;
	}

    public void startFromZero() {
        add(START_FROM_KEY, START_TIME_VALUE);
        add(START_TIME_KEY, "0");
    }
}

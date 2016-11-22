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
package eu.engys.core.project.defaults;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.project.state.State;
import eu.engys.util.Util;

public abstract class AbstractDefaultsProvider implements DefaultsProvider {

    public static final String FIELD_MAPS = "fieldMaps";
    
    private static final Logger logger = LoggerFactory.getLogger(AbstractDefaultsProvider.class);

    @Override
    public Dictionary getDefaultsFor(State state) {
        String primalState = toPrimalState(state);
        if (primalState == null) {
            String msg = "[ {} provider ]: No defaults for state: {}";
            logger.warn(msg,  getName(), state.state2String());
            return new Dictionary("");
        } else {
        	logger.info("[ {} provider ]: Defaults FOUND for state: {}",  getName(), state.state2String());
        }

        return mergeBase(primalState);
    }

    @Override
    public Dictionary getDefaultsFieldMapsFor(State state, String region) {
        if (region != null) {
            return getDefaultsFor(state).subDict(FIELD_MAPS+"."+region);
        } else {
            return getDefaultsFor(state).subDict(FIELD_MAPS);
        }
        
    }
    
    /**
     * 
     * @param stringOfState
     *            for example "(steady incompressible ras)"
     * @return for example "simpleFoam"
     */
    public String toPrimalState(State state) {
        String state2String = state.state2String();

        Dictionary statesDict = getStates();

        // System.out.println("AbstractDefaultsProvider.toPrimalState() "+statesDict);

        if (statesDict != null) {
            Map<String, String> STATES = Util.invertMap(statesDict.getFieldsMap());

            if (STATES.containsKey(state2String)) {
                return STATES.get(state2String);
            } else {
                logger.warn("[ {} Provider ]: State '{}' NOT AVAILABLE", getName(), state2String);
                return null;
            }
        } else {
            logger.warn("[ {} Provider ]: State '{}' NOT AVAILABLE", getName(), state2String);
            return null;
        }
    }

    private Dictionary mergeBase(String subDictID) {
        Dictionary baseDict = new Dictionary(subDictID);
        Dictionary stateData = getDefaultStateData();

        if (stateData != null && stateData.found(subDictID)) {
            baseDict.merge(stateData.subDict(subDictID));
        } else {
            logger.warn("'" + subDictID + "' NOT FOUND");
        }

        // System.out.println("AbstractDefaultsProvider.mergeBase() "+baseDict);

        if (baseDict.found("base")) {
            String baseName = baseDict.lookup("base");
            Dictionary bd = mergeBase(baseName);
            bd.merge(baseDict);
            return bd;
        }

        return baseDict;
    }

    static Dictionary extractModule(DefaultsProvider defaults, String encodedPrimalState, String moduleName) {
        if (defaults.getDefaultStateData().found(moduleName)) {
            Dictionary defaultModule = new Dictionary(defaults.getDefaultStateData().subDict(moduleName));
            Dictionary relativeToStateModule = extractRelativeToStateModule(encodedPrimalState, defaultModule);
            if (relativeToStateModule != null) {
                defaultModule.merge(relativeToStateModule);
            }
            return defaultModule;
        }
        return null;
    }

    private static Dictionary extractRelativeToStateModule(String encodedPrimalState, Dictionary mDict) {
        if (mDict.found("requirements")) {
            Dictionary requirements = (Dictionary) mDict.remove("requirements");
            if (requirements.found("conditional")) {
                if (requirements.subDict("conditional").found(encodedPrimalState)) {
                    Dictionary conditional = requirements.subDict("conditional").subDict(encodedPrimalState);
                    return conditional;
                }
            }
        }
        return null;
    }

}

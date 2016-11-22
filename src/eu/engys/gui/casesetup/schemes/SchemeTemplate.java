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
package eu.engys.gui.casesetup.schemes;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import eu.engys.core.dictionary.Dictionary;

public class SchemeTemplate {
    
    private final String key;
    private final String label;
    private final Dictionary functions;

    public SchemeTemplate(String key, String label, Dictionary functions) {
        this.key = key;
        this.label = label;
        this.functions = functions;
    }
    
    public SchemeTemplate copy(){
        return new SchemeTemplate(key, label, functions == null ? null : new Dictionary(functions));
    }

    @Override
    public String toString() {
        return label;
    }
    
    public boolean hasValue1() {
        return key.contains("%f");
    }

    public boolean hasValue2() {
        return key.contains("%f %f");
    }

    public boolean hasValue3() {
        return StringUtils.countMatches(key, "%f") == 3;
    }

    public boolean equalsIgnoringValues(String field, String schemeKey) {
        String keyWithFieldName = key.replace("%s", SchemeUtils.gradName(field));

        String[] schemeKeyTokens = schemeKey.split("\\s+");
        String[] keyWithFieldNameTokens = keyWithFieldName.split("\\s+");

        if (schemeKeyTokens.length != keyWithFieldNameTokens.length)
            return false;

        for (int i = 0; i < schemeKeyTokens.length; i++) {
            if (schemeKeyTokens[i].equals(keyWithFieldNameTokens[i])) {
                continue;
            } else if (keyWithFieldNameTokens[i].equals("%f")) {
                continue;
            } else {
                return false;
            }
        }
        return true;
    }

    public List<Double> extractValues(String field, String schemeKey) {
        String keyWithFieldName = key.replace("%s", SchemeUtils.gradName(field));

        String[] schemeKeyTokens = schemeKey.split("\\s+");
        String[] keyWithFieldNameTokens = keyWithFieldName.split("\\s+");

        List<Double> values = new ArrayList<Double>();
        for (int i = 0; i < schemeKeyTokens.length; i++) {
            if (keyWithFieldNameTokens[i].equals("%f")) {
                values.add(Double.parseDouble(schemeKeyTokens[i]));
            }
        }
        return values;
    }
    
    public String getKey() {
        return key;
    }
    
    public String getLabel() {
        return label;
    }

    public Dictionary getFunctions() {
        return functions;
    }
}

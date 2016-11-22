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
package eu.engys.core.project;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.lang.ArrayUtils;

import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.project.defaults.Defaults;
import eu.engys.core.project.state.Flow;
import eu.engys.core.project.state.Method;
import eu.engys.core.project.state.SolverType;
import eu.engys.core.project.zero.fields.Fields;

public class TurbulenceModels {

    private final List<TurbulenceModel> compressibleRAS = new ArrayList<>();
    private final List<TurbulenceModel> incompressibleRAS = new ArrayList<>();
    private final List<TurbulenceModel> incompressibleLES = new ArrayList<>();
    private final List<TurbulenceModel> compressibleLES = new ArrayList<>();

    private final Map<String, List<TurbulenceModel>> moduleModels = new HashMap<>();

    @Inject
    public TurbulenceModels(Defaults defaults) {
        loadModelsFromDefaults(defaults.getDefaultTurbulenceProperties());
    }

    public Map<String, List<TurbulenceModel>> getModuleModels() {
        return moduleModels;
    }

    private void loadModelsFromDefaults(Dictionary turbulenceProperties) {
        if (turbulenceProperties.isDictionary("compressibleRAS")) {
            Dictionary d = turbulenceProperties.subDict("compressibleRAS");
            for (Dictionary m : d.getDictionaries()) {
                compressibleRAS.add(dictToTurbulenceModel(m));
            }
        }
        if (turbulenceProperties.isDictionary("incompressibleRAS")) {
            Dictionary d = turbulenceProperties.subDict("incompressibleRAS");
            for (Dictionary m : d.getDictionaries()) {
                incompressibleRAS.add(dictToTurbulenceModel(m));
            }
        }
        if (turbulenceProperties.isDictionary("compressibleLES")) {
            Dictionary d = turbulenceProperties.subDict("compressibleLES");
            for (Dictionary m : d.getDictionaries()) {
                compressibleLES.add(dictToTurbulenceModel(m));
            }
        }
        if (turbulenceProperties.isDictionary("incompressibleLES")) {
            Dictionary d = turbulenceProperties.subDict("incompressibleLES");
            for (Dictionary m : d.getDictionaries()) {
                incompressibleLES.add(dictToTurbulenceModel(m));
            }
        }
    }

    public static TurbulenceModel dictToTurbulenceModel(Dictionary m) {
        String name = nameFromDictionary(m);
        String description = descriptionFromDictionary(m);
        TurbulenceModelType type = typeFromDictionary(m, name);
        return new TurbulenceModel(name, description, type);
    }

    private static String nameFromDictionary(Dictionary m) {
        return m.getName().replace("Coeffs", "");
    }

    private static String descriptionFromDictionary(Dictionary m) {
        return m.found("label") ? fromUnicode(m.lookup("label").replace("\"", "")) : m.getName();
    }

    private static TurbulenceModelType typeFromDictionary(Dictionary m, String name) {
        Dictionary fieldMaps = m.subDict("fieldMaps");
        if (fieldMaps == null) {
            return TurbulenceModelType.LAMINAR;
        } else if (fieldMaps.isField(Fields.K) && fieldMaps.isField(Fields.OMEGA)) {
            return TurbulenceModelType.K_Omega;
        } else if (fieldMaps.isField(Fields.K) && fieldMaps.isField(Fields.EPSILON)) {
            return TurbulenceModelType.K_Epsilon;
        } else if (fieldMaps.isField(Fields.NU_TILDA)) {
            return TurbulenceModelType.Spalart_Allmaras;
        } else if (fieldMaps.isField(Fields.K) && fieldMaps.isField(Fields.NU_SGS)) {
            return TurbulenceModelType.K_Equation_Eddy;
        } else if (name.contains("Smagorinsky")) {
            return TurbulenceModelType.Smagorinsky;
        } else {
            return TurbulenceModelType.LAMINAR;
        }
    }

    public List<TurbulenceModel> getModelsForState(SolverType solverType, Method method, Flow flow) {
        if (solverType.isCoupled()) {
            String key = "coupled" + method.key();
            return moduleModels.containsKey(key) ? moduleModels.get(key) : Collections.<TurbulenceModel> emptyList();
        } else if (solverType.isSegregated()) {
            if (flow.isCompressible()) {
                if (method.isRans()) {
                    return compressibleRAS;
                } else if (method.isLes()) {
                    return compressibleLES;
                } else {
                    return Collections.<TurbulenceModel> emptyList();
                }
            } else if (flow.isIncompressible()) {
                if (method.isRans()) {
                    return incompressibleRAS;
                } else if (method.isLes()) {
                    return incompressibleLES;
                } else {
                    return Collections.<TurbulenceModel> emptyList();
                }
            } else {
                return Collections.<TurbulenceModel> emptyList();
            }
        } else {
            return Collections.<TurbulenceModel> emptyList();
        }
    }

    private static final char[] NUMBERS = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };
    private static final char[] letters = { 'a', 'b', 'c', 'd', 'e', 'f' };
    private static final char[] LETTERS = { 'A', 'B', 'C', 'D', 'E', 'F' };
    private static final char U = 'u';
    private static final char BS = '\\';
    private static final char ZERO = '0';
    private static final char AL = 'a';
    private static final char AU = 'A';
    private static final char T = 't';
    private static final char R = 'r';
    private static final char N = 'n';
    private static final char F = 'f';
    private static final char TAB = '\t';
    private static final char FORM_FEED = '\f';
    private static final char RETURN = '\r';
    private static final char NEW_LINE = '\n';

    private static String fromUnicode(String text) {
        char c;
        int lenght = text.length();
        StringBuffer buffer = new StringBuffer(lenght);

        for (int x = 0; x < lenght;) {
            c = text.charAt(x++);
            if (c == BS) {
                c = text.charAt(x++);
                if (c == U) {
                    int value = 0;
                    for (int i = 0; i < 4; i++) {
                        c = text.charAt(x++);
                        if (ArrayUtils.contains(NUMBERS, c)) {
                            value = (value << 4) + c - ZERO;
                        } else if (ArrayUtils.contains(letters, c)) {
                            value = (value << 4) + 10 + c - AL;
                        } else if (ArrayUtils.contains(LETTERS, c)) {
                            value = (value << 4) + 10 + c - AU;
                        }
                    }
                    buffer.append((char) value);
                } else {
                    if (c == T)
                        c = TAB;
                    else if (c == R)
                        c = RETURN;
                    else if (c == N)
                        c = NEW_LINE;
                    else if (c == F)
                        c = FORM_FEED;
                    buffer.append(c);
                }
            } else
                buffer.append(c);
        }
        return buffer.toString();
    }

}

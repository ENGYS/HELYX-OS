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
package eu.engys.gui.casesetup.boundaryconditions.utils;

import static eu.engys.core.dictionary.Dictionary.VALUE;

import eu.engys.core.dictionary.model.DictionaryModel;
import eu.engys.core.dictionary.model.DictionaryPanelBuilder;

public class ThermalUtils extends BoundaryConditionsUtils {

    public static void buildFixedTemperaturePanel(DictionaryPanelBuilder builder, DictionaryModel model) {
        builder.startDictionary(FIXED_TEMPERATURE_LABEL, model);
        builder.addComponent(TEMPERATURE_VALUE_LABEL, model.bindUniformDouble(VALUE));
        builder.endDictionary();
        model.setDictionary(model.getDictionary());
    }

    public static void buildInletOutletTemperaturePanel(DictionaryPanelBuilder builder, DictionaryModel model) {
        builder.startDictionary(INFLOW_FIXED_TEMPERATURE_LABEL, model);
        builder.addComponent(TEMPERATURE_INITIAL_VALUE_LABEL, model.bindUniformDouble(VALUE));
        builder.addComponent(TEMPERATURE_INFLOW_VALUE_LABEL, model.bindUniformDouble(INLET_VALUE_KEY));
        builder.endDictionary();
    }

    public static void buildAdvectiveTemperaturePanel(DictionaryPanelBuilder builder, DictionaryModel model) {
        builder.startDictionary(ADVECTIVE_TEMPERATURE_LABEL, model);
        builder.endDictionary();
    }

    public static void buildTotalTemperaturePanel(DictionaryPanelBuilder builder, DictionaryModel dict) {
        builder.startDictionary(TOTAL_TEMPERATURE_LABEL, dict);
        builder.addComponent(RATIO_OF_SPECIFIC_HEATS_LABEL, dict.bindDouble(GAMMA_KEY));
        builder.addComponent(TEMPERATURE_VALUE_LABEL, dict.bindUniformDouble(T0_KEY));
        builder.endDictionary();
    }

}

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
package eu.engys.gui.casesetup.cellzones.thermal;

import static eu.engys.core.project.zero.cellzones.CellZonesUtils.T_KEY;
import static eu.engys.gui.casesetup.cellzones.CellZonesFactory.thermalFixedTemperature_OS;
import static eu.engys.gui.casesetup.cellzones.thermal.StandardThermal.THERMAL_LABEL;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;

import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.dictionary.model.DictionaryModel;
import eu.engys.core.dictionary.model.DictionaryPanelBuilder;
import eu.engys.core.modules.cellzones.CellZonePanel;
import eu.engys.core.project.zero.cellzones.CellZoneType;

public class StandardCellZoneThermalPanel implements CellZonePanel {

    private DictionaryPanelBuilder builder;

    @Override
    public void layoutPanel() {
        DictionaryModel fixedModel = new DictionaryModel(new Dictionary(thermalFixedTemperature_OS));

        builder = new DictionaryPanelBuilder();
        builder.startChoice(MODEL_LABEL);

        builder.startDictionary(FIXED_TEMPERATURE_LABEL, fixedModel);
        builder.addComponent(FIXED_TEMPERATURE_K_LABEL, fixedModel.bindConstantDouble(T_KEY));
        builder.endDictionary();

        builder.endChoice();

    }

    @Override
    public JComponent getPanel() {
        JPanel panel = builder.getPanel();
        panel.setBorder(BorderFactory.createTitledBorder(THERMAL_LABEL));
        panel.setName(THERMAL_LABEL);
        return panel;
    }

    @Override
    public void stateChanged() {
    }

    @Override
    public void loadFromDictionary(String cellZoneName, Dictionary cellZoneDictionary) {
        Dictionary d = new Dictionary(CellZoneType.THERMAL_KEY);
        d.merge(cellZoneDictionary);
        builder.selectDictionary(d);
    }

    @Override
    public Dictionary saveToDictionary(String czName) {
        return builder.getSelectedModel().getDictionary();
    }

}

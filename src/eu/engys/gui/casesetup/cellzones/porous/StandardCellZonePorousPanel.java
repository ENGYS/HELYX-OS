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

package eu.engys.gui.casesetup.cellzones.porous;

import static eu.engys.core.project.zero.cellzones.CellZonesUtils.C0_KEY;
import static eu.engys.core.project.zero.cellzones.CellZonesUtils.C1_KEY;
import static eu.engys.core.project.zero.cellzones.CellZonesUtils.D_KEY;
import static eu.engys.core.project.zero.cellzones.CellZonesUtils.E1_KEY;
import static eu.engys.core.project.zero.cellzones.CellZonesUtils.E2_KEY;
import static eu.engys.core.project.zero.cellzones.CellZonesUtils.F_KEY;
import static eu.engys.gui.casesetup.cellzones.CellZonesFactory.porousDarcyForchheimer;
import static eu.engys.gui.casesetup.cellzones.CellZonesFactory.porousPowerLaw;
import static eu.engys.gui.casesetup.cellzones.porous.StandardPorous.POROUS_LABEL;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;

import com.google.inject.Inject;

import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.dictionary.model.DictionaryModel;
import eu.engys.core.dictionary.model.DictionaryPanelBuilder;
import eu.engys.core.modules.cellzones.CellZonePanel;
import eu.engys.core.project.zero.cellzones.CellZoneType;
import eu.engys.util.DimensionalUnits;

public class StandardCellZonePorousPanel implements CellZonePanel {

    private DictionaryPanelBuilder builder;

    @Inject
    public StandardCellZonePorousPanel() {
    }

    @Override
    public void layoutPanel() {
        DictionaryModel porousDarcyModel = new DictionaryModel(new Dictionary(porousDarcyForchheimer));
        DictionaryModel porousPowerLawModel = new DictionaryModel(new Dictionary(porousPowerLaw));

        builder = new DictionaryPanelBuilder();
        builder.startChoice(MODEL_LABEL);

        builder.startDictionary(DARCY_FORCHHEIMER, porousDarcyModel);
        builder.addComponent(E1_LABEL, porousDarcyModel.bindPoint(E1_KEY));
        builder.addComponent(E2_LABEL, porousDarcyModel.bindPoint(E2_KEY));
        builder.addComponent(VISCOUS_LOSS_LABEL, porousDarcyModel.bindDimensionedPoint(D_KEY, DimensionalUnits._M2));
        builder.addComponent(INERTIAL_LOSS_LABEL, porousDarcyModel.bindDimensionedPoint(F_KEY, DimensionalUnits._M));
        builder.endDictionary();

        builder.startDictionary(POWER_LAW, porousPowerLawModel);
        builder.addComponent(C0_LABEL, porousPowerLawModel.bindDouble(C0_KEY));
        builder.addComponent(C1_LABEL, porousPowerLawModel.bindDouble(C1_KEY));
        builder.endDictionary();

        builder.endChoice();
    }

    @Override
    public JComponent getPanel() {
        JPanel panel = builder.getPanel();
        panel.setBorder(BorderFactory.createTitledBorder(POROUS_LABEL));
        panel.setName(POROUS_LABEL);
        return panel;
    }

    @Override
    public void stateChanged() {
    }

    @Override
    public void loadFromDictionary(Dictionary cellZoneDictionary) {
        Dictionary d = new Dictionary(CellZoneType.POROUS_KEY);
        d.merge(cellZoneDictionary);
        builder.selectDictionary(d);
    }

    @Override
    public Dictionary saveToDictionary() {
        return builder.getSelectedModel().getDictionary();
    }
}

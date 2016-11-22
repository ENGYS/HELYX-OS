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
package eu.engys.dynamic.cellzones;

import static eu.engys.dynamic.cellzones.DynamicMesh.DYNAMIC_MESH_LABEL;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;

import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.modules.cellzones.CellZonePanel;
import eu.engys.core.project.Model;
import eu.engys.dynamic.DynamicModule;
import eu.engys.dynamic.data.DynamicAlgorithm;
import eu.engys.dynamic.data.multibody.MultiBodyAlgorithm;
import eu.engys.dynamic.data.singlebody.SolidBodyAlgorithm;
import eu.engys.dynamic.domain.SolidBodyDynamicPanel;

public class MultiBodyDynamicMeshPanel extends JPanel implements CellZonePanel {

    private SolidBodyDynamicPanel solidBodyPanel;
    protected Model model;
    protected DynamicModule module;

    public MultiBodyDynamicMeshPanel(Model model, DynamicModule module) {
        super(new BorderLayout());
        this.model = model;
        this.module = module;
        setName(DYNAMIC_MESH_LABEL);
        this.solidBodyPanel = createSolidBodyPanel();
    }

    protected SolidBodyDynamicPanel createSolidBodyPanel() {
        return new SolidBodyDynamicPanel(model, module);
    }

    @Override
    public void layoutPanel() {
        JPanel panel = (JPanel) solidBodyPanel.layoutComponents();
        panel.setBorder(BorderFactory.createTitledBorder(DYNAMIC_MESH_LABEL));
        add(panel, BorderLayout.CENTER);
    }

    @Override
    public void stateChanged() {
        solidBodyPanel.stateChanged();
    }

    @Override
    public JComponent getPanel() {
        return this;
    }

    @Override
    public void loadFromDictionary(String cellZoneName, Dictionary cellZoneDictionary) {
        // Dynamic cell zones are read from DynamicMeshDict as a bean no need to use dictionaries here
        DynamicAlgorithm algo = module.getDynamicData().getAlgorithm();
        if (algo.getType().isMultiRigidBody()) {
            MultiBodyAlgorithm mba = (MultiBodyAlgorithm) algo;
            SolidBodyAlgorithm singleBodyAlgo = mba.getSingleBodyAlgorithms().get(cellZoneName);
            if (singleBodyAlgo != null) {
                solidBodyPanel.getMainModel().setBean(singleBodyAlgo.copy());
            }
        }
    }

    @Override
    public Dictionary saveToDictionary(String czName) {
        // Dynamic cell zones are saved by the module inside DynamicMeshDict using a bean
        DynamicAlgorithm algo = module.getDynamicData().getAlgorithm();
        if (algo.getType().isMultiRigidBody()) {
            MultiBodyAlgorithm mba = (MultiBodyAlgorithm) algo;
            mba.getSingleBodyAlgorithms().put(czName, solidBodyPanel.getMainModel().getBean().copy());
        }
        return new Dictionary("");
    }

}

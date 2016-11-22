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
package eu.engys.dynamic.domain;

import static eu.engys.dynamic.data.DynamicAlgorithmType.MESH_REFINE;
import static eu.engys.dynamic.data.refine.MeshRefineAlgorithm.FIELD;
import static eu.engys.dynamic.data.refine.MeshRefineAlgorithm.LOWER_REFINE_LEVEL;
import static eu.engys.dynamic.data.refine.MeshRefineAlgorithm.MAX_CELLS;
import static eu.engys.dynamic.data.refine.MeshRefineAlgorithm.MAX_REFINEMENT;
import static eu.engys.dynamic.data.refine.MeshRefineAlgorithm.N_BUFFER_LAYERS;
import static eu.engys.dynamic.data.refine.MeshRefineAlgorithm.REFINE_INTERVAL;
import static eu.engys.dynamic.data.refine.MeshRefineAlgorithm.UNREFINE_LEVEL;
import static eu.engys.dynamic.data.refine.MeshRefineAlgorithm.UPPER_REFINE_LEVEL;

import javax.swing.JComboBox;
import javax.swing.JComponent;

import eu.engys.core.project.Model;
import eu.engys.dynamic.DynamicModule;
import eu.engys.dynamic.data.refine.MeshRefineAlgorithm;
import eu.engys.gui.ListBuilderFactory;
import eu.engys.gui.ReloadableListModel;
import eu.engys.util.bean.BeanModel;
import eu.engys.util.ui.builder.PanelBuilder;

public class MeshRefineDynamicPanel extends AbstractDynamicPanel {

    public static final String REFINE_INTERVAL_LABEL = "Refine Interval";
    public static final String FIELD_LABEL = "Field";
    public static final String LOWER_REFINE_LEVEL_LABEL = "Lower Refine Level";
    public static final String UPPER_REFINE_LEVEL_LABEL = "Upper Refine Level";
    public static final String UNREFINE_LEVEL_LABEL = "Unrefine Level";
    public static final String N_BUFFER_LAYERS_LABEL = "Buffer Layers";
    public static final String MAX_REFINEMENT_LABEL = "Max Refinement";
    public static final String MAX_CELLS_LABEL = "Max Cells";

    private BeanModel<MeshRefineAlgorithm> beanModel;

    private JComboBox<String> fieldCombo;
    private ReloadableListModel fieldModel;
	private DynamicModule module;

    public MeshRefineDynamicPanel(Model model, DynamicModule module) {
        super(model, MESH_REFINE.getLabel());
		this.module = module;
        this.beanModel = new BeanModel<MeshRefineAlgorithm>(new MeshRefineAlgorithm());
    }

    @Override
    protected JComponent layoutComponents() {
        PanelBuilder builder = new PanelBuilder();
        builder.addComponent(FIELD_LABEL, fieldCombo = beanModel.bindSelection(FIELD, fieldModel = ListBuilderFactory.getScalarFieldsListModel(model)));
        builder.addComponent(REFINE_INTERVAL_LABEL, beanModel.bindInteger(REFINE_INTERVAL));
        builder.addComponent(LOWER_REFINE_LEVEL_LABEL, beanModel.bindDouble(LOWER_REFINE_LEVEL));
        builder.addComponent(UPPER_REFINE_LEVEL_LABEL, beanModel.bindDouble(UPPER_REFINE_LEVEL));
        builder.addComponent(UNREFINE_LEVEL_LABEL, beanModel.bindInteger(UNREFINE_LEVEL));
        builder.addComponent(N_BUFFER_LAYERS_LABEL, beanModel.bindInteger(N_BUFFER_LAYERS));
        builder.addComponent(MAX_REFINEMENT_LABEL, beanModel.bindInteger(MAX_REFINEMENT));
        builder.addComponent(MAX_CELLS_LABEL, beanModel.bindInteger(MAX_CELLS));

        return builder.removeMargins().getPanel();
    }

    @Override
    public void stateChanged() {
        Object selection = fieldCombo.getModel().getSelectedItem();
        fieldModel.reload();
        fieldCombo.getModel().setSelectedItem(selection);
    }

    @Override
    public void load() {
        fieldModel.reload();
        if (module.getDynamicData().getAlgorithm().getType().isMeshRefine()) {
            beanModel.setBean((MeshRefineAlgorithm) module.getDynamicData().getAlgorithm().copy());
        }
    }

    @Override
    public void save() {
        if (module.getDynamicData().getAlgorithm().getType().isMeshRefine()) {
            module.getDynamicData().setAlgorithm(beanModel.getBean().copy());
        }
    }

}

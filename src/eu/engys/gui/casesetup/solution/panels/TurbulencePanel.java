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
package eu.engys.gui.casesetup.solution.panels;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

import eu.engys.core.project.Model;
import eu.engys.core.project.TurbulenceModel;
import eu.engys.core.project.state.Flow;
import eu.engys.core.project.state.Method;
import eu.engys.core.project.state.SolutionState;
import eu.engys.core.project.state.SolverType;
import eu.engys.core.project.state.State;
import eu.engys.util.ui.builder.PanelBuilder;

public class TurbulencePanel extends JPanel {

    public static final String TURBULENCE_MODEL = "Turbulence Model";
    private JComboBox<TurbulenceModel> modelsCombo;

    public TurbulencePanel() {
        super(new BorderLayout());
        layoutComponents();
    }

    private void layoutComponents() {
        modelsCombo = new JComboBox<TurbulenceModel>();
        modelsCombo.setPrototypeDisplayValue(TurbulenceModel.getPrototypeForDisplay());
        final ListCellRenderer<? super TurbulenceModel> renderer = modelsCombo.getRenderer();
        modelsCombo.setRenderer(new ListCellRenderer<TurbulenceModel>() {
            @Override
            public Component getListCellRendererComponent(JList<? extends TurbulenceModel> list, TurbulenceModel value, int index, boolean isSelected, boolean cellHasFocus) {
                Component c = renderer.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (c instanceof JLabel && value instanceof TurbulenceModel) {
                    TurbulenceModel model = (TurbulenceModel) value;
                    ((JLabel) c).setText(model.getDescription());
                }
                return c;
            }
        });
        PanelBuilder builder = new PanelBuilder();
        builder.addComponent(TURBULENCE_MODEL, modelsCombo);
        add(builder.margins(.5, 0, .5, 0).getPanel(), BorderLayout.CENTER);
    }

    public TurbulenceModel getSelectedTurbulenceModel() {
        return (TurbulenceModel) modelsCombo.getSelectedItem();
    }

    private void updateTurbulenceModels(Model model, SolverType solvertype, Method method, Flow flow) {
        TurbulenceModel selectedItem = modelsCombo.getItemAt(modelsCombo.getSelectedIndex());

        List<TurbulenceModel> models = model.getTurbulenceModels().getModelsForState(solvertype, method, flow);
        modelsCombo.removeAllItems();
        TurbulenceModel laminar = null;
        for (TurbulenceModel turbModel : models) {
            modelsCombo.addItem(turbModel);
            if (turbModel.getType().isLaminar()) {
                laminar = turbModel;
            }
        }

        if (((DefaultComboBoxModel<TurbulenceModel>) modelsCombo.getModel()).getIndexOf(selectedItem) < 0) {
            modelsCombo.setSelectedItem(laminar);
        } else {
            modelsCombo.setSelectedItem(selectedItem);
        }
    }

    public void updateFromState(Model model, State state) {
        updateTurbulenceModels(model, state.getSolverType(), state.getMethod(), state.getFlow());

        if (state.getTurbulenceModel() != null) {
            modelsCombo.setSelectedItem(state.getTurbulenceModel());
        } else {
            modelsCombo.setSelectedIndex(-1);
        }
    }

    public void fixSolutionState(Model model, SolutionState ss) {
        SolverType solvertype = ss.isCoupled() ? SolverType.COUPLED : ss.isSegregated() ? SolverType.SEGREGATED : SolverType.NONE;
        Method method = ss.isLES() ? Method.LES : ss.isRANS() ? Method.RANS : Method.NONE;
        Flow flow = ss.isCompressible() ? Flow.COMPRESSIBLE : ss.isIncompressible() ? Flow.INCOMPRESSIBLE : Flow.NONE;
        updateTurbulenceModels(model, solvertype, method, flow);
    }

}

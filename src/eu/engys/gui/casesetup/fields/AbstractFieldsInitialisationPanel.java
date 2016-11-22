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
package eu.engys.gui.casesetup.fields;

import static eu.engys.core.controller.AbstractController.INITIALISE_SCRIPT;
import static eu.engys.core.project.zero.fields.Fields.ALPHA_1;
import static eu.engys.core.project.zero.fields.Fields.AOA;
import static eu.engys.core.project.zero.fields.Fields.CO2;
import static eu.engys.core.project.zero.fields.Fields.EPSILON;
import static eu.engys.core.project.zero.fields.Fields.K;
import static eu.engys.core.project.zero.fields.Fields.MU_SGS;
import static eu.engys.core.project.zero.fields.Fields.NU_TILDA;
import static eu.engys.core.project.zero.fields.Fields.OMEGA;
import static eu.engys.core.project.zero.fields.Fields.P;
import static eu.engys.core.project.zero.fields.Fields.P_RGH;
import static eu.engys.core.project.zero.fields.Fields.SMOKE;
import static eu.engys.core.project.zero.fields.Fields.T;
import static eu.engys.core.project.zero.fields.Fields.U;
import static eu.engys.core.project.zero.fields.Fields.W;
import static eu.engys.core.project.zero.fields.Initialisations.CELL_SET_KEY;
import static eu.engys.core.project.zero.fields.Initialisations.DEFAULT_KEY;
import static eu.engys.core.project.zero.fields.Initialisations.FIXED_VALUE_KEY;
import static eu.engys.core.project.zero.fields.Initialisations.POTENTIAL_FLOW_KEY;
import static eu.engys.util.Symbols.EPSILON_SYMBOL;
import static eu.engys.util.Symbols.K_SYMBOL;
import static eu.engys.util.Symbols.M2_S;
import static eu.engys.util.Symbols.M2_S2;
import static eu.engys.util.Symbols.MU_MEASURE;
import static eu.engys.util.Symbols.M_S;
import static eu.engys.util.Symbols.OMEGA_SYMBOL_S;
import static eu.engys.util.Symbols.PASCAL;

import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import eu.engys.core.modules.ApplicationModule;
import eu.engys.core.modules.ModulesUtil;
import eu.engys.core.presentation.ActionManager;
import eu.engys.core.project.Model;
import eu.engys.core.project.geometry.surface.Box;
import eu.engys.core.project.zero.fields.CellSetInitialisation;
import eu.engys.core.project.zero.fields.CellSetInitialisation.ScalarSurface;
import eu.engys.core.project.zero.fields.CellSetInitialisation.VectorSurface;
import eu.engys.core.project.zero.fields.DefaultInitialisation;
import eu.engys.core.project.zero.fields.Field;
import eu.engys.core.project.zero.fields.Fields;
import eu.engys.core.project.zero.fields.FixedScalarInitialisation;
import eu.engys.core.project.zero.fields.FixedVectorInitialisation;
import eu.engys.core.project.zero.fields.Initialisation;
import eu.engys.core.project.zero.fields.ScalarCellSetInitialisation;
import eu.engys.core.project.zero.fields.VectorCellSetInitialisation;
import eu.engys.core.project.zero.patches.BoundaryConditions;
import eu.engys.gui.DefaultGUIPanel;
import eu.engys.util.bean.BeanModel;
import eu.engys.util.bean.BeanPanelBuilder;
import eu.engys.util.progress.ProgressMonitor;
import eu.engys.util.ui.ExecUtil;
import eu.engys.util.ui.ResourcesUtil;
import eu.engys.util.ui.builder.JComboBoxController;
import eu.engys.util.ui.builder.PanelBuilder;

public abstract class AbstractFieldsInitialisationPanel extends DefaultGUIPanel {
    
    protected static final Icon EDIT_ICON = ResourcesUtil.getIcon("edit.icon");

    public static final String TYPE_LABEL = "Type";

    public static final String FIELDS_INITIALISATION = "Fields Initialisation";
    public static final String FIELDS_INITIALISE_BUTTON = "fields.initialise.button";

    public static final String DEFAULT_LABEL = "Default";
    public static final String FIXED_VALUE_LABEL = "Fixed Value";
    public static final String POTENTIAL_FLOW_LABEL = "Potential Flow";
    public static final String CELL_SET_LABEL = "CellSet";
    public static final String INITIALISE_BOUNDARIES_LABEL = "Initialise Boundaries";
    public static final String EDIT_LABEL = "Edit";

    private Map<String, String> unityMeasures = new HashMap<>();
    protected Map<Field, BeanPanelBuilder> fieldBuilderMap = new HashMap<>();
    protected Map<String, Builder> builders = new HashMap<>();

    private PanelBuilder mainBuilder;
    private InitialisationComboGroup group;

    private Set<ApplicationModule> modules;
    protected ProgressMonitor monitor;


    public AbstractFieldsInitialisationPanel(Model model, Set<ApplicationModule> modules, ProgressMonitor monitor) {
        super(FIELDS_INITIALISATION, model);
        this.monitor = monitor;
        this.modules = modules;
    }

    @Override
    protected JComponent layoutComponents() {
        mainBuilder = new PanelBuilder();

        JScrollPane mainScrollPane = new JScrollPane(mainBuilder.removeMargins().getPanel());
        mainScrollPane.setBorder(BorderFactory.createEmptyBorder());

        builders.put(DEFAULT_KEY, defaultBuilder);
        builders.put(FIXED_VALUE_KEY, fixedValueBuilder);
        builders.put(POTENTIAL_FLOW_KEY, createPotentialFlowBuilder());
        builders.put(CELL_SET_KEY, cellSetBuilder);
        
        group = new InitialisationComboGroup();

        return mainScrollPane;
    }


    public interface Builder {
        void build(BeanPanelBuilder builder, Field field);
    }

    private final Builder defaultBuilder = new Builder() {
        @Override
        public void build(BeanPanelBuilder builder, Field field) {
            BeanModel<DefaultInitialisation> defaultModel = new BeanModel<>(new DefaultInitialisation());
            builder.startBean(DEFAULT_LABEL, defaultModel);
            builder.endBean();
        }
    };

    private final Builder fixedValueBuilder = new Builder() {

        private BeanModel<FixedScalarInitialisation> fixedScalarModel;
        private BeanModel<FixedVectorInitialisation> fixedVectorModel;

        @Override
        public void build(BeanPanelBuilder builder, Field field) {
            if (fixedScalarModel != null) {
                fixedScalarModel.release();
            }
            if (fixedVectorModel != null) {
                fixedVectorModel.release();
            }
            if (Fields.getFieldTypeByName(field.getName()).isScalar()) {
                fixedScalarModel = new BeanModel<>(new FixedScalarInitialisation());
                builder.startBean(FIXED_VALUE_LABEL, fixedScalarModel);
                builder.addComponent("Value", fixedScalarModel.bindDouble(FixedScalarInitialisation.VALUE_KEY));
                builder.endBean();
            } else {
                fixedVectorModel = new BeanModel<>(new FixedVectorInitialisation());
                builder.startBean(FIXED_VALUE_LABEL, fixedVectorModel);
                builder.addComponent("Value", fixedVectorModel.bindPoint(FixedVectorInitialisation.VALUE_KEY));
                builder.endBean();
            }
        }
    };
    
    protected abstract Builder createPotentialFlowBuilder();

    @SuppressWarnings("deprecation")
    private final Builder cellSetBuilder = new Builder() {

        private BeanModel<ScalarCellSetInitialisation> cellSetScalarModel;
        private BeanModel<VectorCellSetInitialisation> cellSetVectorModel;

        @Override
        public void build(BeanPanelBuilder builder, final Field field) {
            if (cellSetScalarModel != null) {
                cellSetScalarModel.release();
            }
            if (cellSetVectorModel != null) {
                cellSetVectorModel.release();
            }
            if (Fields.getFieldTypeByName(field.getName()).isVector()) {
                VectorCellSetInitialisation initialisation = new VectorCellSetInitialisation(new double[3], Arrays.asList(new VectorSurface(new Box("box1"), new double[3])));
                cellSetVectorModel = new BeanModel<>(initialisation);
                builder.startBean(CELL_SET_LABEL, cellSetVectorModel);
                builder.addComponent("Default Value", cellSetVectorModel.bindPoint(CellSetInitialisation.DEFAULT_VALUE_KEY));
                JButton editButton = getEditVectorButton(field, cellSetVectorModel);
                builder.addRight(editButton);
                builder.endBean();
            } else {
                ScalarCellSetInitialisation initialisation = new ScalarCellSetInitialisation(0, Arrays.asList(new ScalarSurface(new Box("box1"), 0)));
                cellSetScalarModel = new BeanModel<>(initialisation);
                builder.startBean(CELL_SET_LABEL, cellSetScalarModel);
                builder.addComponent("Default Value", cellSetScalarModel.bindDouble(CellSetInitialisation.DEFAULT_VALUE_KEY));
                JButton editButton = getEditScalarButton(field, cellSetScalarModel);
                builder.addRight(editButton);
                builder.endBean();
            }
        }

        private JButton getEditVectorButton(final Field field, final BeanModel<VectorCellSetInitialisation> beanModel) {
            final InitialisationVectorCellSetDialog cellSetDialog = new InitialisationVectorCellSetDialog(model, field.getName(), monitor);
            final Action action = new AbstractAction(EDIT_LABEL, EDIT_ICON) {
                @Override
                public void actionPerformed(ActionEvent e) {
                    cellSetDialog.setInitialisation(beanModel.getBean());
                    cellSetDialog.showDialog(new WindowAdapter() {
                        @Override
                        public void windowOpened(WindowEvent e) {
                            setEnabled(false);
                        }

                        @Override
                        public void windowClosed(WindowEvent e) {
                            setEnabled(true);
                        }
                    });
                }
            };

            return new EnableDisableActionButton(action, cellSetDialog);
        }

        private JButton getEditScalarButton(final Field field, final BeanModel<ScalarCellSetInitialisation> beanModel) {
            final InitialisationScalarCellSetDialog cellSetDialog = new InitialisationScalarCellSetDialog(model, field.getName(), monitor);
            final Action action = new AbstractAction(EDIT_LABEL, EDIT_ICON) {
                @Override
                public void actionPerformed(ActionEvent e) {
                    cellSetDialog.setInitialisation(beanModel.getBean());
                    cellSetDialog.showDialog(new WindowAdapter() {
                        @Override
                        public void windowOpened(WindowEvent e) {
                            setEnabled(false);
                        }

                        @Override
                        public void windowClosed(WindowEvent e) {
                            setEnabled(true);
                        }
                    });
                }
            };

            return new EnableDisableActionButton(action, cellSetDialog);
        }
    };

    private class EnableDisableActionButton extends JButton {
        public EnableDisableActionButton(final Action action, final CellSetDialog cellSetDialog) {
            super(action);
            setName(EDIT_LABEL);
        }
    }

    @Override
    public void load() {
        rebuildPanel();
    }

    @Override
    public void save() {
        for (Field f : fieldBuilderMap.keySet()) {
            BeanPanelBuilder b = fieldBuilderMap.get(f);
            Initialisation init = (Initialisation) b.getSelectedModel().getBean();
            // System.out.println("AbstractFieldsInitialisationPanel.save() " + f.getName() + " -> " + init);
            f.setInitialisation(init);
        }
    }

    @Override
    public void stateChanged() {
        rebuildLater();
    }

    @Override
    public void materialsChanged() {
        rebuildLater();
    }

    @Override
    public void fieldsChanged() {
        rebuildLater();
    }

    private void rebuildLater() {
        ExecUtil.invokeLater(new Runnable() {
            @Override
            public void run() {
                rebuildPanel();
            }
        });
    }

    protected void rebuildPanel() {
        group.clear();
        
        fieldBuilderMap.clear();
        mainBuilder.clear();

        JButton initialiseButton = new JButton(ActionManager.getInstance().get(INITIALISE_SCRIPT));
        initialiseButton.setName(FIELDS_INITIALISE_BUTTON);
        mainBuilder.addRight(initialiseButton);

        Fields fields = model.getFields();
        unityMeasures(fields, model.getState().isCompressible());

        for (Field field : fields.orderedFields()) {
            buildFieldPanel(field);
        }
        initialiseButton.setEnabled(fields.size() > 0);

        revalidate();
        repaint();
    }

    protected void buildFieldPanel(Field field) {
        if (ModulesUtil.isFieldInitialisationVetoed(field, modules)) {
            return;
        }
        String name = field.getName();
        String labelText = unityMeasure(name);
        BeanPanelBuilder builder = new BeanPanelBuilder();
        builder.addSeparator(labelText);
        builder.indent();
        builder.prefix(name + ".");

        // builder.startChoice(TYPE_LABEL, fieldyModel.bindComboController(TYPE_KEY, noneModifierModel, levelSetModifierModel), TYPE_TOOLTIP);

        JComboBoxController combo = (JComboBoxController) builder.startChoice(TYPE_LABEL);
        String[] initialisationMethods = field.getInitialisationMethods();

        for (String method : initialisationMethods) {
            if (builders.containsKey(method)) {
                builders.get(method).build(builder, field);
            }
        }

        builder.endChoice();
        builder.addSeparator("");
        builder.outdent();
        builder.prefix("");

        // System.out.println("AbstractFieldsInitialisationPanel.buildFieldPanel() " + field.getInitialisation());
        builder.selectBean(field.getInitialisation());

        JPanel builderPanel = builder.getPanel();
        builderPanel.setName(name);
        builderPanel.setBorder(BorderFactory.createTitledBorder(""));

        mainBuilder.addComponent(builderPanel);

        fieldBuilderMap.put(field, builder);
        
        if (BoundaryConditions.isMomentum(name) || BoundaryConditions.isTurbulence(name)) {
            group.add(combo);
        }
    }

    private String unityMeasure(String name) {
        return name + (unityMeasures.containsKey(name) ? " " + unityMeasures.get(name) : "");
    }

    private void unityMeasures(Fields fields, boolean compressible) {
        unityMeasures = new HashMap<String, String>();
        if (fields.containsKey(U))
            unityMeasures.put(U, M_S);
        if (fields.containsKey(P))
            unityMeasures.put(P, compressible ? PASCAL : M2_S2);
        if (fields.containsKey(P_RGH))
            unityMeasures.put(P_RGH, compressible ? PASCAL : M2_S2);
        if (fields.containsKey(K))
            unityMeasures.put(K, K_SYMBOL);
        if (fields.containsKey(OMEGA))
            unityMeasures.put(OMEGA, OMEGA_SYMBOL_S);
        if (fields.containsKey(EPSILON))
            unityMeasures.put(EPSILON, EPSILON_SYMBOL);
        if (fields.containsKey(NU_TILDA))
            unityMeasures.put(NU_TILDA, M2_S);

        // if (fields.containsKey("nut")) unityMeasures.put(get("nut"));
        // if (fields.containsKey("mut")) unityMeasures.put(get("mut"));
        // if (fields.containsKey("nuSgs")) unityMeasures.put(get("nuSgs"));
        if (fields.containsKey(MU_SGS))
            unityMeasures.put(MU_SGS, MU_MEASURE);
        // if (fields.containsKey("D")) unityMeasures.put(get("D"));

        if (fields.containsKey(T))
            unityMeasures.put(T, "[K]");
        if (fields.containsKey(W))
            unityMeasures.put(W, "");
        if (fields.containsKey(ALPHA_1))
            unityMeasures.put(ALPHA_1, "[phase 1]");
        if (fields.containsKey(AOA))
            unityMeasures.put(AOA, "");
        if (fields.containsKey(CO2))
            unityMeasures.put(CO2, "");
        if (fields.containsKey(SMOKE))
            unityMeasures.put(SMOKE, "");
        // if (fields.containsKey("rho")) unityMeasures.put(get("rho"));
        // if (fields.containsKey("alphat")) unityMeasures.put(get("alphat"));
        if (fields.containsKey("Intensity"))
            unityMeasures.put("Intensity", "");
    }

}

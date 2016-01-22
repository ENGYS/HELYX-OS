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

package eu.engys.gui.casesetup.fields;

import static eu.engys.core.dictionary.Dictionary.TYPE;
import static eu.engys.core.dictionary.Dictionary.VALUE;
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
import static eu.engys.util.Symbols.EPSILON_SYMBOL;
import static eu.engys.util.Symbols.K_SYMBOL;
import static eu.engys.util.Symbols.M2_S;
import static eu.engys.util.Symbols.M2_S2;
import static eu.engys.util.Symbols.MU_MEASURE;
import static eu.engys.util.Symbols.M_S;
import static eu.engys.util.Symbols.OMEGA_SYMBOL;
import static eu.engys.util.Symbols.PASCAL;

import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.dictionary.DictionaryBuilder;
import eu.engys.core.dictionary.model.DictionaryModel;
import eu.engys.core.dictionary.model.DictionaryPanelBuilder;
import eu.engys.core.presentation.ActionManager;
import eu.engys.core.project.Model;
import eu.engys.core.project.zero.fields.Field;
import eu.engys.core.project.zero.fields.Fields;
import eu.engys.gui.DefaultGUIPanel;
import eu.engys.util.ui.ExecUtil;
import eu.engys.util.ui.ResourcesUtil;
import eu.engys.util.ui.builder.JComboBoxController;
import eu.engys.util.ui.builder.PanelBuilder;

public abstract class AbstractFieldsInitialisationPanel extends DefaultGUIPanel {

    public static final String FIELDS_INITIALISATION = "Fields Initialisation";

    public static final String DEFAULT_LABEL = "Default";
    public static final String FIXED_VALUE_LABEL = "Fixed Value";
    public static final String CELL_SET_LABEL = "CellSet";
    public static final String EDIT_LABEL = "Edit";

    private Map<String, String> unityMeasures = new HashMap<>();
    protected Map<Field, DictionaryPanelBuilder> fieldBuilderMap = new HashMap<>();
    protected Map<String, Builder> builders = new HashMap<>();

    private PanelBuilder mainBuilder;

    public AbstractFieldsInitialisationPanel(Model model) {
        super(FIELDS_INITIALISATION, model);
    }

    @Override
    protected JComponent layoutComponents() {
        mainBuilder = new PanelBuilder();
        
        JScrollPane mainScrollPane = new JScrollPane(mainBuilder.removeMargins().getPanel());
        mainScrollPane.setBorder(BorderFactory.createEmptyBorder());

        builders.put(DEFAULT_KEY, defaultBuilder);
        builders.put(FIXED_VALUE_KEY, fixedValueBuilder);
        builders.put(CELL_SET_KEY, cellSetBuilder);

        return mainScrollPane;
    }

    public interface Builder {
        void build(DictionaryPanelBuilder builder, Field field);
    }

    private final Builder defaultBuilder = new Builder() {
        @Override
        public void build(DictionaryPanelBuilder builder, Field field) {
            DictionaryModel dictModel = new DictionaryModel(DictionaryBuilder.newDictionary("initialisation").field(TYPE, DEFAULT_KEY).done()) {
                public String getKey() {
                    return DEFAULT_KEY;
                }
            };
            builder.startDictionary(DEFAULT_LABEL, dictModel);
            builder.endGroup();
        }
    };

    private final Builder fixedValueBuilder = new Builder() {
        @Override
        public void build(DictionaryPanelBuilder builder, Field field) {
            if (field.getFieldType().isScalar()) {
                DictionaryModel dictScalarModel = new DictionaryModel(DictionaryBuilder.newDictionary("initialisation").field(TYPE, FIXED_VALUE_KEY).field(VALUE, "uniform 0").done()) {
                    public String getKey() {
                        return FIXED_VALUE_KEY;
                    }
                };
                builder.startDictionary(FIXED_VALUE_LABEL, dictScalarModel);
                builder.addComponent("Value", dictScalarModel.bindUniformDouble(VALUE));
                builder.endDictionary();
            } else {
                DictionaryModel dictVectorModel = new DictionaryModel(DictionaryBuilder.newDictionary("initialisation").field(TYPE, FIXED_VALUE_KEY).field(VALUE, "uniform (0 0 0)").done()) {
                    public String getKey() {
                        return FIXED_VALUE_KEY;
                    }
                };
                builder.startDictionary(FIXED_VALUE_LABEL, dictVectorModel);
                builder.addComponent("Value", dictVectorModel.bindUniformPoint(VALUE));
                builder.endDictionary();
            }
        }
    };

    private final Builder cellSetBuilder = new Builder() {
        @Override
        public void build(DictionaryPanelBuilder builder, final Field field) {
            final DictionaryModel dictModel = new DictionaryModel(getCellSetDefaultDict()) {
                public String getKey() {
                    return CELL_SET_KEY;
                }
            };

            builder.startDictionary(CELL_SET_LABEL, dictModel);
            builder.addComponent("Default Value", dictModel.bindUniformDouble("defaultValue"));

            JButton editButton = getEditButton(field, dictModel);
            builder.addRight(editButton);
            builder.endDictionary();
        }

        private JButton getEditButton(final Field field, final DictionaryModel dictModel) {
            final CellSetDialog cellSetDialog = new CellSetDialog(model, dictModel, field.getName(), "setSources", AbstractFieldsInitialisationPanel.this);
            final Action action = new AbstractAction(EDIT_LABEL, EDIT_ICON) {
                @Override
                public void actionPerformed(ActionEvent e) {
                    cellSetDialog.showDialog();
                }
            };

            cellSetDialog.getDialog().addComponentListener(new ComponentAdapter() {

                @Override
                public void componentShown(ComponentEvent e) {
                    super.componentShown(e);
                    action.setEnabled(false);
                }

                @Override
                public void componentHidden(ComponentEvent e) {
                    super.componentHidden(e);
                    action.setEnabled(true);
                }
            });

            JButton editButton = new JButton(action);
            editButton.setName(EDIT_LABEL);
            return editButton;
        }

        private Dictionary getCellSetDefaultDict() {
            DictionaryBuilder boxBuilder = DictionaryBuilder.newDictionary("boxToCell");
            boxBuilder.field("box", "(0 0 0) (2.0 2.0 1.0 )");
            boxBuilder.field("value", "0.0");

            DictionaryBuilder builder = DictionaryBuilder.newDictionary("initialisation");
            builder.field(TYPE, CELL_SET_KEY);
            builder.field("defaultValue", "uniform 0.0");
            builder.list("setSources", boxBuilder.done());

            return builder.done();
        }

    };

    @Override
    public void load() {
        rebuildPanel();
    }

    @Override
    public void save() {
        for (Field f : fieldBuilderMap.keySet()) {
            DictionaryPanelBuilder b = fieldBuilderMap.get(f);
            Dictionary dictionary = b.getSelectedModel().getDictionary();
            f.setInitialisation(dictionary);
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
        fieldBuilderMap.clear();
        mainBuilder.clear();

        JButton initialiseButton = new JButton(ActionManager.getInstance().get("initialise.fields"));
        initialiseButton.setName("fields.initialise.button");
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

    protected JComboBoxController buildFieldPanel(Field field) {
        String name = field.getName();
        String labelText = unityMeasure(name);
        DictionaryPanelBuilder builder = new DictionaryPanelBuilder();
        builder.addSeparator(labelText);
        builder.indent();
        builder.prefix(name + ".");
        JComboBoxController combo = (JComboBoxController) builder.startChoice("Type");
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

        builder.selectDictionary(field.getInitialisation());

        JPanel builderPanel = builder.getPanel();
        builderPanel.setName(name);
        builderPanel.setBorder(BorderFactory.createTitledBorder(""));

        mainBuilder.addComponent(builderPanel);

        fieldBuilderMap.put(field, builder);

        return combo;
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
            unityMeasures.put(OMEGA, OMEGA_SYMBOL);
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

    /**
     * Resources
     */

    protected static final Icon EDIT_ICON = ResourcesUtil.getIcon("script.edit.icon");

}

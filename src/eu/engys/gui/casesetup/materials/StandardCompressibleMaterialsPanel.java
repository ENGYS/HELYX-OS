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


package eu.engys.gui.casesetup.materials;

import static eu.engys.core.project.constant.ThermophysicalProperties.AS_KEY;
import static eu.engys.core.project.constant.ThermophysicalProperties.A_KEYS;
import static eu.engys.core.project.constant.ThermophysicalProperties.CONSTANT_CP_KEY;
import static eu.engys.core.project.constant.ThermophysicalProperties.CONST_KEY;
import static eu.engys.core.project.constant.ThermophysicalProperties.CP_KEY;
import static eu.engys.core.project.constant.ThermophysicalProperties.EQUATION_OF_STATE_KEY;
import static eu.engys.core.project.constant.ThermophysicalProperties.HF_KEY;
import static eu.engys.core.project.constant.ThermophysicalProperties.HIGH_CP_COEFFS_KEY;
import static eu.engys.core.project.constant.ThermophysicalProperties.JANAF_KEY;
import static eu.engys.core.project.constant.ThermophysicalProperties.LOW_CP_COEFFS_KEY;
import static eu.engys.core.project.constant.ThermophysicalProperties.MATERIAL_NAME_KEY;
import static eu.engys.core.project.constant.ThermophysicalProperties.MOL_WEIGHT_KEY;
import static eu.engys.core.project.constant.ThermophysicalProperties.MU_KEY;
import static eu.engys.core.project.constant.ThermophysicalProperties.N_MOLES_KEY;
import static eu.engys.core.project.constant.ThermophysicalProperties.PERFECT_GAS_KEY;
import static eu.engys.core.project.constant.ThermophysicalProperties.PR_KEY;
import static eu.engys.core.project.constant.ThermophysicalProperties.SUTHERLAND_KEY;
import static eu.engys.core.project.constant.ThermophysicalProperties.TCOMMON_KEY;
import static eu.engys.core.project.constant.ThermophysicalProperties.THERMO_KEY;
import static eu.engys.core.project.constant.ThermophysicalProperties.THIGH_KEY;
import static eu.engys.core.project.constant.ThermophysicalProperties.TLOW_KEY;
import static eu.engys.core.project.constant.ThermophysicalProperties.TRANSPORT_KEY;
import static eu.engys.core.project.constant.ThermophysicalProperties.TS_KEY;
import static eu.engys.util.ui.ComponentsFactory.labelArrayField;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.inject.Inject;
import javax.swing.JPanel;

import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.dictionary.model.DictionaryModel;
import eu.engys.core.modules.materials.MaterialsBuilder;
import eu.engys.core.project.Model;
import eu.engys.core.project.state.State;
import eu.engys.util.ui.builder.JComboBoxController;
import eu.engys.util.ui.builder.PanelBuilder;
import eu.engys.util.ui.textfields.StringField;

public class StandardCompressibleMaterialsPanel implements CompressibleMaterialsPanel {

    private DictionaryModel compressibleModel = new DictionaryModel(new Dictionary(""));

    private JComboBoxController transport;
    private JComboBoxController thermo;
    private MaterialsBuilder materialsBuilder;

    private PanelBuilder builder;
    private StringField nameField;

    @Inject
    public StandardCompressibleMaterialsPanel() {
        this.builder = new PanelBuilder();
        this.materialsBuilder = new StandardMaterialsBuilder();
        buildCompressibleMaterialPanel();
    }

    @Override
    public Dictionary getEmptyMaterial() {
        return new Dictionary(defaultDictionary);
    }

    @Override
    public StringField getNameField() {
        return nameField;
    }

    @Override
    public JPanel getPanel() {
        return builder.getPanel();
    }

    @Override
    public void setEnabled(boolean enabled) {
        builder.setEnabled(enabled);
    }

    @Override
    public void stateChanged(State state) {
    }

    private void buildCompressibleMaterialPanel() {
        buildThermophysicalModelPanel();
        buildTransportPropertiesPanel();
        buildThermodynamicModelPanel();
        buildEquationOfStatePanel();

        /* JANAF works only for SUTHERLAND */
        transport.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (transport.getSelectedIndex() == 0) {
                    thermo.addDisabledItem(JANAF_LABEL);
                    thermo.setSelectedItem(CONSTANT_CP_LABEL);
                } else {
                    thermo.clearDisabledIndexes();
                }
            }
        });
        transport.setSelectedItem(CONSTANT_LABEL);
    }

    @Override
    public void buildThermophysicalModelPanel() {
        builder.addComponent(NAME_LABEL, nameField = compressibleModel.bindLabel(MATERIAL_NAME_KEY));
        builder.addComponent(NUMBER_OF_MOLES_LABEL, compressibleModel.bindIntegerPositive(N_MOLES_KEY));
        builder.addComponent(MOLECULAR_WEIGHT_KG_KMOL_LABEL, compressibleModel.bindDoublePositive(MOL_WEIGHT_KEY));
        nameField.setEnabled(false);
    }

    @Override
    public void buildTransportPropertiesPanel() {
        transport = (JComboBoxController) builder.startChoice(TRANSPORT_PROPERTIES_LABEL, compressibleModel.bindComboBoxController(TRANSPORT_KEY));

        builder.startGroup(CONST_KEY, CONSTANT_LABEL);
        builder.addComponent(DYNAMIC_VISCOSITY_LABEL, compressibleModel.bindDoublePositive(MU_KEY));
        builder.addComponent(PRANDTL_NUMBER_LABEL, compressibleModel.bindDoublePositive(PR_KEY));
        builder.endGroup();

        builder.startGroup(SUTHERLAND_KEY, SUTHERLAND_S_LABEL);
        builder.addComponent(SUTHERLAND_COEFFICIENT_AS_LABEL, compressibleModel.bindDoublePositive(AS_KEY));
        builder.addComponent(SUTHERLAND_TEMPERATURE_TS_LABEL, compressibleModel.bindDoublePositive(TS_KEY));
        builder.endGroup();

        builder.endChoice();
    }

    @Override
    public void buildThermodynamicModelPanel() {
        thermo = (JComboBoxController) builder.startChoice(THERMODYNAMIC_MODEL_LABEL, compressibleModel.bindComboBoxController(THERMO_KEY));

        builder.startGroup(CONSTANT_CP_KEY, CONSTANT_CP_LABEL);
        builder.addComponent(HEAT_CAPACITY_LABEL, compressibleModel.bindDoublePositive(CP_KEY));
        builder.addComponent(HEAT_OF_FUSION_LABEL, compressibleModel.bindDoublePositive(HF_KEY));
        builder.endGroup();

        builder.startGroup(JANAF_KEY, JANAF_LABEL);
        builder.addComponent(TLOW_KEY, compressibleModel.bindDoublePositive(TLOW_KEY));
        builder.addComponent(THIGH_KEY, compressibleModel.bindDoublePositive(THIGH_KEY));
        builder.addComponent(TCOMMON_KEY, compressibleModel.bindDoublePositive(TCOMMON_KEY));
        builder.addComponent(labelArrayField(A_KEYS));
        builder.addComponent(HIGH_CP_LABEL, compressibleModel.bindArray(HIGH_CP_COEFFS_KEY, 7));
        builder.addComponent(labelArrayField(A_KEYS));
        builder.addComponent(LOW_CP_LABEL, compressibleModel.bindArray(LOW_CP_COEFFS_KEY, 7));
        builder.endGroup();

        builder.endChoice();
    }

    @Override
    public void buildEquationOfStatePanel() {
        String[] EOS_KEYS = { PERFECT_GAS_KEY };// , "ICOPolynomial" };
        String[] EOS_LABELS = { PERFECT_GAS_LABEL };// , "Polymomial f(T)" };
        builder.addComponent(EQUATION_OF_STATE_LABEL, compressibleModel.bindChoice(EQUATION_OF_STATE_KEY, EOS_KEYS, EOS_LABELS)).setEnabled(false);
    }

    @Override
    public Dictionary getMaterial(Model model) {
        return materialsBuilder.saveCompressible(model, compressibleModel.getDictionary());
    }

    @Override
    public void setMaterial(Dictionary material) {
        compressibleModel.setDictionary(materialsBuilder.toGUIFormat(material));
    }

}

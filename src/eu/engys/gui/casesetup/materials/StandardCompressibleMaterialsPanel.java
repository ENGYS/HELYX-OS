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

package eu.engys.gui.casesetup.materials;

import static eu.engys.core.project.constant.ThermophysicalProperties.A_KEYS;
import static eu.engys.core.project.constant.ThermophysicalProperties.PERFECT_GAS_KEY;
import static eu.engys.core.project.constant.ThermophysicalProperties.TCOMMON_KEY;
import static eu.engys.core.project.constant.ThermophysicalProperties.THIGH_KEY;
import static eu.engys.core.project.constant.ThermophysicalProperties.TLOW_KEY;
import static eu.engys.util.ui.ComponentsFactory.labelArrayField;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.inject.Inject;
import javax.swing.JPanel;

import eu.engys.core.project.Model;
import eu.engys.core.project.materials.compressible.CompressibleMaterial;
import eu.engys.core.project.materials.compressible.ConstantThermodynamicModel;
import eu.engys.core.project.materials.compressible.ConstantTransport;
import eu.engys.core.project.materials.compressible.JANAFThermodynamicModel;
import eu.engys.core.project.materials.compressible.SutherlandTransport;
import eu.engys.core.project.materials.compressible.ThermodynamicModel;
import eu.engys.core.project.materials.compressible.Transport;
import eu.engys.core.project.state.State;
import eu.engys.util.bean.BeanModel;
import eu.engys.util.bean.BeanPanelBuilder;
import eu.engys.util.ui.JComboBoxWithItemsSupport;
import eu.engys.util.ui.builder.PanelBuilder;
import eu.engys.util.ui.textfields.DoubleField;
import eu.engys.util.ui.textfields.StringField;

public class StandardCompressibleMaterialsPanel implements CompressibleMaterialsPanel {

//    private DictionaryModel compressibleModel = new DictionaryModel(new Dictionary(""));
    private BeanModel<CompressibleMaterial> materialModel = new BeanModel<>(new CompressibleMaterial());

    private JComboBoxWithItemsSupport<BeanModel<? extends Transport>> transport;
    private JComboBoxWithItemsSupport<BeanModel<? extends ThermodynamicModel>> thermo;

    private PanelBuilder builder;
    private StringField nameField;

    private BeanModel<ConstantThermodynamicModel> constantThermoModel;
    private BeanModel<JANAFThermodynamicModel> janafThermoModel;
    private BeanModel<ConstantTransport> constantTransportModel;
    private BeanModel<SutherlandTransport> sutherlandTransportModel;

    @Inject
    public StandardCompressibleMaterialsPanel() {
        this.builder = new PanelBuilder();
        buildCompressibleMaterialPanel();
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

    /*
     * Panels
     */

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
                    thermo.addDisabledItem(janafThermoModel);
                    thermo.setSelectedItem(constantThermoModel);
                } else {
                    thermo.clearDisabledIndexes();
                }
            }
        });
        transport.setSelectedItem(constantTransportModel);
    }

    @Override
    public void buildThermophysicalModelPanel() {
        PanelBuilder pb = new PanelBuilder();
        pb.addComponent(NAME_LABEL, nameField = materialModel.bindLabel(CompressibleMaterial.NAME_KEY));
        pb.addComponent(NUMBER_OF_MOLES_LABEL, materialModel.bindIntegerPositive(CompressibleMaterial.N_MOLES));
        pb.addComponent(MOLECULAR_WEIGHT_KG_KMOL_LABEL, materialModel.bindDoublePositive(CompressibleMaterial.MOL_WEIGHT));
        nameField.setEnabled(false);
        
        JPanel panel = pb.getPanel();
        builder.addComponent(panel);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void buildTransportPropertiesPanel() {
        BeanPanelBuilder pb = new BeanPanelBuilder();
        
        constantTransportModel = new BeanModel<>(new ConstantTransport());
        sutherlandTransportModel = new BeanModel<>(new SutherlandTransport());
        
        transport = (JComboBoxWithItemsSupport<BeanModel<? extends Transport>>) pb.startChoice(TRANSPORT_PROPERTIES_LABEL, 
                materialModel.<Transport>bindComboController(CompressibleMaterial.TRANSPORT, constantTransportModel, sutherlandTransportModel));

        pb.startBean(CONSTANT_LABEL, constantTransportModel);
        pb.addComponent(DYNAMIC_VISCOSITY_LABEL, constantTransportModel.bindDoublePositive(ConstantTransport.MU_KEY));
        pb.addComponent(PRANDTL_NUMBER_LABEL, constantTransportModel.bindDoublePositive(ConstantTransport.PR_KEY));
        pb.endBean();

        pb.startBean(SUTHERLAND_S_LABEL, sutherlandTransportModel);
        pb.addComponent(SUTHERLAND_COEFFICIENT_AS_LABEL, sutherlandTransportModel.bindDoublePositive(SutherlandTransport.AS_KEY));
        pb.addComponent(SUTHERLAND_TEMPERATURE_TS_LABEL, sutherlandTransportModel.bindDoublePositive(SutherlandTransport.TS_KEY));
        pb.endBean();

        pb.endChoice();

        JPanel panel = pb.getPanel();
        builder.addComponent(panel);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void buildThermodynamicModelPanel() {
        BeanPanelBuilder pb = new BeanPanelBuilder();
        
        constantThermoModel = new BeanModel<>(new ConstantThermodynamicModel());
        janafThermoModel = new BeanModel<>(new JANAFThermodynamicModel());
        
        thermo = (JComboBoxWithItemsSupport<BeanModel<? extends ThermodynamicModel>>) pb.startChoice(THERMODYNAMIC_MODEL_LABEL, 
                materialModel.<ThermodynamicModel>bindComboController(CompressibleMaterial.THERMODYNAMIC_MODEL, constantThermoModel, janafThermoModel));
        pb.startBean(CONSTANT_CP_LABEL, constantThermoModel);
        pb.addComponent(HEAT_CAPACITY_LABEL, constantThermoModel.bindDoublePositive(ConstantThermodynamicModel.CP_KEY));
        pb.addComponent(HEAT_OF_FUSION_LABEL, constantThermoModel.bindDoublePositive(ConstantThermodynamicModel.HF_KEY));
        pb.endBean();

        pb.startBean(JANAF_LABEL, janafThermoModel);
        pb.addComponent(TLOW_KEY, janafThermoModel.bindDoublePositive(JANAFThermodynamicModel.TLOW_KEY));
        pb.addComponent(THIGH_KEY, janafThermoModel.bindDoublePositive(JANAFThermodynamicModel.THIGH_KEY));
        pb.addComponent(TCOMMON_KEY, janafThermoModel.bindDoublePositive(JANAFThermodynamicModel.TCOMMON_KEY));

        DoubleField[] lowFields = janafThermoModel.bindDoubleArray(JANAFThermodynamicModel.LOW_CP_COEFFS_KEY, 7);
        DoubleField[] highFields = janafThermoModel.bindDoubleArray(JANAFThermodynamicModel.HIGH_CP_COEFFS_KEY, 7);
        pb.addComponent(labelArrayField(LOW_CP_LABEL, HIGH_CP_LABEL));
        for (int i = 0; i < 7; i++) {
            pb.addComponent(A_KEYS[i], lowFields[i], highFields[i]);
            
        }
        pb.endBean();

        pb.endChoice();

        JPanel panel = pb.getPanel();
        builder.addComponent(panel);
        
    }

    @Override
    public void buildEquationOfStatePanel() {
        String[] EOS_KEYS = { PERFECT_GAS_KEY };// , "ICOPolynomial" };
        String[] EOS_LABELS = { PERFECT_GAS_LABEL };// , "Polymomial f(T)" };
        //builder.addComponent(EQUATION_OF_STATE_LABEL, materialModel.bindChoice(EQUATION_OF_STATE_KEY, EOS_KEYS, EOS_LABELS)).setEnabled(false);
    }

    @Override
    public CompressibleMaterial getMaterial(Model model) {
//        return materialsBuilder.saveCompressible(model, compressibleModel.getDictionary());
        return materialModel.getBean();
    }

    @Override
    public void setMaterial(CompressibleMaterial material) {
//        compressibleModel.setDictionary(materialsBuilder.toGUIFormat(material.getDictionary()));
        materialModel.setBean(material);
    }
    
    
    
    
    
    
    
    
    

}

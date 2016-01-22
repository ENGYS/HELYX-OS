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

import static eu.engys.core.project.constant.ThermophysicalProperties.CONSTANT_CP_KEY;
import static eu.engys.core.project.constant.ThermophysicalProperties.CONST_KEY;
import static eu.engys.core.project.constant.ThermophysicalProperties.CP_KEY;
import static eu.engys.core.project.constant.ThermophysicalProperties.ENERGY_KEY;
import static eu.engys.core.project.constant.ThermophysicalProperties.EQUATION_OF_STATE_KEY;
import static eu.engys.core.project.constant.ThermophysicalProperties.HE_PSI_THERMO_KEY;
import static eu.engys.core.project.constant.ThermophysicalProperties.HF_KEY;
import static eu.engys.core.project.constant.ThermophysicalProperties.MIXTURE_KEY;
import static eu.engys.core.project.constant.ThermophysicalProperties.MOL_WEIGHT_KEY;
import static eu.engys.core.project.constant.ThermophysicalProperties.MU_KEY;
import static eu.engys.core.project.constant.ThermophysicalProperties.N_MOLES_KEY;
import static eu.engys.core.project.constant.ThermophysicalProperties.PERFECT_GAS_KEY;
import static eu.engys.core.project.constant.ThermophysicalProperties.PR_KEY;
import static eu.engys.core.project.constant.ThermophysicalProperties.PURE_MIXTURE_KEY;
import static eu.engys.core.project.constant.ThermophysicalProperties.SENSIBLE_ENTHALPY_KEY;
import static eu.engys.core.project.constant.ThermophysicalProperties.SPECIE_KEY;
import static eu.engys.core.project.constant.ThermophysicalProperties.THERMODYNAMICS_KEY;
import static eu.engys.core.project.constant.ThermophysicalProperties.THERMO_KEY;
import static eu.engys.core.project.constant.ThermophysicalProperties.THERMO_TYPE_KEY;
import static eu.engys.core.project.constant.ThermophysicalProperties.TRANSPORT_KEY;
import static eu.engys.core.project.constant.TransportProperties.MATERIAL_NAME_KEY;
import static eu.engys.util.Symbols.CP;
import static eu.engys.util.Symbols.HF;
import static eu.engys.util.Symbols.MU_MEASURE;

import javax.swing.JPanel;

import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.project.Model;
import eu.engys.core.project.state.State;
import eu.engys.util.ui.textfields.StringField;

public interface CompressibleMaterialsPanel {

    static final String MOLECULAR_WEIGHT_KG_KMOL_LABEL = "Molecular Weight [kg/kmol]";
    static final String NUMBER_OF_MOLES_LABEL = "Number Of Moles";
    static final String NAME_LABEL = "Name";
    static final String EQUATION_OF_STATE_LABEL = "Equation Of State";
    static final String PERFECT_GAS_LABEL = "Perfect Gas";
    static final String SUTHERLAND_TEMPERATURE_TS_LABEL = "Sutherland Temperature (Ts)";
    static final String SUTHERLAND_COEFFICIENT_AS_LABEL = "Sutherland Coefficient (As)";
    static final String PRANDTL_NUMBER_LABEL = "Prandtl Number";
    static final String DYNAMIC_VISCOSITY_LABEL = "Dynamic Viscosity " + MU_MEASURE;
    static final String SUTHERLAND_S_LABEL = "Sutherland's";
    static final String CONSTANT_LABEL = "Constant";
    static final String TRANSPORT_PROPERTIES_LABEL = "Transport Properties";
    static final String THERMODYNAMIC_MODEL_LABEL = "Thermodynamic Model";
    static final String LOW_CP_LABEL = "Low Cp";
    static final String HIGH_CP_LABEL = "High Cp";
    static final String HEAT_OF_FUSION_LABEL = "Heat Of Fusion " + HF;
    static final String HEAT_CAPACITY_LABEL = "Heat Capacity " + CP;
    static final String JANAF_LABEL = "JANAF";
    static final String CONSTANT_CP_LABEL = "Constant Cp";
    
    public static Dictionary defaultDictionary = new Dictionary("newMaterial"){
        {
            add(MATERIAL_NAME_KEY, "newMaterial");
            add(new Dictionary(THERMO_TYPE_KEY){
                {
                    add(TYPE, HE_PSI_THERMO_KEY);
                    add(MIXTURE_KEY, PURE_MIXTURE_KEY);
                    add(TRANSPORT_KEY, CONST_KEY);
                    add(THERMO_KEY, CONSTANT_CP_KEY);
                    add(EQUATION_OF_STATE_KEY, PERFECT_GAS_KEY);
                    add(SPECIE_KEY, SPECIE_KEY);
                    add(ENERGY_KEY, SENSIBLE_ENTHALPY_KEY);
                }
            });
            add(new Dictionary(MIXTURE_KEY){
                {
                    add(new Dictionary(EQUATION_OF_STATE_KEY));
                    add(new Dictionary(TRANSPORT_KEY){
                        {
                            add(MU_KEY, "0.0");
                            add(PR_KEY, "0.0");
                        }
                    });
                    add(new Dictionary(THERMODYNAMICS_KEY){
                        {
                            add(CP_KEY, "0.0");
                            add(HF_KEY, "0.0");
                        }
                    });
                    add(new Dictionary(SPECIE_KEY){
                        {
                            add(N_MOLES_KEY, "0");
                            add(MOL_WEIGHT_KEY, "0.0");
                        }
                    });
                }
            });
        }
    };

    Dictionary getEmptyMaterial();

    Dictionary getMaterial(Model model);

    void setMaterial(Dictionary material);

    JPanel getPanel();

    void setEnabled(boolean enabled);

    void buildThermodynamicModelPanel();

    void buildTransportPropertiesPanel();

    void buildEquationOfStatePanel();

    void buildThermophysicalModelPanel();

    void stateChanged(State state);

    StringField getNameField();

}

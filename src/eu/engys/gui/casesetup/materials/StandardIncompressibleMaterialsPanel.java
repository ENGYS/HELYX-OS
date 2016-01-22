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

import static eu.engys.core.project.constant.TransportProperties.BETA_OS_KEY;
import static eu.engys.core.project.constant.TransportProperties.CP_KEY;
import static eu.engys.core.project.constant.TransportProperties.LAMBDA_KEY;
import static eu.engys.core.project.constant.TransportProperties.MATERIAL_NAME_KEY;
import static eu.engys.core.project.constant.TransportProperties.MU_KEY;
import static eu.engys.core.project.constant.TransportProperties.NEWTONIAN_COEFFS_KEY;
import static eu.engys.core.project.constant.TransportProperties.NEWTONIAN_KEY;
import static eu.engys.core.project.constant.TransportProperties.NU_KEY;
import static eu.engys.core.project.constant.TransportProperties.PRT_KEY;
import static eu.engys.core.project.constant.TransportProperties.PR_KEY;
import static eu.engys.core.project.constant.TransportProperties.P_REF_KEY;
import static eu.engys.core.project.constant.TransportProperties.RHO_KEY;
import static eu.engys.core.project.constant.TransportProperties.TRANSPORT_MODEL_KEY;
import static eu.engys.core.project.constant.TransportProperties.T_REF_KEY;

import javax.swing.JPanel;

import com.google.inject.Inject;

import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.dictionary.DimensionedScalar;
import eu.engys.core.project.Model;
import eu.engys.util.DimensionalUnits;

public class StandardIncompressibleMaterialsPanel extends AbstractIncompressibleMaterialsPanel {

    public static final String LAMINAR_PRANDTL_NUMBER_LABEL = "Laminar Prandtl Number";

    @Inject
    public StandardIncompressibleMaterialsPanel() {
        super();
        this.materialsBuilder = new StandardMaterialsBuilder();
        buildIncompressibleMaterialPanel();
    }

    @Override
    public Dictionary getMaterial(Model model) {
        return materialsBuilder.saveIncompressible(model, incompressibleModel.getDictionary());
    }

    @Override
    public void setMaterial(Dictionary material) {
        adjustNuIfNeeded(material);
        incompressibleModel.setDictionary(new Dictionary(material.getName(), material));
    }

    @Override
    protected void buildIncompressibleMaterialPanel() {
        super.buildIncompressibleMaterialPanel();
        builder.addComponent(THERMAL_EXPANTION_COEFF_LABEL, incompressibleModel.bindDimensionedDouble(BETA_OS_KEY, DimensionalUnits._K, 0D, Double.MAX_VALUE));
        builder.addComponent(LAMINAR_PRANDTL_NUMBER_LABEL, incompressibleModel.bindDimensionedDouble(PR_KEY, DimensionalUnits.NONE, 0D, Double.MAX_VALUE));
    }
    
    @Override
    public JPanel getPanel() {
        return builder.removeMargins().getPanel();
    }
    
    @Override
    public Dictionary getEmptyMaterial() {
        return new Dictionary(defaultDictionary);
    }
    
    public static Dictionary defaultDictionary = new Dictionary("newMaterial") {
        {
            add(MATERIAL_NAME_KEY, "newMaterial");
            add(TRANSPORT_MODEL_KEY, NEWTONIAN_KEY);
            add(new Dictionary(NEWTONIAN_COEFFS_KEY));
            add(new DimensionedScalar(RHO_KEY, "0.0", DimensionalUnits.KG_M3));
            add(new DimensionedScalar(RHO_KEY, "0.0", DimensionalUnits.KG_M3));
            add(new DimensionedScalar(MU_KEY, "0.0", DimensionalUnits.KG_MS));
            add(new DimensionedScalar(NU_KEY, "0.0", DimensionalUnits.M2_S));
            add(new DimensionedScalar(CP_KEY, "0.0", DimensionalUnits.M2_S2K));
            add(new DimensionedScalar(PRT_KEY, "0.0", DimensionalUnits.NONE));
            add(new DimensionedScalar(LAMBDA_KEY, "0.0", DimensionalUnits.KGM_S3K));
            add(new DimensionedScalar(P_REF_KEY, "0.0", DimensionalUnits.KG_MS2));
            add(new DimensionedScalar(T_REF_KEY, "0.0", DimensionalUnits.K));
            add(new DimensionedScalar(BETA_OS_KEY, "0.0", DimensionalUnits._K));
            add(new DimensionedScalar(PR_KEY, "0.0", DimensionalUnits.NONE));
        }
    };

}

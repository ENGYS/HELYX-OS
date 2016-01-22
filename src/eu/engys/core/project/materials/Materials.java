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


package eu.engys.core.project.materials;

import java.util.LinkedList;

import eu.engys.core.project.Model;
import eu.engys.core.project.constant.ThermophysicalProperties;
import eu.engys.core.project.constant.TransportProperties;
import eu.engys.util.progress.ProgressMonitor;

public class Materials extends LinkedList<Material> {
    
    public Materials() {
        super();
    }

    public static String materialsToString(Materials materials) {
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        for (Material material : materials) {
            sb.append(material.getName());
            sb.append(" ");
        }
        sb.append(")");
        return sb.toString();
    }

    public String getFirstMaterialName() {
        if (size() > 1) {
            return get(0).getName();
        }
        return "";
    }

    public void loadMaterials(Model model, MaterialsReader reader, ProgressMonitor monitor) {
        clear();
        TransportProperties transProp = model.getProject().getConstantFolder().getTransportProperties();
        ThermophysicalProperties thermoProp = model.getProject().getConstantFolder().getThermophysicalProperties();

        if (!model.getState().getMultiphaseModel().isMultiphase()) {
            if (model.getState().isIncompressible()) {
                reader.readSingle_Material(this, transProp, monitor);
            } else {
                reader.readSingle_Material(this, thermoProp, monitor);
            }
            model.materialsChanged();
        }
    }

    public void saveMaterials(Model model, MaterialsWriter writer) {
        TransportProperties transProp = new TransportProperties();
        ThermophysicalProperties thermoProp = new ThermophysicalProperties();

        if (!model.getState().getMultiphaseModel().isMultiphase()) {
            if (model.getState().isIncompressible()) {
                writer.writeSingle_IncompressibleMaterial(this, transProp);
            } else {
                writer.writeSingle_CompressibleMaterial(this, thermoProp);
            }
            model.getProject().getConstantFolder().setTransportProperties(transProp);
            model.getProject().getConstantFolder().setThermophysicalProperties(thermoProp);
        } else {
            // ECOMARINE
            if (model.getState().isIncompressible() && model.getMaterials().size() == 1) {
                writer.writeSingle_IncompressibleMaterial(this, transProp);
                model.getProject().getConstantFolder().setTransportProperties(transProp);
                model.getProject().getConstantFolder().setThermophysicalProperties(thermoProp);
            } else {
                /**/
            }
        }
    }
}

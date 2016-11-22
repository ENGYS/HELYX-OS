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

package eu.engys.core.project.materials;

import java.util.LinkedList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.engys.core.project.Model;
import eu.engys.core.project.constant.ThermophysicalProperties;
import eu.engys.core.project.constant.TransportProperties;
import eu.engys.util.progress.ProgressMonitor;

public class Materials extends LinkedList<Material> {

    private static final Logger logger = LoggerFactory.getLogger(Materials.class);

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
                addIfNotNull(reader.readIncompressibleMaterial(transProp));
            } else {
                addIfNotNull(reader.readCompressibleMaterial(thermoProp));
            }
            model.materialsChanged();
        }
    }

    private void addIfNotNull(Material m) {
        if (m != null) {
            add(m);
        }
    }

    public void saveMaterials(Model model, MaterialsWriter writer) {
        TransportProperties transProp = new TransportProperties();
        ThermophysicalProperties thermoProp = new ThermophysicalProperties();

        if (!model.getState().getMultiphaseModel().isMultiphase()) {
            if (size() == 1) {
                if (model.getState().isIncompressible()) {
                    transProp.merge(writer.writeSingle_IncompressibleMaterial(get(0)));
                } else {
                    thermoProp.merge(writer.writeSingle_CompressibleMaterial(get(0)));
                }
            } else {
                logger.warn("Multiphase solution choosen but '{}' materials found", size());
            }
            model.getProject().getConstantFolder().setTransportProperties(transProp);
            model.getProject().getConstantFolder().setThermophysicalProperties(thermoProp);
        } else {
            // ECOMARINE
            if (model.getState().isIncompressible() && model.getMaterials().size() == 1) {
                transProp.merge(writer.writeSingle_IncompressibleMaterial(get(0)));
                model.getProject().getConstantFolder().setTransportProperties(transProp);
                model.getProject().getConstantFolder().setThermophysicalProperties(thermoProp);
            } else {
                logger.warn("Multiphase ECOMARINE solution choosen but '{}' materials found", size());
                /**/
            }
        }
    }
}

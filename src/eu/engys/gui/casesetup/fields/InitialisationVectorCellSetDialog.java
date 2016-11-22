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

import java.util.ArrayList;
import java.util.List;

import eu.engys.core.project.Model;
import eu.engys.core.project.geometry.Surface;
import eu.engys.core.project.zero.fields.CellSetInitialisation.VectorSurface;
import eu.engys.core.project.zero.fields.Fields;
import eu.engys.core.project.zero.fields.VectorCellSetInitialisation;
import eu.engys.util.progress.ProgressMonitor;

public class InitialisationVectorCellSetDialog extends CellSetDialog {
    
    private final String fieldName;
    private VectorCellSetInitialisation initialisation;
    
    public InitialisationVectorCellSetDialog(Model model, String fieldName, ProgressMonitor monitor) {
        super(model, getTitle(fieldName), monitor);
        this.fieldName = fieldName;
    }
    
    private static String getTitle(String fieldName) {
        return fieldName.equals(Fields.ALPHA_1) ? fieldName + " [phase 1]" : fieldName;
    }
    
    @Override
    protected CellSetRow newRow(Surface surface) {
        return new InitialisationVectorCellSetRow(model, fieldName, surface, new double[]{0.0, 0.0, 0.0}, monitor);
    }
    
    private CellSetRow createRow(Surface surface, double[] value) {
        return new InitialisationVectorCellSetRow(model, fieldName, surface, value, monitor);
    }
    
    @Override
    public void load() {
        List<VectorSurface> sources = initialisation.getSurfaces();
        for (int i = 0; i < sources.size(); i++) {
            VectorSurface vectorSurface = sources.get(i);
            addRow(createRow(vectorSurface.getSurface(), vectorSurface.getValue()));
        }
    }
    
    @Override
    public void save() {
        List<Surface> surfaces = getSurfaces();
        List<double[]> values = getValues();
        List<VectorSurface> vs = new ArrayList<>();
        for (int i = 0; i < surfaces.size(); i++) {
            Surface surface = surfaces.get(i);
            double[] value = values.get(i);
            vs.add(new VectorSurface(surface, value));
        }
        initialisation.setSurfaces(vs);
    }

    private List<double[]> getValues() {
        List<double[]> values = new ArrayList<>();
        for (CellSetRow row : rowsMap.values()) {
            values.add(((InitialisationVectorCellSetRow)row).getValue());
        }
        return values;
    }

    public void setInitialisation(VectorCellSetInitialisation initialisation) {
        this.initialisation = initialisation;
    }

    public VectorCellSetInitialisation getInitialisation() {
        return initialisation;
    }
}

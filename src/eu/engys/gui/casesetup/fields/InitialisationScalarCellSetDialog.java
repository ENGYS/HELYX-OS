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
import eu.engys.core.project.zero.fields.CellSetInitialisation.ScalarSurface;
import eu.engys.core.project.zero.fields.Fields;
import eu.engys.core.project.zero.fields.ScalarCellSetInitialisation;
import eu.engys.util.progress.ProgressMonitor;

public class InitialisationScalarCellSetDialog extends CellSetDialog {
    
    private final String fieldName;
    private ScalarCellSetInitialisation initialisation;
    
    public InitialisationScalarCellSetDialog(Model model, String fieldName, ProgressMonitor monitor) {
        super(model, getTitle(fieldName), monitor);
        this.fieldName = fieldName;
    }
    
    private static String getTitle(String fieldName) {
        return fieldName.equals(Fields.ALPHA_1) ? fieldName + " [phase 1]" : fieldName;
    }
    
    @Override
    protected CellSetRow newRow(Surface surface) {
        if (fieldName.startsWith(Fields.ALPHA)) {
            return new InitialisationAlphaCellSetRow(model, fieldName, surface, 0.0, monitor);
        } else {
            return new InitialisationScalarCellSetRow(model, fieldName, surface, 0.0, monitor);
        }
    }
    
    private CellSetRow createRow(Surface surface, double value, Integer i) {
        if (fieldName.startsWith(Fields.ALPHA)) {
            return new InitialisationAlphaCellSetRow(model, fieldName, surface, value, monitor);
        } else {
            return new InitialisationScalarCellSetRow(model, fieldName, surface, value, monitor);
        }
    }
    
    @Override
    public void load() {
        List<ScalarSurface> sources = initialisation.getSurfaces();
        for (int i = 0; i < sources.size(); i++) {
            ScalarSurface scalarSurface = sources.get(i);
            addRow(createRow(scalarSurface.getSurface(), scalarSurface.getValue(), i));
        }
    }
    
    @Override
    public void save() {
        List<Surface> surfaces = getSurfaces();
        List<Double> values = getValues();
        List<ScalarSurface> ss = new ArrayList<>();
        for (int i = 0; i < surfaces.size(); i++) {
            Surface surface = surfaces.get(i);
            Double value = values.get(i);
            ss.add(new ScalarSurface(surface, value));
        }
        initialisation.setSurfaces(ss);
    
    }

    private List<Double> getValues() {
        List<Double> values = new ArrayList<>();
        for (CellSetRow row : rowsMap.values()) {
            if (row instanceof InitialisationScalarCellSetRow) {
                values.add(((InitialisationScalarCellSetRow)row).getValue());
            } else if (row instanceof InitialisationAlphaCellSetRow) {
                values.add(((InitialisationAlphaCellSetRow)row).getValue());
            } 
        }
        return values;
    }

    public void setInitialisation(ScalarCellSetInitialisation initialisation) {
        this.initialisation = initialisation;
    }

    public ScalarCellSetInitialisation getInitialisation() {
        return initialisation;
    }
}

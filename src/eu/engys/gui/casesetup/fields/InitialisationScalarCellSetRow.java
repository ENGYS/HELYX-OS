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

import javax.swing.JPanel;

import eu.engys.core.project.Model;
import eu.engys.core.project.geometry.Surface;
import eu.engys.util.progress.ProgressMonitor;
import eu.engys.util.ui.ComponentsFactory;
import eu.engys.util.ui.builder.PanelBuilder;
import eu.engys.util.ui.textfields.DoubleField;

public class InitialisationScalarCellSetRow extends CellSetRow {

    private DoubleField valueField;
    private double value;

    public InitialisationScalarCellSetRow(Model model, String fieldName, Surface surface, double value, ProgressMonitor monitor) {
        super(model, surface, monitor);
        this.value = value;
        layoutComponents();
        _load();
    }
    
    @Override
    protected JPanel createValuePanel() {
        valueField = ComponentsFactory.doubleField();

        PanelBuilder pb = new PanelBuilder();
        pb.addComponent("Value", valueField);
        
        valueField.setName(CELLSET_VALUE_NAME + "." + surface.getName());
        return pb.getPanel();
    }

    @Override
    protected void load() {
        valueField.setDoubleValue(value);        
    }
    
    @Override
    protected void newBox() {
        valueField.setDoubleValue(0);        
    }

    public Double getValue() {
        return value = valueField.getDoubleValue();
    }
}

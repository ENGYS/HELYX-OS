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
package eu.engys.gui.mesh.actions;

import javax.swing.JCheckBox;

import eu.engys.util.filechooser.HelyxFileChooser;
import eu.engys.util.ui.ComponentsFactory;
import eu.engys.util.ui.builder.PanelBuilder;
import eu.engys.util.ui.textfields.DoubleField;

public class IGESAccessory extends STLAccessory {

    private JCheckBox split;
    private DoubleField precision;

    public IGESAccessory(HelyxFileChooser chooser) {
        super(chooser);
    }

    @Override
    public void layoutOptionsPanel(PanelBuilder optionsBuilder) {
        super.layoutOptionsPanel(optionsBuilder);

        split = ComponentsFactory.checkField();
        precision = ComponentsFactory.doubleField(0.01, 0.0, 1.0);

        optionsBuilder.addComponent("Split by Component", split);
        optionsBuilder.addComponent("Precision", precision);
    }

    public double getPrecision() {
        return precision.getDoubleValue();
    }

    public boolean getSplit() {
        return split.isSelected();
    }
}

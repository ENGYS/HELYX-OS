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


package eu.engys.gui.casesetup.materials.panels;

import java.awt.CardLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.project.Model;
import eu.engys.core.project.state.State;
import eu.engys.gui.casesetup.materials.CompressibleMaterialsPanel;
import eu.engys.gui.casesetup.materials.IncompressibleMaterialsPanel;
import eu.engys.util.ui.textfields.StringField;

public class MaterialParametersPanel extends JPanel {

    private static final String INCOMPRESSIBLE = "Incompressible";
    private static final String COMPRESSIBLE = "Compressible";
    private static final String NONE = "None";

    private CompressibleMaterialsPanel compressiblePanel;
    private IncompressibleMaterialsPanel incompressiblePanel;

    private String selected = NONE;

    public MaterialParametersPanel(CompressibleMaterialsPanel compressiblePanel, IncompressibleMaterialsPanel incompressiblePanel) {
        super(new CardLayout());
        setName("Material Parameters");

        this.compressiblePanel = compressiblePanel;
        this.incompressiblePanel = incompressiblePanel;

        JPanel compPanel = compressiblePanel.getPanel();
        JPanel incompPanel = incompressiblePanel.getPanel();

        compPanel.setName("Compressible Panel");
        incompPanel.setName("Incompressible Panel");
        incompPanel.setBorder(BorderFactory.createTitledBorder("Material Parameters"));

        add(compPanel, COMPRESSIBLE);
        add(incompPanel, INCOMPRESSIBLE);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        compressiblePanel.setEnabled(enabled);
        incompressiblePanel.setEnabled(enabled);
    }

    public void load(Model model) {
        if (model.getState().isCompressible()) {
            selected = COMPRESSIBLE;
            ((CardLayout) getLayout()).show(this, COMPRESSIBLE);
        } else if (model.getState().isIncompressible()) {
            selected = INCOMPRESSIBLE;
            ((CardLayout) getLayout()).show(this, INCOMPRESSIBLE);
        }
    }

    public void show(State state) {
        if (state.isCompressible()) {
            selected = COMPRESSIBLE;
            compressiblePanel.stateChanged(state);
            ((CardLayout) getLayout()).show(this, COMPRESSIBLE);
        } else if (state.isIncompressible()) {
            selected = INCOMPRESSIBLE;
            incompressiblePanel.stateChanged(state);
            ((CardLayout) getLayout()).show(this, INCOMPRESSIBLE);
        }
    }
    
    public Dictionary getEmptyMaterial(Model model) {
        if (model.getState().isCompressible()) {
            return compressiblePanel.getEmptyMaterial();
        } else {
            return incompressiblePanel.getEmptyMaterial();
        }
    }

    public Dictionary getMaterial(Model model) {
        if (model.getState().isCompressible()) {
            return compressiblePanel.getMaterial(model);
        } else {
            return incompressiblePanel.getMaterial(model);
        }
    }

    public StringField getNameField(Model model) {
        if (model.getState().isCompressible()) {
            return compressiblePanel.getNameField();
        } else {
            return incompressiblePanel.getNameField();
        }
    }
    
    public void setMaterial(Dictionary material) {
        if (selected.equals(COMPRESSIBLE)) {
            compressiblePanel.setMaterial(material);
        } else {
            incompressiblePanel.setMaterial(material);
        }
    }

}

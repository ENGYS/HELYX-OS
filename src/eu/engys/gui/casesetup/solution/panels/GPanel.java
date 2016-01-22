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

package eu.engys.gui.casesetup.solution.panels;

import static eu.engys.util.ui.ComponentsFactory.doublePointField;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.project.Model;
import eu.engys.core.project.state.MultiphaseModel;
import eu.engys.core.project.state.SolutionState;
import eu.engys.core.project.state.State;
import eu.engys.core.project.state.ThermalState;
import eu.engys.util.Symbols;
import eu.engys.util.ui.builder.PanelBuilder;
import eu.engys.util.ui.textfields.DoubleField;

public class GPanel extends JPanel {

    public static final String G = "g [m/s" + Symbols.SQUARE + "]";
    public static final String GRAVITY = "Gravity";
    private PanelBuilder builder;
    private DoubleField[] gFields;

    public GPanel() {
        super(new BorderLayout());
        layoutComponents();
    }

    private void layoutComponents() {
        builder = new PanelBuilder();
        gFields = doublePointField(0.0, 0.0, -9.81);
        builder.addComponent(GPanel.G, gFields);
        JPanel gPanel = builder.getPanel();
        gPanel.setBorder(BorderFactory.createTitledBorder(GPanel.GRAVITY));
        add(gPanel, BorderLayout.CENTER);
    }

    public void fix(SolutionState ss, MultiphaseModel mm, ThermalState ts) {
        if (ss.areSolverTypeAndTimeAndFlowAndTurbulenceChoosen()) {
            if (mm.isOn()) {
                builder.setEnabled(true);
            } else if (ts.isBuoyancy()) {
                builder.setEnabled(true);
            } else {
                builder.setEnabled(false);
            }
        } else {
            builder.setEnabled(false);
        }
    }
    
    public void updateFromState(Model model, State state) {
        Dictionary g = model.getProject().getConstantFolder().getG();
        if (g != null && g.found("value")) {
            String[] gValues = g.lookupArray("value");
            try {
                double x = Double.parseDouble(gValues[0]);
                double y = Double.parseDouble(gValues[1]);
                double z = Double.parseDouble(gValues[2]);

                gFields[0].setValue(x);
                gFields[1].setValue(y);
                gFields[2].setValue(z);
            } catch (Exception e) {
            }
        }
        fix(new SolutionState(state), state.getMultiphaseModel(), new ThermalState(state));
    }
    
    public double[] getGValue() {
        return new double[] { gFields[0].getDoubleValue(), gFields[1].getDoubleValue(), gFields[2].getDoubleValue() };
    }

}

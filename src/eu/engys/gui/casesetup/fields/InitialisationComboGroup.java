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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import eu.engys.core.project.zero.fields.BoundaryValueInitialisation;
import eu.engys.core.project.zero.fields.DefaultInitialisation;
import eu.engys.core.project.zero.fields.FixedScalarInitialisation;
import eu.engys.core.project.zero.fields.FixedVectorInitialisation;
import eu.engys.core.project.zero.fields.PotentialFlowInitialisation;
import eu.engys.core.project.zero.fields.PrandtlInitialisation;
import eu.engys.core.project.zero.fields.TurbulentILInitialisation;
import eu.engys.util.ui.builder.JComboBoxController;

public class InitialisationComboGroup implements ActionListener {
    
    private static final String DEFAULT_CLASS = DefaultInitialisation.class.getCanonicalName();
    private static final String FIXED_SCALAR_VALUE_CLASS = FixedScalarInitialisation.class.getCanonicalName();
    private static final String FIXED_VECTOR_VALUE_CLASS = FixedVectorInitialisation.class.getCanonicalName();
    private static final String POTENTIAL_FLOW_CLASS = PotentialFlowInitialisation.class.getCanonicalName();
    private static final String TURBULENT_IL_CLASS = TurbulentILInitialisation.class.getCanonicalName();
    private static final String PRANDTL_CLASS = PrandtlInitialisation.class.getCanonicalName();
    private static final String BOUNDARY_VALUE_CLASS = BoundaryValueInitialisation.class.getCanonicalName();

    private static final List<String> TURBULENCE_TYPES = new ArrayList<String>(Arrays.asList(new String[] { "k.Type", "omega.Type", "epsilon.Type", "nuTilda.Type" }));
    private static final List<String> MOMENTUM_TYPES = new ArrayList<String>(Arrays.asList(new String[] { "U.Type", "p.Type", "p_rgh.Type" }));


    private Vector<JComboBoxController> combos = new Vector<JComboBoxController>();

    public void clear() {
        for (JComboBoxController combo : combos) {
            combo.removeActionListener(this);
        }
        combos.clear();
    }

    public void add(JComboBoxController b) {
        if (b == null) {
            return;
        }
        combos.addElement(b);
        b.addActionListener(this);
    }

    /*
     * When 'potentialFlow' is selected by the user for 'U' or 'p', the
     * other should be automatically put to potentialFlow Same behavior for
     * turbulent quantities
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        JComboBoxController source = (JComboBoxController) e.getSource();
        if (hasBeenSelected(source, POTENTIAL_FLOW_CLASS)) {
            changeTo(null, POTENTIAL_FLOW_CLASS, MOMENTUM_TYPES);
        } else if (hasBeenSelected(source, TURBULENT_IL_CLASS)) {
            changeTo(null, TURBULENT_IL_CLASS, TURBULENCE_TYPES);
        } else if (hasBeenSelected(source, PRANDTL_CLASS)) {
            changeTo(null, PRANDTL_CLASS, TURBULENCE_TYPES);
            changeTo(null, POTENTIAL_FLOW_CLASS, MOMENTUM_TYPES);
        } else if (hasBeenSelected(source, DEFAULT_CLASS) || hasBeenSelected(source, FIXED_SCALAR_VALUE_CLASS) || hasBeenSelected(source, FIXED_VECTOR_VALUE_CLASS)) {
            if (fieldChangedIsA(source, TURBULENCE_TYPES)) {
                changeTo(TURBULENT_IL_CLASS, DEFAULT_CLASS, TURBULENCE_TYPES);
                changeTo(PRANDTL_CLASS, DEFAULT_CLASS, TURBULENCE_TYPES);
            } else if (fieldChangedIsA(source, MOMENTUM_TYPES)) {
                changeTo(PRANDTL_CLASS, DEFAULT_CLASS, TURBULENCE_TYPES);
                changeTo(POTENTIAL_FLOW_CLASS, DEFAULT_CLASS, MOMENTUM_TYPES);
            }
        }
    }

    private boolean hasBeenSelected(JComboBoxController source, String type) {
        return type.equals(source.getSelectedKey());
    }

    private boolean fieldChangedIsA(JComboBoxController source, List<String> type) {
        return type.contains(source.getName());
    }

    private void changeTo(String oldKey, String newKey, List<String> fieldsToChangeList) {
        for (JComboBoxController combo : combos) {
            boolean condition1 = combo.getSelectedKey().equals(oldKey) || oldKey == null;
            boolean condition2 = combo.containsKey(newKey);
            boolean condition3 = !combo.getSelectedKey().equals(newKey);
            boolean condition4 = fieldsToChangeList.contains(combo.getName());

            if (condition1 && condition2 && condition3 && condition4) {
                combo.removeActionListener(this);
                combo.setSelectedKey(newKey);
                combo.addActionListener(this);
            }
        }
    }
}

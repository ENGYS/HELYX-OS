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
package eu.engys.gui.casesetup.solver.panels;

import javax.swing.JPanel;

import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.project.Model;

public interface SolverPanel {

    public static final String OUTER_CORRECTORS_LABEL = "Outer Correctors";
    public static final String CORRECTORS_LABEL = "Correctors";
    public static final String NON_ORTHOGONAL_CORRECTORS_LABEL = "Non-orthogonal Correctors";
    public static final String RHO_MIN_LABEL = "Rho Min";
    public static final String RHO_MAX_LABEL = "Rho Max";
    public static final String RESIDUAL_CONTROL_LABEL = "Residual Control";
    public static final String RELAXATION_FACTORS_LABEL = "Relaxation Factors";
    public static final String RELATIVE_TOLERANCE_LABEL = "Relative Tolerance";
    public static final String TOLERANCE_LABEL = "Tolerance";

    Dictionary getSolverDictionary();

    Dictionary getRelaxationFactorsDictionary();

    Dictionary getResidualControlDictionary();

    void load(Model model);

    JPanel getPanel();

    String getKey();

}

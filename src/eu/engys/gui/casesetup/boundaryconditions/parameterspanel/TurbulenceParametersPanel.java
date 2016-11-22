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
package eu.engys.gui.casesetup.boundaryconditions.parameterspanel;

import static eu.engys.gui.casesetup.boundaryconditions.utils.TurbulenceUtils.setKEpsilon;
import static eu.engys.gui.casesetup.boundaryconditions.utils.TurbulenceUtils.setKEquationEddy;
import static eu.engys.gui.casesetup.boundaryconditions.utils.TurbulenceUtils.setKOmega;
import static eu.engys.gui.casesetup.boundaryconditions.utils.TurbulenceUtils.setSpalartAllmaras;

import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.modules.boundaryconditions.BoundaryTypePanel;
import eu.engys.core.project.Model;
import eu.engys.core.project.TurbulenceModelType;
import eu.engys.core.project.state.State;
import eu.engys.core.project.zero.patches.BoundaryConditions;

public abstract class TurbulenceParametersPanel extends AbstractParametersPanel {

	public static final String TURBULENCE = "Turbulence";

	private TurbulenceModelType type;

	public TurbulenceParametersPanel(BoundaryTypePanel parent) {
		super(parent);
	}

	@Override
	public String getTitle() {
		return TURBULENCE;
	}

	public boolean isEnabled(Model model) {
		State state = model.getState();
		return (state.getTurbulenceModel() != null && state.getTurbulenceModel().getType().hasFields());
	}

	@Override
	public void stateChanged(Model model) {
		TurbulenceModelType type = model.getState().getTurbulenceModel().getType();
		if (this.type == null || this.type != type) {
			this.type = type;
			if (type.isKepsilon()) {
				setKEpsilon(builder);
			} else if (type.isKomega()) {
				setKOmega(builder);
			} else if (type.isSpalartAllmaras()) {
				setSpalartAllmaras(builder);
			} else if (type.isKEquationeddy()) {
				setKEquationEddy(builder);
			}
		}
	}

	@Override
	public void saveToBoundaryConditions(BoundaryConditions bc) {
		Dictionary dictionary = bc.getTurbulence();
		saveToDictionary(dictionary);
	}
}

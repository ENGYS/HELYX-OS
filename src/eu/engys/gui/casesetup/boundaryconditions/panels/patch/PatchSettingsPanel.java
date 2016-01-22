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


package eu.engys.gui.casesetup.boundaryconditions.panels.patch;

import javax.inject.Inject;

import eu.engys.core.project.Model;
import eu.engys.core.project.zero.patches.BoundaryType;
import eu.engys.gui.casesetup.boundaryconditions.panels.AbstractBoundaryTypePanel;

public class PatchSettingsPanel extends AbstractBoundaryTypePanel {

	@Inject
	public PatchSettingsPanel(Model model) {
		super(model);
		setName("Turbulence");
	}

	@Override
	public void layoutPanel() {
		super.layoutPanel();
		MomentumPatch momentum = new MomentumPatch(this);
		TurbulencePatch turbulence = new TurbulencePatch(this);
		ThermalPatch thermal = new ThermalPatch(this);
		PhasePatch phase = new PhasePatch(model, this);

		addMomentumPanel(momentum);
		addTurbulencePanel(turbulence);
		addThermalPanel(thermal);
		addPhasePanel(phase);
	}

	@Override
	public BoundaryType getType() {
		return BoundaryType.PATCH;
	}

}

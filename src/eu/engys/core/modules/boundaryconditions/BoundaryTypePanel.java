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


package eu.engys.core.modules.boundaryconditions;

import java.awt.Component;

import eu.engys.core.project.zero.patches.BoundaryType;
import eu.engys.core.project.zero.patches.Patch;

public interface BoundaryTypePanel {

	public static final String MOMENTUM = "Momentum";
	
	public static final String TURBULENCE = "Turbulence";
	public static final String THERMAL = "Thermal";
	public static final String PASSIVE_SCALARS = "Passive Scalars";
	public static final String PHASE_FRACTION = "Phase Fraction";

	void layoutPanel();
	
	void loadFromPatches(Patch... patches);

	void saveToPatch(Patch patch);

	BoundaryType getType();
	Component getPanel();

	ParametersPanel getMomentumPanel();
	ParametersPanel getTurbulencePanel();
	ParametersPanel getThermalPanel();
	ParametersPanel getPanel(String name);
	
	void stateChanged();
	void materialsChanged();

	void addMomentumPanel(ParametersPanel momentumPanel);
	void addTurbulencePanel(ParametersPanel momentumPanel);
	void addThermalPanel(ParametersPanel momentumPanel);

	void addPanel(String name, ParametersPanel pPanel);
	void addPanel(String name, ParametersPanel pPanel, int index);

	void resetToDefault();
}

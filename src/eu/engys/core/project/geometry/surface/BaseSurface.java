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


package eu.engys.core.project.geometry.surface;

import eu.engys.core.project.geometry.Surface;

public abstract class BaseSurface extends Surface {

	private static final String _REGION0 = "_region0";

	public BaseSurface(String name) {
		super(name);
	}

	@Override
	public String getPatchName() {
		return isAppendRegionName() ? getName() + _REGION0 : getName();
	}

	@Override
	public boolean hasRegions() {
		return false;
	}

	@Override
	public Region[] getRegions() {
		return new Region[0];
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	@Override
	public boolean hasLayers() {
		return true;
	}

	@Override
	public boolean hasSurfaceRefinement() {
		return true;
	}

	@Override
	public boolean hasVolumeRefinement() {
		return true;
	}

	@Override
	public boolean hasZones() {
		return true;
	}
}

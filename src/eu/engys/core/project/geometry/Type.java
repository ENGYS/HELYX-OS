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


package eu.engys.core.project.geometry;

public enum Type {
	STL, BOX, CYLINDER, SPHERE, REGION, SOLID, PLANE, MULTI, RING, LINE;
	
	public boolean isStl() {
		return equals(STL);
	}
	
	public boolean isBox() {
		return equals(BOX);
	}
	
	public boolean isCylinder() {
		return equals(CYLINDER);
	}
	
	public boolean isSphere() {
		return equals(SPHERE);
	}

	public boolean isRing() {
	    return equals(RING);
	}
	
	public boolean isRegion() {
		return equals(REGION);
	}
	
	public boolean isMulti() {
		return equals(MULTI);
	}
	
	public boolean isSolid() {
		return equals(SOLID);
	}
	
	public boolean isPlane() {
		return equals(PLANE);
	}

	public boolean isLine() {
	    return equals(LINE);
	}

    public boolean isBaseShape() {
        return isBox() ||isPlane() ||isSphere() || isRing();
    }
	
	
}

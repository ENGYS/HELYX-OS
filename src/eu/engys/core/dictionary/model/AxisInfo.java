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

package eu.engys.core.dictionary.model;

import eu.engys.util.ui.textfields.DoubleField;

public class AxisInfo {

	public static final String PROPERTY_NAME = "point.location";
	public static final String LABEL = "Point ";
	private DoubleField[] center;
	private EventActionType action;
	private DoubleField[] axis;

	public AxisInfo(DoubleField[] axis, DoubleField[] center, EventActionType action) {
		this.axis = axis;
		this.center = center;
		this.action = action;
	}

	public DoubleField[] getAxis() {
		return axis;
	}
	
	public DoubleField[] getCenter() {
		return center;
	}
	
	public EventActionType getAction() {
		return action;
	}

}

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


package eu.engys.gui.events.view3D;

import eu.engys.core.dictionary.model.EventActionType;
import eu.engys.gui.events.EventObject;
import eu.engys.util.ui.textfields.DoubleField;

public class PlaneEvent extends EventObject implements View3DEvent {

	private DoubleField[] normal;
	private DoubleField[] origin;
	private EventActionType action;
	private boolean interactive;

	public PlaneEvent(DoubleField[] origin, DoubleField[] normal, EventActionType action) {
		super();
		this.origin = origin;
		this.normal = normal;
		this.action = action;
		this.interactive = true;
	}

	public PlaneEvent(DoubleField[] origin, DoubleField[] normal, EventActionType action, boolean interactive) {
	    super();
	    this.origin = origin;
	    this.normal = normal;
	    this.action = action;
        this.interactive = interactive;
	}

	public DoubleField[] getOrigin() {
		return origin;
	}

	public void setOrigin(DoubleField[] origin) {
		this.origin = origin;
	}

	public DoubleField[] getNormal() {
		return normal;
	}

	public void setNormal(DoubleField[] normal) {
		this.normal = normal;
	}

	public EventActionType getAction() {
		return action;
	}

	public void setAction(EventActionType action) {
		this.action = action;
	}

	public void setInteractive(boolean interactive) {
        this.interactive = interactive;
    }
	public boolean isInteractive() {
        return interactive;
    }
}

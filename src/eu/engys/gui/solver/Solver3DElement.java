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


package eu.engys.gui.solver;

import java.util.Set;

import javax.inject.Inject;

import eu.engys.gui.GUIPanel;
import eu.engys.gui.view.AbstractView3DElement;
import eu.engys.gui.view3D.CanvasPanel;

public class Solver3DElement extends AbstractView3DElement {

	@Inject
	public Solver3DElement(@Solver Set<GUIPanel> panels) {
	    super(panels);
	}
	
//	@Override
//	public void start(CanvasPanel view3D) {
//		view3D.applyContext(CaseSetup3DElement.class);
//	}
//	
//	@Override
//	public void save(CanvasPanel view3d) {
//	    view3d.dumpContext(CaseSetup3DElement.class);
//	}
	
	@Override
	public void load(CanvasPanel view3D) {
		view3D.getMeshController().newContext(getClass());
		view3D.getGeometryController().newEmptyContext(getClass());
	}

}

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

package eu.engys.gui.view;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.engys.gui.GUIPanel;
import eu.engys.gui.view3D.CanvasPanel;

public abstract class AbstractView3DElement implements View3DElement {

    private static final Logger logger = LoggerFactory.getLogger(View3DElement.class);
    
    private Set<GUIPanel> panels;
    
    public AbstractView3DElement(Set<GUIPanel> panels) {
        this.panels = panels;
    }

    @Override
    public void install(CanvasPanel view3D) {
        for (GUIPanel panel : panels) {
            panel.install(view3D);
        }
    }
    
    @Override
	public void start(CanvasPanel view3D) {
        logger.info("[START 3D] {}", getClass().getSimpleName());
		view3D.applyContext(getClass());
	}

	@Override
	public void stop(CanvasPanel view3D) {
	    logger.info("[STOP 3D] {}", getClass().getSimpleName());
	}

	@Override
	public void save(CanvasPanel view3D) {
		view3D.dumpContext(getClass());
	}

}

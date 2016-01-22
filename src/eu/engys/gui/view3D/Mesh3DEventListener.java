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


package eu.engys.gui.view3D;

import javax.swing.SwingUtilities;

import eu.engys.core.project.zero.cellzones.CellZone;
import eu.engys.core.project.zero.patches.Patch;
import eu.engys.gui.events.EventManager.Event;
import eu.engys.gui.events.view3D.SelectCellZonesEvent;
import eu.engys.gui.events.view3D.SelectPatchesEvent;
import eu.engys.gui.events.view3D.VisibleItemEvent;
import eu.engys.util.ui.checkboxtree.VisibleItem;

public class Mesh3DEventListener implements View3DEventListener {

    private Mesh3DController mesh3DController;

    public Mesh3DEventListener(Mesh3DController mesh3DController) {
        this.mesh3DController = mesh3DController;
    }

    @Override
    public void eventTriggered(Object obj, final Event event) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                if (event instanceof SelectPatchesEvent) {
                    handleSelectPatches((SelectPatchesEvent) event);
                } else if (event instanceof SelectCellZonesEvent) {
                    handleSelectZones((SelectCellZonesEvent) event);
                } else if (event instanceof VisibleItemEvent) {
                    handleVisibility((VisibleItemEvent) event);
                }
            }
        });
    }

    private void handleSelectPatches(SelectPatchesEvent event) {
        Patch selection[] = ((SelectPatchesEvent) event).getSelection();
        mesh3DController.updatePatchesSelection(selection);
    }

    private void handleSelectZones(SelectCellZonesEvent event) {
        CellZone selection[] = ((SelectCellZonesEvent) event).getSelection();
        mesh3DController.updateCellZonesSelection(selection);
    }

    private void handleVisibility(VisibleItemEvent event) {
        VisibleItem selection = event.getSelection();
        if (selection instanceof Patch) {
            mesh3DController.updatePatchesVisibility((Patch) selection);
        } else if (selection instanceof CellZone) {
            mesh3DController.updateCellZonesVisibility((CellZone) selection);
        }
    }
}

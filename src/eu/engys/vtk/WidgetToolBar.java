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

package eu.engys.vtk;

import static eu.engys.vtk.VTK3DActionsToolBar._3D_LOAD_MESH;

import java.awt.FlowLayout;
import java.util.Set;

import javax.swing.Box;
import javax.swing.JToolBar;

import eu.engys.core.presentation.ActionManager;
import eu.engys.gui.view3D.widget.Widget;
import eu.engys.util.ui.WrappedFlowLayout;

public class WidgetToolBar extends JToolBar {

    private Set<Widget> widgets;

    public WidgetToolBar(Set<Widget> widgets) {
        super(JToolBar.HORIZONTAL);
        setLayout(new WrappedFlowLayout(FlowLayout.LEFT, 0, 0));
        this.widgets = widgets;
        putClientProperty("Synthetica.toolBar.buttons.paintBorder", Boolean.TRUE);
        putClientProperty("Synthetica.opaque", Boolean.FALSE);
        setFloatable(false);
        setRollover(true);
        layoutComponents();
    }

    private void layoutComponents() {
        add(ActionManager.getInstance().get(_3D_LOAD_MESH));
        for (Widget widget : widgets) {
            widget.populate(this);
        }
        add(Box.createHorizontalGlue());
    }

    public void clear() {
    }

    public boolean hasWidgets() {
        return widgets.size() > 0;
    }

}

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
package eu.engys.vtk;

import static eu.engys.vtk.VTK3DActionsToolBar._3D_LOAD_MESH;

import java.util.Set;

import javax.swing.JToolBar;

import eu.engys.core.presentation.ActionManager;
import eu.engys.gui.view3D.widget.Widget;
import eu.engys.util.ui.UiUtil;

public class WidgetToolBar {

    public static final String WIDGET_TOOLBAR_NAME = "widget.toolbar";

    private Set<Widget> widgets;

    private JToolBar toolbar;

    public WidgetToolBar(Set<Widget> widgets) {
        this.toolbar = UiUtil.getToolbarWrapped(WIDGET_TOOLBAR_NAME);
        this.widgets = widgets;
        layoutComponents();
    }

    private void layoutComponents() {
        toolbar.add(ActionManager.getInstance().get(_3D_LOAD_MESH));
        for (Widget widget : widgets) {
            widget.populate(toolbar);
        }
//        toolbar.add(Box.createHorizontalGlue());
//        add(createToolBarToggleButton(ActionManager.getInstance().get(VTK3DActionsToolBar._3D_LOCATION)));
//        add(createToolBarToggleButton(ActionManager.getInstance().get(VTK3DActionsToolBar._3D_COR)));
    }

    public void clear() {
    }

    public boolean hasWidgets() {
        return widgets.size() > 0;
    }
    
    public JToolBar getToolbar() {
        return toolbar;
    }

}

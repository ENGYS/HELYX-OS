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

package eu.engys.vtk.widgets.fake;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;

import eu.engys.gui.view3D.CanvasPanel;
import eu.engys.gui.view3D.widget.Widget;
import eu.engys.gui.view3D.widget.WidgetComponent;

public abstract class FakeWidget implements Widget {

    protected static final String TOOLTIP = "To enable this feature please contact: info@engys.com";

    protected List<JComponent> components = new ArrayList<JComponent>();

    @Override
    public void populate(CanvasPanel view3d) {
    }

    @Override
    public boolean canShow() {
        return false;
    }

    @Override
    public void show() {
        disableAll();
    }

    @Override
    public void hide() {
        disableAll();
    }

    @Override
    public void clear() {
        disableAll();
    }

    @Override
    public void stop() {
        disableAll();
    }

    @Override
    public WidgetComponent getWidgetComponent() {
        return null;
    }

    @Override
    public void load() {
        disableAll();
    }

    @Override
    public void applyContext() {
        disableAll();
    }

    @Override
    public void handleFieldChanged() {
        disableAll();
    }

    @Override
    public void handleTimeStepChanged() {
        disableAll();
    }

    @Override
    public void handleNewTimeStepsRead() {
        disableAll();
    }

//    @Override
//    public void handleInitializeFieldsStarted() {
//        disableAll();
//    }
//
//    @Override
//    public void handleInitializeFieldsFinished() {
//        disableAll();
//    }

    private void disableAll() {
        for (JComponent c : components) {
            c.setEnabled(false);
        }
    }

}

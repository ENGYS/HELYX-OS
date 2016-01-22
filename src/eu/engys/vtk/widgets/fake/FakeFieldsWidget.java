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

import static eu.engys.util.ui.UiUtil.createToolBarComboButton;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComboBox;
import javax.swing.JToolBar;

import eu.engys.core.project.mesh.FieldItem;

public class FakeFieldsWidget extends FakeWidget {

    private static final String PROTOTYPE = "UWater-Magnitude-Icon";

    @Override
    public void populate(JToolBar toolbar) {
        toolbar.addSeparator();
        JComboBox<Action> fieldsCombo = createToolBarComboButton(getFieldsActions(new ArrayList<FieldItem>()), TOOLTIP, PROTOTYPE, false, true);
        fieldsCombo.setEnabled(false);
        components.add(fieldsCombo);
        toolbar.add(fieldsCombo);
    }

    public List<Action> getFieldsActions(List<FieldItem> fieldItems) {
        List<Action> actions = new ArrayList<>();
        actions.add(new AbstractAction(FieldItem.SOLID) {

            @Override
            public void actionPerformed(ActionEvent e) {
            }
        });
        return actions;
    }
}

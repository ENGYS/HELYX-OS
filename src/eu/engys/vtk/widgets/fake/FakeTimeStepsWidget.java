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
import java.util.Collections;

import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JComboBox;
import javax.swing.JToolBar;

import eu.engys.util.ui.ResourcesUtil;
import eu.engys.util.ui.UiUtil;
import eu.engys.util.ui.ViewAction;

public class FakeTimeStepsWidget extends FakeWidget {

    private static final String PROTOTYPE = "12345.67";

    public static final String[] COMPONENTS = new String[] { "Magnitude", "X", "Y", "Z" };

    public static final Icon PREV_ICON = ResourcesUtil.getIcon("3d.widget.times.prev.icon");
    public static final Icon NEXT_ICON = ResourcesUtil.getIcon("3d.widget.times.next.icon");
    public static final Icon FIRST_ICON = ResourcesUtil.getIcon("3d.widget.times.first.icon");
    public static final Icon LAST_ICON = ResourcesUtil.getIcon("3d.widget.times.last.icon");
    public static final Icon REFRESH_ICON = ResourcesUtil.getIcon("3d.widget.times.refresh.icon");

    private Action NEXT_STEP = new ViewAction(null, NEXT_ICON, TOOLTIP, false) {
        @Override
        public void actionPerformed(ActionEvent e) {
        }
    };
    private Action PREVIOUS_STEP = new ViewAction(null, PREV_ICON, TOOLTIP, false) {
        @Override
        public void actionPerformed(ActionEvent e) {
        }
    };
    private Action FIRST_STEP = new ViewAction(null, FIRST_ICON, TOOLTIP, false) {
        @Override
        public void actionPerformed(ActionEvent e) {
        }
    };
    private Action LAST_STEP = new ViewAction(null, LAST_ICON, TOOLTIP, false) {
        @Override
        public void actionPerformed(ActionEvent e) {
        }
    };
    private Action REFRESH = new ViewAction(null, REFRESH_ICON, TOOLTIP, false) {
        @Override
        public void actionPerformed(ActionEvent e) {
        }
    };

    @Override
    public void populate(JToolBar toolbar) {
        JComboBox<Action> timesCombo = createToolBarComboButton(Collections.<Action> emptyList(), TOOLTIP, PROTOTYPE, true, true);
        AbstractButton firstButton = UiUtil.createToolBarButton(FIRST_STEP);
        AbstractButton previousButton = UiUtil.createToolBarButton(PREVIOUS_STEP);
        AbstractButton nextButton = UiUtil.createToolBarButton(NEXT_STEP);
        AbstractButton lastButton = UiUtil.createToolBarButton(LAST_STEP);
        AbstractButton refreshButton = UiUtil.createToolBarButton(REFRESH);

        timesCombo.setEnabled(false);
        firstButton.setEnabled(false);
        previousButton.setEnabled(false);
        nextButton.setEnabled(false);
        lastButton.setEnabled(false);
        refreshButton.setEnabled(false);

        toolbar.addSeparator();
        toolbar.add(timesCombo);
        toolbar.add(firstButton);
        toolbar.add(previousButton);
        toolbar.add(nextButton);
        toolbar.add(lastButton);
        toolbar.add(refreshButton);
        toolbar.addSeparator();

        components.add(timesCombo);
        components.add(firstButton);
        components.add(previousButton);
        components.add(nextButton);
        components.add(lastButton);
        components.add(refreshButton);
    }

}

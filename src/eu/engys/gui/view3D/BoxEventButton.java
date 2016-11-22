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
package eu.engys.gui.view3D;

import java.awt.Dimension;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;

import eu.engys.core.dictionary.model.EventActionType;
import eu.engys.gui.events.EventManager;
import eu.engys.gui.events.view3D.BoxEvent;
import eu.engys.util.ui.ResourcesUtil;
import eu.engys.util.ui.textfields.DoubleField;

public class BoxEventButton extends JToggleButton {

    private static final Icon ICON_ON = ResourcesUtil.getIcon("light.on.icon");
    private static final Icon ICON_OFF = ResourcesUtil.getIcon("light.off.icon");
    
    public BoxEventButton(final DoubleField[] boxMin, final DoubleField[] boxMax) {
        super();
        setAction(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isSelected()) {
                    EventManager.triggerEvent(this, new BoxEvent(boxMin, boxMax, EventActionType.SHOW));
                } else {
                    EventManager.triggerEvent(this, new BoxEvent(boxMin, boxMax, EventActionType.HIDE));
                }
            }
        });
        setPreferredSize(new Dimension(36, 48));
        setIcon(ICON_OFF);
        setSelectedIcon(ICON_ON);
        setPressedIcon(ICON_ON);
//        setVerticalAlignment(SwingConstants.TOP);
        setVerticalTextPosition(SwingConstants.CENTER);
    }
}

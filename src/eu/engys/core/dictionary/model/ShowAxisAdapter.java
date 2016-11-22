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
package eu.engys.core.dictionary.model;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

import eu.engys.util.ui.ResourcesUtil;
import eu.engys.util.ui.textfields.DoubleField;
import net.java.dev.designgridlayout.Componentizer;

public class ShowAxisAdapter extends JPanel {

    private static final Icon ICON_ON = ResourcesUtil.getIcon("light.on.icon");
    private static final Icon ICON_OFF = ResourcesUtil.getIcon("light.off.icon");

    private JToggleButton button;
    private DoubleField[] axis;
    private DoubleField[] centre;

    public ShowAxisAdapter(DoubleField[] axis, DoubleField[] centre) {
        super(new BorderLayout());
        this.axis = axis;
        this.centre = centre;
        this.button = newShowAxisButton(false);
        JComponent component = Componentizer.create().minAndMore(centre).minToPref(button).component();
        add(component, BorderLayout.CENTER);
    }

    @Override
    public void setName(String name) {
        super.setName(name);
        button.setName(getName() + ".button");
        axis[0].setName(getName() + ".axis.0");
        axis[1].setName(getName() + ".axis.1");
        axis[2].setName(getName() + ".axis.2");
        centre[0].setName(getName() + ".centre.0");
        centre[1].setName(getName() + ".centre.1");
        centre[2].setName(getName() + ".centre.2");
    }

    @Override
    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        super.addPropertyChangeListener(propertyName, listener);
        if (propertyName.equals("point.location")) {
            button.getAction().addPropertyChangeListener(listener);
        }
    }

    public void turnOff() {
        if (button.isSelected()) {
            button.doClick();
        }
    }

    public JToggleButton newShowAxisButton(boolean selected) {
        final JToggleButton button = new JToggleButton(new AbstractAction() {
            // private Border originalBorder;

            @Override
            public void actionPerformed(ActionEvent e) {
                AbstractButton b = ((AbstractButton) e.getSource());
                if (b.isSelected()) {
                    firePropertyChange(PointInfo.PROPERTY_NAME, null, new AxisInfo(axis, centre, 1, EventActionType.SHOW));
                } else {
                    firePropertyChange(PointInfo.PROPERTY_NAME, null, new AxisInfo(axis, centre, 1, EventActionType.HIDE));
                }
            }
        });
        if (selected && !button.isSelected() || (!selected && button.isSelected())) {
            button.doClick();
        }
        button.setPreferredSize(new Dimension(22, 22));
        button.setIcon(ICON_OFF);
        button.setSelectedIcon(ICON_ON);
        button.setToolTipText("Click to display this point in the 3D canvas");
        return button;
    }

}

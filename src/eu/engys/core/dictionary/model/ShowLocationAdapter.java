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

package eu.engys.core.dictionary.model;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.border.Border;

import net.java.dev.designgridlayout.Componentizer;
import eu.engys.util.ColorUtil;
import eu.engys.util.ui.ResourcesUtil;
import eu.engys.util.ui.textfields.DoubleField;

public class ShowLocationAdapter extends JPanel {

    private static final Icon ICON_ON = ResourcesUtil.getResourceIcon("eu/engys/resources/images/lightbulb16.png");
    private static final Icon ICON_OFF = ResourcesUtil.getResourceIcon("eu/engys/resources/images/lightbulb_off16.png");

    private JToggleButton button;
    private DoubleField[] fields;

    public ShowLocationAdapter(DoubleField[] fields, Color key) {
        super(new BorderLayout());
        this.fields = fields;
        this.button = newShowPointButton(fields, key, false);
        JComponent component = Componentizer.create().minAndMore(fields).minToPref(button).component();
        add(component, BorderLayout.CENTER);
    }

    @Override
    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        super.addPropertyChangeListener(propertyName, listener);
        if (propertyName.equals(PointInfo.PROPERTY_NAME)) {
            button.getAction().addPropertyChangeListener(listener);
        }
    }

    public void turnMaterialPointsOn() {
        if (!button.isSelected()) {
            button.doClick();
        }
    }

    public void turnMaterialPointsOff() {
        if (button.isSelected()) {
            button.doClick();
        }
    }

    public static JToggleButton newShowPointButton(final DoubleField[] locationInMesh, int index, boolean selected) {
        return newShowPointButton(locationInMesh, ColorUtil.getColor(index), selected);
    }

    public static JToggleButton newShowPointButton(final DoubleField[] locationInMesh, final Color color, boolean selected) {
        final JToggleButton button = new JToggleButton(new AbstractAction() {
            private Border originalBorder;

            @Override
            public void actionPerformed(ActionEvent e) {
                AbstractButton b = ((AbstractButton) e.getSource());
                if (b.isSelected()) {
                    firePropertyChange(PointInfo.PROPERTY_NAME, null, new PointInfo(locationInMesh, color.toString(), EventActionType.SHOW, color));
                    originalBorder = b.getBorder();
                    b.setBorder(BorderFactory.createLineBorder(color, 2));
                } else {
                    firePropertyChange(PointInfo.PROPERTY_NAME, null, new PointInfo(locationInMesh, color.toString(), EventActionType.HIDE, color));
                    if (originalBorder != null) {
                        b.setBorder(originalBorder);
                    }
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

    @Override
    public void setToolTipText(String text) {
        super.setToolTipText(text);
        for (DoubleField f : fields) {
            f.setToolTipText(text);
        }
    }

}

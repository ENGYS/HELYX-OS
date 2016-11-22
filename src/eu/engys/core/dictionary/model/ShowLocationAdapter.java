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
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

import eu.engys.util.ColorUtil;
import eu.engys.util.ui.ResourcesUtil;
import eu.engys.util.ui.textfields.DoubleField;
import net.java.dev.designgridlayout.Componentizer;

public class ShowLocationAdapter extends JPanel {

    private static final Icon ICON_ON = ResourcesUtil.getIcon("light.on.icon");
    private static final Icon ICON_OFF = ResourcesUtil.getIcon("light.off.icon");
    
    public static final String X = "x";
    public static final String Y = "y";
    public static final String Z = "z";

    private JToggleButton button;
    private DoubleField[] fields;

    public ShowLocationAdapter(DoubleField[] fields, Color key) {
        super(new BorderLayout());
        this.fields = fields;
        this.button = newShowPointButton(fields, key, false);

        fields[0].setName(X);
        fields[1].setName(Y);
        fields[2].setName(Z);

        JComponent component = Componentizer.create().minAndMore(fields).minToPref(button).component();
        add(component, BorderLayout.CENTER);
    }

    public static JToggleButton newShowPointButton(final DoubleField[] locationInMesh, int index, boolean selected) {
        return newShowPointButton(locationInMesh, ColorUtil.getColor(index), selected);
    }

    private static JToggleButton newShowPointButton(final DoubleField[] locationInMesh, final Color color, boolean selected) {
        final JToggleButton button = new JToggleButton(new AbstractAction() {
            Object originalColorProperty = new JToggleButton().getClientProperty("Synthetica.background");
            Object originalColorAlphaProperty = new JToggleButton().getClientProperty("Synthetica.background.alpha");

            @Override
            public void actionPerformed(ActionEvent e) {
                AbstractButton b = ((AbstractButton) e.getSource());
                if (b.isSelected()) {
                    firePropertyChange(PointInfo.PROPERTY_NAME, null, new PointInfo(locationInMesh, color.toString(), EventActionType.SHOW, color));
                    b.putClientProperty("Synthetica.background", color);
                    b.putClientProperty("Synthetica.background.alpha", 0.50f);
                } else {
                    firePropertyChange(PointInfo.PROPERTY_NAME, null, new PointInfo(locationInMesh, color.toString(), EventActionType.HIDE, color));
                    b.putClientProperty("Synthetica.background", originalColorProperty);
                    b.putClientProperty("Synthetica.background.alpha", originalColorAlphaProperty);
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
    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        super.addPropertyChangeListener(propertyName, listener);
        if (propertyName.equals(PointInfo.PROPERTY_NAME)) {
            button.getAction().addPropertyChangeListener(listener);
        }
    }
    
    public DoubleField[] getFields() {
        return fields;
    }
    
    public JToggleButton getButton() {
        return button;
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

    @Override
    public void setToolTipText(String text) {
        super.setToolTipText(text);
        for (DoubleField f : fields) {
            f.setToolTipText(text);
        }
    }

}

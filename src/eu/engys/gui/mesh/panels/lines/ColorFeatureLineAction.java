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

package eu.engys.gui.mesh.panels.lines;

import java.awt.Color;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.SwingUtilities;

import eu.engys.core.project.geometry.FeatureLine;
import eu.engys.gui.events.EventManager;
import eu.engys.gui.events.view3D.ColorSurfaceEvent;
import eu.engys.gui.mesh.panels.FeatureLinesPanel;

public class ColorFeatureLineAction extends AbstractAction {

    // private static final Icon PICK_ICON = ResourcesUtil.getIcon("color.pick.icon");

    private Color currentColor = Color.WHITE;
    private FeatureLinesPanel linesPanel;

    public ColorFeatureLineAction(FeatureLinesPanel engysLinesPanel) {
        // super("", PICK_ICON);
        super("Choose");
        this.linesPanel = engysLinesPanel;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        this.currentColor = JColorChooser.showDialog(SwingUtilities.getWindowAncestor(linesPanel), "Select a color", currentColor);
        changeColor(currentColor, (JButton) e.getSource());
    }

    private void changeColor(Color currentColor, JButton sourceButton) {
        if (linesPanel.getSelectedLine() != null) {
            FeatureLine selectedLine = linesPanel.getSelectedLine();
            selectedLine.setColor(currentColor);
            sourceButton.setBackground(currentColor);
            EventManager.triggerEvent(this, new ColorSurfaceEvent(selectedLine, currentColor));
        }
    }

    public void setCurrentColor(Color currentColor) {
        this.currentColor = currentColor;
    }

}

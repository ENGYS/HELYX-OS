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

package eu.engys.vtk.widgets.panels;

import java.awt.Color;
import java.awt.FlowLayout;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import javax.swing.JLabel;
import javax.swing.JPanel;

import eu.engys.core.project.geometry.BoundingBox;
import eu.engys.gui.view3D.CanvasPanel;
import eu.engys.util.Symbols;
import eu.engys.util.plaf.ILookAndFeel;

public class BoundingBoxBar extends JPanel {

    private JLabel xlabel;
    private JLabel ylabel;
    private JLabel zlabel;
    private CanvasPanel view3D;

    public BoundingBoxBar(CanvasPanel view3D, ILookAndFeel laf) {
        super(new FlowLayout(FlowLayout.LEFT));
        this.view3D = view3D;
        this.xlabel = new JLabel();
        this.ylabel = new JLabel();
        this.zlabel = new JLabel();
        xlabel.setForeground(Color.RED.darker());
        ylabel.setForeground(Color.GREEN.darker());
        zlabel.setForeground(Color.BLUE.darker());
        add(xlabel);
        add(ylabel);
        add(zlabel);
        
//        setOpaque(true);
//        double[] color = laf.get3DColor1();
//		setBackground(new Color((float) color[0], (float) color[1], (float) color[2]));
    }

//    @Override
//    protected void paintComponent(Graphics g) {
//    	Dimension size = getSize();
//    	g.setColor(getBackground());
//    	g.fillRect(0, 0, size.width, size.height);
//    }
    
    public BoundingBox update() {
        BoundingBox box = view3D.computeBoundingBox(true);
        if (isEmpty(box)) {
            box = new BoundingBox(0, 0, 0, 0, 0, 0);
        }

        String formattedXmin = getFormattedNumber(box.getXmin());
        String formattedYmin = getFormattedNumber(box.getYmin());
        String formattedZmin = getFormattedNumber(box.getZmin());

        String formattedXmax = getFormattedNumber(box.getXmax());
        String formattedYmax = getFormattedNumber(box.getYmax());
        String formattedZmax = getFormattedNumber(box.getZmax());

        String formattedXDifference = getFormattedNumber((box.getXmax() - box.getXmin()));
        String formattedYDifference = getFormattedNumber((box.getYmax() - box.getYmin()));
        String formattedZDifference = getFormattedNumber((box.getZmax() - box.getZmin()));

        String xLabelText = String.format(Locale.US, "X [%s , %s] delta %s", formattedXmin, formattedXmax, formattedXDifference).replace("delta", Symbols.DELTA);
        String yLabelText = String.format(Locale.US, "Y [%s , %s] delta %s", formattedYmin, formattedYmax, formattedYDifference).replace("delta", Symbols.DELTA);
        String zLabelText = String.format(Locale.US, "Z [%s , %s] delta %s", formattedZmin, formattedZmax, formattedZDifference).replace("delta", Symbols.DELTA);
        
        xlabel.setText(xLabelText);
        ylabel.setText(yLabelText);
        zlabel.setText(zLabelText);

        return box;
    }

    private boolean isEmpty(BoundingBox box) {
        boolean xok = box.getXmin() == Double.MAX_VALUE && box.getXmax() == -Double.MAX_VALUE;
        boolean yok = box.getYmin() == Double.MAX_VALUE && box.getYmax() == -Double.MAX_VALUE;
        boolean zok = box.getZmin() == Double.MAX_VALUE && box.getZmax() == -Double.MAX_VALUE;
        return xok && yok && zok;
    }

    private String getFormattedNumber(double d) {
        DecimalFormat formatter = null;
        if (String.valueOf(d).length() > 6) {
            formatter = new DecimalFormat("0.00E0", new DecimalFormatSymbols(Locale.US));
        } else {
            formatter = new DecimalFormat("#,###,##0.0##############", new DecimalFormatSymbols(Locale.US));
        }
        return formatter.format(d);
    }
}

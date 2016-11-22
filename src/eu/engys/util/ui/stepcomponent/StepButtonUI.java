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

package eu.engys.util.ui.stepcomponent;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;

import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.JComponent;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.plaf.basic.BasicToggleButtonUI;

public class StepButtonUI extends BasicToggleButtonUI {
		
    	Color bgColor  = new Color(200, 220, 240);
    	Color selColor = new Color(66, 100, 150);
    	
    	Color fgColor =  Color.BLACK;
    	Color selFgColor =  Color.WHITE;
    	
    	protected void installDefaults(AbstractButton b) {
    		String str = getPropertyPrefix();
    		LookAndFeel.installColorsAndFont(b, str + "background", str + "foreground", str + "font");
    	}
    	
    	public void paint(Graphics g, JComponent c) {
            StepButton b = (StepButton) c;
            boolean isFirst = b.isFirst();
            boolean isLast = b.isLast();
            
            ButtonModel model = b.getModel();
    	
            Dimension size = b.getSize();
            FontMetrics fm = g.getFontMetrics();

            //Insets i = c.getInsets();

            Rectangle viewRect = new Rectangle(size);

//            viewRect.x += i.left;
//            viewRect.y += i.top;
//            viewRect.width -= (i.right + viewRect.x);
//            viewRect.height -= (i.bottom + viewRect.y);

            Rectangle iconRect = new Rectangle();
            Rectangle textRect = new Rectangle();

            Font f = c.getFont();
            g.setFont(f);

            // layout the text and icon
			String text = SwingUtilities.layoutCompoundLabel(c, fm, b.getText(), b.getIcon(), b.getVerticalAlignment(), b.getHorizontalAlignment(), b.getVerticalTextPosition(), b.getHorizontalTextPosition(), viewRect, iconRect, textRect, b.getText() == null ? 0 : b.getIconTextGap());

            if (model.isArmed() && model.isPressed() || model.isSelected()) {
                paintButton(g,b,viewRect, selColor, isFirst, isLast);
            } else if (model.isRollover()) {
            	paintButton(g,b,viewRect, Color.ORANGE, isFirst, isLast);
            } else if (!b.isEnabled()) {
            	paintButton(g,b,viewRect, Color.LIGHT_GRAY, isFirst, isLast);
            } else {
            	paintButton(g,b,viewRect, bgColor, isFirst, isLast);
            }
    	
            // Paint the Icon
//            if(b.getIcon() != null) { 
//                paintIcon(g, b, iconRect);
//            }
    	
            // Draw the Text
            if(text != null && !text.equals("")) {
            	paintText(g, b, textRect, text);
            }
    	
//            // draw the dashed focus line.
//            if (b.isFocusPainted() && b.hasFocus()) {
//    	    paintFocus(g, b, viewRect, textRect, iconRect);
//            }
        }

    	protected void paintText(Graphics g, AbstractButton b, Rectangle textRect, String text) {}
    	
    	private void paintButton(Graphics g, StepButton b, Rectangle viewRect, Color color, boolean isFirst, boolean isLast) {
    		Polygon poly = new Polygon();
    		
    		int BoRDo = b.getBorderWidth();
    		int BRD = BoRDo/2;
    		int DRIFT = b.getDrift();
    		
//			if (isFirst)
//				poly.addPoint(viewRect.x + BRD + DRIFT, viewRect.y + BRD);
//			else
    		poly.addPoint(viewRect.x + BRD, viewRect.y + BRD);
				
			if (isLast)	{
				poly.addPoint(viewRect.x + viewRect.width - 2*BRD, viewRect.y + BRD);
				poly.addPoint(viewRect.x + viewRect.width - 2*BRD, viewRect.y + viewRect.height - 2*BRD);
			} else {
				poly.addPoint(viewRect.x + viewRect.width - DRIFT - 2*BRD, viewRect.y + BRD);
				poly.addPoint(viewRect.x + viewRect.width - 2*BRD, viewRect.y + viewRect.height/2);
				poly.addPoint(viewRect.x + viewRect.width - DRIFT - 2*BRD, viewRect.y + viewRect.height - 2*BRD);
			}
			
//			if (isFirst) 
//				poly.addPoint(viewRect.x + BRD + DRIFT, viewRect.y + viewRect.height - BRD);
//			else
				poly.addPoint(viewRect.x + BRD, viewRect.y + viewRect.height - 2*BRD);

			Graphics2D g2 = (Graphics2D) g;
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    		
			g2.setColor(color);
    		g2.fillPolygon(poly);
    		
    		g.setColor(Color.WHITE);
    		g2.setStroke(new BasicStroke(BoRDo));
    		g.drawPolygon(poly);

    		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
    	}
    	
        protected void paintIcon(Graphics g, AbstractButton b, Rectangle iconRect) {}
    }

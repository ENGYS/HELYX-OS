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

package eu.engys.util.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JRadioButton;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

/**
 * Special titled border that includes a component in the title area
 */
public class ComponentTitledBorder extends TitledBorder {
	JComponent component;
	//Border border;

	public ComponentTitledBorder(Border border, JComponent component) {
		this(border, component, LEFT, TOP);
	}

	public ComponentTitledBorder(Border border, JComponent component, int titleJustification, int titlePosition) {
		//TitledBorder needs border, title, justification, position, font, and color
		super(border, null, titleJustification, titlePosition, null, null);
		this.component = component;
		if (border == null) {
			this.border = super.getBorder();
		}
	}

	@Override
	public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
		Rectangle borderR = new Rectangle(x + EDGE_SPACING, y + EDGE_SPACING, width - (EDGE_SPACING * 2), height - (EDGE_SPACING * 2));
		Insets borderInsets;
		if (border != null) {
			borderInsets = border.getBorderInsets(c);
		} else {
			borderInsets = new Insets(0, 0, 0, 0);
		}

		Rectangle rect = new Rectangle(x, y, width, height);
		Insets insets = getBorderInsets(c);
		Rectangle compR = getComponentRect(rect, insets);
		int diff;
		switch (titlePosition) {
		case ABOVE_TOP:
			diff = compR.height + TEXT_SPACING;
			borderR.y += diff;
			borderR.height -= diff;
			break;
		case TOP:
		case DEFAULT_POSITION:
			diff = insets.top / 2 - borderInsets.top - EDGE_SPACING;
			borderR.y += diff;
			borderR.height -= diff;
			break;
		case BELOW_TOP:
		case ABOVE_BOTTOM:
			break;
		case BOTTOM:
			diff = insets.bottom / 2 - borderInsets.bottom - EDGE_SPACING;
			borderR.height -= diff;
			break;
		case BELOW_BOTTOM:
			diff = compR.height + TEXT_SPACING;
			borderR.height -= diff;
			break;
		}
		border.paintBorder(c, g, borderR.x, borderR.y, borderR.width, borderR.height);
		Color col = g.getColor();
		g.setColor(new Color(255, 255, 255, 0));
		g.fillRect(compR.x, compR.y, compR.width, compR.height);
		g.setColor(col);
	}

	public Insets getBorderInsets(Component c, Insets insets) {
		Insets borderInsets;
		if (border != null) {
			borderInsets = border.getBorderInsets(c);
		} else {
			borderInsets = new Insets(0, 0, 0, 0);
		}
		insets.top = EDGE_SPACING + TEXT_SPACING + borderInsets.top;
		insets.right = EDGE_SPACING + TEXT_SPACING + borderInsets.right;
		insets.bottom = EDGE_SPACING + TEXT_SPACING + borderInsets.bottom;
		insets.left = EDGE_SPACING + TEXT_SPACING + borderInsets.left;

		if (c == null || component == null) {
			return insets;
		}

		int compHeight = component.getPreferredSize().height;

		switch (titlePosition) {
		case ABOVE_TOP:
			insets.top += compHeight + TEXT_SPACING;
			break;
		case TOP:
		case DEFAULT_POSITION:
			insets.top += Math.max(compHeight, borderInsets.top) - borderInsets.top;
			break;
		case BELOW_TOP:
			insets.top += compHeight + TEXT_SPACING;
			break;
		case ABOVE_BOTTOM:
			insets.bottom += compHeight + TEXT_SPACING;
			break;
		case BOTTOM:
			insets.bottom += Math.max(compHeight, borderInsets.bottom) - borderInsets.bottom;
			break;
		case BELOW_BOTTOM:
			insets.bottom += compHeight + TEXT_SPACING;
			break;
		}
		return insets;
	}

	public JComponent getTitleComponent() {
		return component;
	}

	public void setTitleComponent(JComponent component) {
		this.component = component;
	}

	public Rectangle getComponentRect(Rectangle rect, Insets borderInsets) {
		Dimension compD = component.getPreferredSize();
		Rectangle compR = new Rectangle(0, 0, compD.width, compD.height);
		switch (titlePosition) {
		case ABOVE_TOP:
			compR.y = EDGE_SPACING;
			break;
		case TOP:
		case DEFAULT_POSITION:
			if (component instanceof JButton) {
				compR.y = EDGE_SPACING + (borderInsets.top - EDGE_SPACING - TEXT_SPACING - compD.height) / 2;
			} else if (component instanceof JRadioButton) {
				compR.y = (borderInsets.top - EDGE_SPACING - TEXT_SPACING - compD.height) / 2;
			} else if (component instanceof JCheckBox) {
				compR.y = (borderInsets.top - EDGE_SPACING - TEXT_SPACING - compD.height) / 2;
			}
			break;
		case BELOW_TOP:
			compR.y = borderInsets.top - compD.height - TEXT_SPACING;
			break;
		case ABOVE_BOTTOM:
			compR.y = rect.height - borderInsets.bottom + TEXT_SPACING;
			break;
		case BOTTOM:
			compR.y = rect.height - borderInsets.bottom + TEXT_SPACING + (borderInsets.bottom - EDGE_SPACING - TEXT_SPACING - compD.height) / 2;
			break;
		case BELOW_BOTTOM:
			compR.y = rect.height - compD.height - EDGE_SPACING;
			break;
		}
		switch (titleJustification) {
		case LEFT:
		case DEFAULT_JUSTIFICATION:
			//compR.x = TEXT_INSET_H + borderInsets.left;
			compR.x = TEXT_INSET_H + borderInsets.left - EDGE_SPACING;
			break;
		case RIGHT:
			compR.x = rect.width - borderInsets.right - TEXT_INSET_H - compR.width;
			break;
		case CENTER:
			compR.x = (rect.width - compR.width) / 2;
			break;
		}
		return compR;
	}
}

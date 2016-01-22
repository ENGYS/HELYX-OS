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


package eu.engys.util.ui.stepcomponent;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JComponent;
import javax.swing.JToggleButton;
import javax.swing.border.Border;

class StepButton extends JToggleButton {

	private final int drift;
	private final int borderWidth;
	private final int buttonHeight;
	private final boolean first;
	private final boolean last;

	private JComponent title;
	private JComponent description;

	private boolean changeForeground() {
		return model.isArmed() && model.isPressed() || model.isSelected();
	}

	public StepButton(String title, String text, int drift, int borderWidth, int buttonHeight) {
		this(title, text, drift, borderWidth, buttonHeight, false, false);
	}

	public StepButton(String title, String text, int drift, int borderWidth, int buttonHeight, boolean first, boolean last) {
		super();
		this.drift = drift;
		this.borderWidth = borderWidth;
		this.buttonHeight = buttonHeight;
		this.first = first;
		this.last = last;
		setName(title);
		
		configureTitle(title);
		if (text != null) 
			configureDescription(text);
		setActionCommand(title);
		configureButton();
	}

	public void configureButton() {
		setLayout(new GridBagLayout());
		setRolloverEnabled(true);
		Insets in = new Insets(0, first? getDrift() : 2*getDrift() , 0, first? getDrift() : getDrift());

		if (description != null) {
			add(title,       new GridBagConstraints(0,0, 1,1, 1.0, 1.0, GridBagConstraints.SOUTHWEST, GridBagConstraints.HORIZONTAL, in, 0,0));
			add(description, new GridBagConstraints(0,1, 1,1, 1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, in, 0,0));
		} else {
			add(title,       new GridBagConstraints(0,0, 1,1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, in, 0,0));
		}
	}

	private void configureDescription(String text) {
		description = new MultiLineLabel(text) {
			public Color getForeground() {
				return changeForeground()? Color.WHITE : Color.BLACK;
			}
		};
	}

	private void configureTitle(String text) {
//		title = new RichJLabel(text, 0) {
//			public Color getForeground() {
//				return changeForeground()? Color.WHITE : Color.BLACK;
//			};
//			
//			@Override
//			public Color getLeft_color() {
//				return changeForeground()? Color.DARK_GRAY : super.getLeft_color();
//			}
//			
//			@Override
//			public Color getRight_color() {
//				return changeForeground()? Color.BLACK : super.getRight_color();
//			}
//		};
//		title.setLeftShadow(1, 1, Color.white);
//		title.setRightShadow(1, 1, Color.lightGray);
//		
//		title.setForeground(Color.blue);
//		title.setFont(title.getFont().deriveFont(20f));
		title = new MultiLineLabel(text, MultiLineLabel.CENTER) {
			public Color getForeground() {
				return changeForeground()? Color.WHITE : Color.BLACK;
			}
		};
		//title.setFont(getFont().deriveFont(getFont().getSize2D()+4));
		//title.setBorder(BorderFactory.createLineBorder(Color.RED));
	}

	public void updateUI() {
		setUI(new StepButtonUI());
	}

	@Override
	public Dimension getPreferredSize() {
		Dimension d = super.getPreferredSize();
		return new Dimension(d.width, buttonHeight);
	}

	public boolean isFirst() {
		return first;
	}

	public boolean isLast() {
		return last;
	}

	public int getDrift() {
		return drift;
	}

	public int getBorderWidth() {
		return borderWidth;
	}

	@Override
	public void setBorder(Border border) {
		throw new IllegalStateException("Don't use this method");
	}
	
	@Override
	public Border getBorder() {
		return null;
	}
}

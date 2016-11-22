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

package eu.engys.util.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.UIManager;

import eu.engys.util.ui.builder.PanelBuilder;

public class CheckBoxPanel extends JPanel {

	private JCheckBox titleComponent; // displayed in the titled border

	private PanelBuilder builder;

	public CheckBoxPanel(PanelBuilder builder, JCheckBox checkBox) {
		this.builder = builder;
		this.titleComponent = checkBox;
		
		layoutComponents();
	}

	private void layoutComponents() {
		setLayout(new BorderLayout());

		add(titleComponent, BorderLayout.CENTER);
		add(builder.getPanel(), BorderLayout.CENTER);

		setBorder(new ComponentTitledBorder(null, titleComponent));
		
		setupTitleComponent();
		placeTitleComponent();
	}

	private void placeTitleComponent() {
		Insets insets = this.getInsets();
		Rectangle containerRectangle = this.getBounds();
		Rectangle componentRectangle = ((ComponentTitledBorder) getBorder()).getComponentRect(containerRectangle, insets);
		titleComponent.setBounds(componentRectangle);
	}

	private void setupTitleComponent() {
		Font font = BorderFactory.createTitledBorder("").getTitleFont();
		Color color = BorderFactory.createTitledBorder("").getTitleColor();
		color = UIManager.getColor("TitledBorder.titleColor");

		titleComponent.setFont(font);
		titleComponent.setForeground(color);
		titleComponent.setContentAreaFilled(false);

		titleComponent.addActionListener(new CheckBoxPanel.EnableDisableAction());
	}

	private class EnableDisableAction implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			builder.setEnabled(titleComponent.isSelected());
		}
	}
	
	
}

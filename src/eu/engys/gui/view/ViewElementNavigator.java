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


package eu.engys.gui.view;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

import eu.engys.gui.GUIPanel;
import eu.engys.util.ui.stepcomponent.FlatButtonUI;

public class ViewElementNavigator extends JPanel {

	private ViewElementPanel viewElementPanel;
	private ButtonGroup bg;
	private Map<String, AbstractButton> buttonsMap = new HashMap<String, AbstractButton>();

	public ViewElementNavigator(ViewElementPanel viewElementPanel) {
		super();
		setName("view.element.navigator");
		this.viewElementPanel = viewElementPanel;
		layoutComponents();
	}
	
	private void layoutComponents() {
		setLayout(new GridLayout(0, 1, 4, 4));
		bg = new ButtonGroup() {
			public void setSelected(ButtonModel m, boolean b) {
				if (b) {
					super.setSelected(m, b);
				} else {
					clearSelection();
				}
			}
		};
		Set<GUIPanel> panels = viewElementPanel.getPanels();
		GUIPanel[] panelsArray = panels.toArray(new GUIPanel[panels.size()]);
		for (int i=0; i<panelsArray.length; i++) {
			GUIPanel guiPanel = panelsArray[i];
			String title = guiPanel.getKey();
			final JToggleButton button = new JToggleButton(new AbstractAction(title) {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (e.getSource() instanceof AbstractButton && ((AbstractButton)e.getSource()).isSelected())
						viewElementPanel.selectPanel(e.getActionCommand());
//					else
//						viewElementPanel.hidePanel();
				}
			}) {
				
				public void updateUI() {
					setUI(new FlatButtonUI());
				}
				
				@Override
				public Dimension getPreferredSize() {
					return new Dimension(super.getPreferredSize().width, 30);
				}
			};
			button.setName(title);
			buttonsMap.put(title, button);
			add(button);
			bg.add(button);
		}
		if (bg.getElements().hasMoreElements()) 
			bg.getElements().nextElement().setSelected(true);
	}

	public void selectPanel(String key) {
		buttonsMap.get(key).setSelected(true);
	}
}

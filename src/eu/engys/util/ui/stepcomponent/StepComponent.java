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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.ButtonGroup;
import javax.swing.DefaultSingleSelectionModel;
import javax.swing.JPanel;

public class StepComponent extends JPanel {
	
	private static final int HEIGHT = 30;
	private static final int DRIFT = 30;
	private static final int BRD = 4;
	
	private final ButtonGroup bg = new ButtonGroup();
	private final ArrayList<StepButton> buttons = new ArrayList<StepButton>();
	
	private int drift = DRIFT;
	private int borderWidth = BRD;
	private int buttonHeight = HEIGHT;
	
	private DefaultSingleSelectionModel selectionModel;
	private SelectionActionListener selectionListener = new SelectionActionListener();
	
	public StepComponent() {
		this(DRIFT, BRD, HEIGHT);
	}

	public StepComponent(int drift, int borderWidth, int height) {
//		super(new FlowLayout(FlowLayout.CENTER, -2*drift, 0));
		super(new StepComponentLayout(drift));
		this.drift = drift;
		this.borderWidth = borderWidth;
		this.buttonHeight = height;
		setOpaque(false);
		this.selectionModel = new DefaultSingleSelectionModel();
	}

	public void addStep(String title, String text) {
		addButton(new StepButton(title, text, drift, borderWidth, buttonHeight));
	}
	
	public void addFirst(String title, String text) {
		addButton(new StepButton(title, text, drift, borderWidth, buttonHeight, true, false));
	}
	
	public void addLast(String title, String text) {
		addButton(new StepButton(title, text, drift, borderWidth, buttonHeight, false, true));
	}

	private void addButton(StepButton button) {
		bg.add(button);
		buttons.add(button);
		add(button);
		button.addActionListener(selectionListener);
	}
	
	public String getSelectedStep() {
		int index = selectionModel.getSelectedIndex();
		if (index < 0) return null; 
		return buttons.get(index).getActionCommand();
	}
	
	public void setSelectedStep(String actionCommand) {
		for(int i=0; i<buttons.size(); i++) {
			StepButton button = buttons.get(i);
			if (button.getActionCommand().equals(actionCommand)) {
				setSelectedIndex(i);
				return;
			}
		}
	}
	
	public void setSelectedIndex(int index) {
		selectionModel.setSelectedIndex(index);
		bg.setSelected(buttons.get(index).getModel(), true);
	}
	
	public void setEnabled(int index, boolean b) {
		buttons.get(index).setEnabled(b);
	}

	public void setDisableBut(Integer... index) {
		setEnabledAll(false);
		
		for(int i=0; i<index.length; i++) {
			StepButton button = buttons.get(index[i]);
			button.setEnabled(true);
		}
		
	}

	public void setEnabledAll(boolean b) {
		for(int i=0; i<buttons.size(); i++) {
			StepButton button = buttons.get(i);
			button.setEnabled(b);
		}
	}
	
	public DefaultSingleSelectionModel getSelectionModel() {
		return selectionModel;
	}
	
	class SelectionActionListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() instanceof StepButton) {
				int index = buttons.indexOf(e.getSource());
				selectionModel.setSelectedIndex(index);
			}
		}
	}
}

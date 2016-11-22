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
package eu.engys.gui.view;

import java.util.Set;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import eu.engys.gui.GUIPanel;
import eu.engys.util.ui.stepcomponent.StepComponent;

public class ViewElementTopNavigator extends StepComponent {

    private ViewElementPanelTopNavigator viewElementPanel;

    public ViewElementTopNavigator(ViewElementPanelTopNavigator viewElementPanel) {
        super(15, 2, 60);
        this.viewElementPanel = viewElementPanel;
        load();
    }

    private void load() {
        Set<GUIPanel> panels = viewElementPanel.getPanels();
        GUIPanel[] panelsArray = panels.toArray(new GUIPanel[panels.size()]);
        for (int i = 0; i < panelsArray.length; i++) {
            GUIPanel guiPanel = panelsArray[i];
            String title = guiPanel.getKey();
            if (i == 0) {
                addFirst(title, null);
            } else if (i == panelsArray.length - 1) {
                addLast(title, null);
            } else {
                addStep(title, null);
            }
        }
        getSelectionModel().addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                GUIPanel selectedPanel = viewElementPanel.getSelectedPanel();
                if (selectedPanel != null && !selectedPanel.getKey().equals(getSelectedStep())) {
                    viewElementPanel.selectPanel(getSelectedStep());
                    if (!viewElementPanel.getSelectedPanel().getKey().equals(getSelectedStep())) {
                        selectPanel(selectedPanel.getKey());
                    }
                } else {
                    viewElementPanel.selectPanel(getSelectedStep());
                }
            }
        });
    }

    public void selectPanel(String key) {
        setSelectedStep(key);
    }

    public void clear() {
        getSelectionModel().clearSelection();
    }
}

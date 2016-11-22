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
package eu.engys.gui.casesetup.solution.panels;

import static eu.engys.util.ui.UiUtil.NONE_LABEL;

import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractButton;
import javax.swing.JComponent;

import eu.engys.core.project.state.MultiphaseModel;
import eu.engys.util.ui.ChooserPanel;

public class MultiphaseChooserPanel {

    private ChooserPanel chooserPanel;
    private Map<String, MultiphaseModel> solvers = new HashMap<>();

    public MultiphaseChooserPanel() {
        chooserPanel = new ChooserPanel("", false);
    }

    public void addMultiphaseChoice(MultiphaseModel multiphase) {
        chooserPanel.addChoice(multiphase.getLabel());
        solvers.put(multiphase.getLabel(), multiphase);
    }

    public MultiphaseModel getSelectedMultiphase() {
        String selectedState = chooserPanel.getSelectedState();
        return selectedState == NONE_LABEL ? MultiphaseModel.OFF : solvers.get(selectedState);
    }

    public void select(MultiphaseModel model) {
        chooserPanel.select(model.getLabel());
    }

    public void selectNone() {
        chooserPanel.selectNone();
    }

    public void addListener(PropertyChangeListener listener) {
        chooserPanel.addPropertyChangeListener(listener);
    }

    public void removeListener(PropertyChangeListener listener) {
        chooserPanel.removePropertyChangeListener(listener);
    }

    public boolean isMultiphaseOff() {
        return chooserPanel.getSelectedState().equals(MultiphaseModel.OFF_LABEL);
    }

    public void setMultiphaseOff() {
        chooserPanel.select(MultiphaseModel.OFF_LABEL);
    }

    public JComponent getChooserPanel() {
        return chooserPanel;
    }

    public void enableChoice(MultiphaseModel mm) {
        AbstractButton button = chooserPanel.getButton(mm.getLabel());
        if (!button.isEnabled()) {
            button.setEnabled(true);
        }
    }

    public void disableChoice(MultiphaseModel mm) {
        AbstractButton button = chooserPanel.getButton(mm.getLabel());
        if (button.isSelected()) {
            select(MultiphaseModel.OFF);
        }
        if (button.isEnabled()) {
            button.setEnabled(false);
        }
    }
}

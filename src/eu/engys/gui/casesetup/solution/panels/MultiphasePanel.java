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

import java.awt.BorderLayout;
import java.beans.PropertyChangeListener;
import java.util.Set;

import javax.swing.JPanel;

import eu.engys.core.modules.ApplicationModule;
import eu.engys.core.modules.solutionmodelling.MultiphaseBuilder;
import eu.engys.core.project.state.MultiphaseModel;
import eu.engys.core.project.state.SolutionState;
import eu.engys.core.project.state.State;
import eu.engys.util.ui.ComponentsFactory;
import eu.engys.util.ui.builder.PanelBuilder;
import eu.engys.util.ui.textfields.SpinnerField;

public abstract class MultiphasePanel extends JPanel implements MultiphaseBuilder {

    public static final String PHASES_LABEL = "Phases";
    private static final String MULTIPHASE = "Multiphase";

    protected SpinnerField phasesNumber;
    protected MultiphaseChooserPanel multiphaseChooser;
    protected PanelBuilder multiphaseBuilder;
    private PropertyChangeListener listener;

    public MultiphasePanel(Set<ApplicationModule> modules) {
        super(new BorderLayout());

        multiphaseChooser = new MultiphaseChooserPanel();
        multiphaseChooser.addMultiphaseChoice(MultiphaseModel.OFF);

        phasesNumber = ComponentsFactory.spinnerField(2, Integer.MAX_VALUE);
        phasesNumber.setEnabled(false);

        PanelBuilder phaseBuilderLeft = new PanelBuilder();
        PanelBuilder phaseBuilderRight = new PanelBuilder();
        phaseBuilderLeft.addComponent(multiphaseChooser.getChooserPanel());
        phaseBuilderRight.addComponent(PHASES_LABEL, phasesNumber);

        multiphaseBuilder = new PanelBuilder();
        multiphaseBuilder.addComponent(phaseBuilderLeft.removeMargins().getPanel(), phaseBuilderRight.removeMargins().getPanel());

        add(multiphaseBuilder.withTitle(MULTIPHASE).getPanel());

        for (ApplicationModule m : modules) {
            m.getSolutionView().buildMultiphase(this);
        }
    }

    public void updateFromState(State state) {
        MultiphaseModel multiphaseModel = state.getMultiphaseModel();
        if (multiphaseModel != null) {
            multiphaseChooser.select(multiphaseModel);
            if (multiphaseModel.isMultiphase()) {
                phasesNumber.setIntValue(Math.max(2, state.getPhases()));
            } else {
                phasesNumber.setIntValue(1);
            }
        } else {
            multiphaseChooser.selectNone();
            phasesNumber.setIntValue(1);
        }

        fixSolutionState(new SolutionState(state));
        fixMultiphase(getSelectedModel());
    }

    public abstract void fixSolutionState(SolutionState solutionState);

    public void fixMultiphase(MultiphaseModel mm) {
        if (mm.isOff()) {
            phasesNumber.setIntValue(1);
            phasesNumber.setEnabled(false);
        }
    }

    @Override
    public void addMultiphaseChoice(MultiphaseModel mm) {
        multiphaseChooser.addMultiphaseChoice(mm);
    }

    public MultiphaseChooserPanel getPhasesPanel() {
        return multiphaseChooser;
    }

    @Override
    public SpinnerField getPhasesField() {
        return phasesNumber;
    }

    public int getPhasesNumber() {
        return phasesNumber.getIntValue();
    }

    public MultiphaseModel getSelectedModel() {
        return multiphaseChooser.getSelectedMultiphase();
    }

    public void addListener() {
        multiphaseChooser.addListener(listener);
    }

    public void removeListener() {
        multiphaseChooser.removeListener(listener);
    }

    public boolean isMultiphaseOn() {
        return !isMultiphaseOff();
    }

    public boolean isMultiphaseOff() {
        return multiphaseChooser.isMultiphaseOff();
    }

    protected void setMultiphaseOff() {
        multiphaseChooser.setMultiphaseOff();
    }

    @Override
    public void enableChoice(MultiphaseModel mm) {
        multiphaseChooser.enableChoice(mm);
    }

    @Override
    public void disableChoice(MultiphaseModel mm) {
        multiphaseChooser.disableChoice(mm);
    }

    public void setListener(PropertyChangeListener listener) {
        this.listener = listener;
    }
}

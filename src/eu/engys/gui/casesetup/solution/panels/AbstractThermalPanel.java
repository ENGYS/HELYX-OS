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

package eu.engys.gui.casesetup.solution.panels;

import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

import eu.engys.core.modules.ApplicationModule;
import eu.engys.core.project.state.MultiphaseModel;
import eu.engys.core.project.state.SolutionState;
import eu.engys.core.project.state.State;
import eu.engys.core.project.state.ThermalState;
import eu.engys.util.ui.builder.PanelBuilder;

public abstract class AbstractThermalPanel extends JPanel {

    public static final String BUOYANCY = "Buoyancy";
    public static final String ENERGY = "Energy";
    public static final String THERMAL = "Thermal";

    private Set<ApplicationModule> modules;
    private ActionListener listener;
    protected JCheckBox energy;
    private JCheckBox buoyancy;

    public AbstractThermalPanel(Set<ApplicationModule> modules) {
        super(new BorderLayout());
        this.modules = modules;
        layoutComponents();
    }


    private void layoutComponents() {
        PanelBuilder builder = new PanelBuilder();
        this.energy = (JCheckBox) builder.startCheck(ENERGY);

        this.buoyancy = (JCheckBox) builder.startCheck(BUOYANCY);
        buoyancy.setName(BUOYANCY);
        builder.endCheck();

        for (ApplicationModule m : modules) {
            m.getSolutionView().buildThermal(builder);
        }
        
        builder.endCheck(false);
        
        JPanel panel = builder.getPanel();
        panel.setBorder(BorderFactory.createTitledBorder(AbstractThermalPanel.THERMAL));
        add(panel, BorderLayout.CENTER);
    }
    
    public abstract void fixEnergy(SolutionState ss, MultiphaseModel mm);

    public void fixBuoyancy(SolutionState ss, MultiphaseModel mm) {
        if (ss.areSolverTypeAndTimeAndFlowAndTurbulenceChoosen()) {
            if (ss.isCoupled()) {
                if (buoyancy.isSelected()) {
                    buoyancy.setEnabled(true);
                    buoyancy.doClick();
                }
                buoyancy.setEnabled(false);
            } else {
                boolean isTransientCompressibleLES = ss.isTransient() && ss.isCompressible() && ss.isLES();
                buoyancy.setEnabled(true);
                if (buoyancy.isSelected() && (!energy.isSelected() || isTransientCompressibleLES || ss.isHighMach())) {
                    buoyancy.doClick();
                }
                buoyancy.setEnabled(energy.isSelected() && !isTransientCompressibleLES && ss.isLowMach());
            }
        } else {
            buoyancy.setEnabled(false);
        }
    }

    public void updateEnergyFromState(State state) {
        boolean energyEnabled = energy.isEnabled();
        energy.setEnabled(true);
        if (state.isEnergy()) {
            if (!energy.isSelected()) {
                energy.doClick();
            }
        } else {
            if (energy.isSelected()) {
                energy.doClick();
            }
        }
        energy.setEnabled(energyEnabled);
        fixEnergy(new SolutionState(state), state.getMultiphaseModel());
    }

    public void updateBuoyancyFromState(State state) {
        boolean buoyancyEnabled = buoyancy.isEnabled();
        buoyancy.setEnabled(true);
        if (state.isBuoyant()) {
            if (!buoyancy.isSelected()) {
                buoyancy.doClick();
            }
        } else {
            if (buoyancy.isSelected()) {
                buoyancy.doClick();
            }
        }
        buoyancy.setEnabled(buoyancyEnabled);
        fixBuoyancy(new SolutionState(state), state.getMultiphaseModel());
    }
    
    public ThermalState getThermalState() {
        ThermalState ts = new ThermalState();
        ts.setEnergy(energy.isSelected());
        ts.setBuoyancy(buoyancy.isSelected());
        return ts;
    }
    
    public void removeListeners() {
        energy.removeActionListener(listener);
        buoyancy.removeActionListener(listener);
    }

    public void addListeners() {
        energy.addActionListener(listener);
        buoyancy.addActionListener(listener);
    }
    
    public void setThermalListener(ActionListener listener) {
        this.listener = listener;
    }
    
    public boolean isEnergySelected(){
        return energy.isSelected();
    }

    public boolean isBuoyancySelected(){
        return buoyancy.isSelected();
    }
    
}

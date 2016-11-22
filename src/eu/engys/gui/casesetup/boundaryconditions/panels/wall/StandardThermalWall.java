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

package eu.engys.gui.casesetup.boundaryconditions.panels.wall;

import static eu.engys.gui.casesetup.boundaryconditions.factories.TemperatureFactory.fixedValue;
import static eu.engys.gui.casesetup.boundaryconditions.factories.TemperatureFactory.turbulentHeatFluxTemperatureOCFD_FLUX_COMP;
import static eu.engys.gui.casesetup.boundaryconditions.factories.TemperatureFactory.turbulentHeatFluxTemperatureOCFD_POWER_COMP;
import static eu.engys.gui.casesetup.boundaryconditions.factories.TemperatureFactory.zeroGradient;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.TOTAL_HEAT_LOAD_AT_WALL_W_LABEL;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.WALL_HEAT_LABEL;

import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.dictionary.model.DictionaryModel;
import eu.engys.core.modules.boundaryconditions.BoundaryTypePanel;
import eu.engys.core.project.Model;
import eu.engys.core.project.state.State;
import eu.engys.core.project.zero.patches.BoundaryConditions;
import eu.engys.gui.casesetup.boundaryconditions.parameterspanel.ThermalParametersPanel;
import eu.engys.util.ui.builder.JComboBoxController;

public class StandardThermalWall extends ThermalParametersPanel {

    public static final String TOTAL_HEAT_LOAD_LABEL = "Total Heat Load";
    public static final String HEAT_FLUX_LABEL = "Heat Flux";
    public static final String FIXED_TEMPERATURE_LABEL = "Fixed Temperature";
    
    private DictionaryModel heatPowerModel;
	private DictionaryModel heatFluxModel;
	private DictionaryModel fixedTemperatureModel;
	private DictionaryModel zeroGradientModel;
    private JComboBoxController typeCombo;
	
    public StandardThermalWall(BoundaryTypePanel parent) {
    	super(parent);
	}
    
    @Override
    protected void init() {
    	fixedTemperatureModel = new DictionaryModel();
    	heatFluxModel = new DictionaryModel();
    	heatPowerModel = new DictionaryModel();
    	zeroGradientModel = new DictionaryModel();
    }
    
    @Override
    public void resetToDefault(Model model) {
    	fixedTemperatureModel.setDictionary(new Dictionary(fixedValue));
    	if (model != null && model.getState() != null) {
    	    fixCombo(model.getState());
    	    if(model.getState().isCompressible()){
    	        heatFluxModel.setDictionary(new Dictionary(turbulentHeatFluxTemperatureOCFD_FLUX_COMP));
    	        heatPowerModel.setDictionary(new Dictionary(turbulentHeatFluxTemperatureOCFD_POWER_COMP));
    	    }
    	}
    	zeroGradientModel.setDictionary(new Dictionary(zeroGradient));
    }

	@Override
	public void populatePanel() {
		resetToDefault(null);
        typeCombo = (JComboBoxController) builder.startChoice("Type");
        buildFixedTemperaturePanel();
        buildHeatFluxPanel();
        buildTotalHeatPanel();
        buildZeroGradientPanel();
        builder.endChoice();
    }
    
    private void buildFixedTemperaturePanel() {
        builder.startDictionary(FIXED_TEMPERATURE_LABEL, fixedTemperatureModel);
        builder.addComponent("Temperature Value [K]", fixedTemperatureModel.bindUniformDouble("value"));
        builder.endDictionary();
    }

    private void buildHeatFluxPanel() {
        builder.startDictionary(HEAT_FLUX_LABEL, heatFluxModel);
        builder.addComponent(WALL_HEAT_LABEL, heatFluxModel.bindUniformDouble("q"));
        builder.endDictionary();
    }

    private void buildTotalHeatPanel() {
        builder.startDictionary(TOTAL_HEAT_LOAD_LABEL, heatPowerModel);
        builder.addComponent(TOTAL_HEAT_LOAD_AT_WALL_W_LABEL, heatPowerModel.bindUniformDouble("q"));
        builder.endDictionary();
    }

    private void buildZeroGradientPanel() {
        builder.startDictionary("Zero Gradient", zeroGradientModel);
        builder.endDictionary();
    }
    
    @Override
    public void stateChanged(Model model) {
    	State state = model.getState();
    	
    	fixCombo(state);
    	if (state.isCompressible()) {
    	    heatFluxModel.setDictionary(new Dictionary(turbulentHeatFluxTemperatureOCFD_FLUX_COMP));
    	    heatPowerModel.setDictionary(new Dictionary(turbulentHeatFluxTemperatureOCFD_POWER_COMP));
    	}
    }
    
    private void fixCombo(State state){
        if(state.isCompressible()){
            typeCombo.clearDisabledIndexes();
        } else {
            typeCombo.addDisabledItem(HEAT_FLUX_LABEL);
            typeCombo.addDisabledItem(TOTAL_HEAT_LOAD_LABEL);
        }
        
    }

    @Override
    public void loadFromBoundaryConditions(BoundaryConditions bc) {
		Dictionary dictionary = bc.getThermal();
		Dictionary T = dictionary.subDict("T");
		if (T != null) {
			if (T.found("heatSource")) {
				String source = T.lookup("heatSource");
				if (source.equals("power")) {
					builder.selectDictionaryByModel(heatPowerModel, T);
				} else if (source.equals("flux")) {
					builder.selectDictionaryByModel(heatFluxModel, T);
				}
			} else {
				builder.selectDictionary(T);
			}
		}
	}
    
}

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

import static eu.engys.core.dictionary.Dictionary.TYPE;
import static eu.engys.core.project.zero.fields.Fields.U;
import static eu.engys.gui.casesetup.boundaryconditions.factories.VelocityFactory.fixedValueVelocity;
import static eu.engys.gui.casesetup.boundaryconditions.factories.VelocityFactory.movingWallVelocity;
import static eu.engys.gui.casesetup.boundaryconditions.factories.VelocityFactory.noSlipWall;
import static eu.engys.gui.casesetup.boundaryconditions.factories.VelocityFactory.slipWall;
import static eu.engys.gui.casesetup.boundaryconditions.factories.VelocityFactory.standardRotatingWallVelocity;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.AXIS_KEY;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.AXIS_LABEL;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.FIXED_VALUE_KEY;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.FIXED_VELOCITY_LABEL;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.MOVING_WALL_VELOCITY_KEY;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.MOVING_WALL_VELOCITY_LABEL;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.NO_SLIP_LABEL;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.OMEGA_KEY;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.OMEGA_VELOCITY_LABEL;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.ORIGIN_KEY;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.ORIGIN_LABEL;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.ROTATING_WALL_LABEL;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.ROTATING_WALL_VELOCITY_KEY;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.SLIP_KEY;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.SLIP_LABEL;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.TYPE_LABEL;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.VALUE_KEY;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.VELOCITY_TYPE_LABEL;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.WALL_TYPE_LABEL;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.buildSimpleFixedVelocityPanel;

import java.util.Arrays;

import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.dictionary.model.DictionaryModel;
import eu.engys.core.dictionary.model.DictionaryPanelBuilder;
import eu.engys.core.modules.boundaryconditions.BoundaryTypePanel;
import eu.engys.core.project.Model;
import eu.engys.core.project.zero.patches.BoundaryConditions;
import eu.engys.gui.casesetup.boundaryconditions.parameterspanel.MomentumParametersPanel;

public class StandardMomentumWall extends MomentumParametersPanel {

	private static final String MOVING_WALL = "Moving Wall";
	private static final String FIXED_WALL = "Fixed Wall";
	public static final String[] TYPE_KEYS = { "noslip", "slip" };
	public static final String[] TYPE_LABELS = { "No-slip", "Slip" };

	private DictionaryModel noSlipModel;
	private DictionaryModel slipModel;
	private DictionaryModel fixedVelocityModel;
	private DictionaryModel rotatingWallModel;
	private DictionaryModel movingWallVelocityModel;

	public StandardMomentumWall(BoundaryTypePanel parent) {
		super(parent);
	}

	@Override
	protected void init() {
		noSlipModel = new DictionaryModel();
		slipModel = new DictionaryModel();
		fixedVelocityModel = new DictionaryModel();
		rotatingWallModel = new DictionaryModel();
		movingWallVelocityModel = new DictionaryModel();
	}

	@Override
	public void resetToDefault(Model model) {
		noSlipModel.setDictionary(new Dictionary(noSlipWall));
		slipModel.setDictionary(new Dictionary(slipWall));
		fixedVelocityModel.setDictionary(new Dictionary(fixedValueVelocity));
		rotatingWallModel.setDictionary(new Dictionary(standardRotatingWallVelocity));
		movingWallVelocityModel.setDictionary(new Dictionary(movingWallVelocity));
	}

	@Override
	public void populatePanel() {
		resetToDefault(null);
		builder.startChoice(TYPE_LABEL);
		fixedWallPanel(builder);
		movingWallPanel(builder);
		builder.endChoice();
	}

	public void fixedWallPanel(DictionaryPanelBuilder builder) {
		builder.startGroup(FIXED_WALL);
		builder.startChoice(WALL_TYPE_LABEL);
		
		builder.startDictionary(NO_SLIP_LABEL, noSlipModel);
		builder.endDictionary();
		
		builder.startDictionary(SLIP_LABEL, slipModel);
		builder.endDictionary();
		
		builder.endChoice();
		builder.endGroup();
	}

	public void movingWallPanel(DictionaryPanelBuilder builder) {
		builder.startGroup(MOVING_WALL);
		builder.startChoice(VELOCITY_TYPE_LABEL);
		
		buildFixedVelocityPanel(builder);
		buildRotatingWallPanel(builder);
		buildMovingWallPanel(builder);
		
		builder.endChoice();
		builder.endGroup();
	}

	private void buildFixedVelocityPanel(DictionaryPanelBuilder builder) {
		builder.startDictionary(FIXED_VELOCITY_LABEL, fixedVelocityModel);
		buildSimpleFixedVelocityPanel(builder, fixedVelocityModel);
		builder.endDictionary();
	}

	private void buildRotatingWallPanel(DictionaryPanelBuilder builder) {
		builder.startDictionary(ROTATING_WALL_LABEL, rotatingWallModel);
		builder.addComponent(ORIGIN_LABEL, rotatingWallModel.bindPoint(ORIGIN_KEY));
		builder.addComponent(AXIS_LABEL, rotatingWallModel.bindPoint(AXIS_KEY));
		builder.addComponent(OMEGA_VELOCITY_LABEL, rotatingWallModel.bindDouble(OMEGA_KEY));
		builder.endDictionary();
	}

	private void buildMovingWallPanel(DictionaryPanelBuilder builder) {
	    builder.startDictionary(MOVING_WALL_VELOCITY_LABEL, movingWallVelocityModel);
        builder.endDictionary();
	}

	public void loadFromBoundaryConditions(BoundaryConditions bc) {
		Dictionary dictionary = bc.getMomentum();
		Dictionary uDict = dictionary.subDict(U);
//		Dictionary pDict = dictionary.subDict(P);
		if (uDict != null) {
			String U_type = uDict.lookup(TYPE);
			if (U_type.contains(SLIP_KEY)) {
				builder.selectDictionary(uDict);
			} else if (U_type.equals(FIXED_VALUE_KEY)) {
				if (uDict.found(VALUE_KEY)) {
					double[] value = uDict.lookupDoubleArray(VALUE_KEY);
					double[] zeros = new double[] { 0, 0, 0 };
					if (Arrays.equals(value, zeros)) {
						builder.selectDictionaryByModel(noSlipModel, uDict);
					} else {
						builder.selectDictionaryByModel(fixedVelocityModel, uDict);
					}
				} else {

				}
			} else if (U_type.equals(MOVING_WALL_VELOCITY_KEY) 
			        || U_type.equals(ROTATING_WALL_VELOCITY_KEY)){ 
				builder.selectDictionary(uDict);
			}
		}
	}
	
}

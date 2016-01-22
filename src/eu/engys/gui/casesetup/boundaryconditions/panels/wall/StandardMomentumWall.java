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


package eu.engys.gui.casesetup.boundaryconditions.panels.wall;

import static eu.engys.gui.casesetup.boundaryconditions.factories.VelocityFactory.fixedValueVelocity;
import static eu.engys.gui.casesetup.boundaryconditions.factories.VelocityFactory.noSlipWall;
import static eu.engys.gui.casesetup.boundaryconditions.factories.VelocityFactory.slipWall;
import static eu.engys.gui.casesetup.boundaryconditions.factories.VelocityFactory.standardRotatingWallVelocity;

import java.util.Arrays;

import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.dictionary.model.DictionaryModel;
import eu.engys.core.dictionary.model.DictionaryPanelBuilder;
import eu.engys.core.modules.boundaryconditions.BoundaryTypePanel;
import eu.engys.core.project.Model;
import eu.engys.core.project.zero.patches.BoundaryConditions;
import eu.engys.gui.casesetup.boundaryconditions.panels.MomentumParametersPanel;
import eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils;

public class StandardMomentumWall extends MomentumParametersPanel {

	private static final String MOVING_WALL = "Moving Wall";
	private static final String FIXED_WALL = "Fixed Wall";
	public static final String[] TYPE_KEYS = { "noslip", "slip" };
	public static final String[] TYPE_LABELS = { "No-slip", "Slip" };

	private DictionaryModel noSlipModel;
	private DictionaryModel slipModel;
	private DictionaryModel fixedVelocityModel;
	private DictionaryModel rotatingWallModel;

	public StandardMomentumWall(BoundaryTypePanel parent) {
		super(parent);
	}

	@Override
	protected void init() {
		noSlipModel = new DictionaryModel();
		slipModel = new DictionaryModel();
		fixedVelocityModel = new DictionaryModel();
		rotatingWallModel = new DictionaryModel();
	}

	@Override
	public void resetToDefault(Model model) {
		noSlipModel.setDictionary(new Dictionary(noSlipWall));
		slipModel.setDictionary(new Dictionary(slipWall));
		fixedVelocityModel.setDictionary(new Dictionary(fixedValueVelocity));
		rotatingWallModel.setDictionary(new Dictionary(standardRotatingWallVelocity));
	}

	@Override
	public void populatePanel() {
		resetToDefault(null);
		builder.startChoice("Type");
		fixedWallPanel(builder);
		movingWallPanel(builder);
		builder.endChoice();
	}

	public void fixedWallPanel(DictionaryPanelBuilder builder) {
		builder.startGroup(FIXED_WALL);
		builder.startChoice("Wall Type");
		
		builder.startDictionary("No-slip", noSlipModel);
		builder.endDictionary();
		
		builder.startDictionary("Slip", slipModel);
		builder.endDictionary();
		
		builder.endChoice();
		builder.endGroup();
	}

	public void movingWallPanel(DictionaryPanelBuilder builder) {
		builder.startGroup(MOVING_WALL);
		builder.startChoice("Velocity Type");
		
		buildFixedVelocityPanel(builder);
		buildRotatingWallPanel(builder);
		
		builder.endChoice();
		builder.endGroup();
	}

	private void buildFixedVelocityPanel(DictionaryPanelBuilder builder) {
		builder.startDictionary("Fixed Velocity", fixedVelocityModel);
		BoundaryConditionsUtils.buildSimpleFixedVelocityPanel(builder, fixedVelocityModel);
		builder.endDictionary();
	}

	private void buildRotatingWallPanel(DictionaryPanelBuilder builder) {
		builder.startDictionary("Rotating Wall", rotatingWallModel);
		builder.addComponent("Origin", rotatingWallModel.bindPoint("origin"));
		builder.addComponent("Axis", rotatingWallModel.bindPoint("axis"));
		builder.addComponent("Omega [rad/s]", rotatingWallModel.bindDouble("omega"));
		builder.endDictionary();
	}

	public void loadFromBoundaryConditions(String patchName, BoundaryConditions bc) {
		Dictionary dictionary = bc.getMomentum();
		Dictionary U = dictionary.subDict("U");
		Dictionary p = dictionary.subDict("p");
		if (U != null) {
			String U_type = U.lookup("type");
			if (U_type.contains("slip")) {
				builder.selectDictionary(U);
			} else if (U_type.equals("fixedValue")) {
				if (U.found("value")) {
					double[] value = U.lookupDoubleArray("value");
					double[] zeros = new double[] { 0, 0, 0 };
					if (Arrays.equals(value, zeros)) {
						builder.selectDictionaryByModel(noSlipModel, U);
					} else {
						builder.selectDictionaryByModel(fixedVelocityModel, U);
					}
				} else {

				}
			} else if (U_type.equals("tangentialVelocity") || U_type.equals("timeVaryingUniformFixedValue") || U_type.equals("rotatingWallVelocity") || U_type.equals("wheelVelocity")) {
				builder.selectDictionary(U);
			}
		}
	}
	
}

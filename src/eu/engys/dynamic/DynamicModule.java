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
package eu.engys.dynamic;

import static eu.engys.core.dictionary.Dictionary.TYPE;
import static eu.engys.core.project.zero.fields.Fields.POINT_DISPLACEMENT;
import static eu.engys.core.project.zero.fields.Fields.U;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.CALCULATED_KEY;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.FIXED_VALUE_KEY;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.MOVING_WALL_VELOCITY_KEY;

import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;

import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.modules.ApplicationModuleAdapter;
import eu.engys.core.modules.CaseSetupReader;
import eu.engys.core.modules.CaseSetupWriter;
import eu.engys.core.modules.ModuleDefaults;
import eu.engys.core.modules.ModulePanel;
import eu.engys.core.modules.cellzones.CellZonesView;
import eu.engys.core.modules.solutionmodelling.SolutionView;
import eu.engys.core.modules.tree.TreeView;
import eu.engys.core.project.Model;
import eu.engys.core.project.defaults.DefaultsProvider;
import eu.engys.core.project.state.Solver;
import eu.engys.core.project.state.SolverFamily;
import eu.engys.core.project.state.State;
import eu.engys.core.project.state.StateBuilder;
import eu.engys.core.project.zero.fields.Field;
import eu.engys.core.project.zero.fields.Fields;
import eu.engys.core.project.zero.fields.FieldsDefaults;
import eu.engys.core.project.zero.patches.BoundaryConditions;
import eu.engys.dynamic.cellzones.DynamicCellZonesView;
import eu.engys.dynamic.data.DynamicData;
import eu.engys.dynamic.domain.MeshRefineDynamicPanel;
import eu.engys.dynamic.domain.SixDoFDynamicPanel;
import eu.engys.dynamic.domain.SolidBodyDynamicPanel;
import eu.engys.gui.DefaultGUIPanel;

public class DynamicModule extends ApplicationModuleAdapter {

    public static final String DYNAMIC_KEY = "dynamic";
    public static final String PIMPLE_DYM_FOAM = "pimpleDyMFoam";
    public static final String RHO_PIMPLE_DYM_FOAM = "rhoPimpleDyMFoam";
    public static final String RHO_CENTRAL_DYM_FOAM = "rhoCentralDyMFoam";

    protected SolutionView solutionView;
    protected CellZonesView cellZonesView;

    private Model model;
    private DefaultsProvider defaults;
    private Dictionary dynamicMeshDict;

    private CaseSetupReader caseSetupReader;
    private CaseSetupWriter caseSetupWriter;

    private DynamicReader reader;

    private DynamicData dynamicData;
    protected TreeView treeView;
    protected DefaultGUIPanel sixDoFPanel;
    protected DefaultGUIPanel solidBodyPanel;
    private DefaultGUIPanel refinePanel;

    @Inject
    public DynamicModule(Model model) {
        this.model = model;
        this.dynamicData = new DynamicData();

        this.solutionView = new DynamicSolutionView(model, this);
        this.cellZonesView = new DynamicCellZonesView(model, this);

        this.defaults = new ModuleDefaults(this, model.getDefaults(), null);

        this.reader = new DynamicReader(model, this);
        this.reader.readDynamicMeshDict();

        this.caseSetupReader = new DynamicCaseSetupReader(model, this);
        this.caseSetupWriter = new DynamicCaseSetupWriter(model, this);

        this.sixDoFPanel = new SixDoFDynamicPanel(model, this);
        this.solidBodyPanel = new SolidBodyDynamicPanel(model, this);
        this.refinePanel = new MeshRefineDynamicPanel(model, this);

        this.treeView = new DynamicTreeView(this, sixDoFPanel, solidBodyPanel, refinePanel);
    }

    @Override
    public String getName() {
        return DYNAMIC_KEY;
    }

    @Override
    public void updateSolverFamilies(State state, Set<SolverFamily> families) {
        if (state.isDynamic()) {
            if (state.getSolverType().isSegregated()) {
                if (state.isLowMach()) {
                    if (state.isTransient()) {
                        if (state.isIncompressible()) {
                            if (state.isEnergy() || state.getMultiphaseModel().isMultiphase()) {
                                // NONE
                            } else {
                                if (families.contains(SolverFamily.PISO)) {
                                    families.remove(SolverFamily.PISO);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void updateSolver(State state) {
        if (!state.getMultiphaseModel().isMultiphase()) {
            if (state.getSolverFamily().isPimple()) {
                if (state.isCompressible()) {
                    if (!state.isBuoyant()) {
                        if (state.isDynamic()) {
                            if (state.isHighMach()) {
                                state.setSolver(new Solver(RHO_CENTRAL_DYM_FOAM));
                            } else {
                                state.setSolver(new Solver(RHO_PIMPLE_DYM_FOAM));
                            }
                        }
                    }
                } else if (state.isIncompressible()) {
                    if (!state.isEnergy()) {
                        if (state.isDynamic()) {
                            state.setSolver(new Solver(PIMPLE_DYM_FOAM));
                        }
                    }
                }
            } else if (state.getSolverFamily().isCentral()) {
                if (state.isDynamic()) {
                    if (state.isHighMach()) {
                        state.setSolver(new Solver(RHO_CENTRAL_DYM_FOAM));
                    } else {
                        state.setSolver(new Solver(PIMPLE_DYM_FOAM));
                    }
                }
            }
        }
    }

    @Override
    public CellZonesView getCellZonesView() {
        return cellZonesView;
    }

    @Override
    public void loadState() {
        reader.loadState();
    }

    @Override
    public void save() {
    }

    @Override
    public void read() {
        reader.readDynamicMeshDict();
    }

    @Override
    public void write() {
        new DynamicWriter(model, this).write();
    }

    @Override
    public CaseSetupReader getCaseSetupReader() {
        return caseSetupReader;
    }

    @Override
    public CaseSetupWriter getCaseSetupWriter() {
        return caseSetupWriter;
    }

    @Override
    public void saveDefaultsToProject() {
        if (model.getState().isDynamic()) {
            StateBuilder.saveDefaultsToProject(model, defaults);
        }
    }

    @Override
    public void saveToBoundaryConditions(BoundaryConditions bc) {
        if (dynamicData.getAlgorithm().getType().is6DOF()) {
            boolean isMovingWallVelocity = bc.getMomentum().found(U) && bc.getMomentum().subDict(U).found(TYPE) && bc.getMomentum().subDict(U).lookupString(TYPE).equals(MOVING_WALL_VELOCITY_KEY);
            if (isMovingWallVelocity) {
                bc.getDisplacement().subDict(POINT_DISPLACEMENT).add(TYPE, CALCULATED_KEY);
            } else {
                boolean isCalculated = bc.getDisplacement().found(POINT_DISPLACEMENT) && bc.getDisplacement().subDict(POINT_DISPLACEMENT).found(TYPE) && bc.getDisplacement().subDict(POINT_DISPLACEMENT).lookupString(TYPE).equals(CALCULATED_KEY);
                if(isCalculated){
                    bc.getDisplacement().subDict(POINT_DISPLACEMENT).add(TYPE, FIXED_VALUE_KEY);
                }
            }
        } else {
            boolean isCalculated = bc.getDisplacement().found(POINT_DISPLACEMENT) && bc.getDisplacement().subDict(POINT_DISPLACEMENT).found(TYPE) && bc.getDisplacement().subDict(POINT_DISPLACEMENT).lookupString(TYPE).equals(CALCULATED_KEY);
            if(isCalculated){
                bc.getDisplacement().subDict(POINT_DISPLACEMENT).add(TYPE, FIXED_VALUE_KEY);
            }
        }

    }

    @Override
    public Set<ModulePanel> getCaseSetupPanels() {
        Set<ModulePanel> panels = new HashSet<>();
        panels.add(sixDoFPanel);
        panels.add(solidBodyPanel);
        panels.add(refinePanel);
        return panels;
    }

    @Override
    public TreeView getTreeView() {
        return treeView;
    }

    @Override
    public Fields loadDefaultsFields(String region) {
        if (model.getState().isDynamic()) {
            return FieldsDefaults.loadFieldsFromDefaults(model.getProject().getBaseDir(), model.getState(), defaults, model.getPatches(), region);
        } else {
            return new Fields();
        }
    }

    @Override
    public SolutionView getSolutionView() {
        return solutionView;
    }

    public DynamicData getDynamicData() {
        return dynamicData;
    }

    public void setDynamicData(DynamicData dynamicData) {
        this.dynamicData = dynamicData;
    }

    public Dictionary getDynamicMeshDict() {
        return dynamicMeshDict;
    }

    public void setDynamicMeshDict(Dictionary dynamicMeshDict) {
        this.dynamicMeshDict = dynamicMeshDict;
    }

    @Override
    public boolean isFieldInitialisationVetoed(Field field) {
        return field.getName().equals(Fields.P_RGH) && model.getState().isDynamic() && !model.getState().getMultiphaseModel().isMultiphase();
    }
}

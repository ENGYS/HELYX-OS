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
package eu.engys.core.modules;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.swing.JTabbedPane;

import eu.engys.core.modules.boundaryconditions.BoundaryConditionsView;
import eu.engys.core.modules.cellzones.CellZonesView;
import eu.engys.core.modules.materials.MaterialsView;
import eu.engys.core.modules.residuals.ResidualsViewConfigurator;
import eu.engys.core.modules.tree.TreeView;
import eu.engys.core.project.state.SolverFamily;
import eu.engys.core.project.state.State;
import eu.engys.core.project.system.monitoringfunctionobjects.Parser;
import eu.engys.core.project.system.monitoringfunctionobjects.ParserView;
import eu.engys.core.project.zero.fields.Field;
import eu.engys.core.project.zero.patches.BoundaryConditions;

public abstract class ApplicationModuleAdapter implements ApplicationModule {

    @Override
    public void checkLicense() {
    }
    
    @Override
    public void checkIfCanOpenCase() {
    }
    
    @Override
    public TreeView getTreeView() {
        return null;
    }

    @Override
    public void deleteUselessLogFiles() {
    }

    @Override
    public Parser createResidualsParser() {
        return null;
    }

    @Override
    public List<Parser> createParsers() {
        return Collections.<Parser> emptyList();
    }

    @Override
    public List<ParserView> getParserViews() {
        return Collections.<ParserView> emptyList();
    }

    @Override
    public ResidualsViewConfigurator createResidualsViewConfigurator(JTabbedPane pane) {
        return null;
    }

    @Override
    public MaterialsView getMaterialsView() {
        return null;
    }

    @Override
    public BoundaryConditionsView getBoundaryConditionsView() {
        return null;
    }

    @Override
    public CellZonesView getCellZonesView() {
        return null;
    }

    @Override
    public FieldsInitialisationView getFieldsInitialisationView() {
        return null;
    }

    @Override
    public CaseSetupReader getCaseSetupReader() {
        return null;
    }

    @Override
    public CaseSetupWriter getCaseSetupWriter() {
        return null;
    }

    @Override
    public void read() {
    }

    @Override
    public void write() {
    }

    @Override
    public void loadMaterials() {
    }

    @Override
    public void saveMaterials() {
    }

    @Override
    public void saveDefaultsToProject() {
    }

    @Override
    public void saveDefaultsTurbulenceModelsToProject() {
    }

    @Override
    public void saveMaterialsToProject() {
    }

    @Override
    public void updateSolver(State state) {
    }

    @Override
    public void updateSolverFamilies(State state, Set<SolverFamily> families) {
    }

    @Override
    public Set<ModulePanel> getCaseSetupPanels() {
        return Collections.<ModulePanel> emptySet();
    }

    @Override
    public Set<ModulePanel> getSolverPanels() {
        return Collections.<ModulePanel> emptySet();
    }

    @Override
    public Set<String> getFieldsToBeMonitored() {
        return Collections.<String> emptySet();
    }

    @Override
    public boolean isFieldInitialisationVetoed(Field field) {
        return false;
    }

    @Override
    public boolean isNonNewtonianViscosityModelVetoed() {
        return false;
    }
    
    @Override
    public void saveToBoundaryConditions(BoundaryConditions bc) {
    }

}

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

package eu.engys.core.modules;

import java.util.Collections;
import java.util.Set;

import eu.engys.core.modules.boundaryconditions.BoundaryConditionsView;
import eu.engys.core.modules.cellzones.CellZonesView;
import eu.engys.core.modules.materials.MaterialsView;
import eu.engys.core.modules.tree.TreeView;
import eu.engys.core.project.state.SolverFamily;
import eu.engys.core.project.state.State;
import eu.engys.core.project.zero.fields.Field;

public abstract class ApplicationModuleAdapter implements ApplicationModule {
    
    @Override
    public boolean checkLicense() {
        return true;
    }

    @Override
    public void loadMaterials() {
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

//    @Override
//    public void initialiseFields(Controller controller, ExecutorService service, Server server) {
//    }

    @Override
    public void updateSolver(State state) {
    }

    @Override
    public void updateSolverFamilies(State state, Set<SolverFamily> families) {
    }
    
    @Override
    public TreeView getTreeView() {
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
    public Set<ModulePanel> getCaseSetupPanels() {
        return Collections.<ModulePanel> emptySet();
    }

    @Override
    public boolean isFieldInitialisationVetoed(Field field) {
        return false;
    }

    @Override
    public boolean isNonNewtonianViscosityModelVetoed() {
        return false;
    }

}

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

import java.util.Set;

import eu.engys.core.modules.boundaryconditions.BoundaryConditionsView;
import eu.engys.core.modules.cellzones.CellZonesView;
import eu.engys.core.modules.materials.MaterialsView;
import eu.engys.core.modules.solutionmodelling.SolutionView;
import eu.engys.core.modules.tree.TreeView;
import eu.engys.core.project.InvalidProjectException;
import eu.engys.core.project.state.SolverFamily;
import eu.engys.core.project.state.State;
import eu.engys.core.project.zero.fields.Field;
import eu.engys.core.project.zero.fields.Fields;

public interface ApplicationModule {

    TreeView getTreeView();

    Set<ModulePanel> getCaseSetupPanels();

    SolutionView getSolutionView();

    MaterialsView getMaterialsView();

    BoundaryConditionsView getBoundaryConditionsView();

    CellZonesView getCellZonesView();

    FieldsInitialisationView getFieldsInitialisationView();

    String getName();
    
    void loadState() throws InvalidProjectException;

    void loadMaterials();

    void save();

    void write();

    void saveDefaultsToProject();

    void saveDefaultsTurbulenceModelsToProject();

    void saveMaterialsToProject();

    Fields loadDefaultsFields(String region);

//    void initialiseFields(Controller controller, ExecutorService service, Server server);

    CaseSetupWriter getCaseSetupWriter();

    CaseSetupReader getCaseSetupReader();

    boolean isFieldInitialisationVetoed(Field field);

    boolean isNonNewtonianViscosityModelVetoed();

    void updateSolver(State state);

    void updateSolverFamilies(State state, Set<SolverFamily> families);
    
    boolean checkLicense();

}

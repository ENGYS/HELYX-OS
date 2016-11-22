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

import java.util.List;
import java.util.Set;

import javax.swing.JTabbedPane;

import eu.engys.core.modules.boundaryconditions.BoundaryConditionsView;
import eu.engys.core.modules.cellzones.CellZonesView;
import eu.engys.core.modules.materials.MaterialsView;
import eu.engys.core.modules.residuals.ResidualsViewConfigurator;
import eu.engys.core.modules.solutionmodelling.SolutionView;
import eu.engys.core.modules.tree.TreeView;
import eu.engys.core.project.state.SolverFamily;
import eu.engys.core.project.state.State;
import eu.engys.core.project.system.monitoringfunctionobjects.Parser;
import eu.engys.core.project.system.monitoringfunctionobjects.ParserView;
import eu.engys.core.project.zero.fields.Field;
import eu.engys.core.project.zero.fields.Fields;
import eu.engys.core.project.zero.patches.BoundaryConditions;

public interface ApplicationModule {

	void checkLicense();
	void checkIfCanOpenCase();
	
    TreeView getTreeView();

    Set<ModulePanel> getCaseSetupPanels();

    Set<ModulePanel> getSolverPanels();

    SolutionView getSolutionView();

    MaterialsView getMaterialsView();

    BoundaryConditionsView getBoundaryConditionsView();

    CellZonesView getCellZonesView();

    FieldsInitialisationView getFieldsInitialisationView();
    
    String getName();

    void read();
    
    void write();
    
    void loadState();

    void save();
    
    void loadMaterials();

    void saveMaterials();

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
    
    ResidualsViewConfigurator createResidualsViewConfigurator(JTabbedPane pane);

    Parser createResidualsParser();
    
    List<Parser> createParsers();

    List<ParserView> getParserViews();

    void deleteUselessLogFiles();

    Set<String> getFieldsToBeMonitored();
    
    void saveToBoundaryConditions(BoundaryConditions bc);

}

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

package eu.engys.application.modules;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.name.Names;

import eu.engys.application.Application;
import eu.engys.application.HELYXOS;
import eu.engys.core.Arguments;
import eu.engys.core.controller.Controller;
import eu.engys.core.controller.HelyxOSController;
import eu.engys.core.controller.ScriptFactory;
import eu.engys.core.modules.ApplicationModule;
import eu.engys.core.modules.boundaryconditions.BoundaryTypePanel;
import eu.engys.core.project.DefaultProjectReader;
import eu.engys.core.project.DefaultProjectWriter;
import eu.engys.core.project.Model;
import eu.engys.core.project.NullProjectReader;
import eu.engys.core.project.NullProjectWriter;
import eu.engys.core.project.ProjectReader;
import eu.engys.core.project.ProjectWriter;
import eu.engys.core.project.defaults.Defaults;
import eu.engys.core.project.defaults.DictDataFolder;
import eu.engys.core.project.defaults.JarDictDataFolder;
import eu.engys.core.project.geometry.factory.DefaultGeometryFactory;
import eu.engys.core.project.geometry.factory.GeometryFactory;
import eu.engys.core.project.materials.MaterialsReader;
import eu.engys.core.project.materials.MaterialsWriter;
import eu.engys.core.project.state.Table15;
import eu.engys.core.project.system.fieldmanipulationfunctionobjects.FieldManipulationFunctionObjectType;
import eu.engys.core.project.system.monitoringfunctionobjects.MonitoringFunctionObjectType;
import eu.engys.core.project.zero.cellzones.CellZoneType;
import eu.engys.core.project.zero.cellzones.CellZonesBuilder;
import eu.engys.core.project.zero.fields.Initialisations;
import eu.engys.gui.Actions;
import eu.engys.gui.GUIPanel;
import eu.engys.gui.StandardScriptFactory;
import eu.engys.gui.casesetup.CaseSetup;
import eu.engys.gui.casesetup.CaseSetup3DElement;
import eu.engys.gui.casesetup.CaseSetupElement;
import eu.engys.gui.casesetup.RuntimeControlsPanel;
import eu.engys.gui.casesetup.actions.StandardCaseSetupActions;
import eu.engys.gui.casesetup.boundaryconditions.BoundaryConditionsPanel;
import eu.engys.gui.casesetup.boundaryconditions.panels.CyclicSettingsPanel;
import eu.engys.gui.casesetup.boundaryconditions.panels.StandardCyclicAMISettingsPanel;
import eu.engys.gui.casesetup.boundaryconditions.panels.patch.PatchSettingsPanel;
import eu.engys.gui.casesetup.boundaryconditions.panels.wall.StandardWallSettingsPanel;
import eu.engys.gui.casesetup.cellzones.CellZonesPanel;
import eu.engys.gui.casesetup.cellzones.StandardCellZonesBuilder;
import eu.engys.gui.casesetup.cellzones.mrf.StandardMRF;
import eu.engys.gui.casesetup.cellzones.porous.StandardPorous;
import eu.engys.gui.casesetup.cellzones.thermal.StandardThermal;
import eu.engys.gui.casesetup.fields.StandardFieldsInitialisationPanel;
import eu.engys.gui.casesetup.fields.StandardInitialisations;
import eu.engys.gui.casesetup.materials.CompressibleMaterialsPanel;
import eu.engys.gui.casesetup.materials.IncompressibleMaterialsPanel;
import eu.engys.gui.casesetup.materials.StandardCompressibleMaterialsPanel;
import eu.engys.gui.casesetup.materials.StandardIncompressibleMaterialsPanel;
import eu.engys.gui.casesetup.materials.StandardMaterialsReader;
import eu.engys.gui.casesetup.materials.StandardMaterialsWriter;
import eu.engys.gui.casesetup.materials.panels.MaterialsDatabasePanel;
import eu.engys.gui.casesetup.materials.panels.MaterialsPanel;
import eu.engys.gui.casesetup.run.StandardTable15;
import eu.engys.gui.casesetup.schemes.NumericalSchemesPanel;
import eu.engys.gui.casesetup.solution.StandardSolutionModellingPanel;
import eu.engys.gui.casesetup.solver.SolverSettingsPanel;
import eu.engys.gui.custom.CustomNodePanel;
import eu.engys.gui.mesh.Mesh;
import eu.engys.gui.mesh.Mesh3DElement;
import eu.engys.gui.mesh.MeshElement;
import eu.engys.gui.mesh.actions.StandardMeshActions;
import eu.engys.gui.mesh.panels.DefaultBoundaryMeshPanel;
import eu.engys.gui.mesh.panels.DefaultMeshAdvancedOptionsPanel;
import eu.engys.gui.mesh.panels.MaterialPointsPanel;
import eu.engys.gui.mesh.panels.SolverBoundaryMeshPanel;
import eu.engys.gui.mesh.panels.StandardBaseMeshPanel;
import eu.engys.gui.mesh.panels.StandardFeatureLinesPanel;
import eu.engys.gui.mesh.panels.StandardGeometryPanel;
import eu.engys.gui.mesh.panels.StandardMeshAdvancedOptionsPanel;
import eu.engys.gui.solver.DefaultRunOptionsPanel;
import eu.engys.gui.solver.Solver;
import eu.engys.gui.solver.Solver3DElement;
import eu.engys.gui.solver.SolverElement;
import eu.engys.gui.solver.SolverRuntimeControlsPanel;
import eu.engys.gui.solver.actions.StandardSolverActions;
import eu.engys.gui.solver.postprocessing.panels.residuals.ResidualsPanel;
import eu.engys.gui.view.View;
import eu.engys.gui.view.View3DElement;
import eu.engys.gui.view.ViewElement;
import eu.engys.gui.view3D.CanvasPanel;
import eu.engys.gui.view3D.Controller3D;
import eu.engys.gui.view3D.Geometry3DController;
import eu.engys.gui.view3D.Mesh3DController;
import eu.engys.gui.view3D.fallback.FallbackGeometry3DController;
import eu.engys.gui.view3D.fallback.FallbackMesh3DController;
import eu.engys.gui.view3D.fallback.FallbackView3D;
import eu.engys.gui.view3D.widget.Widget;
import eu.engys.launcher.ApplicationLauncher;
import eu.engys.launcher.HELYXOSLauncher;
import eu.engys.standardVOF.StandardVOFModule;
import eu.engys.util.VTKSettings;
import eu.engys.util.plaf.HelyxOSLookAndFeel;
import eu.engys.util.plaf.ILookAndFeel;
import eu.engys.util.progress.ProgressMonitor;
import eu.engys.util.progress.ProgressMonitorImpl;
import eu.engys.vtk.VTKEmptyView3D;
import eu.engys.vtk.VTKGeometry3DController;
import eu.engys.vtk.VTKMesh3DController;
import eu.engys.vtk.VTKView3D;
import eu.engys.vtk.WidgetPanel;

public class HELYXOSModule extends AbstractModule {

    @Override
    protected void configure() {
        configureApp();
        configureMVC();
        configureModules();
        configure3D();
        configurePanels();
        configureBoundaryConditions();
        configureCellZones();
        configureFunctionObjects();
    }

    protected void configureApp() {
        bind(ILookAndFeel.class).to(HelyxOSLookAndFeel.class).in(Singleton.class);
        bind(ApplicationLauncher.class).to(HELYXOSLauncher.class).in(Singleton.class);
        bind(String.class).annotatedWith(Names.named("Application")).toInstance("HELYX-OS");
        bind(Application.class).to(HELYXOS.class).in(Singleton.class);
    }

    private void configureMVC() {
        bind(DictDataFolder.class).to(JarDictDataFolder.class).in(Singleton.class);
        bind(Defaults.class).in(Singleton.class);
        bind(Model.class).in(Singleton.class);
        bind(View.class).in(Singleton.class);
        bind(Controller.class).to(HelyxOSController.class).in(Singleton.class);
        bind(ProgressMonitor.class).to(ProgressMonitorImpl.class).in(Singleton.class);

        bind(Initialisations.class).to(StandardInitialisations.class).in(Singleton.class);

        bind(ProjectWriter.class).to(DefaultProjectWriter.class);
        bind(ProjectReader.class).to(DefaultProjectReader.class);
        bind(ProjectWriter.class).annotatedWith(CaseSetup.class).to(NullProjectWriter.class);
        bind(ProjectReader.class).annotatedWith(CaseSetup.class).to(NullProjectReader.class);

        bind(CellZonesBuilder.class).to(StandardCellZonesBuilder.class);

        bind(MaterialsReader.class).to(StandardMaterialsReader.class);
        bind(MaterialsWriter.class).to(StandardMaterialsWriter.class);
        bind(CompressibleMaterialsPanel.class).to(StandardCompressibleMaterialsPanel.class);

        bind(Table15.class).to(StandardTable15.class).in(Singleton.class);
        bind(ScriptFactory.class).to(StandardScriptFactory.class).in(Singleton.class);
    }

    private void configureModules() {
        Multibinder<ApplicationModule> applicationModules = Multibinder.newSetBinder(binder(), ApplicationModule.class);
        applicationModules.addBinding().to(StandardVOFModule.class).in(Singleton.class);
    }

    protected void configure3D() {
        bind(WidgetPanel.class).in(Singleton.class);
        if (!VTKSettings.librariesAreLoaded()) {
            VTKSettings.LoadAllNativeLibraries();
        }
        if (VTKSettings.librariesAreLoaded()) {
            if (Arguments.no3D) {
                bind(CanvasPanel.class).to(VTKEmptyView3D.class).in(Singleton.class);
            } else {
                bind(CanvasPanel.class).to(VTKView3D.class).in(Singleton.class);
            }
            bind(Geometry3DController.class).to(VTKGeometry3DController.class).in(Singleton.class);
            bind(Mesh3DController.class).to(VTKMesh3DController.class).in(Singleton.class);
        } else {
            bind(CanvasPanel.class).to(FallbackView3D.class).in(Singleton.class);
            bind(Geometry3DController.class).to(FallbackGeometry3DController.class).in(Singleton.class);
            bind(Mesh3DController.class).to(FallbackMesh3DController.class).in(Singleton.class);
        }

        Multibinder<Controller3D> controllers = Multibinder.newSetBinder(binder(), Controller3D.class);
        controllers.addBinding().to(Geometry3DController.class).in(Singleton.class);
        controllers.addBinding().to(Mesh3DController.class).in(Singleton.class);

        Multibinder.newSetBinder(binder(), Widget.class);
    }

    private void configurePanels() {
        bind(String.class).annotatedWith(Mesh.class).toInstance("Mesh");
        bind(String.class).annotatedWith(CaseSetup.class).toInstance("Case Setup");
        bind(String.class).annotatedWith(Solver.class).toInstance("Solver");

        bind(View3DElement.class).annotatedWith(Mesh.class).to(Mesh3DElement.class);
        bind(View3DElement.class).annotatedWith(CaseSetup.class).to(CaseSetup3DElement.class);
        bind(View3DElement.class).annotatedWith(Solver.class).to(Solver3DElement.class);

        bind(Actions.class).annotatedWith(Mesh.class).to(StandardMeshActions.class).in(Singleton.class);
        bind(Actions.class).annotatedWith(CaseSetup.class).to(StandardCaseSetupActions.class).in(Singleton.class);
        bind(Actions.class).annotatedWith(Solver.class).to(StandardSolverActions.class).in(Singleton.class);

        bind(GeometryFactory.class).to(DefaultGeometryFactory.class);
        bind(DefaultMeshAdvancedOptionsPanel.class).to(StandardMeshAdvancedOptionsPanel.class);

        bind(MaterialsDatabasePanel.class).in(Singleton.class);
        bind(CompressibleMaterialsPanel.class).to(StandardCompressibleMaterialsPanel.class);
        bind(IncompressibleMaterialsPanel.class).to(StandardIncompressibleMaterialsPanel.class);

        Multibinder<ViewElement> binder = Multibinder.newSetBinder(binder(), ViewElement.class);
        binder.addBinding().to(MeshElement.class).in(Singleton.class);
        binder.addBinding().to(CaseSetupElement.class).in(Singleton.class);
        binder.addBinding().to(SolverElement.class).in(Singleton.class);

        Multibinder<GUIPanel> panelsMesh = Multibinder.newSetBinder(binder(), GUIPanel.class, Mesh.class);
        panelsMesh.addBinding().to(StandardBaseMeshPanel.class).in(Singleton.class);
        panelsMesh.addBinding().to(StandardGeometryPanel.class).in(Singleton.class);
        panelsMesh.addBinding().to(StandardFeatureLinesPanel.class).in(Singleton.class);
        panelsMesh.addBinding().to(MaterialPointsPanel.class).in(Singleton.class);
        panelsMesh.addBinding().to(DefaultBoundaryMeshPanel.class).in(Singleton.class);
        panelsMesh.addBinding().to(CustomNodePanel.class).in(Singleton.class);

        Multibinder<GUIPanel> panelsCaseSetup = Multibinder.newSetBinder(binder(), GUIPanel.class, CaseSetup.class);
        panelsCaseSetup.addBinding().to(StandardSolutionModellingPanel.class).in(Singleton.class);
        panelsCaseSetup.addBinding().to(MaterialsPanel.class).in(Singleton.class);
        panelsCaseSetup.addBinding().to(BoundaryConditionsPanel.class).in(Singleton.class);
        panelsCaseSetup.addBinding().to(CellZonesPanel.class).in(Singleton.class);
        panelsCaseSetup.addBinding().to(NumericalSchemesPanel.class).in(Singleton.class);
        panelsCaseSetup.addBinding().to(SolverSettingsPanel.class).in(Singleton.class);
        panelsCaseSetup.addBinding().to(RuntimeControlsPanel.class).in(Singleton.class);
        panelsCaseSetup.addBinding().to(StandardFieldsInitialisationPanel.class).in(Singleton.class);
        panelsCaseSetup.addBinding().to(CustomNodePanel.class).in(Singleton.class);

        Multibinder<GUIPanel> panelsSolver = Multibinder.newSetBinder(binder(), GUIPanel.class, Solver.class);
        panelsSolver.addBinding().to(DefaultRunOptionsPanel.class).in(Singleton.class);
        panelsSolver.addBinding().to(SolverRuntimeControlsPanel.class).in(Singleton.class);
        panelsSolver.addBinding().to(ResidualsPanel.class).in(Singleton.class);
        panelsSolver.addBinding().to(SolverBoundaryMeshPanel.class).in(Singleton.class);
    }

    private void configureBoundaryConditions() {
        Multibinder<BoundaryTypePanel> bcMultibinder = Multibinder.newSetBinder(binder(), BoundaryTypePanel.class);
        bcMultibinder.addBinding().to(PatchSettingsPanel.class).in(Singleton.class);
        bcMultibinder.addBinding().to(StandardWallSettingsPanel.class).in(Singleton.class);
        bcMultibinder.addBinding().to(CyclicSettingsPanel.class).in(Singleton.class);
        bcMultibinder.addBinding().to(StandardCyclicAMISettingsPanel.class).in(Singleton.class);
    }

    private void configureCellZones() {
        Multibinder<CellZoneType> zonesMultibinder = Multibinder.newSetBinder(binder(), CellZoneType.class);
        zonesMultibinder.addBinding().to(StandardMRF.class).in(Singleton.class);
        zonesMultibinder.addBinding().to(StandardPorous.class).in(Singleton.class);
        zonesMultibinder.addBinding().to(StandardThermal.class).in(Singleton.class);
    }

    private void configureFunctionObjects() {
        Multibinder.newSetBinder(binder(), FieldManipulationFunctionObjectType.class);
        Multibinder.newSetBinder(binder(), MonitoringFunctionObjectType.class);
    }
}

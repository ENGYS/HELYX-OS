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
package eu.engys.core.project.state;

import static eu.engys.core.project.constant.ConstantFolder.CONSTANT;
import static eu.engys.core.project.constant.ConstantFolder.FREE_SURFACE_PROPERTIES;
import static eu.engys.core.project.constant.ConstantFolder.G;
import static eu.engys.core.project.constant.MRFProperties.MRF_PROPERTIES;
import static eu.engys.core.project.constant.ThermophysicalProperties.THERMOPHYSICAL_PROPERTIES;
import static eu.engys.core.project.constant.TransportProperties.MATERIAL_NAME_KEY;
import static eu.engys.core.project.constant.TransportProperties.PHASE1_KEY;
import static eu.engys.core.project.constant.TransportProperties.PHASE2_KEY;
import static eu.engys.core.project.constant.TransportProperties.PHASES_KEY;
import static eu.engys.core.project.constant.TransportProperties.TRANSPORT_MODEL_KEY;
import static eu.engys.core.project.constant.TransportProperties.TRANSPORT_PROPERTIES;
import static eu.engys.core.project.constant.TurbulenceProperties.LES_KEY;
import static eu.engys.core.project.constant.TurbulenceProperties.LES_MODEL_KEY;
import static eu.engys.core.project.constant.TurbulenceProperties.RAS_KEY;
import static eu.engys.core.project.constant.TurbulenceProperties.RAS_MODEL_KEY;
import static eu.engys.core.project.constant.TurbulenceProperties.SIMULATION_TYPE_KEY;
import static eu.engys.core.project.constant.TurbulenceProperties.TURBULENCE_PROPERTIES;
import static eu.engys.core.project.system.ControlDict.CONTROL_DICT;
import static eu.engys.core.project.system.FvSchemes.BACKWARD;
import static eu.engys.core.project.system.FvSchemes.DEFAULT;
import static eu.engys.core.project.system.FvSchemes.EULER;
import static eu.engys.core.project.system.FvSchemes.FV_SCHEMES;
import static eu.engys.core.project.system.FvSchemes.LOCAL_EULER_RDELTAT;
import static eu.engys.core.project.system.FvSchemes.STEADY_STATE;
import static eu.engys.core.project.system.FvSolution.FV_SOLUTION;
import static eu.engys.core.project.system.SystemFolder.SYSTEM;

import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.modules.ApplicationModule;
import eu.engys.core.modules.ModulesUtil;
import eu.engys.core.project.Model;
import eu.engys.core.project.TurbulenceModel;
import eu.engys.core.project.TurbulenceModels;
import eu.engys.core.project.openFOAMProject;
import eu.engys.core.project.constant.ConstantFolder;
import eu.engys.core.project.constant.ThermophysicalProperties;
import eu.engys.core.project.constant.TransportProperties;
import eu.engys.core.project.constant.TurbulenceProperties;
import eu.engys.core.project.defaults.DefaultsProvider;
import eu.engys.core.project.system.FvSchemes;
import eu.engys.core.project.system.FvSolution;
import eu.engys.core.project.system.SystemFolder;
import eu.engys.core.project.zero.fields.Fields;
import eu.engys.core.project.zero.fields.FieldsDefaults;
import eu.engys.core.project.zero.patches.BoundaryConditionsDefaults;
import eu.engys.util.progress.ProgressMonitor;

public class StateBuilder {

    private static final Logger logger = LoggerFactory.getLogger(StateBuilder.class);

    public static void changeState(Model model, Set<ApplicationModule> modules) {
        logger.info("Project: clear");
        clearProject(model);

        logger.info("Turbulence: saveDefaultsToProject");
        TurbulenceBuilder.saveDefaultsToProject(model, model.getDefaults());

        logger.info("Modules: saveDefaultsTurbulenceModelsToProject");
        ModulesUtil.saveDefaultsTurbulenceModelsToProject(modules);

        logger.info("State: saveDefaultsToProject");
        StateBuilder.saveDefaultsToProject(model, model.getDefaults());

        logger.info("Modules: saveDefaultsToProject");
        ModulesUtil.saveDefaultsToProject(modules);

        logger.info("Materials: saveMaterialsToProject");
        PhaseBuilder.saveDefaultMaterialsToProject(model);

        logger.info("Modules: saveMaterialsToProject");
        ModulesUtil.saveDefaultMaterialsToProject(modules);

        logger.info("Fields: clear");
        FieldsDefaults.prepareFields(model.getFields());

        Fields fields = new Fields();
        if (model.getProject().isParallel()) {
            fields.newParallelFields(model.getProject().getProcessors());
        }

        logger.info("Fields: loadFieldsFromDefaults");
        fields.merge(FieldsDefaults.loadFieldsFromDefaults(model.getProject().getBaseDir(), model.getState(), model.getDefaults(), model.getPatches(), null));

        logger.info("Modules: loadFieldsFromDefaults");
        fields.merge(ModulesUtil.loadFieldsFromDefaults(modules, null));

        fields.fixPVisibility(model.getState());

        model.setFields(fields);

        logger.info("Boundary Conditions: loadBoundaryConditionsFromFields");
        BoundaryConditionsDefaults.loadBoundaryConditionsFromFields(model.getPatches(), model.getFields());

        logger.info("Boundary Conditions: updateBoundaryConditionsDefaultsByFields");
        BoundaryConditionsDefaults.updateBoundaryConditionsDefaultsByFields(model);

        logger.info("Fire State Changed");
        model.stateChanged();

        logger.info("FINISHED");
    }

    public static void changeMaterial(Model model, Set<ApplicationModule> modules) {
        logger.info("Modules: saveMaterialsToProject");
        ModulesUtil.saveDefaultMaterialsToProject(modules);

        logger.info("Fields: clear");
        FieldsDefaults.prepareFields(model.getFields());

        Fields fields = new Fields();

        logger.info("Fields: loadFieldsFromDefaults");
        fields.merge(FieldsDefaults.loadFieldsFromDefaults(model.getProject().getBaseDir(), model.getState(), model.getDefaults(), model.getPatches(), null));

        logger.info("Modules: loadFieldsFromDefaults");
        fields.merge(ModulesUtil.loadFieldsFromDefaults(modules, null));

        fields.fixPVisibility(model.getState());

        model.setFields(fields);

        logger.info("Boundary Conditions: loadBoundaryConditionsFromFields");
        BoundaryConditionsDefaults.loadBoundaryConditionsFromFields(model.getPatches(), model.getFields());

        logger.info("Boundary Conditions: updateBoundaryConditionsDefaultsByFields");
        BoundaryConditionsDefaults.updateBoundaryConditionsDefaultsByFields(model);

        logger.info("Fire Materials Changed");
        model.materialsChanged();

        logger.info("FINISHED");
    }

    public static void saveDefaultsToProject(Model model, DefaultsProvider defaults) {
        Dictionary stateDictionary = defaults.getDefaultsFor(model.getState());
        saveToProject(model, stateDictionary);
    }

    private static void saveToProject(Model model, Dictionary stateDict) {
        if (stateDict == null)
            return;

        openFOAMProject prj = model.getProject();
        if (stateDict.found(SYSTEM)) {
            Dictionary systemDict = stateDict.subDict(SYSTEM);
            if (systemDict.isDictionary(CONTROL_DICT)) {
                if (prj.getSystemFolder().getControlDict() == null)
                    prj.getSystemFolder().setControlDict(systemDict.subDict(CONTROL_DICT));
                else
                    prj.getSystemFolder().getControlDict().merge(systemDict.subDict(CONTROL_DICT));
            } else {
                /* error */
            }
            if (systemDict.isDictionary(FV_SCHEMES)) {
                if (prj.getSystemFolder().getFvSchemes() == null)
                    prj.getSystemFolder().setFvSchemes(systemDict.subDict(FV_SCHEMES));
                else
                    prj.getSystemFolder().getFvSchemes().merge(systemDict.subDict(FV_SCHEMES));
            } else {
                /* error */
            }
            if (systemDict.isDictionary(FV_SOLUTION)) {
                if (prj.getSystemFolder().getFvSolution() == null) {
                    prj.getSystemFolder().setFvSolution(systemDict.subDict(FV_SOLUTION));
                } else {
                    prj.getSystemFolder().getFvSolution().merge(systemDict.subDict(FV_SOLUTION));
                }
            } else {
                /* error */
            }
        }

        if (stateDict.found(CONSTANT)) {
            Dictionary constantDict = stateDict.subDict(CONSTANT);

            if (constantDict.isDictionary(G)) {
                if (prj.getConstantFolder().getG() == null)
                    prj.getConstantFolder().setG(constantDict.subDict(G));
                else
                    prj.getConstantFolder().getG().merge(constantDict.subDict(G));
            }
            if (constantDict.isDictionary(THERMOPHYSICAL_PROPERTIES)) {
                if (prj.getConstantFolder().getThermophysicalProperties() == null)
                    prj.getConstantFolder().setThermophysicalProperties(constantDict.subDict(THERMOPHYSICAL_PROPERTIES));
                else
                    prj.getConstantFolder().getThermophysicalProperties().merge(constantDict.subDict(THERMOPHYSICAL_PROPERTIES));
            }
            if (constantDict.isDictionary(TRANSPORT_PROPERTIES)) {
                if (prj.getConstantFolder().getTransportProperties() == null)
                    prj.getConstantFolder().setTransportProperties(constantDict.subDict(TRANSPORT_PROPERTIES));
                else
                    prj.getConstantFolder().getTransportProperties().merge(constantDict.subDict(TRANSPORT_PROPERTIES));
            }
            if (constantDict.isDictionary(TURBULENCE_PROPERTIES)) {
                if (prj.getConstantFolder().getTurbulenceProperties() == null)
                    prj.getConstantFolder().setTurbulenceProperties(constantDict.subDict(TURBULENCE_PROPERTIES));
                else
                    prj.getConstantFolder().getTurbulenceProperties().merge(constantDict.subDict(TURBULENCE_PROPERTIES));
            }
            if (constantDict.isDictionary(MRF_PROPERTIES)) {
                if (prj.getConstantFolder().getMrfProperties() == null)
                    prj.getConstantFolder().setMrfProperties(constantDict.subDict(MRF_PROPERTIES));
                else
                    prj.getConstantFolder().getMrfProperties().merge(constantDict.subDict(MRF_PROPERTIES));
            }
        }
    }

    /*
     * Load
     */

    public static void loadState(Model model, Table15 solversTable, ProgressMonitor monitor) {
        SystemFolder systemFolder = model.getProject().getSystemFolder();

        if (systemFolder != null) {
            FvSchemes fvSchemes = systemFolder.getFvSchemes();
            FvSolution fvSolution = systemFolder.getFvSolution();

            SolverType solverType = readSolverType(fvSolution);
            model.getState().setSolverType(solverType);

            Time time = readTime(model, solverType, fvSchemes, monitor);
            model.getState().setTime(time);

            Mach mach = readMach(fvSolution, monitor);
            model.getState().setMach(mach);
        }

        ConstantFolder constantFolder = model.getProject().getConstantFolder();
        if (constantFolder != null) {
            Method method = readMethod(constantFolder);
            model.getState().setMethod(method);

            ThermophysicalProperties compressible = constantFolder.getThermophysicalProperties();
            TransportProperties incompressible = constantFolder.getTransportProperties();

            Flow flow = readFlow(compressible, incompressible);
            model.getState().setFlow(flow);

            model.getState().setMultiphaseModel(MultiphaseModel.OFF);

            boolean multiphase = readMultiphase(constantFolder);

            boolean energy = readEnergy(model.getState(), constantFolder, multiphase);
            model.getState().setEnergy(energy);

            boolean buoyancy = readBuoyancy(model.getState(), constantFolder, multiphase);
            model.getState().setBuoyant(buoyancy);

            TurbulenceModel turbulenceModel = readTurbulenceModel(model, model.getState().getSolverType(), constantFolder, monitor);
            model.getState().setTurbulenceModel(turbulenceModel);
        }

        if (systemFolder != null) {
            FvSolution fvSolution = systemFolder.getFvSolution();

            SolverFamily solverFamily = readSolverFamily(model.getState(), fvSolution);
            model.getState().setSolverFamily(solverFamily);
        }

        monitor.info(model.getState().toString(), 1);
    }

    private static SolverType readSolverType(FvSolution fvSolution) {
        // Coupled handled in its own module
        if (fvSolution != null) {
            return SolverType.SEGREGATED;
        } else {
            return SolverType.NONE;
        }
    }

    private static SolverFamily readSolverFamily(State state, FvSolution fvSolution) {
        // Coupled handled in its own module
        if (state.getSolverType().isSegregated()) {
            if (state.isLowMach()) {
                if (state.isSteady()) {
                    if (fvSolution != null && fvSolution.found(FvSolution.PIMPLE)) {
                        return SolverFamily.PIMPLE;
                    } else {
                        return SolverFamily.SIMPLE;
                    }
                } else if (state.isTransient()) {
                    if (state.isCompressible()) {
                        return SolverFamily.PIMPLE;
                    } else if (state.isIncompressible()) {
                        if (state.isEnergy() || state.getMultiphaseModel().isMultiphase()) {
                            return SolverFamily.PIMPLE;
                        } else {
                            if (fvSolution != null && fvSolution.found(FvSolution.PISO)) {
                                return SolverFamily.PISO;
                            } else {
                                return SolverFamily.PIMPLE;
                            }
                        }
                    } else {
                        return SolverFamily.NONE;
                    }
                } else {
                    return SolverFamily.NONE;
                }
            } else if (state.isHighMach()) {
                if (fvSolution != null && fvSolution.found(FvSolution.PIMPLE)) {
                    return SolverFamily.PIMPLE;
                } else {
                    return SolverFamily.CENTRAL;
                }
            } else {
                return SolverFamily.NONE;
            }
        } else {
            return SolverFamily.NONE;
        }
    }

    public static Time readTime(Model model, SolverType solverType, FvSchemes fvSchemes, ProgressMonitor monitor) {
        if (fvSchemes != null) {
            Dictionary ddtSchemes = fvSchemes.getDdtSchemes();
            if (ddtSchemes != null) {
                if (ddtSchemes.found(DEFAULT)) {
                    String timeField = ddtSchemes.lookup(DEFAULT);
                    if (solverType.isCoupled()) {
                        if (timeField.equals(EULER)) {
                            return Time.STEADY;
                        } else if (timeField.equals(BACKWARD)) {
                            return Time.TRANSIENT;
                        }
                    } else if (solverType.isSegregated()) {
                        if (timeField.equals(STEADY_STATE) || timeField.equals(LOCAL_EULER_RDELTAT)) {
                            return Time.STEADY;
                        } else {
                            return Time.TRANSIENT;
                        }
                    }
                } else {
                    monitor.warning("ddtSchemes: bad file structure", 1);
                }
            } else {
                monitor.warning("fvSchemes: no ddtScheme found.", 1);
            }
        } else {
            monitor.warning("fvSchemes: not found", 1);
        }
        return Time.NONE;
    }

    private static Mach readMach(FvSolution fvSolution, ProgressMonitor monitor) {
        if (fvSolution != null) {
            String sonic = fvSolution.lookup("sonic");
            if ("true".equals(sonic)) {
                return Mach.HIGH;
            } else {
                return Mach.LOW;
            }
        } else {
            monitor.warning("fvSolution: not found", 1);
            return Mach.LOW;
        }
    }

    private static Method readMethod(ConstantFolder constantFolder) {
        Dictionary turbPropDict = constantFolder.getTurbulenceProperties();
        if (turbPropDict != null && turbPropDict.found(SIMULATION_TYPE_KEY)) {
            String turbType = turbPropDict.lookup(SIMULATION_TYPE_KEY);
            if (turbType.startsWith(RAS_KEY)) {
                return Method.RANS;
            } else if (turbType.startsWith(LES_KEY)) {
                return Method.LES;
            } else if(turbType.equals(TurbulenceProperties.LAMINAR_KEY)){
                if(turbPropDict.isDictionary(LES_KEY)){
                    return Method.LES;
                } else {
                    return Method.RANS;
                }
            }
        }
        return Method.NONE;
    }

    private static Flow readFlow(ThermophysicalProperties compressible, TransportProperties incompressible) {
        if (compressible != null && compressible.found(ThermophysicalProperties.THERMO_TYPE_KEY)) {
            return Flow.COMPRESSIBLE;
        } else if (incompressible != null) {
            if (incompressible.found(TRANSPORT_MODEL_KEY) || incompressible.found(PHASE1_KEY) || incompressible.found(PHASES_KEY)) {
                return Flow.INCOMPRESSIBLE;
            }
        }
        return Flow.NONE;
    }

    private static boolean readMultiphase(ConstantFolder constantFolder) {
        TransportProperties incompressible = constantFolder.getTransportProperties();
        if (incompressible != null) {
            if ((incompressible.found(PHASE1_KEY) && incompressible.found(PHASE2_KEY)) || incompressible.found(PHASES_KEY)) {
                return true;
            } else if (constantFolder.getFileManager().getFile(FREE_SURFACE_PROPERTIES).exists()) {
                return true;
            } else if (incompressible.found(MATERIAL_NAME_KEY)) {
                return false;
            }
        }
        return false;
    }

    private static boolean readEnergy(State state, ConstantFolder constantFolder, boolean isMultiphase) {
        Dictionary g = constantFolder.getG();
        if (g != null && g.found("value")) {
            if (isMultiphase) {
                return false;
            } else {
                return true;
            }
        } else {
            return state.isCompressible();
        }
    }

    private static boolean readBuoyancy(State state, ConstantFolder constantFolder, boolean isMultiphase) {
        Dictionary g = constantFolder.getG();
        if (g != null && g.found("value")) {
            if (isMultiphase) {
                return false;
            } else {
                return isBuoyant(g);
            }
        } else {
            return false;
        }
    }

    public static TurbulenceModel readTurbulenceModel(Model model, SolverType solverType, ConstantFolder constantFolder, ProgressMonitor monitor) {
        Dictionary turbulenceProperties = constantFolder.getTurbulenceProperties();
        if (model.getState().isLES() && turbulenceProperties.found(LES_KEY)) {
            String lesModel = turbulenceProperties.subDict(LES_KEY).lookup(LES_MODEL_KEY);
            return readTurbulenceModelFromState(model, solverType, lesModel, monitor);
        } else if (model.getState().isRANS() && turbulenceProperties.found(RAS_KEY)) {
            String rasModel = turbulenceProperties.subDict(RAS_KEY).lookup(RAS_MODEL_KEY);
            return readTurbulenceModelFromState(model, solverType, rasModel, monitor);
        }
        return null;
    }

    public static TurbulenceModel readTurbulenceModelFromState(Model model, SolverType solverType, String modelName, ProgressMonitor monitor) {
        TurbulenceModels turbulenceModels = model.getTurbulenceModels();
        Method method = model.getState().getMethod();
        Flow flow = model.getState().getFlow();
        logger.info("Loading Turbulence model for {} {} {}", solverType, method, flow);

        List<TurbulenceModel> modelsForState = turbulenceModels.getModelsForState(solverType, method, flow);
        if (modelsForState != null && !modelsForState.isEmpty()) {
            for (TurbulenceModel tm : modelsForState) {
                if (tm.getName().equals(modelName)) {
                    return tm;
                }
            }
            TurbulenceModel firstTurbulenceModel = modelsForState.get(0);
            monitor.warning(String.format("%s not found. Changed to %s", modelName, firstTurbulenceModel), 1);
            return firstTurbulenceModel;
        } else {
            monitor.warning("Turbulence models not loaded", 1);
            return null;
        }
    }

    private static boolean isBuoyant(Dictionary g) {
        String[] gValues = g.lookupArray("value");
        try {
            double x = Double.parseDouble(gValues[0]);
            double y = Double.parseDouble(gValues[1]);
            double z = Double.parseDouble(gValues[2]);

            if (x == 0 && y == 0 && z == 0) {
                return false;
            } else {
                return true;
            }
        } catch (Exception e) {
            return true;
        }
    }

    /*
     * Other
     */

    private static void clearProject(Model model) {
        openFOAMProject prj = model.getProject();
        if (prj.getSystemFolder().getControlDict() != null)
            prj.getSystemFolder().getControlDict().clear();
        if (prj.getSystemFolder().getFvSchemes() != null)
            prj.getSystemFolder().getFvSchemes().clear();
        if (prj.getSystemFolder().getFvSolution() != null)
            prj.getSystemFolder().getFvSolution().clear();

        prj.getConstantFolder().setG(null);

        if (prj.getConstantFolder().getThermophysicalProperties() != null)
            prj.getConstantFolder().getThermophysicalProperties();
        if (prj.getConstantFolder().getTransportProperties() != null)
            prj.getConstantFolder().getTransportProperties();
        if (prj.getConstantFolder().getTurbulenceProperties() != null)
            prj.getConstantFolder().getTurbulenceProperties().clear();
    }

}

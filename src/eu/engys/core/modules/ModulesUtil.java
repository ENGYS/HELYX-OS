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

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.Action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.dictionary.DictionaryLinkResolver;
import eu.engys.core.dictionary.parser.DictionaryReader2;
import eu.engys.core.modules.boundaryconditions.BoundaryConditionsView;
import eu.engys.core.modules.boundaryconditions.BoundaryTypePanel;
import eu.engys.core.modules.boundaryconditions.IBoundaryConditionsPanel;
import eu.engys.core.modules.cellzones.CellZonesView;
import eu.engys.core.modules.materials.MaterialsView;
import eu.engys.core.modules.tree.ModuleElementPanel;
import eu.engys.core.modules.tree.TreeView;
import eu.engys.core.project.InvalidProjectException;
import eu.engys.core.project.state.SolverFamily;
import eu.engys.core.project.state.State;
import eu.engys.core.project.zero.cellzones.CellZoneType;
import eu.engys.core.project.zero.cellzones.CellZones;
import eu.engys.core.project.zero.fields.Field;
import eu.engys.core.project.zero.fields.Fields;
import eu.engys.util.ui.builder.PanelBuilder;

public class ModulesUtil {

    private static final Logger logger = LoggerFactory.getLogger(ModulesUtil.class);
    public static final String EXCEPTION_MESSAGE = "You need a valid license for %s add-on module to open this case!";

    public static Dictionary readDictionary(ApplicationModule module, Dictionary linkResolver, String resource) {
        String resourcePath = module.getClass().getPackage().getName().replace(".", "/") + "/resources" + "/" + resource;
        InputStream is = ModulesUtil.class.getClassLoader().getResourceAsStream(resourcePath);
        if (is != null) {
            if (linkResolver != null) {
                return new Dictionary(resource, is, new DictionaryLinkResolver(linkResolver));
            } else {
                return new Dictionary(resource, is);
            }
        } else {
            logger.warn("FILE NOT FOUND: {}", resourcePath);
            return null;
        }
    }

    public static Dictionary readDictionary2(ApplicationModule module, String resource) {
        String resourcePath = module.getClass().getPackage().getName().replace(".", "/") + "/resources" + "/" + resource;
        InputStream is = ModulesUtil.class.getClassLoader().getResourceAsStream(resourcePath);
        if (is != null) {

            Dictionary dictionary = new Dictionary(resource);
            DictionaryReader2 reader = new DictionaryReader2(dictionary);
            reader.read(is);

            return dictionary;
        } else {
            logger.warn("FILE NOT FOUND: {}", resourcePath);
            return null;
        }
    }

    public static Dictionary extractRelativeToStateSettings(String encodedPrimalState, Dictionary mDict) {
        if (mDict.found("requirements")) {
            Dictionary requirements = (Dictionary) mDict.remove("requirements");
            if (requirements.found("conditional")) {
                if (requirements.subDict("conditional").found(encodedPrimalState)) {
                    Dictionary conditional = requirements.subDict("conditional").subDict(encodedPrimalState);
                    return conditional;
                }
            }
        }
        return null;
    }

    public static void updateStateFromGUI(Set<ApplicationModule> modules) {
        for (ApplicationModule module : modules) {
            module.getSolutionView().updateStateFromGUI();
        }
    }

    public static void loadState(Set<ApplicationModule> modules) throws InvalidProjectException {
        for (ApplicationModule module : modules) {
            module.loadState();
        }
    }

    public static void updateSolver(Set<ApplicationModule> modules, State state) {
        for (ApplicationModule module : modules) {
            module.updateSolver(state);
        }
    }

    public static void updateSolverFamilies(Set<ApplicationModule> modules, State state, Set<SolverFamily> families) {
        for (ApplicationModule module : modules) {
            module.updateSolverFamilies(state, families);
        }
    }

    public static void loadMaterials(Set<ApplicationModule> modules) {
        for (ApplicationModule module : modules) {
            module.loadMaterials();
        }
    }

    public static void saveDefaultsToProject(Set<ApplicationModule> modules) {
        for (ApplicationModule module : modules) {
            module.saveDefaultsToProject();
        }
    }

    public static void saveDefaultsTurbulenceModelsToProject(Set<ApplicationModule> modules) {
        for (ApplicationModule module : modules) {
            module.saveDefaultsTurbulenceModelsToProject();
        }
    }

    public static void saveDefaultMaterialsToProject(Set<ApplicationModule> modules) {
        for (ApplicationModule module : modules) {
            module.saveMaterialsToProject();
        }
    }

    public static void save(Set<ApplicationModule> modules) {
        for (ApplicationModule module : modules) {
            module.save();
        }
    }

//    public static void initaliseFields(Set<ApplicationModule> modules, Controller controller, ExecutorService service, Server server) {
//        for (ApplicationModule module : modules) {
//            module.initialiseFields(controller, service, server);
//        }
//    }

    public static Fields loadFieldsFromDefaults(Set<ApplicationModule> modules, String region) {
        Fields fields = new Fields();
        for (ApplicationModule module : modules) {
            fields.merge(module.loadDefaultsFields(region));
        }
        return fields;
    }

    public static void configureMaterialsView(Set<ApplicationModule> modules, PanelBuilder parametersBuilder) {
        for (ApplicationModule module : modules) {
            MaterialsView materialsView = module.getMaterialsView();
            if (materialsView == null) {
                continue;
            }
            materialsView.configure(parametersBuilder);
        }
    }

    public static void configureFieldsInitialization(Set<ApplicationModule> modules, List<Action> actions) {
        for (ApplicationModule module : modules) {
            FieldsInitialisationView fieldsView = module.getFieldsInitialisationView();
            if (fieldsView == null) {
                continue;
            }
            fieldsView.configure(actions);
        }
    }

    public static void configureBoundaryConditionsView(Set<ApplicationModule> modules, IBoundaryConditionsPanel panel) {
        for (ApplicationModule module : modules) {
            BoundaryConditionsView boundaryConditionsView = module.getBoundaryConditionsView();
            if (boundaryConditionsView == null)
                continue;
            boundaryConditionsView.configure(panel);
        }
    }

    public static void configureBoundaryConditionsView(Set<ApplicationModule> modules, BoundaryTypePanel typePanel) {
        for (ApplicationModule module : modules) {
            BoundaryConditionsView boundaryConditionsView = module.getBoundaryConditionsView();
            if (boundaryConditionsView == null)
                continue;
            boundaryConditionsView.configure(typePanel);
        }
    }

    public static List<CellZoneType> getCellZoneTypes(Set<ApplicationModule> modules) {
        List<CellZoneType> moduleCellZoneTypes = new ArrayList<CellZoneType>();
        for (ApplicationModule module : modules) {
            CellZonesView cellZonesView = module.getCellZonesView();
            if (cellZonesView != null){
                moduleCellZoneTypes.addAll(cellZonesView.getCellZoneTypes());
            }
        }
        return moduleCellZoneTypes;
    }

    public static void updateCellZonesFromModel(Set<ApplicationModule> modules, CellZones cellZones) {
        for (ApplicationModule module : modules) {
            CellZonesView cellZonesView = module.getCellZonesView();
            if (cellZonesView == null)
                continue;
            cellZonesView.updateCellZonesFromModel(cellZones);
        }
    }

    public static void updateModelFromCellZones(Set<ApplicationModule> modules) {
        for (ApplicationModule module : modules) {
            CellZonesView cellZonesView = module.getCellZonesView();
            if (cellZonesView == null)
                continue;
            cellZonesView.updateModelFromCellZones();
        }
    }

    public static boolean isFieldInitialisationVetoed(Field field, Set<ApplicationModule> modules) {
        for (ApplicationModule module : modules) {
            if (module.isFieldInitialisationVetoed(field))
                return true;
        }
        return false;
    }

    public static boolean isNonNewtonianViscosityModelVetoed(Set<ApplicationModule> modules) {
        for (ApplicationModule module : modules) {
            if (module.isNonNewtonianViscosityModelVetoed())
                return true;
        }
        return false;
    }

    public static Set<ModulePanel> getCaseSetupPanels(Set<ApplicationModule> modules) {
        Set<ModulePanel> panels = new HashSet<>();
        for (ApplicationModule module : modules) {
            panels.addAll(module.getCaseSetupPanels());
        }
        return panels;
    }

    public static void updateTree(Set<ApplicationModule> modules, ModuleElementPanel viewElementPanel) {
        for (ApplicationModule module : modules) {
            TreeView treeView = module.getTreeView();
            if (treeView == null)
                continue;
            treeView.updateTree(viewElementPanel);
        }
    }

}

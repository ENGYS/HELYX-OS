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

package eu.engys.standardVOF;

import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;

import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.dictionary.FieldElement;
import eu.engys.core.modules.ApplicationModuleAdapter;
import eu.engys.core.modules.ModuleDefaults;
import eu.engys.core.modules.ModulePanel;
import eu.engys.core.modules.boundaryconditions.BoundaryConditionsView;
import eu.engys.core.modules.solutionmodelling.SolutionView;
import eu.engys.core.modules.tree.TreeView;
import eu.engys.core.project.Model;
import eu.engys.core.project.defaults.DefaultsProvider;
import eu.engys.core.project.state.MultiphaseModel;
import eu.engys.core.project.state.Solver;
import eu.engys.core.project.state.State;
import eu.engys.core.project.state.StateBuilder;
import eu.engys.core.project.zero.fields.Fields;
import eu.engys.core.project.zero.fields.FieldsDefaults;
import eu.engys.gui.casesetup.phases.PhasesPanel;

public class StandardVOFModule extends ApplicationModuleAdapter {

    private static final String MODULE_NAME = "standardVOF";

    public static final String VOF_LABEL = "VOF";
    public static final String VOF_KEY = "VOF";

    public static final Solver INTER_FOAM = new Solver("interFoam");
    public static final MultiphaseModel VOF_MODEL = new MultiphaseModel(VOF_LABEL, VOF_KEY, true, true);

    private StandardVOFSolutionView solutionView;
    private StandardVOFBoundaryConditionsView boundaryConditionsView;

    private PhasesPanel phasesPanel;
    private TreeView treeView;

    private double sigma;

    private Model model;

    private DefaultsProvider defaults;
    private StandardVOFReader reader;

    @Inject
    public StandardVOFModule(Model model) {
        this.model = model;
        this.solutionView = new StandardVOFSolutionView(this);
        this.boundaryConditionsView = new StandardVOFBoundaryConditionsView(this);

        this.phasesPanel = new PhasesPanel(model, new StandardVOFPhasesView(this, model));
        this.treeView = new StandardVOFTreeView(this, phasesPanel);

        this.reader = new StandardVOFReader(model, this);

        this.defaults = new ModuleDefaults(this, model.getDefaults(), model.getDefaults().getDefaultStateData()) {
            @Override
            public Dictionary getDefaultsFieldMapsFor(State state, String region) {
                Dictionary fieldMaps = super.getDefaultsFieldMapsFor(state, region);
                fixAlphaFieldName(fieldMaps);
                return fieldMaps;
            }
        };
    }

    private void fixAlphaFieldName(Dictionary fieldMaps) {
        if (model.getState().getMultiphaseModel().isMultiphase() && model.getState().getPhases() > 1 && fieldMaps != null && fieldMaps.found(Fields.ALPHA)) {
            String alpha = ((FieldElement) fieldMaps.remove(Fields.ALPHA)).getValue();
            fieldMaps.add(Fields.ALPHA + "." + model.getMaterials().getFirstMaterialName(), alpha);
        }
    }

    @Override
    public String getName() {
        return MODULE_NAME;
    }

    @Override
    public TreeView getTreeView() {
        return treeView;
    }

    @Override
    public Set<ModulePanel> getCaseSetupPanels() {
        Set<ModulePanel> panels = new HashSet<>();
        panels.add(phasesPanel);
        return panels;
    }

    @Override
    public void updateSolver(State state) {
        if (state.isTransient()) {
            if (state.isIncompressible()) {
                if (state.getMultiphaseModel().equals(VOF_MODEL)) {
                    state.setSolver(INTER_FOAM);
                }
            }
        }
    }

    @Override
    public void loadState() {
        reader.loadState();
    }

    @Override
    public void loadMaterials() {
        reader.loadMaterials();
    }

    @Override
    public void save() {
        new StandardVOFWriter(model, this).write();
    }

    @Override
    public void write() {
    }

    @Override
    public void saveDefaultsToProject() {
        if (isVOF()) {
            StateBuilder.saveDefaultsToProject(model, defaults);
        } else {
        }
    }

    @Override
    public Fields loadDefaultsFields(String region) {
        if (isVOF()) {
            return FieldsDefaults.loadFieldsFromDefaults(model.getState(), defaults, model.getPatches(), region);
        } else {
            return new Fields();
        }
    }

    @Override
    public SolutionView getSolutionView() {
        return solutionView;
    }

    @Override
    public BoundaryConditionsView getBoundaryConditionsView() {
        return boundaryConditionsView;
    }

    public boolean isVOF() {
        return model.getState().getMultiphaseModel().equals(VOF_MODEL);
    }

    public void setSigma(double sigma) {
        this.sigma = sigma;
    }

    public double getSigma() {
        return sigma;
    }

    /*
     * For test purposes only
     */
    public PhasesPanel getPhasesPanel() {
        return phasesPanel;
    }
}

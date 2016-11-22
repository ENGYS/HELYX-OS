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
package eu.engys.gui.casesetup.fields;

import static eu.engys.core.dictionary.Dictionary.TYPE;
import static eu.engys.core.project.system.FvSolution.INITIALISE_UBCS_KEY;
import static eu.engys.core.project.system.FvSolution.N_NON_ORTHOGONAL_CORRECTORS_KEY;
import static eu.engys.core.project.zero.fields.Fields.ALPHA;
import static eu.engys.core.project.zero.fields.Fields.U;
import static eu.engys.core.project.zero.fields.Initialisations.CELL_SET_KEY;
import static eu.engys.core.project.zero.fields.PotentialFlowInitialisation.INIT_UBCS;
import static eu.engys.core.project.zero.fields.PotentialFlowInitialisation.N_NON_ORTHOGONAL_CORRECTORS;

import java.util.Set;

import javax.inject.Inject;

import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.modules.ApplicationModule;
import eu.engys.core.project.Model;
import eu.engys.core.project.system.FvSolution;
import eu.engys.core.project.system.SetFieldsDict;
import eu.engys.core.project.zero.fields.Field;
import eu.engys.core.project.zero.fields.Fields;
import eu.engys.core.project.zero.fields.LoadSaveInitialisation;
import eu.engys.core.project.zero.fields.PotentialFlowInitialisation;
import eu.engys.util.bean.BeanModel;
import eu.engys.util.bean.BeanPanelBuilder;
import eu.engys.util.progress.ProgressMonitor;

public class StandardFieldsInitialisationPanel extends AbstractFieldsInitialisationPanel {

    private static final String POTENTIAL_FLOW_KEY = "potentialFlow";

    public static final String NON_ORTHOGONAL_CORRECTORS_LABEL = "Non-orthogonal Correctors";

    @Inject
    public StandardFieldsInitialisationPanel(Model model, Set<ApplicationModule> modules, ProgressMonitor monitor) {
        super(model, modules, monitor);
    }

    @Override
    protected Builder createPotentialFlowBuilder() {
        return new Builder() {
            private BeanModel<PotentialFlowInitialisation> beanModel;

            @Override
            public void build(BeanPanelBuilder builder, Field field) {
                if (beanModel != null) {
                    beanModel.release();
                }
                beanModel = createPotentialFlowDictionaryModel(field);
                
                boolean isPRGH = model.getState().isBuoyant() || model.getState().getMultiphaseModel().isOn(); 
                if(model.getState().isIncompressible() && !isPRGH){
                    builder.startBean(POTENTIAL_FLOW_LABEL, beanModel);
                    if (U.equals(field.getName())) {
                        builder.addComponent(INITIALISE_BOUNDARIES_LABEL, beanModel.bindBoolean(INIT_UBCS));
                        builder.addComponent(NON_ORTHOGONAL_CORRECTORS_LABEL, beanModel.bindIntegerPositive(N_NON_ORTHOGONAL_CORRECTORS));
                    }
                    builder.endBean();
                }
            }

            private BeanModel<PotentialFlowInitialisation> createPotentialFlowDictionaryModel(Field field) {
                return new BeanModel<>(new PotentialFlowInitialisation());
            }
        };
    }

    @Override
    public void save() {
        super.save();
        SetFieldsDict setFieldsDict = new SetFieldsDict();
        for (Field f : fieldBuilderMap.keySet()) {
            LoadSaveInitialisation loadSaveInitialisation = new LoadSaveInitialisation(model.getProject().getBaseDir(), model.getState(), monitor);
            Dictionary dictionary = loadSaveInitialisation.save(f.getInitialisation(), Fields.getFieldTypeByName(f.getName()));
            saveCellSetInitialisation(setFieldsDict, f, dictionary);
            savePotentialFlowInitialisation(f, dictionary);
        }
        model.getProject().getSystemFolder().setSetFieldsDict(setFieldsDict);
    }

    private void savePotentialFlowInitialisation(Field f, Dictionary dictionary) {
        if (f.getName().equals(U)) {
            FvSolution fvSolution = model.getProject().getSystemFolder().getFvSolution();
            if (dictionary.found(TYPE) && dictionary.lookup(TYPE).equals(POTENTIAL_FLOW_KEY)) {
                Dictionary potentialFlowDict = new Dictionary(POTENTIAL_FLOW_KEY);
                potentialFlowDict.add(N_NON_ORTHOGONAL_CORRECTORS_KEY, ((PotentialFlowInitialisation) f.getInitialisation()).getnNonOrthogonalCorrectors());
                potentialFlowDict.add(INITIALISE_UBCS_KEY, ((PotentialFlowInitialisation) f.getInitialisation()).isInitUBCS());
                fvSolution.add(potentialFlowDict);
            } else {
                fvSolution.remove(POTENTIAL_FLOW_KEY);
            }
        }
    }

    private void saveCellSetInitialisation(SetFieldsDict setFieldsDict, Field f, Dictionary dictionary) {
        if (f.getName().equals(ALPHA + "." + model.getMaterials().getFirstMaterialName())) {
            if (dictionary.found(TYPE) && dictionary.lookup(TYPE).equals(CELL_SET_KEY)) {
                new SetFieldsDictConverter(setFieldsDict, f).writeToSetFieldsDict(dictionary);
            }
        }
    }

}

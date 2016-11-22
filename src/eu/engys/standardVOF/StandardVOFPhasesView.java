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
package eu.engys.standardVOF;

import static eu.engys.core.project.constant.TransportProperties.SIGMA_KEY;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.dictionary.DimensionedScalar;
import eu.engys.core.dictionary.model.DictionaryModel;
import eu.engys.core.project.Model;
import eu.engys.gui.casesetup.phases.PhasesView;
import eu.engys.util.DimensionalUnits;
import eu.engys.util.ui.builder.PanelBuilder;

public class StandardVOFPhasesView implements PhasesView {

    public static final String SURFACE_TENSION_LABEL = "Surface Tension [N/m]";

    private static final Logger logger = LoggerFactory.getLogger(StandardVOFPhasesView.class);

    private Model model;
    private StandardVOFModule module;
    private DictionaryModel sigmaModel = new DictionaryModel(new Dictionary(""));

    private PanelBuilder parametersBuilder;

    public StandardVOFPhasesView(StandardVOFModule module, Model model) {
        this.module = module;
        this.model = model;
    }

    @Override
    public void layoutComponents(PanelBuilder parametersBuilder) {
        this.parametersBuilder = parametersBuilder;
    }

    @Override
    public void load(Model model) {
        if (module.isVOF()) {
            _layoutComponents();
            _load(model);
        }
    }

    private void _layoutComponents() {
        parametersBuilder.clear();
        parametersBuilder.addComponent(SURFACE_TENSION_LABEL, sigmaModel.bindDimensionedDouble(SIGMA_KEY, DimensionalUnits.KG_S2, 0D, Double.MAX_VALUE));
    }

    private void _load(Model model) {
        Dictionary dict = new Dictionary("");
        dict.add(new DimensionedScalar(SIGMA_KEY, String.valueOf(module.getSigma()), DimensionalUnits.KG_S2));
        sigmaModel.setDictionary(dict);
    }

    @Override
    public void save(Model model) {
        if (module.isVOF()) {
            _save(model);
        }
    }

    private void _save(Model model) {
        Dictionary sigmaDict = sigmaModel.getDictionary();
        if (sigmaDict.found(SIGMA_KEY)) {
            module.setSigma(sigmaDict.lookupScalar(SIGMA_KEY).doubleValue());
        }
    }

}

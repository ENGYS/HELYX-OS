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
package eu.engys.gui.casesetup.schemes;

import static eu.engys.core.project.system.ControlDict.FUNCTIONS_KEY;
import static eu.engys.core.project.system.FvSolution.CONSTANT_LIMITER_KEY;
import static eu.engys.core.project.system.FvSolution.COUPLED;
import static eu.engys.core.project.system.FvSolution.EQUATIONS_KEY;
import static eu.engys.core.project.system.FvSolution.HIGH_ORDER_KEY;
import static eu.engys.core.project.system.FvSolution.RELAXATION_FACTORS_KEY;
import static eu.engys.core.project.zero.fields.Fields.U;
import static eu.engys.gui.casesetup.schemes.CoupledSchemeTemplates.COUPLED_LES_SCHEMES;
import static eu.engys.gui.casesetup.schemes.SchemeUtils.searchTemplate;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.dictionary.ListField;
import eu.engys.core.project.Model;
import eu.engys.core.project.system.ControlDict;
import eu.engys.core.project.system.FvSchemes;
import eu.engys.core.project.system.FvSolution;

public class AdvectionSchemes {

    private static final Logger logger = LoggerFactory.getLogger(AdvectionSchemes.class);

    private static final String KEY = "key";
    private static final String LABEL = "label";
    private static final String FUNCTIONS = "functions";
    private static final String SCALAR = "scalar";
    private static final String VECTOR = "vector";

    private List<SchemeTemplate> scalar = new ArrayList<SchemeTemplate>();
    private List<SchemeTemplate> vector = new ArrayList<SchemeTemplate>();
    private List<SchemeTemplate> coupledRANSVector = new ArrayList<SchemeTemplate>();
    private List<SchemeTemplate> coupledLESVector = new ArrayList<SchemeTemplate>();

    private Model model;

    private CoupledSchemeTemplates coupledRansTemplates;

    public AdvectionSchemes(Model model) {
        this.model = model;
        this.coupledRansTemplates = new CoupledSchemeTemplates();

        Dictionary defaultSchemes = model.getDefaults().getDefaultSchemes();
        if (defaultSchemes != null) {

            ListField scalarSchemes = defaultSchemes.getList(SCALAR);
            for (Dictionary d : scalarSchemes.getDictionaries()) {
                scalar.add(new SchemeTemplate(d.lookup(KEY), d.lookup(LABEL), d.subDict(FUNCTIONS)));
            }

            ListField vectorSchemes = defaultSchemes.getList(VECTOR);
            for (Dictionary d : vectorSchemes.getDictionaries()) {
                vector.add(new SchemeTemplate(d.lookup(KEY), d.lookup(LABEL), d.subDict(FUNCTIONS)));
            }

            coupledRANSVector.addAll(coupledRansTemplates.getAll());

            for (SchemeTemplate s : vector) {
                if (ArrayUtils.contains(COUPLED_LES_SCHEMES, s.getKey())) {
                    coupledLESVector.add(s.copy());
                }
            }

        } else {
            logger.warn("Advection schemes defaults not found!");
        }
    }

    public Scheme load(String field) {
        boolean coupled_U_RAS = model.getState().isCoupled() && model.getState().isRANS() && field.equals(U);
        boolean coupled_LES = model.getState().isCoupled() && model.getState().isLES();

        if(coupled_U_RAS){
            return loadCoupledRAS(field);
        } else if(coupled_LES){
            return loadCoupledLES(field);
        } else {
            return loadSegregated(field);
        }
    }

    private Scheme loadSegregated(String field) {
        Scheme scheme = new Scheme();
        String schemeKey = SchemeUtils.readSchemeKeyFor(field, model.getProject().getSystemFolder().getFvSchemes());
        SchemeTemplate template = searchTemplate(field, schemeKey, field.startsWith(U) ? vector : scalar);
        if (template != null) {
            List<Double> values = template.extractValues(field, schemeKey);
            scheme.setTemplate(template);
            scheme.setField(field);
            scheme.setValue1(values.size() > 0 ? values.get(0) : 0);
            scheme.setValue2(values.size() > 1 ? values.get(1) : 0);
            scheme.setValue3(values.size() > 2 ? values.get(2) : 0);
        } else {
            logger.error("Template Scheme {} not found for field {}", schemeKey, field);
        }
        return scheme;
    }

    private Scheme loadCoupledRAS(String field) {
        Scheme scheme = new Scheme();
        coupledRansTemplates.load(model.getProject().getSystemFolder().getFvSolution(), field, scheme);
        return scheme;
    }

    private Scheme loadCoupledLES(String field) {
        Scheme scheme = new Scheme();
        String schemeKey = SchemeUtils.readSchemeKeyFor(field, model.getProject().getSystemFolder().getFvSchemes());
        SchemeTemplate template = searchTemplate(field, schemeKey, field.startsWith(U) ? coupledLESVector : scalar);
        if (template != null) {
            List<Double> values = template.extractValues(field, schemeKey);
            scheme.setTemplate(template);
            scheme.setField(field);
            scheme.setValue1(values.size() > 0 ? values.get(0) : 0);
            scheme.setValue2(values.size() > 1 ? values.get(1) : 0);
            scheme.setValue3(values.size() > 2 ? values.get(2) : 0);
        } else {
            logger.error("Template Scheme {} not found for field {}", schemeKey, field);
        }
        return scheme;
    }

    public void save(Scheme scheme) {
        boolean coupled_U_RAS = model.getState().isCoupled() && model.getState().isRANS() && scheme.getField().equals(U);
        boolean coupled_U_LES = model.getState().isCoupled() && model.getState().isLES() && scheme.getField().equals(U);

        if (coupled_U_RAS) {
            saveCoupledRAS(scheme);
        } else {
            saveSegregated(scheme);
            if (coupled_U_LES) {
                saveCoupledLES(scheme);
            }
        }
    }

    private void saveSegregated(Scheme scheme) {
        FvSchemes fvSchemes = model.getProject().getSystemFolder().getFvSchemes();

        Dictionary divSchemes = fvSchemes.getDivSchemes();
        String gradName = SchemeUtils.gradName(scheme.getField());
        String divName = SchemeUtils.divPhi(gradName);
        String value = "";
        if (divSchemes != null && scheme.getTemplate() != null) {
            String keyWithFieldName = scheme.getTemplate().getKey().replace("%s", gradName);
            if (scheme.getTemplate().hasValue1()) {
                if (scheme.getTemplate().hasValue3()) {
                    value = String.format(keyWithFieldName, scheme.getValue1(), scheme.getValue2(), scheme.getValue3());
                } else if (scheme.getTemplate().hasValue2()) {
                    value = String.format(keyWithFieldName, scheme.getValue1(), scheme.getValue2());
                } else {
                    value = String.format(keyWithFieldName, scheme.getValue1());
                }
            } else {
                value = keyWithFieldName;
            }
            divSchemes.add(divName, value);

            if (scheme.getTemplate().getFunctions() != null) {
                ControlDict controlDict = model.getProject().getSystemFolder().getControlDict();
                Dictionary functions = controlDict.subDict(FUNCTIONS_KEY);
                if (functions == null) {
                    functions = new Dictionary(FUNCTIONS_KEY);
                    controlDict.add(functions);
                }
                functions.merge(scheme.getTemplate().getFunctions());
            }
        }
    }

    private void saveCoupledRAS(Scheme scheme) {
        FvSolution fvSolution = model.getProject().getSystemFolder().getFvSolution();
        coupledRansTemplates.save(fvSolution, scheme);
    }

    private void saveCoupledLES(Scheme scheme) {
        FvSolution fvSolution = model.getProject().getSystemFolder().getFvSolution();
        if (fvSolution.found(COUPLED)) {
            fvSolution.subDict(COUPLED).add(CONSTANT_LIMITER_KEY, true);
        }
        if (fvSolution.found(RELAXATION_FACTORS_KEY) && fvSolution.subDict(RELAXATION_FACTORS_KEY).found(EQUATIONS_KEY)) {
            fvSolution.subDict(RELAXATION_FACTORS_KEY).subDict(EQUATIONS_KEY).add(HIGH_ORDER_KEY, 0);
        }
    }

    public List<SchemeTemplate> getScalarSchemes() {
        return scalar;
    }

    public List<SchemeTemplate> getVectorSchemes() {
        return vector;
    }

    public List<SchemeTemplate> getCoupledVectorSchemesRANS() {
        return coupledRANSVector;
    }

    public List<SchemeTemplate> getCoupledVectorSchemesLES() {
        return coupledLESVector;
    }

}

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

import static eu.engys.core.project.system.FvSolution.CONSTANT_LIMITER_KEY;
import static eu.engys.core.project.system.FvSolution.COUPLED;
import static eu.engys.core.project.system.FvSolution.EQUATIONS_KEY;
import static eu.engys.core.project.system.FvSolution.HIGH_ORDER_KEY;
import static eu.engys.core.project.system.FvSolution.RELAXATION_FACTORS_KEY;

import java.util.LinkedList;
import java.util.List;

import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.project.system.FvSolution;

public class CoupledSchemeTemplates {

    public static final String DYNAMIC_LABEL = "Dynamic - 2nd Order";
    public static final String UPWIND_1ST_ORDER_LABEL = "Upwind - 1st Order";
    public static final String UPWIND_2ND_ORDER_LABEL = "Upwind - 2nd Order";
    public static final String BLENDED_LABEL = "Blended";
    
    public static final String[] COUPLED_LES_SCHEMES = new String[]{
            "Gauss filteredLinear2V %f %f",
            "Gauss LUSTV unlimitedGrad(U)",
            "bounded Gauss localBlended filteredLinear2V %f %f LUSTV grad(U)"
            };
    
    public static final double DEFAULT_BLENDED_HIGH_ORDER = 0.75;

    private SchemeTemplate dynamicTemplate;
    private SchemeTemplate upwind1stOrderTemplate;
    private SchemeTemplate upwind2ndOrderTemplate;
    private SchemeTemplate blendedTemplate;

    public CoupledSchemeTemplates() {
        this.dynamicTemplate = new SchemeTemplate("", DYNAMIC_LABEL, null);
        this.upwind1stOrderTemplate = new SchemeTemplate("", UPWIND_1ST_ORDER_LABEL, null);
        this.upwind2ndOrderTemplate = new SchemeTemplate("", UPWIND_2ND_ORDER_LABEL, null);
        this.blendedTemplate = new SchemeTemplate("", BLENDED_LABEL, null) {
            @Override
            public boolean hasValue1() {
                return true;
            }
        };
    }

    public void load(FvSolution fvSolution, String field, Scheme scheme) {
        if (fvSolution.found(COUPLED)) {
            Dictionary coupledDict = fvSolution.subDict(COUPLED);
            boolean constantLimiter = coupledDict.lookupBoolean(CONSTANT_LIMITER_KEY);
            if (constantLimiter && fvSolution.found(RELAXATION_FACTORS_KEY) && fvSolution.subDict(RELAXATION_FACTORS_KEY).found(EQUATIONS_KEY)) {
                Dictionary eqDict = fvSolution.subDict(RELAXATION_FACTORS_KEY).subDict(EQUATIONS_KEY);
                if (eqDict.found(HIGH_ORDER_KEY)) {
                    double highOrder = eqDict.lookupDouble(HIGH_ORDER_KEY);
                    if (highOrder == 0) {
                        scheme.setTemplate(upwind1stOrderTemplate);
                        scheme.setValue1(DEFAULT_BLENDED_HIGH_ORDER);
                    } else if (highOrder == 1) {
                        scheme.setTemplate(upwind2ndOrderTemplate);
                        scheme.setValue1(DEFAULT_BLENDED_HIGH_ORDER);
                    } else {
                        scheme.setTemplate(blendedTemplate);
                        scheme.setValue1(highOrder);
                    }
                } else {
                    scheme.setTemplate(dynamicTemplate);
                    scheme.setValue1(DEFAULT_BLENDED_HIGH_ORDER);
                }
            } else {
                scheme.setTemplate(dynamicTemplate);
                scheme.setValue1(DEFAULT_BLENDED_HIGH_ORDER);
            }
        }
        scheme.setField(field);
    }

    public void save(FvSolution fvSolution, Scheme scheme) {
        if (fvSolution.found(COUPLED)) {
            Dictionary coupledDict = fvSolution.subDict(COUPLED);
            if (scheme.getTemplate().getLabel().equals(DYNAMIC_LABEL)) {
                coupledDict.add(CONSTANT_LIMITER_KEY, false);
            } else {
                coupledDict.add(CONSTANT_LIMITER_KEY, true);
            }
        }
        if (fvSolution.found(RELAXATION_FACTORS_KEY) && fvSolution.subDict(RELAXATION_FACTORS_KEY).found(EQUATIONS_KEY)) {
            Dictionary equationsDict = fvSolution.subDict(RELAXATION_FACTORS_KEY).subDict(EQUATIONS_KEY);
            if (scheme.getTemplate().getLabel().equals(UPWIND_1ST_ORDER_LABEL)) {
                equationsDict.add(HIGH_ORDER_KEY, 0);
            } else if (scheme.getTemplate().getLabel().equals(UPWIND_2ND_ORDER_LABEL)) {
                equationsDict.add(HIGH_ORDER_KEY, 1);
            } else if (scheme.getTemplate().getLabel().equals(BLENDED_LABEL)) {
                equationsDict.add(HIGH_ORDER_KEY, scheme.getValue1());
            } else {
                equationsDict.add(HIGH_ORDER_KEY, 1);
            }
        }
    }

    public List<SchemeTemplate> getAll() {
        List<SchemeTemplate> list = new LinkedList<>();
        list.add(dynamicTemplate);
        list.add(upwind1stOrderTemplate);
        list.add(upwind2ndOrderTemplate);
        list.add(blendedTemplate);
        return list;
    }

}

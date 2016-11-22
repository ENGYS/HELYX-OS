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
package eu.engys.core.project.zero.patches;

import static eu.engys.core.project.zero.fields.Fields.ALPHA_SGS;
import static eu.engys.core.project.zero.fields.Fields.ALPHA_T;
import static eu.engys.core.project.zero.fields.Fields.AOA;
import static eu.engys.core.project.zero.fields.Fields.CO2;
import static eu.engys.core.project.zero.fields.Fields.DT_AOA;
import static eu.engys.core.project.zero.fields.Fields.DT_CO2;
import static eu.engys.core.project.zero.fields.Fields.DT_SMOKE;
import static eu.engys.core.project.zero.fields.Fields.DT_W;
import static eu.engys.core.project.zero.fields.Fields.EPSILON;
import static eu.engys.core.project.zero.fields.Fields.ETA;
import static eu.engys.core.project.zero.fields.Fields.IDEFAULT;
import static eu.engys.core.project.zero.fields.Fields.K;
import static eu.engys.core.project.zero.fields.Fields.MUT;
import static eu.engys.core.project.zero.fields.Fields.MU_SGS;
import static eu.engys.core.project.zero.fields.Fields.NUT;
import static eu.engys.core.project.zero.fields.Fields.NU_SGS;
import static eu.engys.core.project.zero.fields.Fields.NU_TILDA;
import static eu.engys.core.project.zero.fields.Fields.OMEGA;
import static eu.engys.core.project.zero.fields.Fields.P;
import static eu.engys.core.project.zero.fields.Fields.POINT_DISPLACEMENT;
import static eu.engys.core.project.zero.fields.Fields.SMOKE;
import static eu.engys.core.project.zero.fields.Fields.T;
import static eu.engys.core.project.zero.fields.Fields.U;
import static eu.engys.core.project.zero.fields.Fields.W;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.project.zero.fields.Field;
import eu.engys.core.project.zero.fields.Fields;

public class MergeBoundaryConditions {

	private static final Logger logger = LoggerFactory.getLogger(MergeBoundaryConditions.class);

    private static final boolean VERBOSE = false;
	
    private Patches patches;
	private Fields fields;

    public MergeBoundaryConditions(Patches patches, Fields fields) {
        this.patches = patches;
        this.fields = fields;
    }

    public void execute() {
        for (Patch patch : patches) {
            String name = patch.getName();
            logger.debug("Merging boundary condition for patch {}", name);
            // System.out.println("*** patch "+name+" ***");
            Patches[] parallelPatches = patches.getParallelPatches();
            if (parallelPatches != null) {
                boolean emptyForAllProcessors = true;
                for (int i = 0; i < parallelPatches.length; i++) {
                    Map<String, Patch> patchesMap = parallelPatches[i].toMap();
                    // System.out.println("\tprocessor "+i);
                    if (patchesMap.containsKey(name)) {
                        Patch parallelPatch = patchesMap.get(name);
                        if (parallelPatch.getBoundaryConditions() != null) {
                            // System.out.println("MergeBoundaryConditions.execute() merge "+name);
                            merge(parallelPatch.getBoundaryConditions(), patch.getBoundaryConditions());
                        }
                        emptyForAllProcessors = emptyForAllProcessors && parallelPatch.isEmpty();
//                        System.out.println("*** AFTER MERGE ***"+parallelPatch.getBoundaryConditions().toDictionary());
//                        System.out.println("*** AFTER MERGE ***"+patch.getBoundaryConditions().toDictionary());
                    }
                }
                patch.setEmpty(patch.isEmpty() || emptyForAllProcessors);
            }
        }
    }

    private void merge(BoundaryConditions source, BoundaryConditions target) {
        mergeMomentum(source, target);
        mergeTurbulence(source, target);
        mergeRoughness(source, target);
        mergeThermal(source, target);
        mergeHumidity(source, target);
        mergeRadiation(source, target);
        mergePassiveScalars(source, target);
        mergePhase(source, target);
        mergeDisplacement(source, target);
    }

    private void mergeMomentum(BoundaryConditions source, BoundaryConditions target) {
        if (!source.getMomentum().isEmpty()) {
            mergeField(source.getMomentum(), target.getMomentum(), U);
            for (Field U : fields.getMultiphaseUFields()) {
            	mergeField(source.getMomentum(), target.getMomentum(), U.getName());
            }
            mergeField(source.getMomentum(), target.getMomentum(), P);
        }
    }

    private void mergeTurbulence(BoundaryConditions source, BoundaryConditions target) {
        if (!source.getTurbulence().isEmpty()) {
            mergeField(source.getTurbulence(), target.getTurbulence(), K);
            mergeField(source.getTurbulence(), target.getTurbulence(), OMEGA);
            mergeField(source.getTurbulence(), target.getTurbulence(), EPSILON);
            mergeField(source.getTurbulence(), target.getTurbulence(), NU_TILDA);
            mergeField(source.getTurbulence(), target.getTurbulence(), NUT);
            mergeField(source.getTurbulence(), target.getTurbulence(), NU_SGS);
            mergeField(source.getTurbulence(), target.getTurbulence(), MUT);
            mergeField(source.getTurbulence(), target.getTurbulence(), MU_SGS);
            mergeField(source.getTurbulence(), target.getTurbulence(), ALPHA_T);
            mergeField(source.getTurbulence(), target.getTurbulence(), ALPHA_SGS);
        }
    }

    private void mergeRoughness(BoundaryConditions source, BoundaryConditions target) {
        if (!source.getRoughness().isEmpty()) {
            mergeField(source.getRoughness(), target.getRoughness(), NUT);
            mergeField(source.getRoughness(), target.getRoughness(), NU_SGS);
            mergeField(source.getRoughness(), target.getRoughness(), MUT);
            mergeField(source.getRoughness(), target.getRoughness(), MU_SGS);
        }
    }

    private void mergeThermal(BoundaryConditions source, BoundaryConditions target) {
        if (!source.getThermal().isEmpty()) {
            mergeField(source.getThermal(), target.getThermal(), T);
        }
    }

    private void mergeHumidity(BoundaryConditions source, BoundaryConditions target) {
        if (!source.getHumidity().isEmpty()) {
            mergeField(source.getHumidity(), target.getHumidity(), W);
            mergeField(source.getHumidity(), target.getHumidity(), DT_W);
        }
    }

    private void mergeRadiation(BoundaryConditions source, BoundaryConditions target) {
        if (!source.getRadiation().isEmpty()) {
            mergeField(source.getRadiation(), target.getRadiation(), IDEFAULT);
        }
    }

    private void mergePassiveScalars(BoundaryConditions source, BoundaryConditions target) {
        if (!source.getPassiveScalars().isEmpty()) {
            mergeField(source.getPassiveScalars(), target.getPassiveScalars(), AOA);
            mergeField(source.getPassiveScalars(), target.getPassiveScalars(), DT_AOA);
            mergeField(source.getPassiveScalars(), target.getPassiveScalars(), CO2);
            mergeField(source.getPassiveScalars(), target.getPassiveScalars(), DT_CO2);
            mergeField(source.getPassiveScalars(), target.getPassiveScalars(), SMOKE);
            mergeField(source.getPassiveScalars(), target.getPassiveScalars(), DT_SMOKE);
        }
    }

    private void mergePhase(BoundaryConditions source, BoundaryConditions target) {
        if (!source.getPhase().isEmpty()) {
            mergeField(source.getPhase(), target.getPhase(), ETA);
        	for (Field af : fields.getAlphaFields()) {
        		mergeField(source.getPhase(), target.getPhase(), af.getName());
			}
        }
    }
    
    private void mergeDisplacement(BoundaryConditions source, BoundaryConditions target) {
        if (!source.getDisplacement().isEmpty()) {
            mergeField(source.getDisplacement(), target.getDisplacement(), POINT_DISPLACEMENT);
        }
    }

    static void mergeField(Dictionary source, Dictionary target, String field) {
        if (source.isDictionary(field) /* && target.isDictionary(field) */) {
            Dictionary fieldSource = source.subDict(field);
            // Dictionary fieldTarget = target.subDict(field);

            if (BoundaryConditions.isPlaceHolder(fieldSource)) {
                info("\t\t"+field+" PH");
                /* DO NOTHING */
            } else {
                info("\t\t"+field+" UN");
                target.add(new Dictionary(fieldSource));
            }
        } else if (source.isDictionary(field)) {
            info("ERROR: missing TARGET "+field+" dictionary");
        } else {
            info("ERROR: missing SOURCE "+field+" dictionary");
            // System.out.println(""+source+target);
        }
    }

    public void mergeExcludingNonUniform(BoundaryConditions source, BoundaryConditions target) {
        if (!source.getMomentum().isEmpty()) {
            mergeExcludingNonUniform(source.getMomentum(), target.getMomentum(), U);
            for (Field U : fields.getMultiphaseUFields()) {
            	mergeExcludingNonUniform(source.getMomentum(), target.getMomentum(), U.getName());
            }
            mergeExcludingNonUniform(source.getMomentum(), target.getMomentum(), P);
        }
        if (!source.getTurbulence().isEmpty()) {
            mergeExcludingNonUniform(source.getTurbulence(), target.getTurbulence(), K);
            mergeExcludingNonUniform(source.getTurbulence(), target.getTurbulence(), OMEGA);
            mergeExcludingNonUniform(source.getTurbulence(), target.getTurbulence(), EPSILON);
            mergeExcludingNonUniform(source.getTurbulence(), target.getTurbulence(), NU_TILDA);
            mergeExcludingNonUniform(source.getTurbulence(), target.getTurbulence(), NUT);
            mergeExcludingNonUniform(source.getTurbulence(), target.getTurbulence(), NU_SGS);
            mergeExcludingNonUniform(source.getTurbulence(), target.getTurbulence(), MUT);
            mergeExcludingNonUniform(source.getTurbulence(), target.getTurbulence(), MU_SGS);
            mergeExcludingNonUniform(source.getTurbulence(), target.getTurbulence(), ALPHA_T);
            mergeExcludingNonUniform(source.getTurbulence(), target.getTurbulence(), ALPHA_SGS);
        }
        if (!source.getRoughness().isEmpty()) {
            mergeExcludingNonUniform(source.getRoughness(), target.getRoughness(), NUT);
            mergeExcludingNonUniform(source.getRoughness(), target.getRoughness(), NU_SGS);
            mergeExcludingNonUniform(source.getRoughness(), target.getRoughness(), MUT);
            mergeExcludingNonUniform(source.getRoughness(), target.getRoughness(), MU_SGS);
        }
        if (!source.getDisplacement().isEmpty()) {
            mergeExcludingNonUniform(source.getDisplacement(), target.getDisplacement(), POINT_DISPLACEMENT);
        }
        if (!source.getThermal().isEmpty()) {
            mergeExcludingNonUniform(source.getThermal(), target.getThermal(), T);
        }
        if (!source.getHumidity().isEmpty()) {
            mergeExcludingNonUniform(source.getHumidity(), target.getHumidity(), W);
            mergeExcludingNonUniform(source.getHumidity(), target.getHumidity(), DT_W);
        }
        if (!source.getRadiation().isEmpty()) {
            mergeExcludingNonUniform(source.getRadiation(), target.getRadiation(), IDEFAULT);
        }
        if (!source.getPassiveScalars().isEmpty()) {
            mergeExcludingNonUniform(source.getPassiveScalars(), target.getPassiveScalars(), AOA);
            mergeExcludingNonUniform(source.getPassiveScalars(), target.getPassiveScalars(), DT_AOA);
            mergeExcludingNonUniform(source.getPassiveScalars(), target.getPassiveScalars(), CO2);
            mergeExcludingNonUniform(source.getPassiveScalars(), target.getPassiveScalars(), DT_CO2);
            mergeExcludingNonUniform(source.getPassiveScalars(), target.getPassiveScalars(), SMOKE);
            mergeExcludingNonUniform(source.getPassiveScalars(), target.getPassiveScalars(), DT_SMOKE);
        }
        if (!source.getPhase().isEmpty()) {
            mergeExcludingNonUniform(source.getPhase(), target.getPhase(), ETA);
        	for (Field af : fields.getAlphaFields()) {
        		mergeExcludingNonUniform(source.getPhase(), target.getPhase(), af.getName());
			}
        }
    }

    private void mergeExcludingNonUniform(Dictionary source, Dictionary target, String field) {
        if (source.isDictionary(field) && target.isDictionary(field)) {
            Dictionary fieldSource = source.subDict(field);
            Dictionary fieldTarget = target.subDict(field);
            if (haveSameType(fieldSource, fieldTarget)) {
                if (BoundaryConditions.isNonUniform(fieldTarget)) {
                    fieldTarget.merge(fieldSource);
                } else {
                    /* DO NOTHING */
                }
            } else {
                /* DO NOTHING */
            }
        } else if (source.isDictionary(field) && !target.isDictionary(field)) {
            // System.err.println("ERROR: missing target "+field+" dictionary");
        } else if (!source.isDictionary(field) && target.isDictionary(field)) {
            // System.err.println("ERROR: missing source "+field+" dictionary");
        } else {
            // System.err.println("ERROR: missing both "+field+" dictionary");
        }
    }

    private boolean haveSameType(Dictionary d1, Dictionary d2) {
        return d1.isField(Dictionary.TYPE) && d2.isField(Dictionary.TYPE) && d1.lookup(Dictionary.TYPE).equals(d2.lookup(Dictionary.TYPE));
    }


    public void excludeNonUniform(BoundaryConditions target) {
        if (!target.getMomentum().isEmpty()) {
            excludeNonUniform(target.getMomentum(), U);
            for (Field U : fields.getMultiphaseUFields()) {
                excludeNonUniform(target.getMomentum(), U.getName());
            }
            excludeNonUniform(target.getMomentum(), P);
        }
        if (!target.getTurbulence().isEmpty()) {
            excludeNonUniform(target.getTurbulence(), K);
            excludeNonUniform(target.getTurbulence(), OMEGA);
            excludeNonUniform(target.getTurbulence(), EPSILON);
            excludeNonUniform(target.getTurbulence(), NU_TILDA);
            excludeNonUniform(target.getTurbulence(), NUT);
            excludeNonUniform(target.getTurbulence(), NU_SGS);
            excludeNonUniform(target.getTurbulence(), MUT);
            excludeNonUniform(target.getTurbulence(), MU_SGS);
            excludeNonUniform(target.getTurbulence(), ALPHA_T);
            excludeNonUniform(target.getTurbulence(), ALPHA_SGS);
        }
        if (!target.getRoughness().isEmpty()) {
            excludeNonUniform(target.getRoughness(), NUT);
            excludeNonUniform(target.getRoughness(), NU_SGS);
            excludeNonUniform(target.getRoughness(), MUT);
            excludeNonUniform(target.getRoughness(), MU_SGS);
        }
        if (!target.getDisplacement().isEmpty()) {
            excludeNonUniform(target.getDisplacement(), POINT_DISPLACEMENT);
        }
        if (!target.getThermal().isEmpty()) {
            excludeNonUniform(target.getThermal(), T);
        }
        if (!target.getHumidity().isEmpty()) {
            excludeNonUniform(target.getHumidity(), W);
            excludeNonUniform(target.getHumidity(), DT_W);
        }
        if (!target.getRadiation().isEmpty()) {
            excludeNonUniform(target.getRadiation(), IDEFAULT);
        }
        if (!target.getPassiveScalars().isEmpty()) {
            excludeNonUniform(target.getPassiveScalars(), AOA);
            excludeNonUniform(target.getPassiveScalars(), DT_AOA);
            excludeNonUniform(target.getPassiveScalars(), CO2);
            excludeNonUniform(target.getPassiveScalars(), DT_CO2);
            excludeNonUniform(target.getPassiveScalars(), SMOKE);
            excludeNonUniform(target.getPassiveScalars(), DT_SMOKE);
        }
        if (!target.getPhase().isEmpty()) {
            excludeNonUniform(target.getPhase(), ETA);
            for (Field af : fields.getAlphaFields()) {
                excludeNonUniform(target.getPhase(), af.getName());
            }
        }
    }

    private void excludeNonUniform(Dictionary target, String field) {
        if (target.isDictionary(field)) {
            Dictionary fieldTarget = target.subDict(field);
            if (BoundaryConditions.isNonUniform(fieldTarget)) {
                if (field.equals(Fields.U)) {
                    BoundaryConditions.replaceNonUniformVector(fieldTarget);
                } else {
                    BoundaryConditions.replaceNonUniformScalar(fieldTarget);
                }
            }
        }
    }

    private static void info(String msg) {
        if (VERBOSE) System.err.println(msg);
    }
    
}

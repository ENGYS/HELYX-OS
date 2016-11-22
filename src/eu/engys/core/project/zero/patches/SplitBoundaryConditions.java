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

import static eu.engys.core.project.zero.fields.Fields.P;
import static eu.engys.core.project.zero.fields.Fields.U;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.project.zero.fields.Field;
import eu.engys.core.project.zero.fields.Fields;

public class SplitBoundaryConditions {

    public static boolean VERBOSE = false;

    private static final Logger logger = LoggerFactory.getLogger(SplitBoundaryConditions.class);

    private Patches patches;
    private Fields fields;

    public SplitBoundaryConditions(Patches patches, Fields fields) {
        this.patches = patches;
        this.fields = fields;
    }

    public void execute() {
        for (Patch patch : patches) {
            String patchName = patch.getName();
            logger.info("Splitting boundary condition for patch {}", patchName);
            // System.out.println("*** patch " + patchName + " *** " + patch.getBoundaryConditions().toDictionary());
            Patches[] parallelPatches = patches.getParallelPatches();
            if (parallelPatches != null) {
                for (int i = 0; i < parallelPatches.length; i++) {
                    info("\t processor " + i);
                    Map<String, Patch> patchesMap = parallelPatches[i].toMap();
                    if (patchesMap.containsKey(patchName)) {
                        Patch parallelPatch = patchesMap.get(patchName);
                        parallelPatch.setPhysicalType(patch.getPhysicalType());
                        if (patch.getBoundaryConditions() != null && parallelPatch.getBoundaryConditions() != null) {
                            // System.out.println("SplitBoundaryConditions.execute() split BEFORE "+patchName+parallelPatch.getBoundaryConditions().toDictionary());
                            splitTo(patch.getBoundaryConditions(), parallelPatch.getBoundaryConditions());
                            // System.out.println("SplitBoundaryConditions.execute() split AFTER "+patchName+parallelPatch.getBoundaryConditions().toDictionary());
                        } else if (patch.getBoundaryConditions() == null) {
                            // System.out.println("SplitBoundaryConditions.execute() GUI patch "+patch.getName()+" has null BC");
                            parallelPatch.setBoundaryConditions(null);
                        } else if (parallelPatch.getBoundaryConditions() == null) {
                            // System.out.println("SplitBoundaryConditions.execute() parallel patch "+patch.getName()+" has null BC");
                            /* do nothing ? */
                        }
                    }
                }
            }
        }
    }

    private void splitTo(BoundaryConditions source, BoundaryConditions target) {
        splitMomentum(source, target);
        splitTurbulence(source, target);
        splitRoughness(source, target);
        splitThermal(source, target);
        splitHumidity(source, target);
        splitRadiation(source, target);
        splitPassiveScalars(source, target);
        splitPhase(source, target);
        splitDisplacement(source, target);
    }

    private void splitMomentum(BoundaryConditions source, BoundaryConditions target) {
        if (target.getMomentum() != null) {
            splitField(source.getMomentum(), target.getMomentum(), U);
            for (Field U : fields.getMultiphaseUFields()) {
                splitField(source.getMomentum(), target.getMomentum(), U.getName());
            }
            splitField(source.getMomentum(), target.getMomentum(), P);
        }
    }

    private void splitTurbulence(BoundaryConditions source, BoundaryConditions target) {
        if (target.getTurbulence() != null) {
            splitField(source.getTurbulence(), target.getTurbulence(), Fields.K);
            splitField(source.getTurbulence(), target.getTurbulence(), Fields.OMEGA);
            splitField(source.getTurbulence(), target.getTurbulence(), Fields.EPSILON);
            splitField(source.getTurbulence(), target.getTurbulence(), Fields.NU_TILDA);
            splitField(source.getTurbulence(), target.getTurbulence(), Fields.NUT);
            splitField(source.getTurbulence(), target.getTurbulence(), Fields.NU_SGS);
            splitField(source.getTurbulence(), target.getTurbulence(), Fields.MUT);
            splitField(source.getTurbulence(), target.getTurbulence(), Fields.MU_SGS);
            splitField(source.getTurbulence(), target.getTurbulence(), Fields.ALPHA_T);
            splitField(source.getTurbulence(), target.getTurbulence(), Fields.ALPHA_SGS);
        }
    }

    private void splitRoughness(BoundaryConditions source, BoundaryConditions target) {
        if (target.getRoughness() != null) {
            splitField(source.getRoughness(), target.getRoughness(), Fields.NUT);
            splitField(source.getRoughness(), target.getRoughness(), Fields.NU_SGS);
            splitField(source.getRoughness(), target.getRoughness(), Fields.MUT);
            splitField(source.getRoughness(), target.getRoughness(), Fields.MU_SGS);
        }
    }

    private void splitThermal(BoundaryConditions source, BoundaryConditions target) {
        if (target.getThermal() != null) {
            splitField(source.getThermal(), target.getThermal(), Fields.T);
        }
    }

    private void splitHumidity(BoundaryConditions source, BoundaryConditions target) {
        if (target.getHumidity() != null) {
            splitField(source.getHumidity(), target.getHumidity(), Fields.W);
            splitField(source.getHumidity(), target.getHumidity(), Fields.DT_W);
        }
    }

    private void splitRadiation(BoundaryConditions source, BoundaryConditions target) {
        if (target.getRadiation() != null) {
            splitField(source.getRadiation(), target.getRadiation(), Fields.IDEFAULT);
        }
    }

    private void splitPassiveScalars(BoundaryConditions source, BoundaryConditions target) {
        if (target.getPassiveScalars() != null) {
            splitField(source.getPassiveScalars(), target.getPassiveScalars(), Fields.AOA);
            splitField(source.getPassiveScalars(), target.getPassiveScalars(), Fields.DT_AOA);
            splitField(source.getPassiveScalars(), target.getPassiveScalars(), Fields.CO2);
            splitField(source.getPassiveScalars(), target.getPassiveScalars(), Fields.DT_CO2);
            splitField(source.getPassiveScalars(), target.getPassiveScalars(), Fields.SMOKE);
            splitField(source.getPassiveScalars(), target.getPassiveScalars(), Fields.DT_SMOKE);
        }
    }

    private void splitPhase(BoundaryConditions source, BoundaryConditions target) {
        if (target.getPhase() != null) {
            splitField(source.getPhase(), target.getPhase(), Fields.ETA);
            for (Field af : fields.getAlphaFields()) {
                splitField(source.getPhase(), target.getPhase(), af.getName());
            }
        }
    }

    private void splitDisplacement(BoundaryConditions source, BoundaryConditions target) {
        if (target.getDisplacement() != null) {
            splitField(source.getDisplacement(), target.getDisplacement(), Fields.POINT_DISPLACEMENT);
        }
    }

    static void splitField(Dictionary source, Dictionary target, String field) {
        if (source.isDictionary(field) && target.isDictionary(field)) {
            Dictionary fieldSource = source.subDict(field);
            Dictionary fieldTarget = target.subDict(field);
            if (haveSameType(fieldSource, fieldTarget)) {
                if (BoundaryConditions.isPlaceHolder(fieldTarget)) {
                    if (BoundaryConditions.isPlaceHolder(fieldSource)) {
                        /* DO NOTHING */
                        info("\t\t " + field + " (PH + PH) DO NOTHING");
                    } else {
                        /* DO NOTHING */
                        info("\t\t " + field + " (UN + PH) SAVE EXCLUDING UNIFORM" + target);
                        fieldTarget.merge(fieldSource, BoundaryConditions.PLACE_HOLDER_KEYS);
                    }
                } else {
                    if (BoundaryConditions.isPlaceHolder(fieldSource)) {
                        /* BOH? */
                        info("\t\t " + field + " (PH + UN) BOH?" + fieldSource + fieldTarget);
                    } else {
                        /* SAVE */
                        if (BoundaryConditions.isNonUniform(fieldSource)) {
                            /* DO NOTHING */
                            info("\t\t " + field + " (NUN + UN) DO NOTHING");
                            fieldTarget.merge(fieldSource, BoundaryConditions.PLACE_HOLDER_KEYS);
                        } else {
                            info("\t\t " + field + " (UN + UN) SAVE");
                            // System.out.println("SplitBoundaryConditions.splitField() SOURCE BEFORE "+fieldSource);
                            // System.out.println("SplitBoundaryConditions.splitField() TARGET BEFORE "+fieldTarget);
                            fieldTarget.clear();
                            fieldTarget.merge(fieldSource);
                            // System.out.println("SplitBoundaryConditions.splitField() SOURCE AFTER "+fieldSource);
                            // System.out.println("SplitBoundaryConditions.splitField() TARGET AFTER "+fieldTarget);
                        }
                    }
                }
            } else {
                // System.err.println("ERROR: different type for "+field+" dictionary");
                fieldTarget.clear();
                fieldTarget.merge(fieldSource);
            }
        } else if (source.isDictionary(field) && !target.isDictionary(field)) {
            // System.err.println("ERROR: missing target "+field+" dictionary");
        } else if (!source.isDictionary(field) && target.isDictionary(field)) {
            // System.err.println("ERROR: missing source "+field+" dictionary");
        } else {
            // System.err.println("ERROR: missing both "+field+" dictionary");
        }
    }

    private static void info(String msg) {
	    if (VERBOSE) System.err.println(msg);
    }

    private static boolean haveSameType(Dictionary d1, Dictionary d2) {
        return d1.isField("type") && d2.isField("type") && d1.lookup("type").equals(d2.lookup("type"));
    }

}

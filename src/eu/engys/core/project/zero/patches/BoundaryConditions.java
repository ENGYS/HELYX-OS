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

import static eu.engys.core.project.zero.fields.Fields.ALPHA;
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
import static eu.engys.core.project.zero.fields.Fields.P_RGH;
import static eu.engys.core.project.zero.fields.Fields.SMOKE;
import static eu.engys.core.project.zero.fields.Fields.T;
import static eu.engys.core.project.zero.fields.Fields.U;
import static eu.engys.core.project.zero.fields.Fields.W;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.project.zero.fields.Fields;

public class BoundaryConditions {

    private static final Logger logger = LoggerFactory.getLogger(BoundaryConditions.class);

    public static final String[] PLACE_HOLDER_KEYS = { "value", "refValue", "inletValue", "gradient", "inletDirection" };

    public static void replaceNonUniformVector(Dictionary d) {
        for (String ph : PLACE_HOLDER_KEYS) {
            if (isNonUniform(d, ph)) {
                d.add(ph, "uniform (0 0 0)");
                logger.warn("Nonuniform field {} replaced with {}", ph, d.lookup(ph));
            }
        }
    }

    public static void replaceNonUniformScalar(Dictionary d) {
        for (String ph : PLACE_HOLDER_KEYS) {
            if (isNonUniform(d, ph)) {
                d.add(ph, "uniform 0");
                logger.warn("Nonuniform field {} replaced with {}", ph, d.lookup(ph));
            }
        }
    }

    public static boolean isNonUniform(Dictionary d) {
        for (String ph : PLACE_HOLDER_KEYS) {
            if (isNonUniform(d, ph)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isPlaceHolder(Dictionary d) {
        for (String ph : PLACE_HOLDER_KEYS) {
            if (isPlaceHolder(d, ph)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isPlaceHolder(Dictionary d, String key) {
        return (d.isField(key) && d.lookup(key).contains("nonuniform 0()")) || (d.isList2(key) && d.getList2(key).isEmpty());
    }

    private static boolean isNonUniform(Dictionary d, String key) {
        return (d.isField(key) && d.lookup(key).contains("nonuniform List")) || (d.isList2(key) && d.getList2(key).isNonuniform());
    }

    private static final String BOUNDARY_CONDITIONS = "boundaryConditions";
    private static final String MOMENTUM = "momentum";
    private static final String TURBULENCE = "turbulence";
    private static final String THERMAL = "thermal";
    private static final String HUMIDITY = "humidity";
    private static final String RADIATION = "radiation";
    private static final String PASSIVE_SCALARS = "passiveScalars";
    private static final String PHASE = "phase";
    private static final String ROUGHNESS = "roughness";
    private static final String DISPLACEMENT = "displacement";

    private Dictionary momentum;
    private Dictionary turbulence;
    private Dictionary thermal;
    private Dictionary humidity;
    private Dictionary radiation;
    private Dictionary passiveScalars;
    private Dictionary phase;
    private Dictionary roughness;
    private Dictionary displacement;

    public BoundaryConditions() {
        setMomentum(new Dictionary(MOMENTUM));
        setTurbulence(new Dictionary(TURBULENCE));
        setThermal(new Dictionary(THERMAL));
        setHumidity(new Dictionary(HUMIDITY));
        setRadiation(new Dictionary(RADIATION));
        setPassiveScalars(new Dictionary(PASSIVE_SCALARS));
        setPhase(new Dictionary(PHASE));
        setRoughness(new Dictionary(ROUGHNESS));
        setDisplacement(new Dictionary(DISPLACEMENT));
    }

    public BoundaryConditions(BoundaryConditions defaults) {
        this();
        if (defaults != null) {
            getMomentum().merge(defaults.getMomentum());
            getTurbulence().merge(defaults.getTurbulence());
            getThermal().merge(defaults.getThermal());
            getHumidity().merge(defaults.getHumidity());
            getRadiation().merge(defaults.getRadiation());
            getPassiveScalars().merge(defaults.getPassiveScalars());
            getPhase().merge(defaults.getPhase());
            getRoughness().merge(defaults.getRoughness());
            getDisplacement().merge(defaults.getDisplacement());
        }
    }

    public Dictionary toDictionary() {
        Dictionary dict = new Dictionary(BOUNDARY_CONDITIONS);
        dict.merge(getMomentum());
        dict.merge(getTurbulence());
        dict.merge(getThermal());
        dict.merge(getHumidity());
        dict.merge(getRadiation());
        dict.merge(getPassiveScalars());
        dict.merge(getPhase());
        dict.merge(getRoughness());
        dict.merge(getDisplacement());
        return dict;
    }

    public void fromDictionary(Dictionary dictionary) {
        for (Dictionary d : dictionary.getDictionaries()) {
            add(d.getName(), d);
        }
    }

    // public void loadFromField(String name, Dictionary patchInField) {
    // Dictionary dict = new Dictionary(name);
    // dict.merge(patchInField);
    // add(name, patchInField);
    // }

    public void add(String name, Dictionary original) {
        Dictionary dictionary = new Dictionary(original);
        dictionary.setName(name.equals(Fields.P_RGH) ? Fields.P : name);

        if (isMomentum(name)) {
            getMomentum().add(dictionary);
        } else if (isTurbulence(name)) {
            getTurbulence().add(dictionary);
        } else if (isThermal(name)) {
            getThermal().add(dictionary);
        } else if (isHumidity(name)) {
            getHumidity().add(dictionary);
        } else if (isRadiation(name)) {
            getRadiation().add(dictionary);
        } else if (isPassiveScalar(name)) {
            getPassiveScalars().add(dictionary);
        } else if (isPhase(name)) {
            getPhase().add(dictionary);
        } else if (isRoughness(name)) {
            getRoughness().add(dictionary);
        } else if (isDisplacement(name)) {
            getDisplacement().add(dictionary);
        } else {

        }
    }

    private boolean isRoughness(String name) {
        String[] list = new String[] { NUT, MUT, NU_SGS, MU_SGS };
        return Arrays.asList(list).contains(name);
    }

    private boolean isDisplacement(String name) {
        String[] list = new String[] { POINT_DISPLACEMENT };
        return Arrays.asList(list).contains(name);
    }

    public static boolean isPassiveScalar(String name) {
        String[] list = new String[] { AOA, DT_AOA, CO2, DT_CO2, SMOKE, DT_SMOKE };
        return Arrays.asList(list).contains(name);
    }

    public static boolean isPhase(String name) {
        return name.equals(ETA) || (name.startsWith(ALPHA) && !name.equals(ALPHA_SGS) && !name.equals(ALPHA_T));
    }

    public static boolean isRadiation(String name) {
        return name.equals(IDEFAULT);
    }

    public static boolean isHumidity(String name) {
        return name.equals(W) || name.equals(DT_W);
    }

    public static boolean isThermal(String name) {
        return name.equals(T);
    }

    public static boolean isTurbulence(String name) {
        String[] list = new String[] { K, OMEGA, EPSILON, NU_TILDA, /* NUT, NU_SGS, MUT, MU_SGS, */ALPHA_SGS, ALPHA_T };
        return Arrays.asList(list).contains(name);
    }

    public static boolean isMomentum(String name) {
        return name.equals(P) || name.equals(P_RGH) || name.startsWith(U);
    }

    public Dictionary getMomentum() {
        return momentum;
    }

    public Dictionary setMomentum(Dictionary momentum) {
        this.momentum = momentum;
        return momentum;
    }

    public Dictionary getTurbulence() {
        return turbulence;
    }

    public Dictionary setTurbulence(Dictionary turbulence) {
        this.turbulence = turbulence;
        return turbulence;
    }

    public Dictionary getThermal() {
        return thermal;
    }

    public Dictionary setThermal(Dictionary thermal) {
        this.thermal = thermal;
        return thermal;
    }

    public Dictionary getHumidity() {
        return humidity;
    }

    public Dictionary setHumidity(Dictionary humidity) {
        this.humidity = humidity;
        return humidity;
    }

    public Dictionary getRadiation() {
        return radiation;
    }

    public Dictionary setRadiation(Dictionary radiation) {
        this.radiation = radiation;
        return radiation;
    }

    public Dictionary getPassiveScalars() {
        return passiveScalars;
    }

    public Dictionary setPassiveScalars(Dictionary passiveScalars) {
        this.passiveScalars = passiveScalars;
        return passiveScalars;
    }

    public Dictionary getPhase() {
        return phase;
    }

    public Dictionary setPhase(Dictionary phase) {
        this.phase = phase;
        return phase;
    }

    public Dictionary getRoughness() {
        return roughness;
    }

    public void setRoughness(Dictionary roughness) {
        this.roughness = roughness;
    }

    public Dictionary getDisplacement() {
        return displacement;
    }

    public void setDisplacement(Dictionary displacement) {
        this.displacement = displacement;
    }

    public static BoundaryConditions toMomentumRoughness(Dictionary dict) {
        BoundaryConditions bc = new BoundaryConditions();
        bc.setMomentum(dict);
        bc.setRoughness(dict);
        return bc;
    }

    public static BoundaryConditions toMomentumThermal(Dictionary dict) {
        BoundaryConditions bc = new BoundaryConditions();
        bc.setMomentum(dict);
        bc.setThermal(dict);
        return bc;
    }

    public static BoundaryConditions toTurbulence(Dictionary turbulence) {
        BoundaryConditions bc = new BoundaryConditions();
        bc.setTurbulence(turbulence);
        return bc;
    }

    public static BoundaryConditions toThermal(Dictionary thermal) {
        BoundaryConditions bc = new BoundaryConditions();
        bc.setThermal(thermal);
        return bc;
    }

    public static BoundaryConditions toRadiation(Dictionary radiation) {
        BoundaryConditions bc = new BoundaryConditions();
        bc.setRadiation(radiation);
        return bc;
    }

    public static BoundaryConditions toPhase(Dictionary phase) {
        BoundaryConditions bc = new BoundaryConditions();
        bc.setPhase(phase);
        return bc;
    }

    public static BoundaryConditions toPassiveScalars(Dictionary passiveScalars) {
        BoundaryConditions bc = new BoundaryConditions();
        bc.setPassiveScalars(passiveScalars);
        return bc;
    }

    public static BoundaryConditions toHumidity(Dictionary humidity) {
        BoundaryConditions bc = new BoundaryConditions();
        bc.setHumidity(humidity);
        return bc;
    }

    public static BoundaryConditions toMomentum(Dictionary momentum) {
        BoundaryConditions bc = new BoundaryConditions();
        bc.setMomentum(momentum);
        return bc;
    }
}

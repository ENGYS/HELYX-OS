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
package eu.engys.core.project.zero.fields;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import eu.engys.core.project.state.State;

public class Fields extends LinkedHashMap<String, Field> {

    public static final String U = "U";
    public static final String P = "p";
    public static final String P_RGH = "p_rgh";
    public static final String T = "T";
    public static final String K = "k";
    public static final String OMEGA = "omega";
    public static final String EPSILON = "epsilon";
    public static final String NU_TILDA = "nuTilda";
    public static final String NUT = "nut";
    public static final String MUT = "mut";
    public static final String RHO = "rho";
    public static final String MU_SGS = "muSgs";
    public static final String ALPHA_1 = "alpha1";
    public static final String ALPHA_SGS = "alphaSgs";
    public static final String ALPHA_T = "alphat";
    public static final String ALPHA_S = "alphas";
    public static final String IDEFAULT = "IDefault";
    public static final String NU_SGS = "nuSgs";
    public static final String ILAMBDA = "ILambda";
    public static final String ALPHA = "alpha";
    public static final String ETA = "eta";
    public static final String FINAL = "Final";

    public static final String P_CORR = "pcorr";
    public static final String POINT_MOTION_U = "pointMotionU";
    public static final String POINT_DISPLACEMENT = "pointDisplacement";
    public static final String CELL_MOTION_U = "cellMotionU";
    public static final String CELL_DISPLACEMENT = "cellDisplacement";

    /*
     * Passive scalars
     */
    public static final String AOA = "AoA";
    public static final String D_AOA = "DAoA";
    public static final String DT_AOA = "DtAoA";
    public static final String SCT_AOA = "SctAoA";

    public static final String CO2 = "CO2";
    public static final String D_CO2 = "DCO2";
    public static final String DT_CO2 = "DtCO2";
    public static final String SCT_CO2 = "SctCO2";

    public static final String SMOKE = "smoke";
    public static final String D_SMOKE = "Dsmoke";
    public static final String DT_SMOKE = "Dtsmoke";
    public static final String SCT_SMOKE = "Sctsmoke";

    public static final String W = "w";
    public static final String D_W = "Dw";
    public static final String DT_W = "Dtw";
    public static final String SCT_W = "Sctw";
    public static final String CP_W = "Cpw";

    private Fields[] parallelFields;

    public static final String EDITABLE_FIELDS[] = new String[] { U, P, P_RGH, K, OMEGA, EPSILON, NU_TILDA, T, W, AOA, CO2, SMOKE };
    private static final String PASSIVE_SCALARS[] = new String[] { W, AOA, CO2, SMOKE };

    public static FieldType getFieldTypeByName(String name) {
        if (name.startsWith(U) || name.equals(POINT_DISPLACEMENT)) {
            return FieldType.VECTOR;
        } else if (name.equals(POINT_MOTION_U)) {
            return FieldType.POINT;
        } else {
            return FieldType.SCALAR;
        }
    }

    public List<Field> listFields(FieldFilter filter) {
        List<Field> list = new LinkedList<Field>();
        for (Field field : orderedFields()) {
            if (filter.accept(field)) {
                list.add(field);
            }
        }
        return list;
    }

    public List<Field> orderedFieldsExcludingPassiveScalars() {
        List<Field> list = new LinkedList<Field>();
        for (Field field : orderedFields()) {
            if (!isPassiveScalar(field)) {
                list.add(field);
            }
        }
        return list;
    }

    public List<Field> orderedFields() {
        List<Field> list = new LinkedList<Field>();
        list.addAll(getMultiphaseUFields());
        for (String fieldName : EDITABLE_FIELDS) {
            if (this.containsKey(fieldName)) {
                Field field = this.get(fieldName);
                if (field.isVisible()) {
                    list.add(field);
                }
            }
        }
        list.addAll(getAlphaFields());
        return list;
    }

    public List<Field> orderedScalarFields() {
        List<Field> list = new LinkedList<Field>();
        for (Field f : orderedFields()) {
            if (f.isScalar()) {
                list.add(f);
            }
        }
        return list;
    }

    public List<String> orderedFieldsScalarNames() {
        List<String> list = new LinkedList<>();
        for (Field field : orderedScalarFields()) {
            list.add(field.getName());
        }
        return list;
    }

    public List<String> orderedFieldsNames() {
        List<String> list = new LinkedList<>();
        for (Field field : orderedFields()) {
            list.add(field.getName());
        }
        return list;
    }

    public List<Field> getMultiphaseUFields() {
        List<Field> list = new LinkedList<Field>();
        for (Field field : this.values()) {
            String name = field.getName();
            if (name.startsWith(U) && !name.equals(U)) {
                list.add(field);
            }
        }
        return list;
    }

    public List<Field> getAlphaFields() {
        List<Field> list = new LinkedList<Field>();
        for (Field field : this.values()) {
            String name = field.getName();
            if (name.startsWith(ALPHA) && !name.equals(ALPHA_S) && !name.equals(ALPHA_SGS) && !name.equals(ALPHA_T)) {
                list.add(field);
            }
        }
        return list;
    }

    private boolean isPassiveScalar(Field field) {
        for (String s : PASSIVE_SCALARS) {
            if (s.equals(field.getName())) {
                return true;
            }
        }
        return false;
    }

    public List<String> fieldNames() {
        return new LinkedList<>(keySet());
    }

    public Fields[] getParallelFields() {
        return parallelFields;
    }

    public Fields getFieldsForProcessor(int processor) {
        return parallelFields[processor];
    }

    public void setParallelFields(Fields[] parallelFields) {
        this.parallelFields = parallelFields;
    }

    public void newParallelFields(int processors) {
        parallelFields = new Fields[processors];
        for (int i = 0; i < parallelFields.length; i++) {
            parallelFields[i] = new Fields();
        }
    }

    public boolean hasCellSetInitialisationField() {
        for (Field f : values()) {
            if (f.getInitialisation() instanceof CellSetInitialisation) {
                return true;
            }
        }
        return false;
    }

    public boolean hasPotentialFlowInitialisationField() {
        Field uField = get(Fields.U);
        if (uField != null) {
            return uField.getInitialisation() instanceof PotentialFlowInitialisation;
        }
        return false;
    }

    public boolean hasInitialiseUBCsField() {
        if(hasPotentialFlowInitialisationField()){
            return ((PotentialFlowInitialisation)get(Fields.U).getInitialisation()).isInitUBCS();
        }
        return false;
    }

    @Override
    public void clear() {
        super.clear();
        if (parallelFields != null) {
            for (Fields pf : parallelFields) {
                pf.clear();
            }
        }
    }

    public static String ALPHA(String phaseName) {
        return ALPHA + phaseName;
    }

    public static String PHASE(String fieldName) {
        return fieldName.replace(ALPHA, "");
    }

    public static String PHASE_OS(String fieldName) {
        return fieldName.replace(ALPHA + ".", "");
    }

    public void merge(Fields fields) {
        // putAll(fields);
        for (String key : fields.keySet()) {
            if (containsKey(key)) {
                get(key).merge(fields.get(key));
            } else {
                put(key, fields.get(key));
            }
        }

        if (fields.getParallelFields() != null) {
            if (parallelFields == null) {
                newParallelFields(fields.getParallelFields().length);
            }

            Fields[] parallelFields2 = fields.getParallelFields();
            for (int i = 0; i < parallelFields.length; i++) {
                if (parallelFields2.length > i) {
                    parallelFields[i].merge(parallelFields2[i]);
                }
            }
        }
    }

    public void fixPVisibility(State state) {
        boolean stateCompressible = state.isCompressible();
        boolean stateBuoyant = state.isBuoyant();
        boolean prghPresent = containsKey(P_RGH);
        boolean pPresent = containsKey(P);

        if (pPresent && prghPresent) {
            if (stateCompressible && stateBuoyant) {
                get(P).setVisible(false);
                get(P_RGH).setVisible(true);
            } else {
                get(P).setVisible(true);
                get(P_RGH).setVisible(false);
            }
        }
    }

    public void resetInternalFields() {
        _resetInternalFields(this);
        if (parallelFields != null) {
            for (Fields fields : parallelFields) {
                _resetInternalFields(fields);
            }
        }
    }

    private void _resetInternalFields(Fields fields) {
        for (Field field : fields.values()) {
            if (field.getName().equals(Fields.U)) {
                field.setInternalField(new ArrayInternalField(new double[3]));
            } else {
                field.setInternalField(new ScalarInternalField(0));
            }
        }
    }
}

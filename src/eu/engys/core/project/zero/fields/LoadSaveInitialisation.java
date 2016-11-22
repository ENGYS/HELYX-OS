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

import static eu.engys.core.dictionary.Dictionary.TYPE;
import static eu.engys.core.dictionary.Dictionary.VALUE;
import static eu.engys.core.dictionary.DictionaryBuilder.newDictionary;
import static eu.engys.core.project.system.FvSolution.N_NON_ORTHOGONAL_CORRECTORS_KEY;
import static eu.engys.core.project.zero.fields.Field.INITIALISATION_KEY;
import static eu.engys.core.project.zero.fields.Initialisations.BOUNDARY_VALUE_KEY;
import static eu.engys.core.project.zero.fields.Initialisations.CELL_SET_KEY;
import static eu.engys.core.project.zero.fields.Initialisations.DEFAULT_KEY;
import static eu.engys.core.project.zero.fields.Initialisations.DEFAULT_VALUE_KEY;
import static eu.engys.core.project.zero.fields.Initialisations.FIXED_VALUE_KEY;
import static eu.engys.core.project.zero.fields.Initialisations.INITIALISE_UBCS_KEY;
import static eu.engys.core.project.zero.fields.Initialisations.I_KEY;
import static eu.engys.core.project.zero.fields.Initialisations.L_KEY;
import static eu.engys.core.project.zero.fields.Initialisations.PATCH_KEY;
import static eu.engys.core.project.zero.fields.Initialisations.POTENTIAL_FLOW_KEY;
import static eu.engys.core.project.zero.fields.Initialisations.PRANDTL_KEY;
import static eu.engys.core.project.zero.fields.Initialisations.RHO_REF_KEY;
import static eu.engys.core.project.zero.fields.Initialisations.SET_SOURCES_KEY;
import static eu.engys.core.project.zero.fields.Initialisations.TURBULENT_IL_KEY;
import static eu.engys.core.project.zero.fields.Initialisations.TYPE_KEY;
import static eu.engys.core.project.zero.fields.Initialisations.UREF_KEY;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.dictionary.ListField;
import eu.engys.core.project.geometry.Surface;
import eu.engys.core.project.state.State;
import eu.engys.core.project.zero.fields.CellSetInitialisation.ScalarSurface;
import eu.engys.core.project.zero.fields.CellSetInitialisation.VectorSurface;
import eu.engys.util.progress.ProgressMonitor;

public class LoadSaveInitialisation {

    private ProgressMonitor monitor;
    private File baseDir;
    private State state;

    public LoadSaveInitialisation(File baseDir, State state, ProgressMonitor monitor) {
        this.baseDir = baseDir;
        this.state = state;
        this.monitor = monitor;
    }

    public Initialisation load(Dictionary dict, FieldType fieldType) {
        // System.out.println("LoadSaveInitialisation.load() " + fieldType);
        String type = dict.lookup(TYPE_KEY);
        switch (type) {
        case POTENTIAL_FLOW_KEY:
            PotentialFlowInitialisation pfi = new PotentialFlowInitialisation();
            if (dict.found(INITIALISE_UBCS_KEY)) {
                pfi.setInitUBCS(dict.lookupBoolean(INITIALISE_UBCS_KEY));
            }
            if (dict.found(RHO_REF_KEY)) {
                pfi.setRhoRef(dict.lookupDouble(RHO_REF_KEY));
            }
            if (dict.found(N_NON_ORTHOGONAL_CORRECTORS_KEY)) {
                pfi.setnNonOrthogonalCorrectors(dict.lookupInt(N_NON_ORTHOGONAL_CORRECTORS_KEY));
            }
            return pfi;

        case BOUNDARY_VALUE_KEY:
            BoundaryValueInitialisation bvi = new BoundaryValueInitialisation();
            if (dict.found(PATCH_KEY)) {
                bvi.setPatch(dict.lookup(PATCH_KEY));
            }
            return bvi;

        case PRANDTL_KEY:
            return new PrandtlInitialisation();

        case TURBULENT_IL_KEY:
            TurbulentILInitialisation ti = new TurbulentILInitialisation();
            if (dict.found(I_KEY)) {
                ti.setI(dict.lookupDouble(I_KEY));
            }
            if (dict.found(L_KEY)) {
                ti.setL(dict.lookupDouble(L_KEY));
            }
            if (dict.found(UREF_KEY)) {
                ti.setUref(dict.lookupDouble(UREF_KEY));
            }
            return ti;

        case DEFAULT_KEY:
            return new DefaultInitialisation();

        case FIXED_VALUE_KEY:
            if (fieldType == FieldType.SCALAR) {
                FixedScalarInitialisation init = new FixedScalarInitialisation();
                if (dict.found(VALUE)) {
                    init.setValue(dict.lookupDoubleUniform(VALUE));
                }
                return init;
            } else if (fieldType == FieldType.VECTOR || fieldType == FieldType.POINT) {
                FixedVectorInitialisation init = new FixedVectorInitialisation();
                if (dict.found(VALUE)) {
                    init.setValue(dict.lookupDoubleArray(VALUE));
                }
                return init;
            } else {
                return new DefaultInitialisation();
            }

        case CELL_SET_KEY:
            if (fieldType == FieldType.SCALAR) {
                ScalarCellSetInitialisation scsi = new ScalarCellSetInitialisation();
                scsi.setDefaultValue(dict.lookupDoubleUniform(DEFAULT_VALUE_KEY));
                List<ScalarSurface> surfaces = new ArrayList<>();
                ListField list = dict.getList(SET_SOURCES_KEY);
                TopoSetReader tsr = new TopoSetReader(baseDir, monitor);
                for (Dictionary d : list.getDictionaries()) {
                    if (d.found(VALUE)) {
                        surfaces.add(new ScalarSurface(tsr.read(d), d.lookupDouble(VALUE)));
                    }
                }
                scsi.setSurfaces(surfaces);
                return scsi;
            } else if (fieldType == FieldType.VECTOR || fieldType == FieldType.POINT) {
                VectorCellSetInitialisation vcsi = new VectorCellSetInitialisation();
                vcsi.setDefaultValue(dict.lookupDoubleArray(DEFAULT_VALUE_KEY));
                List<VectorSurface> surfaces = new ArrayList<>();
                ListField list = dict.getList(SET_SOURCES_KEY);
                TopoSetReader tsr = new TopoSetReader(baseDir, monitor);
                for (Dictionary d : list.getDictionaries()) {
                    if (d.found(VALUE)) {
                        surfaces.add(new VectorSurface(tsr.read(d), d.lookupDoubleArray(VALUE)));
                    }
                }
                vcsi.setSurfaces(surfaces);
                return vcsi;
            } else {
                return new DefaultInitialisation();
            }

        default:
            return new DefaultInitialisation();
        }
    }

    public Dictionary save(Initialisation initialisation, FieldType fieldType) {
        if (initialisation instanceof PotentialFlowInitialisation) {
            PotentialFlowInitialisation pfi = (PotentialFlowInitialisation) initialisation;
            if (fieldType == FieldType.VECTOR) {
                if (state.isCompressible()) {
                    return newDictionary(INITIALISATION_KEY).field(TYPE, POTENTIAL_FLOW_KEY).field(INITIALISE_UBCS_KEY, pfi.isInitUBCS()).field(RHO_REF_KEY, pfi.getRhoRef()).done();
                } else {
                    return newDictionary(INITIALISATION_KEY).field(TYPE, POTENTIAL_FLOW_KEY).field(INITIALISE_UBCS_KEY, pfi.isInitUBCS()).done();
                }
            } else {
                return newDictionary(INITIALISATION_KEY).field(TYPE, POTENTIAL_FLOW_KEY).done();
            }

        } else if (initialisation instanceof BoundaryValueInitialisation) {
            BoundaryValueInitialisation bvi = (BoundaryValueInitialisation) initialisation;
            return newDictionary(INITIALISATION_KEY).field(TYPE, BOUNDARY_VALUE_KEY).field(PATCH_KEY, bvi.getPatch()).done();
        } else if (initialisation instanceof PrandtlInitialisation) {
            PrandtlInitialisation pi = (PrandtlInitialisation) initialisation;
            return newDictionary(INITIALISATION_KEY).field(TYPE, PRANDTL_KEY).done();
        } else if (initialisation instanceof TurbulentILInitialisation) {
            TurbulentILInitialisation ti = (TurbulentILInitialisation) initialisation;
            return newDictionary(INITIALISATION_KEY).field(TYPE, TURBULENT_IL_KEY).field(I_KEY, ti.getI()).field(L_KEY, ti.getL()).field(UREF_KEY, ti.getUref()).done();
        } else if (initialisation instanceof DefaultInitialisation) {
            DefaultInitialisation di = (DefaultInitialisation) initialisation;
            return newDictionary(INITIALISATION_KEY).field(TYPE, DEFAULT_KEY).done();
        } else if (initialisation instanceof FixedScalarInitialisation) {
            FixedScalarInitialisation fsi = (FixedScalarInitialisation) initialisation;
            return newDictionary(INITIALISATION_KEY).field(TYPE, FIXED_VALUE_KEY).fieldUniform(VALUE, fsi.getValue()).done();
        } else if (initialisation instanceof FixedVectorInitialisation) {
            FixedVectorInitialisation fvi = (FixedVectorInitialisation) initialisation;
            return newDictionary(INITIALISATION_KEY).field(TYPE, FIXED_VALUE_KEY).fieldUniform(VALUE, fvi.getValue()).done();
        } else if (initialisation instanceof ScalarCellSetInitialisation) {
            ScalarCellSetInitialisation scsi = (ScalarCellSetInitialisation) initialisation;
            return newDictionary(INITIALISATION_KEY).field(TYPE, CELL_SET_KEY).fieldUniform(DEFAULT_VALUE_KEY, scsi.getDefaultValue()).list(SET_SOURCES_KEY, getDictionaries(scsi)).done();
        } else if (initialisation instanceof VectorCellSetInitialisation) {
            VectorCellSetInitialisation vcsi = (VectorCellSetInitialisation) initialisation;
            return newDictionary(INITIALISATION_KEY).field(TYPE, CELL_SET_KEY).fieldUniform(DEFAULT_VALUE_KEY, vcsi.getDefaultValue()).list(SET_SOURCES_KEY, getDictionaries(vcsi)).done();
        } else {
            return newDictionary(INITIALISATION_KEY).field(TYPE, DEFAULT_KEY).done();
        }
    }

    private Dictionary[] getDictionaries(ScalarCellSetInitialisation scsi) {
        List<ScalarSurface> surfaces = scsi.getSurfaces();
        Dictionary[] dicts = new Dictionary[surfaces.size()];
        TopoSetWriter tsw = new TopoSetWriter(baseDir, monitor);
        for (int i = 0; i < dicts.length; i++) {
            Surface s = surfaces.get(i).getSurface();
            double value = surfaces.get(i).getValue();
            dicts[i] = tsw.write(s);
            dicts[i].add(VALUE, value);
        }
        return dicts;
    }

    private Dictionary[] getDictionaries(VectorCellSetInitialisation vcsi) {
        List<VectorSurface> surfaces = vcsi.getSurfaces();
        Dictionary[] dicts = new Dictionary[surfaces.size()];
        TopoSetWriter tsw = new TopoSetWriter(baseDir, monitor);
        for (int i = 0; i < dicts.length; i++) {
            Surface s = surfaces.get(i).getSurface();
            double[] value = surfaces.get(i).getValue();
            dicts[i] = tsw.write(s);
            dicts[i].add(VALUE, value);
        }
        return dicts;
    }
    
    // For tests only
    public void setState(State state) {
        this.state = state;
    }
}

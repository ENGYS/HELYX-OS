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

import static eu.engys.core.project.zero.fields.Fields.U;

import java.io.File;
import java.util.Arrays;

import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.project.state.State;
import eu.engys.util.progress.ProgressMonitor;

public class Field {

	public static final String FIELD_DEFINITION_KEY = "fieldDefinition";
    public static final String INITIALISATION_KEY = "initialisation";
    public static final String BOUNDARY_FIELD = "boundaryField";
	public static final String DIMENSIONS = "dimensions";
	public static final String INTERNAL_FIELD = "internalField";

	private InternalField internalField;
	private Dictionary boundaryField;
	
	private String[] initialisationMethods;
	private Initialisation initialisation = new DefaultInitialisation();
	private Dictionary definition = new Dictionary(FIELD_DEFINITION_KEY);
	private String name;
	private String dimensions;
	private transient boolean visible;

	public Field(String name) {
		this.name = name;
		this.dimensions = null;
		this.internalField = null;
		this.boundaryField = new Dictionary(BOUNDARY_FIELD);
		this.visible = true;
	}
	
	public boolean isVisible() {
		return visible;
	}
	
	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
        this.name = name;
    }

	public Initialisation getInitialisation() {
		return initialisation;
	}

	public void setInitialisation(Initialisation initialisation) {
		this.initialisation = initialisation;
	}
	
	public boolean isScalar(){
	    return !name.startsWith(U);
	}

	public void setInitialisationMethods(String[] initMethods) {
		this.initialisationMethods = initMethods;
	}

	public String[] getInitialisationMethods() {
		return initialisationMethods;
	}

	public Dictionary getDefinition() {
		return definition;
	}

	public void setDefinition(Dictionary definition) {
		this.definition = definition;
	}

	public Dictionary getBoundaryField() {
		return boundaryField;
	}

	public String getDimensions() {
		return dimensions;
	}

	public void read(File file) {
		new FieldReader(this).read(file);
	}

	public void setDimensions(String dimensions) {
		this.dimensions = dimensions;
	}

	public void setInternalField(String internalField) {
		if (internalField != null && !internalField.isEmpty()) {
			this.internalField = new FieldReader(this).readValue(internalField);
		} else {
			this.internalField = null;
		}
	}

	public void setInternalField(InternalField internalField) {
		this.internalField = internalField;
	}

	public void setBoundaryField(Dictionary boundaryField) {
		this.boundaryField = boundaryField;
	}

	public InternalField getInternalField() {
		return internalField;
	}

	public void write(File zeroDir, ProgressMonitor monitor) {
		new FieldWriter(this, monitor).write(zeroDir);
	}

	public void bufferInternalField(File zeroDir, ProgressMonitor monitor) {
		new FieldWriter(this, monitor).bufferInternalField(zeroDir);
	}

    public void merge(Field field) {
        if (field.boundaryField != null && !field.boundaryField.isEmpty() ) {
            setBoundaryField(field.boundaryField);
        }
        if (field.definition != null) {
            if (this.definition != null) {
                this.definition.merge(field.definition);
            } else {
                setDefinition(new Dictionary(field.definition));
            }
        }
        if (field.dimensions != null) {
            setDimensions(field.dimensions);
        }
        if (field.initialisation != null) {
//            if (this.initialisation != null) {
//                this.initialisation.merge(field.initialisation);
//            } else {
                setInitialisation(field.initialisation);
//            }
        }
        if (field.initialisationMethods != null) {
            setInitialisationMethods(field.initialisationMethods);
        }
        if (field.internalField != null) {
            setInternalField(field.internalField);
        }
        
        setVisible(field.visible);
    }
    
    public double getInternalFieldScalarValue(State state) {
        if (state.isCompressible() || state.isBuoyant()) {
            if (getInternalField() instanceof ScalarInternalField) {
                return ((ScalarInternalField) getInternalField()).getValue()[0][0];
            } else {
                return 0;
            }
        } else {
            return 0;
        }
    }

    public double[] getInternalFieldArrayValue(State state) {
        if (state.isCompressible() || state.isBuoyant()) {
            if (getInternalField() instanceof ArrayInternalField) {
                return ((ArrayInternalField) getInternalField()).getValue()[0];
            } else {
                return new double[] { 0, 0, 0 };
            }
        } else {
            return new double[] { 0, 0, 0 };
        }
    }
    
    @Override
    public String toString() {
        return name + " " + Fields.getFieldTypeByName(name) + " " + Arrays.toString(initialisationMethods) + " " + initialisation + definition;
    }

	public static void main(String[] args) {
		/* U */
		Field U0 = new Field("U");
		Field U1 = new Field("U");
		Field U2 = new Field("U");
		Field U3 = new Field("U");
		U0.read(new File("/home/stefano/ENGYS/examples/HELYX2/DanicaRANS/processor0/0/U"));
		U1.read(new File("/home/stefano/ENGYS/examples/HELYX2/DanicaRANS/processor1/0/U"));
		U2.read(new File("/home/stefano/ENGYS/examples/HELYX2/DanicaRANS/processor2/0/U"));
		U3.read(new File("/home/stefano/ENGYS/examples/HELYX2/DanicaRANS/processor3/0/U"));

		/* p */
		Field p0 = new Field("p");
		Field p1 = new Field("p");
		Field p2 = new Field("p");
		Field p3 = new Field("p");
		p0.read(new File("/home/stefano/ENGYS/examples/HELYX2/DanicaRANS/processor0/0/p"));
		p1.read(new File("/home/stefano/ENGYS/examples/HELYX2/DanicaRANS/processor1/0/p"));
		p2.read(new File("/home/stefano/ENGYS/examples/HELYX2/DanicaRANS/processor2/0/p"));
		p3.read(new File("/home/stefano/ENGYS/examples/HELYX2/DanicaRANS/processor3/0/p"));

		/* epsilon */
		Field epsilon0 = new Field("epsilon");
		Field epsilon1 = new Field("epsilon");
		Field epsilon2 = new Field("epsilon");
		Field epsilon3 = new Field("epsilon");
		epsilon0.read(new File("/home/stefano/ENGYS/examples/HELYX2/DanicaRANS/processor0/0/epsilon"));
		epsilon1.read(new File("/home/stefano/ENGYS/examples/HELYX2/DanicaRANS/processor1/0/epsilon"));
		epsilon2.read(new File("/home/stefano/ENGYS/examples/HELYX2/DanicaRANS/processor2/0/epsilon"));
		epsilon3.read(new File("/home/stefano/ENGYS/examples/HELYX2/DanicaRANS/processor3/0/epsilon"));

		/* nut */
		Field nut0 = new Field("nut");
		Field nut1 = new Field("nut");
		Field nut2 = new Field("nut");
		Field nut3 = new Field("nut");
		nut0.read(new File("/home/stefano/ENGYS/examples/HELYX2/DanicaRANS/processor0/0/nut"));
		nut1.read(new File("/home/stefano/ENGYS/examples/HELYX2/DanicaRANS/processor1/0/nut"));
		nut2.read(new File("/home/stefano/ENGYS/examples/HELYX2/DanicaRANS/processor2/0/nut"));
		nut3.read(new File("/home/stefano/ENGYS/examples/HELYX2/DanicaRANS/processor3/0/nut"));

		/* k */
		Field k0 = new Field("k");
		Field k1 = new Field("k");
		Field k2 = new Field("k");
		Field k3 = new Field("k");
		k0.read(new File("/home/stefano/ENGYS/examples/HELYX2/DanicaRANS/processor0/0/k"));
		k1.read(new File("/home/stefano/ENGYS/examples/HELYX2/DanicaRANS/processor1/0/k"));
		k2.read(new File("/home/stefano/ENGYS/examples/HELYX2/DanicaRANS/processor2/0/k"));
		k3.read(new File("/home/stefano/ENGYS/examples/HELYX2/DanicaRANS/processor3/0/k"));

	}
}

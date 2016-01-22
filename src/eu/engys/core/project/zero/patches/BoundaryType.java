/*--------------------------------*- Java -*---------------------------------*\
 |		 o                                                                   |                                                                                     
 |    o     o       | HelyxOS: The Open Source GUI for OpenFOAM              |
 |   o   O   o      | Copyright (C) 2012-2016 ENGYS                          |
 |    o     o       | http://www.engys.com                                   |
 |       o          |                                                        |
 |---------------------------------------------------------------------------|
 |	 License                                                                 |
 |   This file is part of HelyxOS.                                           |
 |                                                                           |
 |   HelyxOS is free software; you can redistribute it and/or modify it      |
 |   under the terms of the GNU General Public License as published by the   |
 |   Free Software Foundation; either version 2 of the License, or (at your  |
 |   option) any later version.                                              |
 |                                                                           |
 |   HelyxOS is distributed in the hope that it will be useful, but WITHOUT  |
 |   ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or   |
 |   FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License   |
 |   for more details.                                                       |
 |                                                                           |
 |   You should have received a copy of the GNU General Public License       |
 |   along with HelyxOS; if not, write to the Free Software Foundation,      |
 |   Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA            |
\*---------------------------------------------------------------------------*/


package eu.engys.core.project.zero.patches;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.ImageIcon;

public class BoundaryType {
    
    public static final String MAPPED_PATCH_KEY = "mappedPatch";
	public static final String MAPPED_WALL_KEY = "mappedWall";
    public static final String CYCLIC_KEY = "cyclic";
	public static final String CYCLIC_AMI_KEY = "cyclicAMI";
	public static final String EMPTY_KEY = "empty";
	public static final String INLET_KEY = "inlet";
	public static final String OPENING_KEY = "opening";
	public static final String OUTLET_KEY = "outlet";
	public static final String PATCH_KEY = "patch";
	public static final String PROCESSOR_KEY = "processor";
	public static final String PROCESSOR_CYCLIC_KEY = "processorCyclic";
	public static final String SYMMETRY_KEY = "symmetry";
	public static final String SYMMETRY_PLANE_KEY = "symmetryPlane";
	public static final String WALL_KEY = "wall";
	public static final String WEDGE_KEY = "wedge";
	public static final String FREE_SURFACE_KEY = "freeSurface";

	public static final String CYCLIC_LABEL = "Cyclic";
	public static final String CYCLIC_AMI_LABEL = "Cyclic AMI";
	public static final String EMPTY_LABEL = "Empty";
	public static final String INLET_LABEL = "Inlet";
	public static final String OPENING_LABEL = "Opening";
	public static final String OUTLET_LABEL = "Outlet";
	public static final String PATCH_LABEL = "Patch";
	public static final String PROCESSOR_LABEL = "Processor";
	public static final String PROCESSOR_CYCLIC_LABEL = "Processor Cyclic";
	public static final String SYMMETRY_LABEL = "Symmetry";
	public static final String SYMMETRY_PLANE_LABEL = "Symmetry Plane";
	public static final String WALL_LABEL = "Wall";
	public static final String WEDGE_LABEL = "Wedge";
	public static final String FREE_SURFACE_LABEL = "Free Surface";

	public static final BoundaryType INLET = new BoundaryType(INLET_KEY, INLET_LABEL, true);
	public static final BoundaryType OUTLET = new BoundaryType(OUTLET_KEY, OUTLET_LABEL, true);
	public static final BoundaryType OPENING = new BoundaryType(OPENING_KEY, OPENING_LABEL, true);
	public static final BoundaryType WALL = new BoundaryType(WALL_KEY, WALL_LABEL, true);
	public static final BoundaryType PATCH = new BoundaryType(PATCH_KEY, PATCH_LABEL, true);
	public static final BoundaryType SYMMETRY = new BoundaryType(SYMMETRY_KEY, SYMMETRY_LABEL, false);
	public static final BoundaryType SYMMETRY_PLANE = new BoundaryType(SYMMETRY_PLANE_KEY, SYMMETRY_PLANE_LABEL, false);
	public static final BoundaryType CYCLIC = new BoundaryType(CYCLIC_KEY, CYCLIC_LABEL, true);
	public static final BoundaryType CYCLIC_AMI = new BoundaryType(CYCLIC_AMI_KEY, CYCLIC_AMI_LABEL, true);
	public static final BoundaryType EMPTY = new BoundaryType(EMPTY_KEY, EMPTY_LABEL, false);
	public static final BoundaryType WEDGE = new BoundaryType(WEDGE_KEY, WEDGE_LABEL, false);
	public static final BoundaryType PROCESSOR = new BoundaryType(PROCESSOR_KEY, PROCESSOR_LABEL, false);
	public static final BoundaryType PROCESSOR_CYCLIC = new BoundaryType(PROCESSOR_CYCLIC_KEY, PROCESSOR_CYCLIC_LABEL, false);
	public static final BoundaryType FREE_SURFACE = new BoundaryType(FREE_SURFACE_KEY, FREE_SURFACE_LABEL, true);

	private static Map<String, BoundaryType> registeredTypes = new LinkedHashMap<>();
	private static Map<String, Icon> registeredTypesIcon = new LinkedHashMap<>();

	public static void registerBoundaryType(BoundaryType type) {
		registeredTypes.put(type.getKey(), type);
		registeredTypesIcon.put(type.getKey(), getIcon("eu/engys/resources/images/" + type.getKey() + "16.png"));
	}

	public static void unregisterBoundaryType(BoundaryType type) {
	    registeredTypes.remove(type.getKey());
	    registeredTypesIcon.remove(type.getKey());
	}

	private static Icon getIcon(String string) {
		try {
			return new ImageIcon(BoundaryType.class.getClassLoader().getResource(string));
		} catch (Exception e) {
			return null;
		}
	}

	public static Map<String, BoundaryType> getRegisteredBoundaryTypes() {
		return Collections.unmodifiableMap(registeredTypes);
	}

	public static boolean isPatch(String key) {
		return key.equals(PATCH_KEY);
	}
	
	public static boolean isWall(String key) {
	    return key.equals(WALL_KEY);
	}
	
	public static boolean isMappedWall(String key) {
	    return key.equals(MAPPED_WALL_KEY);
	}

	public static boolean isMappedPatch(String key) {
	    return key.equals(MAPPED_PATCH_KEY);
	}

	public static boolean isOpening(String key) {
		return key.equals(OPENING_KEY);
	}

	public static boolean isPatchPhysicalType(String key) {
		return key.equals(INLET_KEY) || key.equals(OUTLET_KEY) || key.equals(OPENING_KEY);
	}

	public static boolean isWallPhysicalType(String key) {
	    return key.equals(FREE_SURFACE_KEY);
	}
	
    public static boolean isCoupledSymmetryPlaneType(String patchType) {
        return patchType.equals(SYMMETRY_PLANE_KEY);
    }

	public static boolean isProcessor(String key) {
		return key.equals(PROCESSOR_KEY);
	}

	public static boolean isProcessorCyclic(String key) {
		return key.equals(PROCESSOR_CYCLIC_KEY);
	}

	public static boolean isCyclicAMI(String key) {
		return key.equals(CYCLIC_AMI_KEY);
	}

	public static boolean isInlet(Patch patch) {
		return patch.getPhisicalType().getKey().equals(INLET_KEY);
	}

	public static boolean isKnown(String key) {
		return registeredTypes.containsKey(key);
	}

	public static BoundaryType getDefaultType() {
		return WALL;
	}

	public static String getDefaultKey() {
		return WALL_KEY;
	}

	public static BoundaryType getType(String key) {
		return registeredTypes.get(key);
	}

	private String label;
	private String key;
	private boolean hasBoundaryConditions;

	private BoundaryType(String key, String label, boolean hasBoundaryConditions) {
		this.key = key;
		this.label = label;
		this.hasBoundaryConditions = hasBoundaryConditions;
	}

	public String getLabel() {
		return label;
	}

	public String getKey() {
		return key;
	}

	public boolean hasBoundaryConditions() {
		return hasBoundaryConditions;
	}

	public Icon getIcon() {
		return registeredTypesIcon.get(key);
	}

	@Override
	public String toString() {
		return getKey();
	}

	@Override
	public boolean equals(Object obj) {
		return toString().equals(obj.toString());
	}

	public boolean isProcessor() {
		return this == PROCESSOR;
	}

	public boolean isProcessorCyclic() {
		return this == PROCESSOR_CYCLIC;
	}

	public boolean isCyclicAMI() {
		return this == CYCLIC_AMI;
	}

	public boolean isPatch() {
		return this == PATCH;
	}
};

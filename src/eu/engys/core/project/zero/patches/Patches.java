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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.engys.util.PrefUtil;

public class Patches extends ArrayList<Patch> {

	private Patches[] parallelPatches;

	public Patches() {
		super();
	}

	public Map<String, Patch> toMap() {
		Map<String, Patch> patchesMap = new HashMap<String, Patch>();
		for (Patch patch : this) {
			patchesMap.put(patch.getName(), patch);
		}
		return Collections.unmodifiableMap(patchesMap);
	}

	public void print() {
		for (Patch patch : this) {
			System.err.println(" >>>>>>>>>> " + patch.getName()+", hash: "+patch.hashCode());
			System.err.println(patch.getBoundaryConditions().toDictionary());
		}
	}

	public Patches filterProcBoundary() {
		Patches nonProcPatches = new Patches();
		for (Patch p : this) {
			if (!p.getPhisicalType().isProcessor() && !p.getPhisicalType().isProcessorCyclic()) {
				nonProcPatches.add(p);
			}
		}
		return nonProcPatches;
	}

	public Patches patchesToDisplay() {
		Patches patches = new Patches();
		for (Patch patch : this) {
		    Boolean hideEmptyPatches = PrefUtil.getBoolean(PrefUtil.HIDE_EMPTY_PATCHES);
			if ( (patch.isEmpty() && hideEmptyPatches) || patch.getPhisicalType().isProcessor() || patch.getPhisicalType().isProcessorCyclic()) {
				continue;
			}
			patches.add(patch);
		}
		return patches;
	}

	public void printBoundaryConditions(int procIndex, int patchIndex) {
		if (procIndex < 0) {
			for (int i = 0; i < parallelPatches.length; i++) {
				Patch patch = parallelPatches[i].get(patchIndex);
				System.out.println("PATCHES PRINT BOUNDARY CONDITIONS processor " + i + ", patch: " + patch.getName() + " " + patch.getBoundaryConditions().toDictionary());
			}
		} else {
			Patch patch = parallelPatches[procIndex].get(patchIndex);
			System.out.println("PATCHES PRINT BOUNDARY CONDITIONS processor " + procIndex + ", patch: " + patch.getName() + " " + patch.getBoundaryConditions().toDictionary());
		}
	}

	public void setParallelPatches(Patches[] parallelPatches) {
		this.parallelPatches = parallelPatches;
	}

	public Patches[] getParallelPatches() {
		return parallelPatches;
	}

	public Patches getPatchesOfProcessor(int processor) {
		return parallelPatches[processor];
	}

	public void clearBoundaryConditions() {
		for (Patch patch : this) {
			patch.setBoundaryConditions(new BoundaryConditions());
		}
		if (parallelPatches != null) {
			for (Patches patches : parallelPatches) {
				patches.clearBoundaryConditions();
			}
		}
	}
	
	public boolean addPatches(Collection<? extends Patch> c) {
		if (parallelPatches != null) {
			for (Patches patches : parallelPatches) {
				for (Patch patch : c) {
					patches.add(new Patch(patch));
				}
			}
		}
		return super.addAll(c);
	}

	public void newParallelPatches(int processors) {
		parallelPatches = new Patches[processors];
		for (int i = 0; i < parallelPatches.length; i++) {
			parallelPatches[i] = new Patches();
		}
	}
	
	public List<String> patchesNames() {
	    List<String> names = new ArrayList<>();
	    for (Patch patch : this) {
            names.add(patch.getName());
        }
        return names;
    }

	public List<String> patchesNames(BoundaryType type) {
	    List<String> names = new ArrayList<>();
	    for (Patch patch : this) {
	        if (patch.getPhisicalType().equals(type)) {
	            names.add(patch.getName());
	        }
	    }
	    return names;
	}
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		for (Patch patch : this) {
			sb.append(patch.getName() + " - ");
		}
		return sb.toString();
	}

}

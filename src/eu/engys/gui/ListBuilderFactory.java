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

package eu.engys.gui;

import java.util.ArrayList;
import java.util.List;

import eu.engys.core.controller.GeometryToMesh;
import eu.engys.core.project.Model;
import eu.engys.core.project.runtimefields.RuntimeField;
import eu.engys.core.project.zero.facezones.FaceZone;
import eu.engys.core.project.zero.facezones.FaceZones;
import eu.engys.core.project.zero.fields.Field;
import eu.engys.core.project.zero.patches.Patch;
import eu.engys.core.project.zero.patches.Patches;
import eu.engys.util.ui.ListBuilder;

public class ListBuilderFactory {

	public static ListBuilder getPatchesListBuilder(final Model model) {
		ListBuilder listBuilder = new ListBuilder() {

			@Override
			public String getTitle() {
				return "Select Patches";
			}

			@Override
			public String[] getSourceElements() {
				Patches patches = model.getPatches();
				List<String> elements = new ArrayList<>();
				if (patches != null) {
					for (Patch patch : patches.patchesToDisplay()) {
						elements.add(patch.getName());
					}
				}
				return elements.toArray(new String[0]);
			}

			@Override
			public int getSelectionMode() {
				return ListBuilder.MULTIPLE_SELECTION;
			}
		};
		return listBuilder;
	}

	public static ListBuilder getFaceZonesListBuilder(final Model model) {
		ListBuilder listBuilder = new ListBuilder() {

			@Override
			public String getTitle() {
				return "Select Face Zone";
			}

			@Override
			public String[] getSourceElements() {
				FaceZones faceZones = model.getFaceZones();
				List<String> elements = new ArrayList<>();
				if (faceZones != null) {
					for (FaceZone zone : faceZones) {
						elements.add(zone.getName());
					}
				}
				return elements.toArray(new String[0]);
			}

			@Override
			public int getSelectionMode() {
				return ListBuilder.MULTIPLE_SELECTION;
			}
		};
		return listBuilder;
	}

	public static ListBuilder getFieldsListBuilder(final Model model) {
		ListBuilder fieldsListBuilder = new ListBuilder() {

			@Override
			public String getTitle() {
				return "Select Fields";
			}

			@Override
			public String[] getSourceElements() {
				List<Field> fields = model.getFields().orderedFields();
				List<RuntimeField> runTimeFields = model.getRuntimeFields().fields();
				
				List<String> elements = new ArrayList<>();
				if (fields != null) {
					for (Field f : fields) {
						elements.add(f.getName());
					}
					for (RuntimeField rtf : runTimeFields) {
					    elements.add(rtf.getName());
					}
				}
				return elements.toArray(new String[0]);
			}

			@Override
			public int getSelectionMode() {
				return ListBuilder.MULTIPLE_SELECTION;
			}
		};
		return fieldsListBuilder;
	}
	
	public static ListBuilder getAdvancedMeshPatchesListBuilder(final Model model, final String title) {
        ListBuilder listBuilder = new ListBuilder() {
            
            @Override
            public String getTitle() {
                return title;
            }
            
            @Override
            public String[] getSourceElements() {
                return new GeometryToMesh(model).listPatches();
            }
            
            @Override
            public int getSelectionMode() {
                return ListBuilder.MULTIPLE_SELECTION;
            }
        };
        return listBuilder;
    }
}
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
package eu.engys.gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import eu.engys.core.controller.GeometryToMesh;
import eu.engys.core.project.Model;
import eu.engys.core.project.runtimefields.RuntimeField;
import eu.engys.core.project.zero.cellzones.CellZone;
import eu.engys.core.project.zero.cellzones.CellZones;
import eu.engys.core.project.zero.facezones.FaceZone;
import eu.engys.core.project.zero.facezones.FaceZones;
import eu.engys.core.project.zero.fields.Field;
import eu.engys.core.project.zero.fields.Fields;
import eu.engys.core.project.zero.patches.Patch;
import eu.engys.core.project.zero.patches.Patches;
import eu.engys.util.ui.ListBuilder;

public class ListBuilderFactory {

    /*
     * List Model
     */

    public static ReloadableListModel getPatchesListModel(final Model model) {
        return new ReloadableListModel() {
            {
                reload();
            }

            @Override
            public void reload() {
                Patches patches = model.getPatches();
                List<String> elements = new ArrayList<>();
                if (patches != null) {
                    for (Patch patch : patches.patchesToDisplay()) {
                        elements.add(patch.getName());
                    }
                }
                this.elements = elements.toArray(new String[0]);
            }
        };
    }

    public static ReloadableListModel getFieldsListModel(final Model model) {
        return new ReloadableListModel() {
            {
                reload();
            }

            @Override
            public void reload() {
                Fields fields = model.getFields();
                List<String> elements = new ArrayList<>();
                if (fields != null) {
                    for (Field field : fields.orderedFields()) {
                        elements.add(field.getName());
                    }
                }
                this.elements = elements.toArray(new String[0]);
            }
        };
    }

    public static ReloadableListModel getScalarFieldsListModel(final Model model) {
        return new ReloadableListModel() {
            {
                reload();
            }

            @Override
            public void reload() {
                Fields fields = model.getFields();
                List<String> elements = new ArrayList<>();
                if (fields != null) {
                    for (Field field : fields.orderedScalarFields()) {
                        elements.add(field.getName());
                    }
                }
                this.elements = elements.toArray(new String[0]);
            }
        };
    }
    
    public static ReloadableListModel getFaceZonesListModel(final Model model) {
        return new ReloadableListModel() {
            {
                reload();
            }

            @Override
            public void reload() {
                FaceZones faceZones = model.getFaceZones();
                List<String> elements = new ArrayList<>();
                if (faceZones != null) {
                    for (FaceZone zone : faceZones) {
                        elements.add(zone.getName());
                    }
                }
                this.elements = elements.toArray(new String[0]);
            }
        };
    }

    /*
     * List Builder
     */

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

    public static ListBuilder getCellZonesListBuilder(final Model model, final ListBuilder... additionalBuilders) {
        ListBuilder listBuilder = new ListBuilder() {

            @Override
            public String getTitle() {
                return "Select Cell Zone";
            }

            @Override
            public String[] getSourceElements() {
                CellZones cellZones = model.getCellZones();
                List<String> elements = new ArrayList<>();
                if (cellZones != null) {
                    for (CellZone zone : cellZones) {
                        elements.add(zone.getName());
                    }
                }
                for (ListBuilder listBuilder : additionalBuilders) {
                    elements.addAll(Arrays.asList(listBuilder.getSourceElements()));
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

    public static ListBuilder getFieldsListBuilder(final Model model, final ListBuilder... additionalBuilders) {
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
                    for (ListBuilder listBuilder : additionalBuilders) {
                        elements.addAll(Arrays.asList(listBuilder.getSourceElements()));
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
